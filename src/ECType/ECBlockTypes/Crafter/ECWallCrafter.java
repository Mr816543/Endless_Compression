package ECType.ECBlockTypes.Crafter;

import ECConfig.Config;
import ECConfig.ECData;
import ECConfig.ECSetting;
import ECConfig.ECTool;
import ECContents.Achievements;
import arc.Core;
import arc.func.Cons;
import arc.func.Intc2;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.util.Nullable;
import arc.util.Strings;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.ui.Bar;
import mindustry.ui.Styles;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.production.Drill;
import mindustry.world.blocks.production.WallCrafter;
import mindustry.world.consumers.ConsumeLiquidBase;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValues;

import static mindustry.Vars.*;

public class ECWallCrafter extends WallCrafter {

    public static Config config = new Config().addConfigSimple(null, "buildType")
            .scaleConfig("drillEffectChance").linearConfig("itemCapacity", "rotateSpeed");
    public WallCrafter root;
    public int level;
    public float outputMultiple;
    public boolean compressOre = false;


    public ECWallCrafter(WallCrafter root, int level) throws IllegalAccessException {
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
        stats.remove(Stat.output);
        stats.remove(Stat.tiles);
        stats.remove(Stat.drillSpeed);


        if (level> 2 && Achievements.wallCrafterStrengthen.working(this)) stats.add(new Stat("compressore"),table -> {
            table.button(compressOre?Core.bundle.get("stat.true"):Core.bundle.get("stat.false"), Styles.flatTogglet,()->{
                compressOre = !compressOre;
                Core.settings.put(name,compressOre);
            }).size(75,30);
            table.setSize(75,30);
        });


        stats.add(Stat.output,
                (Achievements.drillStrengthen.working(this)&&compressOre?
                        ECData.get(output,level-2) :output));
        stats.add(Stat.tiles, StatValues.blocks(attribute, floating, 1f, true, false));
        stats.add(Stat.drillSpeed, 60f / drillTime * size * outputMultiple *
                (Achievements.drillStrengthen.working(this)&&compressOre?Mathf.pow(1f/9f,level-2):1)
                , StatUnit.itemsSecond);
    }


    @Override
    public void setBars(){
        super.setBars();
        removeBar("drillSpeed");

        addBar("drillspeed", (WallCrafterBuild e) ->
                new Bar(() -> Core.bundle.format("bar.drillspeed", Strings.fixed(e.lastEfficiency * 60 / drillTime * outputMultiple *
                                (Achievements.drillStrengthen.working(this)&&compressOre?Mathf.pow(1f/9f,level-2):1)
                        , 2)), () -> Pal.ammo, () -> e.warmup));
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        float eff = getEfficiency(x, y, rotation, null, null);

        drawPlaceText(Core.bundle.formatFloat("bar.drillspeed", 60f / drillTime * eff * outputMultiple *
                        (Achievements.drillStrengthen.working(this)&&compressOre?Mathf.pow(1f/9f,level-2):1)
                , 2), x, y, valid);
    }

    float getEfficiency(int tx, int ty, int rotation, @Nullable Cons<Tile> ctile, @Nullable Intc2 cpos){
        float eff = 0f;
        int cornerX = tx - (size-1)/2, cornerY = ty - (size-1)/2, s = size;

        for(int i = 0; i < size; i++){
            int rx = 0, ry = 0;

            switch(rotation){
                case 0 -> {
                    rx = cornerX + s;
                    ry = cornerY + i;
                }
                case 1 -> {
                    rx = cornerX + i;
                    ry = cornerY + s;
                }
                case 2 -> {
                    rx = cornerX - 1;
                    ry = cornerY + i;
                }
                case 3 -> {
                    rx = cornerX + i;
                    ry = cornerY - 1;
                }
            }

            if(cpos != null){
                cpos.get(rx, ry);
            }

            Tile other = world.tile(rx, ry);
            if(other != null && other.solid()){
                float at = other.block().attributes.get(attribute);
                eff += at;
                if(at > 0 && ctile != null){
                    ctile.get(other);
                }
            }
        }
        return eff;
    }

    public class ECWallCrafterBuild extends WallCrafterBuild {

        @Override
        public void updateTile(){



            if (output == null) {
                return;
            }
            if (Achievements.wallCrafterStrengthen.working(this.block)&&compressOre){
                if (level > 2 && items.get(output) >= Mathf.pow(9,level-2)){
                    items.remove(output,Mathf.pow(9,level-2));
                    produced(output,-Mathf.pow(9,level-2));
                    items.add(ECData.get(output,level-2),1);
                    produced(ECData.get(output,level-2),1);
                }
                dump(ECData.get(output,level-2));
            }else {
                dump();
            }

            boolean cons = shouldConsume();
            boolean itemValid = itemConsumer != null && itemConsumer.efficiency(this) > 0;

            warmup = Mathf.approachDelta(warmup, Mathf.num(efficiency > 0), 1f / 40f);
            float dx = Geometry.d4x(rotation) * 0.5f, dy = Geometry.d4y(rotation) * 0.5f;

            float eff = getEfficiency(tile.x, tile.y, rotation, dest -> {
                //TODO make not chance based?
                if(wasVisible && cons && Mathf.chanceDelta(updateEffectChance * warmup)){
                    updateEffect.at(
                            dest.worldx() + Mathf.range(3f) - dx * tilesize,
                            dest.worldy() + Mathf.range(3f) - dy * tilesize,
                            dest.block().mapColor
                    );
                }
            }, null) * Mathf.lerp(1f, liquidBoostIntensity, hasLiquidBooster ? optionalEfficiency : 0f) * (itemValid ? itemBoostIntensity : 1f);

            if(itemValid && eff * efficiency > 0 && timer(timerUse, boostItemUseTime)){
                consume();
            }

            lastEfficiency = eff * timeScale * efficiency;

            if(cons && (time += edelta() * eff) >= drillTime){
                int num =  Math.min(itemCapacity - items.get(output) ,(int) (1*outputMultiple));
                items.add(output,num);
                produced(output,num);
                time %= drillTime;
            }

            totalTime += edelta() * warmup * (eff <= 0f ? 0f : 1f);


        }

    }
}
