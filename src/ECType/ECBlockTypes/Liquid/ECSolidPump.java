package ECType.ECBlockTypes.Liquid;

import ECConfig.Config;
import ECConfig.ECData;
import ECConfig.ECSetting;
import ECConfig.ECTool;
import arc.Core;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.ctype.UnlockableContent;
import mindustry.world.blocks.production.Pump;
import mindustry.world.blocks.production.SolidPump;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

public class ECSolidPump extends SolidPump {
    public Pump root;

    public int level;

    public float outputMultiple;

    public static Config config = new Config().addConfigSimple(null, "buildType")
            .scaleConfig().linearConfig("liquidCapacity","health");


    public float itemUseTime = 60f;

    public ECSolidPump(SolidPump root, int level) throws IllegalAccessException {

        super("c" + level + "-" + root.name);
        this.root = root;
        this.level = level;
        this.outputMultiple = Mathf.pow(ECSetting.LINEAR_MULTIPLIER,level);

        ECTool.compress(root, this,SolidPump.class, UnlockableContent.class, config, level);
        ECTool.loadCompressContentRegion(root, this);
        ECTool.setIcon(root, this, level);
        requirements(root.category, root.buildVisibility, ECTool.compressItemStack(root.requirements,level));

        localizedName = level + Core.bundle.get("num-Compression.localizedName") + root.localizedName;
        description = root.description;
        details = root.details;

        ECData.register(root,this,level);
    }

    @Override
    public void init() {
        consumeBuilder = ECTool.consumeBuilderCopy(root,level);
        health = root.health * Mathf.pow(5,level);
        super.init();
    }

    @Override
    public void setStats() {
        stats.timePeriod = itemUseTime;
        super.setStats();

        stats.add(Stat.productionTime, itemUseTime / 60f, StatUnit.seconds);
        stats.remove(Stat.output);
        stats.add(Stat.output, result, 60f * pumpAmount * outputMultiple, true);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        drawPotentialLinks(x, y);

        if(attribute != null){
            drawPlaceText(Core.bundle.format("bar.efficiency", Math.round(Math.max((sumAttribute(attribute, x, y)) / size / size + percentSolid(x, y) * baseEfficiency, 0f) * 100 * outputMultiple)), x, y, valid);
        }
    }

    public class ECSolidPumpBuild extends SolidPumpBuild{


        public float accumulator;

        @Override
        public void updateTile() {


            if(efficiency > 0){
                if(accumulator >= itemUseTime){
                    consume();
                    accumulator -= itemUseTime;
                }

                accumulator += delta() * efficiency;
                superUpdateTile();

            }else{
                warmup = Mathf.lerpDelta(warmup, 0f, 0.02f);
                lastPump = 0f;
                dumpLiquid(result);
            }


        }

        private void superUpdateTile() {
            liquidDrop = result;
            float fraction = Math.max(validTiles + boost + (attribute == null ? 0 : attribute.env()), 0);

            if(efficiency > 0 && typeLiquid() < liquidCapacity - 0.001f){
                float maxPump = Math.min(liquidCapacity - typeLiquid(), pumpAmount * delta() * fraction * efficiency * outputMultiple);
                liquids.add(result, maxPump);
                lastPump = maxPump / Time.delta;
                warmup = Mathf.lerpDelta(warmup, 1f, 0.02f);
                if(Mathf.chance(delta() * updateEffectChance))
                    updateEffect.at(x + Mathf.range(size * 2f), y + Mathf.range(size * 2f));
            }else{
                warmup = Mathf.lerpDelta(warmup, 0f, 0.02f);
                lastPump = 0f;
            }

            pumpTime += warmup * edelta();

            ECTool.dumpLiquids(result,2F / outputMultiple,-1,this);
        }




    }
}
