package ECType.ECTurretTypes;

import ECConfig.Config;
import ECConfig.ECData;
import ECConfig.ECTool;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.ui.Image;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.ctype.UnlockableContent;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.type.Liquid;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import mindustry.world.consumers.Consume;
import mindustry.world.consumers.ConsumeCoolant;
import mindustry.world.consumers.ConsumeLiquid;
import mindustry.world.consumers.ConsumePower;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatCat;
import mindustry.world.meta.StatValues;

public class ECPowerTurret extends PowerTurret {

    public PowerTurret root;

    public ObjectMap<Float,BulletType> ammoTypes = new ObjectMap<>();

    public ObjectMap<Integer,Float> powerUse = new ObjectMap<>();

    public ObjectMap<Integer,ConsumePower> consumePowers = new ObjectMap<>();

    public static Config config = new Config().addConfigSimple(null,"buildType","shootTypes","consumers","optionalConsumers","nonOptionalConsumers","updateConsumers");


    public ECPowerTurret(PowerTurret root) throws IllegalAccessException {
        super(root.name);
        this.root = root;
        ECTool.compress(root,this, UnlockableContent.class , config, 0);
        ECTool.loadCompressContentRegion(root, this);
        ECTool.setIcon(root, this, 0);
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

        config(Integer.class, (ECPowerTurretBuild tile, Integer index) -> tile.index = index);
        configClear((ECPowerTurretBuild tile) -> tile.index = 0);

        ECData.register(root,this,1);
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.remove(Stat.ammo);
        stats.remove(Stat.powerUse);
        for (int i = 0 ; i < 10 ; i ++){
            stats.add(new Stat("ammo"+i, StatCat.function), StatValues.ammo(ObjectMap.of(this, ammoTypes.get(powerUse.get(i)))));
        }
    }

    @Override
    public void init() {
        try {
            initAmmo();
            initConsumePower();
            initCoolant();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        //consumeBuilder = ECTool.consumeBuilderCopy(root,1);
        super.init();
    }

    public void initAmmo() throws IllegalAccessException {
        BulletType rootAmmo = root.shootType.copy();
        float power = 0.01f;
        for (Consume consume : root.consumers){
            if (consume instanceof ConsumePower c){
                if (c.usage!=0) power = c.usage;
            }
        }
        ammoTypes.put(power,rootAmmo);
        powerUse.put(0,power);
       // consumePowers.put(0,new ConsumePower(power, 1f, false));
        for (int i = 1 ; i < 10;i++){
            float usePower = power*Mathf.pow(5f,i);
            ammoTypes.put(usePower,ECTool.compressBulletType(rootAmmo,i));
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

    public void initCoolant() throws IllegalAccessException {

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

    public class ECPowerTurretBuild extends PowerTurretBuild{

        public int index = 0;

        /*/
        @Override
        public float getPowerProduction() {
            return isShooting()? -powerUse.get(index):0f;
        }
        //*/


        public BulletType getAmmo(int index){
            return ammoTypes.get(powerUse.get(index));
        }

        @Override
        public BulletType useAmmo(){
            //nothing used directly
            return getAmmo(index);
        }

        @Override
        public boolean hasAmmo(){
            //you can always rotate, but never shoot if there's no power
            return true;
        }

        @Override
        public BulletType peekAmmo(){
            return getAmmo(index);
        }


        @Override
        public void write(Writes write) {
            super.write(write);
            write.i(index);//保存当前配方索引
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            index = read.i();//读取保存的索引
        }

        @Override//配置菜单
        public void buildConfiguration(Table table) {
            table.button(Icon.pencil, Styles.clearTogglei, () -> new ECPowerTurretDialog(this).show()).size(40f);
        }

        /*/
        @Override
        public void consume() {
            Consume[] consumes = this.block.consumers;
            int size = consumes.length;

            for(int i = 0; i < size; ++i) {
                Consume cons = consumes[i];

                if (cons instanceof ConsumePower){
                    consumePowers.get(index).trigger(this);
                }
                else cons.trigger(this);
            }

        }

        //*/
    }


    public class ECPowerTurretDialog extends BaseDialog {
        public ECPowerTurretBuild build;
        public ECPowerTurret block;

        public ECPowerTurretDialog(ECPowerTurretBuild build) {
            super(Core.bundle.get("ECType.Recipe.RecipeDialog"));
            this.build = build;
            this.block = (ECPowerTurret) build.block;
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
                BulletType ammo = ammoTypes.get(powerUse.get(i));




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
                    hide();

                }).get();


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

    public class ECConsumePower extends ConsumePower{
        public ECConsumePower(float usage, float capacity, boolean buffered) {
            super(usage,capacity,buffered);
        }

        @Override
        public float requestedPower(Building entity) {
            ECTool.print("requestedPower");

            if (entity instanceof ECPowerTurretBuild build){
                ECTool.print(buffered ?
                        (1f - entity.power.status) * capacity :
                        ((ECPowerTurret)build.block).powerUse.get(build.index) * (entity.shouldConsume() ? 1f : 0f));
                return buffered ?
                        (1f - entity.power.status) * capacity :
                        ((ECPowerTurret)build.block).powerUse.get(build.index) * (entity.shouldConsume() ? 1f : 0f);
            }

            return super.requestedPower(entity);
        }
    }
}
