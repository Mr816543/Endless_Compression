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
import arc.math.geom.Geometry;
import arc.util.Log;
import arc.util.Strings;
import arc.util.Tmp;
import mindustry.game.Team;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.ui.Bar;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.environment.StaticWall;
import mindustry.world.blocks.production.BeamDrill;
import mindustry.world.blocks.production.Drill;
import mindustry.world.consumers.ConsumeLiquid;
import mindustry.world.consumers.ConsumeLiquidBase;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValues;

import static mindustry.Vars.*;

public class ECBeamDrill extends BeamDrill {

    public static Config config = new Config().addConfigSimple(null, "buildType")
            .scaleConfig("drillEffectChance","range").linearConfig("itemCapacity", "rotateSpeed");
    public BeamDrill root;
    public int level;
    public float outputMultiple;
    public boolean compressOre = false;


    public ECBeamDrill(BeamDrill root, int level) throws IllegalAccessException {
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
    public void setStats(){
        super.setStats();

        stats.remove(Stat.drillTier);
        stats.remove(Stat.drillSpeed);
        stats.remove(Stat.booster);

        if (level> 2 && Achievements.beamDrillStrengthen.working(this)) stats.add(new Stat("compressore"),table -> {
            table.button(compressOre?Core.bundle.get("stat.true"):Core.bundle.get("stat.false"), Styles.flatTogglet,()->{
                compressOre = !compressOre;
                Core.settings.put(name,compressOre);
            }).size(75,30);
            table.setSize(75,30);
        });

        stats.add(Stat.drillTier, StatValues.drillables(drillTime, 0f, size * outputMultiple, drillMultipliers, b ->
                (b instanceof Floor f && f.wallOre && f.itemDrop != null && f.itemDrop.hardness <= tier && (blockedItems == null || !blockedItems.contains(f.itemDrop))) ||
                        (b instanceof StaticWall w && w.itemDrop != null && w.itemDrop.hardness <= tier && (blockedItems == null || !blockedItems.contains(w.itemDrop)))
        ));

        stats.add(Stat.drillSpeed, 60f / drillTime * size * outputMultiple, StatUnit.itemsSecond);

        if(optionalBoostIntensity != 1 && findConsumer(f -> f instanceof ConsumeLiquidBase && f.booster) instanceof ConsumeLiquidBase consBase){
            stats.remove(Stat.booster);
            stats.add(Stat.booster,
                    StatValues.speedBoosters("{0}" + StatUnit.timesSpeed.localized(),
                            consBase.amount, optionalBoostIntensity, false,
                            l -> (consumesLiquid(l) && (findConsumer(f -> f instanceof ConsumeLiquid).booster || ((ConsumeLiquid)findConsumer(f -> f instanceof ConsumeLiquid)).liquid != l)))
            );
        }
    }

    @Override
    public void setBars(){
        super.setBars();
        removeBar("drillspeed");
        addBar("drillspeed", (BeamDrillBuild e) ->
                new Bar(() -> Core.bundle.format("bar.drillspeed", Strings.fixed(e.lastDrillSpeed * 60 * outputMultiple *
                        (Achievements.beamDrillStrengthen.working(this)&&compressOre?Mathf.pow(1f/9f,level-2):1)
                        , 2)), () -> Pal.ammo, () -> e.warmup));
    }


    @Override
    public boolean canReplace(Block other){
        if(other.alwaysReplace) return true;
        if(other.privileged) return false;
        return other.replaceable &&
                (other != this || (rotate && quickRotate)) &&
                (((this.group != BlockGroup.none && other.group == this.group) || other == this)
                        || (other == root) || (other instanceof ECBeamDrill d && d.root == this.root&&d.level<level))
                &&
                (size == other.size || (size >= other.size && ((subclass != null && subclass == other.subclass) || group.anyReplace)));
    }


    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        Item item = null, invalidItem = null;
        boolean multiple = false;
        int count = 0;

        for(int i = 0; i < size; i++){
            nearbySide(x, y, rotation, i, Tmp.p1);

            int j = 0;
            Item found = null;
            for(; j < range; j++){
                int rx = Tmp.p1.x + Geometry.d4x(rotation)*j, ry = Tmp.p1.y + Geometry.d4y(rotation)*j;
                Tile other = world.tile(rx, ry);
                if(other != null && other.solid()){
                    Item drop = other.wallDrop();
                    if(drop != null){
                        if(drop.hardness <= tier && (blockedItems == null || !blockedItems.contains(drop))){
                            found = drop;
                            count++;
                        }else{
                            invalidItem = drop;
                        }
                    }
                    break;
                }
            }

            if(found != null){
                //check if multiple items will be drilled
                if(item != found && item != null){
                    multiple = true;
                }
                item = found;
            }

            int len = Math.min(j, range - 1);
            Drawf.dashLine(found == null ? Pal.remove : Pal.placing,
                    Tmp.p1.x * tilesize,
                    Tmp.p1.y *tilesize,
                    (Tmp.p1.x + Geometry.d4x(rotation)*len) * tilesize,
                    (Tmp.p1.y + Geometry.d4y(rotation)*len) * tilesize
            );
        }

        if(item != null){
            float width = drawPlaceText(Core.bundle.formatFloat("bar.drillspeed", 60f / getDrillTime(item) * count * outputMultiple *
                    (Achievements.beamDrillStrengthen.working(this)&&compressOre?Mathf.pow(1f/9f,level-2):1)
                    , 2) , x, y, valid);
            if(!multiple){

                if (Achievements.beamDrillStrengthen.working(this)&&compressOre){
                    item = ECData.get(item, level-2);
                }

                float dx = x * tilesize + offset - width/2f - 4f, dy = y * tilesize + offset + size * tilesize / 2f + 5, s = iconSmall / 4f;
                Draw.mixcol(Color.darkGray, 1f);
                Draw.rect(item.fullIcon, dx, dy - 1, s, s);
                Draw.reset();
                Draw.rect(item.fullIcon, dx, dy, s, s);
            }
        }else if(invalidItem != null){
            drawPlaceText(Core.bundle.get("bar.drilltierreq"), x, y, false);
        }

    }

    public class ECBeamDrillBuild extends BeamDrillBuild{


        @Override
        public void updateTile(){

            for(Tile tile : facing){
                Item dominantItem = tile == null ? null : tile.wallDrop();

                if (Achievements.beamDrillStrengthen.working(this.block)&&compressOre){

                    int powNum = Mathf.pow(9, level - 2);
                    int have = items.get(dominantItem);
                    Item cItem = ECData.get(dominantItem, level - 2);

                    if (level > 2 && have >= powNum){
                        int num = have / powNum;
                        if (num > 0&& have - ( num * powNum) >= 0){
                            items.remove(dominantItem,num * powNum);
                            produced(dominantItem,num * -powNum);
                            items.add(cItem,num);
                            produced(cItem,num);
                        }
                    }
                    if(timer(timerDump, dumpTime / timeScale)){
                        dump(cItem);
                    }
                }
                else {
                    if(timer(timerDump, dumpTime / timeScale)){
                        dump();
                    }
                }

            }


            if(lasers[0] == null) updateLasers();

            warmup = Mathf.approachDelta(warmup, Mathf.num(efficiency > 0), 1f / 60f);

            updateFacing();

            float multiplier = Mathf.lerp(1f, optionalBoostIntensity, optionalEfficiency);
            float drillTime = getDrillTime(lastItem);
            boostWarmup = Mathf.lerpDelta(boostWarmup, optionalEfficiency, 0.1f);
            lastDrillSpeed = (facingAmount * multiplier * timeScale) / drillTime;

            time += edelta() * multiplier;

            if(time >= drillTime){
                for(Tile tile : facing){
                    Item drop = tile == null ? null : tile.wallDrop();
                    if(items.total() < itemCapacity && drop != null){
                        items.add(drop, (int) (1 * outputMultiple));
                        produced(drop, (int) (1 * outputMultiple));
                    }
                }
                time %= drillTime;
            }

        }


        @Override
        public boolean dump(Item item) {
            return ECTool.dump(this, item);
        }


        @Override
        public void drawSelect() {
            drawItemSelection(
                    (Achievements.beamDrillStrengthen.working(this.block)&&compressOre ? ECData.get(lastItem, level-2) : lastItem)
            );
        }


    }




}
