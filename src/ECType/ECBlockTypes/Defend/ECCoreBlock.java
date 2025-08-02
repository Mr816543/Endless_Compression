package ECType.ECBlockTypes.Defend;

import ECConfig.Config;
import ECConfig.ECData;
import ECConfig.ECSetting;
import ECConfig.ECTool;
import arc.Core;
import arc.math.Mathf;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.world.Tile;
import mindustry.world.blocks.defense.ForceProjector;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.blocks.storage.StorageBlock;
import mindustry.world.consumers.ConsumeCoolant;

import static mindustry.Vars.*;

public class ECCoreBlock extends CoreBlock {

    public static Config config = new Config().addConfigSimple(null, "buildType","unitType")
            .scaleConfig("unitCapModifier").linearConfig("itemCapacity","armor");
    public CoreBlock root;
    public int level;
    public ECCoreBlock(CoreBlock root,int level) throws IllegalAccessException {
        super("c" + level + "-" + root.name);
        this.root = root;
        this.level = level;
        ECTool.compress(root, this, config, level);
        ECTool.loadCompressContentRegion(root, this);
        ECTool.setIcon(root, this, level);
        ECTool.loadHealth(this, root, level);
        requirements(root.category, ECTool.compressItemStack(root.requirements, level));

        unitType = ECData.get(root.unitType,level);

        localizedName = level + Core.bundle.get("num-Compression.localizedName") + root.localizedName;
        description = root.description;
        details = root.details;
        isFirstTier = false;

        ECData.register(root, this, level);
    }


    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation){
        if(tile == null) return false;
        //in the editor, you can place them anywhere for convenience
        if(state.isEditor()) return true;

        CoreBuild core = team.core();

        //special floor upon which cores can be placed
        tile.getLinkedTilesAs(this, tempTiles);
        if(!tempTiles.contains(o -> !o.floor().allowCorePlacement || o.block() instanceof CoreBlock)){
            return true;
        }

        //must have all requirements
        if(core == null || (!state.rules.infiniteResources && !core.items.has(requirements, state.rules.buildCostMultiplier))) return false;

        return tile.block() instanceof CoreBlock && (
                size > tile.block().size ||
                        (size == tile.block().size && tile.block() instanceof ECCoreBlock t && level > t.level )
                        ||(tile.block() == root)
        ) && (!requiresCoreZone || tempTiles.allMatch(o -> o.floor().allowCorePlacement));
    }

    public class ECCoreBlockBuild extends CoreBuild{


        @Override
        public void onProximityUpdate(){
            this.noSleep();

            long storageCapacity;

            for(Building other : state.teams.cores(team)){
                if(other.tile != tile){
                    this.items = other.items;
                }
            }
            state.teams.registerCore(this);

            storageCapacity = itemCapacity;

            for (Building build : proximity){
                if (owns(build)){
                    storageCapacity+=build.block.itemCapacity;
                }
            }


            proximity.each(this::owns, t -> {
                t.items = items;
                ((StorageBuild)t).linkedCore = this;
            });

            for(Building other : state.teams.cores(team)){
                if(other.tile == tile) continue;
                storageCapacity += other.block.itemCapacity;


                for (Building build : other.proximity){
                    if (owns(build)){
                        storageCapacity+=build.block.itemCapacity;
                    }
                }
            }

            this.storageCapacity = toInt(storageCapacity);

            if(!world.isGenerating()){
                for(Item item : content.items()){
                    items.set(item, Math.min(items.get(item), this.storageCapacity));
                }
            }

            for(CoreBuild other : state.teams.cores(team)){
                other.storageCapacity = this.storageCapacity;
            }
        }

        public int toInt(long l){
            if (l>=Integer.MAX_VALUE) return Integer.MAX_VALUE;
            return (int) l;
        }


        //改了onProximityUpdate就不需要改这个了
        /*/
        @Override
        public boolean owns(Building core, Building tile){

            if ( Integer.MAX_VALUE - storageCapacity < tile.block.itemCapacity) return false;

            return tile instanceof StorageBuild b && ((StorageBlock)b.block).coreMerge && (b.linkedCore == core || b.linkedCore == null);
        }
        //*/
    }
}
