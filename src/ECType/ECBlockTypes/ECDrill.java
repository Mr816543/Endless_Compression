package ECType.ECBlockTypes;

import ECConfig.Config;
import ECConfig.ECData;
import ECConfig.ECSetting;
import ECConfig.ECTool;
import arc.Core;
import arc.math.Mathf;
import arc.util.Log;
import arc.util.Strings;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.ui.Bar;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.production.Drill;
import mindustry.world.consumers.ConsumeLiquidBase;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValues;

import static mindustry.Vars.indexer;
import static mindustry.Vars.state;

public class ECDrill extends Drill {

    public Drill root;

    public int level;

    public float outputMultiple;

    public static Config config = new Config().addConfigSimple(null, "buildType")
            .scaleConfig("drillEffectChance").linearConfig("itemCapacity", "rotateSpeed");


    public ECDrill(Drill root, int level) throws IllegalAccessException {
        super("c" + level + "-" + root.name);
        this.root = root;
        this.level = level;
        this.outputMultiple = Mathf.pow(ECSetting.LINEAR_MULTIPLIER,level);
        ECTool.compress(root, this, config, level);
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
        super.init();
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.remove(Stat.drillTier);
        stats.remove(Stat.drillSpeed);
        stats.remove(Stat.booster);

        stats.add(Stat.drillTier, StatValues.drillables(drillTime, hardnessDrillMultiplier, size * size * outputMultiple, drillMultipliers, b -> b instanceof Floor f && !f.wallOre && f.itemDrop != null &&
                f.itemDrop.hardness <= tier && (blockedItems == null || !blockedItems.contains(f.itemDrop)) && (indexer.isBlockPresent(f) || state.isMenu())));

        stats.add(Stat.drillSpeed, 60f / drillTime * size * size * outputMultiple, StatUnit.itemsSecond);

        if(liquidBoostIntensity != 1 && findConsumer(f -> f instanceof ConsumeLiquidBase && f.booster) instanceof ConsumeLiquidBase consBase){
            stats.remove(Stat.booster);
            stats.add(Stat.booster,
                    StatValues.speedBoosters("{0}" + StatUnit.timesSpeed.localized(),
                            consBase.amount,
                            liquidBoostIntensity * liquidBoostIntensity, false, consBase::consumes)
            );
        }
    }

    @Override
    public void setBars() {
        super.setBars();
        removeBar("drillspeed");
        addBar("drillspeed", (DrillBuild e) ->
                new Bar(() -> Core.bundle.format("bar.drillspeed", Strings.fixed(e.lastDrillSpeed * 60 * e.timeScale() * outputMultiple  , 2)), () -> Pal.ammo, () -> e.warmup));
    }

    public class ECDrillBuild extends DrillBuild {


        @Override
        public void updateTile() {
            //*/


            if (dominantItem == null) {
                return;
            }

            if (items.has(dominantItem)) {
                dump(dominantItem);
            }


            timeDrilled += warmup * delta();

            float delay = getDrillTime(dominantItem);

            if (items.total() < itemCapacity && dominantItems > 0 && efficiency > 0) {
                float speed = Mathf.lerp(1f, liquidBoostIntensity, optionalEfficiency) * efficiency;

                lastDrillSpeed = (speed * dominantItems * warmup) / delay;
                warmup = Mathf.approachDelta(warmup, speed, warmupSpeed);
                progress += delta() * dominantItems * speed * warmup;

                if (Mathf.chanceDelta(updateEffectChance * warmup))
                    updateEffect.at(x + Mathf.range(size * 2f), y + Mathf.range(size * 2f));
            } else {
                lastDrillSpeed = 0f;
                warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed);
                return;
            }

            if (dominantItems > 0 && progress >= delay && items.total() < itemCapacity) {
                int amount = (int) ((progress / delay) * outputMultiple);
                amount = Math.min(getMaximumAccepted(dominantItem) - items.get(dominantItem), amount);

                // amount *= (int) Math.pow(ECSetting.SCALE_MULTIPLIER,level);

                items.add(dominantItem, amount);
                progress %= delay;

                if (wasVisible && Mathf.chanceDelta(drillEffectChance * warmup))
                    drillEffect.at(x + Mathf.range(drillEffectRnd), y + Mathf.range(drillEffectRnd), dominantItem.color);
            }
            //*/
        }

        //*/
        @Override
        public boolean dump(Item item) {
            return ECTool.dump(this,item);
        }

//*/
    }
}
