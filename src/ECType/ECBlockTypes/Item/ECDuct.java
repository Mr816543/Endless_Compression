package ECType.ECBlockTypes.Item;

import ECConfig.Config;
import ECConfig.ECData;
import ECConfig.ECSetting;
import ECConfig.ECTool;
import arc.Core;
import arc.math.Mathf;
import arc.util.Log;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.Building;
import mindustry.gen.Teamc;
import mindustry.type.Item;
import mindustry.world.Edges;
import mindustry.world.blocks.distribution.BufferedItemBridge;
import mindustry.world.blocks.distribution.Duct;
import mindustry.world.blocks.distribution.ItemBridge;
import mindustry.world.blocks.sandbox.ItemVoid;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

public class ECDuct extends Duct {

    public static Config config = new Config().addConfigSimple(null, "buildType")
            .scaleConfig()
            .linearConfig("itemCapacity");
    public Duct root;
    public int level;

    public ECDuct(Duct root, int level) throws IllegalAccessException {
        super("c" + level + "-" + root.name);

        this.root = root;
        this.level = level;
        ECTool.compress(root, this, config, level);
        ECTool.loadCompressContentRegion(root, this);
        ECTool.setIcon(root, this, level);
        ECTool.loadHealth(this,root,level);
        requirements(root.category, root.buildVisibility, ECTool.compressItemStack(root.requirements, level));

        localizedName = level + Core.bundle.get("num-Compression.localizedName") + root.localizedName;
        description = root.description;
        details = root.details;

        ECData.register(root, this, level);
    }

    @Override
    public void init() {
        consumeBuilder = ECTool.consumeBuilderCopy(root,level);
        super.init();
        if (root.bridgeReplacement != null){
            bridgeReplacement = ECData.get(root.bridgeReplacement,level);
        }
    }


    @Override
    public void setStats(){
        super.setStats();
        stats.remove(Stat.itemsMoved);
        stats.add(Stat.itemsMoved, 60f / speed * Mathf.pow(5,level), StatUnit.itemsSecond);
    }

    public class ECDuctBuild extends DuctBuild{


        @Override
        public void updateTile(){
            progress += edelta() / speed * 2f;

            if(current != null && next != null){
                if(progress >= (1f - 1f/speed) && moveForward(current)){
                    current = null;
                    progress %= (1f - 1f/speed);
                }
            }else{
                progress = 0;
            }

            if(current == null && items.total() > 0){
                current = items.first();
            }
        }

        @Override
        public boolean moveForward(Item item) {
            Building other = front();
            if (other != null && other.team == this.team) {


                if (other instanceof ItemVoid.ItemVoidBuild) {
                    other.flowItems().add(item,items.get(item));
                    items.set(item, 0);
                    return true;
                }

                int move = other.acceptStack(item,items.get(item),this);
                if (!other.acceptItem(this,item)) move = 0;
                if (move>0){
                    other.handleStack(item,move,this);
                    items.remove(item,move);
                    return true;

                }else if(other.acceptItem(this,item)) {
                    other.handleItem(this,item);
                    items.remove(item,1);
                    return true;
                }
                else {
                    return false;
                }

            } else {
                return false;
            }
        }

        @Override
        public int acceptStack(Item item, int amount, Teamc source) {
            return !this.acceptItem(this, item) || !this.block.hasItems || source != null && source.team() != this.team ? 0 : Math.min(this.getMaximumAccepted(item) - this.items.get(item), amount);
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            if (source == this && (current == null || current == item)) return true;
            return ((current == null && items.total() < itemCapacity )||(current == item && items.total() < itemCapacity))&&
                    (armored ?
                            //armored acceptance
                            ((source.block.rotate && source.front() == this && source.block.hasItems && source.block.isDuct) ||
                                    Edges.getFacingEdge(source.tile, tile).relativeTo(tile) == rotation) :
                            //standard acceptance - do not accept from front
                            !(source.block.rotate && next == source) && Edges.getFacingEdge(source.tile, tile) != null && Math.abs(Edges.getFacingEdge(source.tile, tile).relativeTo(tile.x, tile.y) - rotation) != 2
                    );
        }

        @Override
        public int removeStack(Item item, int amount){
            int removed = super.removeStack(item, amount);
            if(item == current) current = null;
            return removed;
        }

        @Override
        public void handleStack(Item item, int amount, Teamc source){
            items.add(item, amount);
            noSleep();
            current = item;
            progress = -1f;
            if (source instanceof Building){
                recDir = relativeToEdge(((Building)source).tile);
            }else {
                recDir = relativeToEdge(tile);
            }
        }

        @Override
        public void handleItem(Building source, Item item){
            current = item;
            progress = -1f;
            recDir = relativeToEdge(source.tile);
            items.add(item, 1);
            noSleep();
        }

    }
}
