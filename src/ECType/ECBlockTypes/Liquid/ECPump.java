package ECType.ECBlockTypes.Liquid;

import ECConfig.Config;
import ECConfig.ECData;
import ECConfig.ECSetting;
import ECConfig.ECTool;
import ECContents.Achievements;
import ECType.ECBlockTypes.Crafter.ECDrill;
import arc.Core;
import arc.func.Func;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.gen.Building;
import mindustry.type.Liquid;
import mindustry.ui.Bar;
import mindustry.ui.Styles;
import mindustry.world.Tile;
import mindustry.world.blocks.production.Pump;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static mindustry.Vars.*;

public class ECPump extends Pump{

    public Pump root;

    public int level;

    public float outputMultiple;

    public static Config config = new Config().addConfigSimple(null, "buildType")
            .scaleConfig().linearConfig("liquidCapacity");

    public boolean compressLiquid = true;

    public ECPump(Pump root,int level) throws IllegalAccessException {

        super("c" + level + "-" + root.name);
        this.root = root;
        this.level = level;
        this.outputMultiple = Mathf.pow(ECSetting.LINEAR_MULTIPLIER,level);

        ECTool.compress(root, this, config, level);
        ECTool.loadCompressContentRegion(root, this);
        ECTool.setIcon(root, this, level);
        ECTool.loadHealth(this,root,level);
        requirements(root.category, root.buildVisibility, ECTool.compressItemStack(root.requirements,level));

        localizedName = level + Core.bundle.get("num-Compression.localizedName") + root.localizedName;
        description = root.description;
        details = root.details;

        ECData.register(root,this,level);
    }


    @Override
    public void init() {
        consumeBuilder = ECTool.consumeBuilderCopy(root,level);
        Core.settings.defaults(name,true);
        compressLiquid = Core.settings.getBool(name);
        super.init();
    }

    @Override
    public void setStats() {
        super.setStats();

        if (level> 2) stats.add(new Stat("compressore"),table -> {
            table.button(compressLiquid?Core.bundle.get("stat.true"):Core.bundle.get("stat.false"), Styles.flatTogglet,()->{
                compressLiquid = !compressLiquid;
                Core.settings.put(name,compressLiquid);
            }).size(75,30);
            table.setSize(75,30);
        });

        stats.remove(Stat.output);
        stats.add(Stat.output, 60f * pumpAmount * size * size * outputMultiple *
                (Achievements.pumpStrengthen.working(this)&&compressLiquid?Mathf.pow(1f/9f,level-2):1)
                , StatUnit.liquidSecond);
    }

    @Override
    public void setBars() {
        super.setBars();
        /*/
        barMap.remove("liquid");
        this.addLiquidBar((PumpBuild build) -> build.liquidDrop);
        //*/
    }

    public <T extends Building> void addLiquidBar(Func<T, Liquid> current){
        addBar("liquid", entity -> new Bar(
                () -> current.get((T)entity) == null || entity.liquids.get(current.get((T)entity)) <= 0.001f ? Core.bundle.get("bar.liquid") : current.get((T)entity).localizedName,
                () -> current.get((T)entity) == null ? Color.clear : current.get((T)entity).barColor(),
                () -> current.get((T)entity) == null ? 0f : entity.liquids.get(current.get((T)entity)) / liquidCapacity)
        );
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        drawPotentialLinks(x, y);
        drawOverlay(x * tilesize + offset, y * tilesize + offset, rotation);

        Tile tile = world.tile(x, y);
        if(tile == null) return;

        float amount = 0f;
        Liquid liquidDrop = null;

        for(Tile other : tile.getLinkedTilesAs(this, tempTiles)){
            if(canPump(other)){
                if(liquidDrop != null && other.floor().liquidDrop != liquidDrop){
                    liquidDrop = null;
                    break;
                }
                liquidDrop = other.floor().liquidDrop;
                amount += other.floor().liquidMultiplier;
            }
        }

        if(liquidDrop != null){
            float width = drawPlaceText(Core.bundle.formatFloat("bar.pumpspeed", amount * pumpAmount * outputMultiple * 60f *
                            (Achievements.pumpStrengthen.working(this)&&compressLiquid?Mathf.pow(1f/9f,level-2):1)
                    , 0), x, y, valid);
            float dx = x * tilesize + offset - width/2f - 4f, dy = y * tilesize + offset + size * tilesize / 2f + 5, s = iconSmall / 4f;
            float ratio = (float)liquidDrop.fullIcon.width / liquidDrop.fullIcon.height;
            Draw.mixcol(Color.darkGray, 1f);

            Liquid liquid = liquidDrop;


            if (Achievements.pumpStrengthen.working(this)&&compressLiquid) {
                liquid = ECData.get(liquidDrop, level-2);
            }

            Draw.rect(liquid.fullIcon, dx, dy - 1, s * ratio, s);
            Draw.reset();
            Draw.rect(liquid.fullIcon, dx, dy, s * ratio, s);
        }
    }


    public class ECPumpBuild extends PumpBuild{

        @Override
        public void updateTile() {
            if(efficiency > 0 && liquidDrop != null){
                float maxPump = Math.min(liquidCapacity - liquids.get(liquidDrop), amount * pumpAmount * outputMultiple *edelta());
                liquids.add(liquidDrop, maxPump);

                //does nothing for most pumps, as those do not require items.
                if((consTimer += delta()) >= consumeTime){
                    consume();
                    consTimer %= 1f;
                }

                warmup = Mathf.approachDelta(warmup, maxPump > 0.001f ? 1f : 0f, warmupSpeed);
            }else{
                warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed);
            }

            totalProgress += warmup * Time.delta;


            if(liquidDrop != null){
                if (Achievements.pumpStrengthen.working(this.block)&&compressLiquid){
                    if (level > 2 && liquids.get(liquidDrop) >= Mathf.pow(9,level-2)){
                        int amount = (int) (liquids.get(liquidDrop)/Mathf.pow(9,level-2));
                        liquids.remove(liquidDrop,Mathf.pow(9,level-2)*amount);
                        liquids.add(ECData.get(liquidDrop,level-2),amount);
                    }
                    dumpLiquid(ECData.get(liquidDrop,level-2));
                }else {
                    dumpLiquid(liquidDrop);
                }
            }
        }

        @Override
        public void dumpLiquid(Liquid liquid, float scaling, int outputDir) {
            ECTool.dumpLiquids(liquid, scaling, outputDir , this);
        }

    }
}
