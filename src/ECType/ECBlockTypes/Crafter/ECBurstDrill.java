package ECType.ECBlockTypes.Crafter;

import ECConfig.Config;
import ECConfig.ECData;
import ECConfig.ECSetting;
import ECConfig.ECTool;
import ECContents.Achievements;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.util.Strings;
import mindustry.entities.Effect;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.ui.Bar;
import mindustry.ui.Styles;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.production.BurstDrill;
import mindustry.world.blocks.production.Drill;
import mindustry.world.consumers.ConsumeLiquidBase;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValues;

import static mindustry.Vars.*;

public class ECBurstDrill extends BurstDrill {

    public static Config config = new Config().addConfigSimple(null, "buildType")
            .scaleConfig("drillEffectChance").linearConfig("itemCapacity", "rotateSpeed");
    public BurstDrill root;
    public int level;
    public float outputMultiple;
    public boolean compressOre = false;


    public ECBurstDrill(BurstDrill root, int level) throws IllegalAccessException {
        super("c" + level + "-" + root.name);
        this.root = root;
        this.level = level;
        this.outputMultiple = Mathf.pow(ECSetting.LINEAR_MULTIPLIER, level);
        ECTool.compress(root, this, config, level);
        ECTool.loadCompressContentRegion(root, this);
        ECTool.setIcon(root, this, level);
        ECTool.loadHealth(this, root, level);
        requirements(root.category, root.buildVisibility, ECTool.compressItemStack(root.requirements, level));

        localizedName = level + Core.bundle.get("num-Compression.localizedName") + root.localizedName;
        description = root.description;
        details = root.details;


        ECData.register(root, this, level);
    }

    @Override
    public void init() {
        consumeBuilder = ECTool.consumeBuilderCopy(root, level);
        compressOre = Core.settings.getBool(name,false);
        super.init();
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.remove(Stat.drillTier);
        stats.remove(Stat.drillSpeed);
        stats.remove(Stat.booster);


        if (level> 2 && Achievements.burstDrillStrengthen.working(this)) stats.add(new Stat("compressore"),table -> {
            table.button(compressOre?Core.bundle.get("stat.true"):Core.bundle.get("stat.false"), Styles.flatTogglet,()->{
                compressOre = !compressOre;
                Core.settings.put(name,compressOre);
            }).size(75,30);
            table.setSize(75,30);
        });

        stats.add(Stat.drillTier, StatValues.drillables(drillTime, hardnessDrillMultiplier, size * size * outputMultiple
                , drillMultipliers, b -> b instanceof Floor f && !f.wallOre && f.itemDrop != null &&
                        f.itemDrop.hardness <= tier && (blockedItems == null || !blockedItems.contains(f.itemDrop)) && (indexer.isBlockPresent(f) || state.isMenu())));

        stats.add(Stat.drillSpeed, 60f / drillTime * size * size * outputMultiple
                , StatUnit.itemsSecond);

        if(liquidBoostIntensity != 1 && findConsumer(f -> f instanceof ConsumeLiquidBase && f.booster) instanceof ConsumeLiquidBase consBase){
            stats.remove(Stat.booster);
            stats.add(Stat.booster,
                    StatValues.speedBoosters("{0}" + StatUnit.timesSpeed.localized(),
                            consBase.amount, liquidBoostIntensity, false, consBase::consumes)
            );
        }
    }



    @Override
    public void setBars() {
        super.setBars();
        removeBar("drillspeed");


        addBar("drillspeed", (DrillBuild e) ->
                new Bar(() -> Core.bundle.format("bar.drillspeed", Strings.fixed(e.lastDrillSpeed * 60 * e.timeScale() * outputMultiple *
                                (Achievements.burstDrillStrengthen.working(this)&&compressOre?Mathf.pow(1f/9f,level-2):1)
                        , 2)), () -> Pal.ammo, () -> e.warmup));

    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        drawPotentialLinks(x, y);
        drawOverlay(x * tilesize + offset, y * tilesize + offset, rotation);

        Tile tile = world.tile(x, y);
        if (tile == null) return;

        countOre(tile);

        if (returnItem != null) {
            float width = drawPlaceText(Core.bundle.formatFloat("bar.drillspeed", 60f * outputMultiple / getDrillTime(returnItem) * returnCount *
                            (Achievements.burstDrillStrengthen.working(this)&&compressOre?Mathf.pow(1f/9f,level-2):1)
                    , 2 + (int) (level/3f)), x, y, valid);
            float dx = x * tilesize + offset - width / 2f - 4f, dy = y * tilesize + offset + size * tilesize / 2f + 5, s = iconSmall / 4f;
            Draw.mixcol(Color.darkGray, 1f);
            Item item = returnItem;
            if (Achievements.burstDrillStrengthen.working(this)&&compressOre) {
                item = ECData.get(returnItem, level-2);
            }
            Draw.rect(item.fullIcon, dx, dy - 1, s, s);
            Draw.reset();
            Draw.rect(item.fullIcon, dx, dy, s, s);

            if (drawMineItem) {
                Draw.color(returnItem.color);
                Draw.rect(itemRegion, tile.worldx() + offset, tile.worldy() + offset);
                Draw.color();
            }
        } else {
            Tile to = tile.getLinkedTilesAs(this, tempTiles).find(t -> t.drop() != null && (t.drop().hardness > tier || (blockedItems != null && blockedItems.contains(t.drop()))));
            Item item = to == null ? null : to.drop();
            if (item != null) {
                drawPlaceText(Core.bundle.get("bar.drilltierreq"), x, y, valid);
            }
        }
    }

    public class ECDrillBuild extends BurstDrillBuild {


        @Override
        public void updateTile(){
            if(dominantItem == null){
                return;
            }
            if (Achievements.burstDrillStrengthen.working(this.block)&&compressOre){
                if (level > 2 && items.get(dominantItem) >= Mathf.pow(9,level-2)){
                    items.remove(dominantItem,Mathf.pow(9,level-2));
                    produced(dominantItem,-Mathf.pow(9,level-2));
                    items.add(ECData.get(dominantItem,level-2),1);
                    produced(ECData.get(dominantItem,level-2),1);
                }
                dump(ECData.get(dominantItem,level-2));
            }else {
                dump();
            }



            if(invertTime > 0f) invertTime -= delta() / invertedTime;

            float drillTime = getDrillTime(dominantItem);

            smoothProgress = Mathf.lerpDelta(smoothProgress, progress / (drillTime - 20f), 0.1f);

            if(items.total() <= itemCapacity - dominantItems * outputMultiple && dominantItems > 0 && efficiency > 0){
                warmup = Mathf.approachDelta(warmup, progress / drillTime, 0.01f);

                float speed = Mathf.lerp(1f, liquidBoostIntensity, optionalEfficiency) * efficiency;

                timeDrilled += speedCurve.apply(progress / drillTime) * speed;

                lastDrillSpeed = 1f / drillTime * speed * dominantItems;
                progress += delta() * speed;
            }else{
                warmup = Mathf.approachDelta(warmup, 0f, 0.01f);
                lastDrillSpeed = 0f;
                return;
            }

            if(dominantItems > 0 && progress >= drillTime && items.total() < itemCapacity){

                int amount = (int) (dominantItems * outputMultiple);
                amount = Math.min(getMaximumAccepted(dominantItem) - items.get(dominantItem), amount);

                items.add(dominantItem, amount);
                produced(dominantItem,amount);

                invertTime = 1f;
                progress %= drillTime;

                if(wasVisible){
                    Effect.shake(shake, shake, this);
                    drillSound.at(x, y, 1f + Mathf.range(drillSoundPitchRand), drillSoundVolume);
                    drillEffect.at(x + Mathf.range(drillEffectRnd), y + Mathf.range(drillEffectRnd), dominantItem.color);
                }
            }
        }

        //*/
        @Override
        public boolean dump(Item item) {
            return ECTool.dump(this, item);
        }
        //*/

        @Override
        public void drawSelect() {
            drawItemSelection(
                    (Achievements.burstDrillStrengthen.working(this.block)&&compressOre ? ECData.get(dominantItem, level-2) : dominantItem)
            );
        }
    }
}
