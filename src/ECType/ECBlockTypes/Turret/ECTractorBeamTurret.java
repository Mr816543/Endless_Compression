package ECType.ECBlockTypes.Turret;

import ECConfig.Config;
import ECConfig.ECData;
import ECConfig.ECSetting;
import ECConfig.ECTool;
import ECType.ECConsumePower;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.scene.ui.Image;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.content.Items;
import mindustry.content.StatusEffects;
import mindustry.core.UI;
import mindustry.ctype.UnlockableContent;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Icon;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.Liquid;
import mindustry.ui.Bar;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.world.blocks.defense.turrets.TractorBeamTurret;
import mindustry.world.blocks.power.PowerGraph;
import mindustry.world.consumers.Consume;
import mindustry.world.consumers.ConsumeCoolant;
import mindustry.world.consumers.ConsumeLiquid;
import mindustry.world.consumers.ConsumePower;

import static mindustry.Vars.*;
import static mindustry.Vars.state;

public class ECTractorBeamTurret extends TractorBeamTurret {

    public static Config config = new Config().addConfigSimple(null, "buildType")
            .scaleConfig("force","scaledForce","rotateSpeed")
            .linearConfig("damage");
    public TractorBeamTurret root;
    public ObjectMap<Integer,Float> powerUse = new ObjectMap<>();


    public ECTractorBeamTurret(TractorBeamTurret root) throws IllegalAccessException {
        super(root.name);
        this.root = root;
        ECTool.compress(root,this, UnlockableContent.class , config, 0);
        ECTool.loadCompressContentRegion(root, this);
        ECTool.setIcon(root, this, 0);
        ECTool.loadHealth(this,root,1);
        requirements(root.category, root.buildVisibility, ECTool.compressItemStack(root.requirements,1));
        localizedName = Core.bundle.get("Compression.localizedName") + root.localizedName;
        description = root.description;
        details = root.details;

        sync = true;//多人游戏时需要同步
        saveConfig = true;//保存设置
        copyConfig = true;//复制设置
        configurable = true;//允许设置

        consumesPower = true;//消耗电力
        outputsPower = true;//输出电力

        config(Integer.class, (TractorBeamBuild b, Integer index) -> {
            if (b instanceof ECTractorBeamTurretBuild tile) tile.index = index;
        });
        configClear( (TractorBeamBuild b) -> {
            if (b instanceof ECTractorBeamTurretBuild tile) tile.index = 0;
        });

        ECData.register(root,this,1);
    }

    @Override
    public void init() {
        initAmmo();
        initCoolant();
        initConsumePower();
        super.init();
    }

    @Override
    public void setStats() {
        super.setStats();
    }


    public void initAmmo() {
        float power = 0.01f;
        for (Consume consume : root.consumers){
            if (consume instanceof ConsumePower c){
                if (c.usage!=0) power = c.usage;
            }
        }
        powerUse.put(0,power);
        // consumePowers.put(0,new ConsumePower(power, 1f, false));
        for (int i = 1 ; i < 10;i++){
            float usePower = power*Mathf.pow(5f,i);
            powerUse.put(i,usePower);
            // consumePowers.put(i,new ConsumePower(usePower, 1f, false));
        }
    }

    public void initConsumePower(){
        consPower = new ECConsumePower(powerUse.get(0), 1f, false);//电力消耗器
        consumeBuilder.add(consPower);//添加
    }

    boolean findLiquid(Seq<Liquid> liquids, Liquid liquid){

        for (Liquid l:liquids){
            if (l==liquid) return true;
        }

        return false;
    }

    public void initCoolant() {

        if (coolant instanceof ConsumeLiquid c){
            Liquid rLiquid = c.liquid;
            float amount = c.amount;

            if (!ECData.hasECContent(rLiquid)) return;

            Seq<Liquid> liquids = ECData.ECLiquids.get(rLiquid);



            coolant = new ConsumeCoolant(amount){{

                this.filter = liquid -> findLiquid(liquids,liquid) && liquid.coolant && (this.allowLiquid && !liquid.gas || this.allowGas && liquid.gas) && liquid.temperature <= maxTemp && liquid.flammability < maxFlammability;

            }};


        }

    }


    public class ECTractorBeamTurretBuild extends TractorBeamBuild {


        public int index = 0;

        public int barIndex = -1;


        @Override
        public void updateTile(){
            float eff = efficiency * coolantMultiplier, edelta = eff * delta();

            //retarget
            if(timer(timerTarget, retargetTime)){
                target = Units.closestEnemy(team, x, y, range(), u -> u.checkTarget(targetAir, targetGround));
            }

            //consume coolant
            if(target != null && coolant != null){
                float maxUsed = coolant.amount;

                Liquid liquid = liquids.current();

                float used = Math.min(Math.min(liquids.get(liquid), maxUsed * Time.delta), Math.max(0, (1f / coolantMultiplier) / liquid.heatCapacity));

                liquids.remove(liquid, used);

                if(Mathf.chance(0.06 * used)){
                    coolEffect.at(x + Mathf.range(size * tilesize / 2f), y + Mathf.range(size * tilesize / 2f));
                }

                coolantMultiplier = 1f + (used * liquid.heatCapacity * coolantMultiplier);
            }

            any = false;

            //look at target
            if(target != null && target.within(this, range() + target.hitSize/2f) && target.team() != team && target.checkTarget(targetAir, targetGround) && efficiency > 0.02f){
                if(!headless){
                    control.sound.loop(shootSound, this, shootSoundVolume);
                }

                float dest = angleTo(target);
                rotation = Angles.moveToward(rotation, dest, rotateSpeed * edelta);
                lastX = target.x;
                lastY = target.y;
                strength = Mathf.lerpDelta(strength, 1f, 0.1f);

                //shoot when possible
                if(Angles.within(rotation, dest, shootCone)){
                    if(damage > 0){
                        target.damageContinuous(damage * eff * timeScale * state.rules.blockDamage(team));
                    }

                    if(status != StatusEffects.none){
                        target.apply(status, statusDuration);
                    }

                    any = true;
                    target.impulseNet(Tmp.v1.set(this).sub(target).limit((
                            force * Mathf.pow(ECSetting.SCALE_MULTIPLIER,index)
                                    + (1f - target.dst(this) / range()) *
                                    scaledForce * Mathf.pow(ECSetting.SCALE_MULTIPLIER,index)
                    ) * edelta));
                }
            }else{
                strength = Mathf.lerpDelta(strength, 0, 0.1f);
            }

            if (barIndex != index){
                updateBars();
                barIndex = index;
            }
        }

        @Override
        public boolean shouldConsume(){
            return super.shouldConsume() && target != null;
        }

        @Override
        public float estimateDps(){
            if(!any || damage <= 0) return 0f;
            return damage * 60f * efficiency * coolantMultiplier;
        }

        @Override
        public void draw(){
            Draw.rect(baseRegion, x, y);
            Drawf.shadow(region, x - (size / 2f), y - (size / 2f), rotation - 90);
            Draw.rect(region, x, y, rotation - 90);

            //draw laser if applicable
            if(any && !isPayload()){
                Draw.z(Layer.bullet);
                float ang = angleTo(lastX, lastY);

                Draw.mixcol(laserColor, Mathf.absin(4f, 0.6f));

                Drawf.laser(laser, laserStart, laserEnd,
                        x + Angles.trnsx(ang, shootLength), y + Angles.trnsy(ang, shootLength),
                        lastX, lastY, strength * efficiency * laserWidth);

                Draw.mixcol();
            }
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.i(index);//保存当前配方索引
            write.f(rotation);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            index = read.i();//读取保存的索引
            rotation = read.f();
        }

        @Override
        public Object config() {
            return index;
        }

        public void updateBars(){

            removeBar("power");
            if(consPower != null){
                boolean buffered = consPower.buffered;
                float capacity = consPower.capacity;


                PowerGraph graph = power.graph;

                addBar("power", entity -> new Bar(
                                () -> buffered ? Core.bundle.format("bar.poweramount", Float.isNaN(entity.power.status * capacity) ? "<ERROR>" : UI.formatAmount((int)(entity.power.status * capacity))) :
                                        Core.bundle.get("bar.power"),
                                () -> Pal.powerBar,
                                () -> graph == null ? 0 : Mathf.clamp(
                                        (graph.getPowerProduced()+ graph.getBatteryStored() ) / powerUse.get(index)
                                )

                        )
                );
            }



        }


        @Override//配置菜单
        public void buildConfiguration(Table table) {
            table.button(Icon.pencil, Styles.clearTogglei, () -> new ECTractorBeamTurretDialog(this).show()).size(40f);
        }



        @Override
        public float range() {
            return range * Mathf.pow(ECSetting.SCALE_MULTIPLIER,index) ;
        }



    }


    public class ECTractorBeamTurretDialog extends BaseDialog {
        public ECTractorBeamTurretBuild build;
        public ECTractorBeamTurret block;

        public ECTractorBeamTurretDialog(ECTractorBeamTurretBuild build) {
            super(Core.bundle.get("ECType.Recipe.RecipeDialog"));
            this.build = build;
            this.block = (ECTractorBeamTurret) build.block;
            setup();
        }


        public void setup() {
            cont.clear();

            Table table = new Table();
            //table.defaults().growX().height(50f).pad(10f);
            table.defaults().growX().uniformX().fillX().height(50f).pad(5f);
            ScrollPane pane = new ScrollPane(table);

            pane.setScrollingDisabled(false, false);
            int buttonsPerRow = Vars.android ? 2 : 4; // 每行显示的按钮数量

            int count = 0;

            for (int i = 0; i < powerUse.size; i++) {

                float power = powerUse.get(i);




                int finalI = i;
                table.button(b -> {
                    //b.add(r.name).left();
                    //b.row();
                    //b.add(new Image(imageForRecipe(r))).size(32f);

                    b.add(new Table(t -> {
                        if (finalI == build.index) {
                            t.add(Core.bundle.get("ECType.Recipe.RecipeDialog.now") + " ").color(Color.yellow);
                        }
                        //t.add(r.name + " : ");

                        Seq<Object> dialogTXT = new Seq<>();

                        if (power != 0) {
                            boolean isInt = power - (int) power < 0.01F || power - (int) power > 0.99F;
                            if (isInt) {
                                dialogTXT.add(Integer.toString((int) power * 60));
                            } else dialogTXT.add(Float.toString(power*60));
                            dialogTXT.add(Icon.power.getRegion());
                        }





                        for (Object o : dialogTXT) {
                            if (o instanceof TextureRegion textureRegion) {
                                t.add(new Image(textureRegion)).size(32f);
                            } else if (o instanceof String string) {
                                t.add(string).fontScale(0.8f);
                            } else t.add(new Image(Core.atlas.find("error")));
                        }

                        //t.add(new Image(imageForRecipe(r))).size(40f);
                    })).grow().left();
                }, Styles.cleart, () -> {//触发事件
                    build.index = finalI;
                    build.updateBars();
                    hide();

                }).get().setDisabled(!(Vars.state == null || (Vars.state.rules.infiniteResources || ECData.get(Items.silicon,finalI).unlocked())));


                count++;
                // 每行添加指定数量按钮后换行
                if (count % buttonsPerRow == 0) {
                    table.row();
                }

            }

            // 处理最后一行不满的情况
            if (count % buttonsPerRow != 0) {
                // 填充剩余单元格以保持布局
                int remaining = buttonsPerRow - (count % buttonsPerRow);
                for (int i = 0; i < remaining; i++) {
                    table.add().growX(); // 添加空单元格
                }
            }


            cont.add(pane).grow();
        }

    }

}
