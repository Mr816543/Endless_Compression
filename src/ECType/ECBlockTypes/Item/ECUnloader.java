package ECType.ECBlockTypes.Item;

import ECConfig.Config;
import ECConfig.ECData;
import ECConfig.ECSetting;
import ECConfig.ECTool;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.io.Reads;
import arc.util.io.Writes;
import arc.util.pooling.Pool;
import arc.util.pooling.Pools;
import mindustry.content.Blocks;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.blocks.storage.StorageBlock;
import mindustry.world.blocks.storage.Unloader;

import java.util.Comparator;

import static mindustry.Vars.content;

public class ECUnloader extends Unloader {

    public Unloader root;

    public int level;

    public static Item[] allItems;

    public static Config config = new Config().addConfigSimple(null, "buildType")
            .scaleConfig().linearConfig().addConfigSimple(1/ ECSetting.LINEAR_MULTIPLIER,"speed");


    public ECUnloader(Unloader root,int level) throws IllegalAccessException {
        super("c" + level + "-" + root.name);
        this.root = root;
        this.level = level;
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
        allItems = content.items().toArray(Item.class);
        super.init();
    }


    public static class ContainerStat implements Pool.Poolable {
        Building building;
        float loadFactor;
        boolean canLoad;
        boolean canUnload;
        /** Cached !(building instanceof StorageBuild) */
        boolean notStorage;
        int lastUsed;

        @Override
        public void reset(){
            building = null;
        }
    }

    public class ECUnloaderBuild extends UnloaderBuild{
        public float unloadTimer = 0f;
        public int rotations = 0;
        public Item sortItem = null;
        public ContainerStat dumpingFrom, dumpingTo;
        public final Seq<ContainerStat> possibleBlocks = new Seq<>(ContainerStat.class);

        protected final Comparator<ContainerStat> comparator = (x, y) -> {
            //sort so it gives priority for blocks that can only either receive or give (not both), and then by load, and then by last use
            //highest = unload from, lowest = unload to
            int unloadPriority = Boolean.compare(x.canUnload && !x.canLoad, y.canUnload && !y.canLoad); //priority to receive if it cannot give
            if(unloadPriority != 0) return unloadPriority;
            int loadPriority = Boolean.compare(x.canUnload || !x.canLoad, y.canUnload || !y.canLoad); //priority to give if it cannot receive
            if(loadPriority != 0) return loadPriority;
            int loadFactor = Float.compare(x.loadFactor, y.loadFactor);
            if(loadFactor != 0) return loadFactor;
            return Integer.compare(y.lastUsed, x.lastUsed); //inverted
        };

        private boolean isPossibleItem(Item item){
            boolean hasProvider = false,
                    hasReceiver = false,
                    isDistinct = false;

            var pbi = possibleBlocks.items;
            for(int i = 0, l = possibleBlocks.size; i < l; i++){
                var pb = pbi[i];
                var other = pb.building;

                //set the stats of buildings in possibleBlocks while we are at it
                pb.canLoad = pb.notStorage && other.acceptItem(this, item);
                pb.canUnload = other.canUnload() && other.items != null && other.items.has(item);

                //thats also handling framerate issues and slow conveyor belts, to avoid skipping items if nulloader
                isDistinct |= (hasProvider && pb.canLoad) || (hasReceiver && pb.canUnload);
                hasProvider |= pb.canUnload;
                hasReceiver |= pb.canLoad;
            }
            return isDistinct;
        }

        @Override
        public void onProximityUpdate(){
            //filter all blocks in the proximity that will never be able to trade items

            super.onProximityUpdate();
            Pools.freeAll(possibleBlocks, true);
            possibleBlocks.clear();

            for(int i = 0; i < proximity.size; i++){
                var other = proximity.get(i);
                if(!other.interactable(team)) continue; //avoid blocks of the wrong team

                //partial check
                boolean canLoad = !(other.block instanceof StorageBlock);
                boolean canUnload = other.canUnload() && other.items != null;

                if(canLoad || canUnload){ //avoid blocks that can neither give nor receive items
                    var pb = Pools.obtain(ContainerStat.class, ContainerStat::new);
                    pb.building = other;
                    pb.notStorage = canLoad;
                    //TODO store the partial canLoad/canUnload?
                    possibleBlocks.add(pb);
                }
            }
        }

        @Override
        public void updateTile(){
            if(((unloadTimer += delta()) < speed) || (possibleBlocks.size < 2)) return;
            Item item = null;
            boolean any = false;

            if(sortItem != null){
                if(isPossibleItem(sortItem)) item = sortItem;
            }else{
                //selects the next item for nulloaders
                //inspired of nextIndex() but for all "proximity" (possibleBlocks) at once, and also way more powerful
                for(int i = 0, l = allItems.length; i < l; i++){
                    int id = (rotations + i + 1) % l;
                    var possibleItem = allItems[id];

                    if(isPossibleItem(possibleItem)){
                        item = possibleItem;
                        break;
                    }
                }
            }

            if(item != null){
                rotations = item.id; //next rotation for nulloaders //TODO maybe if(sortItem == null)
                var pbi = possibleBlocks.items;
                int pbs = possibleBlocks.size;

                for(int i = 0; i < pbs; i++){
                    var pb = pbi[i];
                    var other = pb.building;
                    int maxAccepted = other.getMaximumAccepted(item);
                    pb.loadFactor = maxAccepted == 0 || other.items == null ? 0 : other.items.get(item) / (float)maxAccepted;
                    pb.lastUsed = (pb.lastUsed + 1) % Integer.MAX_VALUE; //increment the priority if not used
                }

                possibleBlocks.sort(comparator);

                dumpingTo = null;
                dumpingFrom = null;

                //choose the building to accept the item
                for(int i = 0; i < pbs; i++){
                    if(pbi[i].canLoad){
                        dumpingTo = pbi[i];
                        break;
                    }
                }

                //choose the building to take the item from
                for(int i = pbs - 1; i >= 0; i--){
                    if(pbi[i].canUnload){
                        dumpingFrom = pbi[i];
                        break;
                    }
                }

                //trade the items
                if(dumpingFrom != null && dumpingTo != null && (dumpingFrom.loadFactor != dumpingTo.loadFactor || !dumpingFrom.canLoad)){

                    int num = (int) (unloadTimer / speed);

                    if (num==1){
                        dumpingTo.building.handleItem(this, item);
                        dumpingFrom.building.removeStack(item, 1);
                    }else {
                        int max = dumpingTo.building.acceptStack(item,num,this);
                        ECTool.print(max);
                        if (dumpingTo.building.items==null||max == 0){
                            for (int i = 0 ; i < num && i < 81;i++){
                                if (!dumpingTo.building.acceptItem(this,item) || dumpingFrom.building.items.get(item)<=0) break;
                                dumpingTo.building.handleItem(this , item);
                                dumpingFrom.building.removeStack(item,1);
                            }
                        }else {

                            max = Math.min(max,dumpingFrom.building.items.get(item));
                            if (max<0) max = 0;
                            dumpingTo.building.handleStack(item,max,this);
                            dumpingFrom.building.removeStack(item,max);
                        }
                    }

                    dumpingTo.lastUsed = 0;
                    dumpingFrom.lastUsed = 0;
                    any = true;
                }
            }

            if(any){
                unloadTimer %= speed;
            }else{
                unloadTimer = Math.min(unloadTimer, speed);
            }
        }

        @Override
        public void draw(){
            super.draw();

            Draw.color(sortItem == null ? Color.clear : sortItem.color);
            Draw.rect(centerRegion, x, y);
            Draw.color();
        }

        @Override
        public void drawSelect(){
            super.drawSelect();
            drawItemSelection(sortItem);
        }

        @Override
        public void buildConfiguration(Table table){
            ItemSelection.buildTable(ECUnloader.this, table, content.items(), () -> sortItem, this::configure, selectionRows, selectionColumns);
        }

        @Override
        public Item config(){
            return sortItem;
        }

        @Override
        public byte version(){
            return 1;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.s(sortItem == null ? -1 : sortItem.id);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            int id = revision == 1 ? read.s() : read.b();
            sortItem = id == -1 ? null : content.item(id);
        }
    }




}
