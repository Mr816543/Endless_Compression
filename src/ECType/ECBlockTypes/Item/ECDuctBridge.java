package ECType.ECBlockTypes.Item;

import ECConfig.Config;
import ECConfig.ECData;
import ECConfig.ECSetting;
import ECConfig.ECTool;
import arc.Core;
import arc.math.Mathf;
import arc.util.Log;
import mindustry.gen.Building;
import mindustry.gen.Teamc;
import mindustry.type.Item;
import mindustry.world.Edges;
import mindustry.world.blocks.distribution.Duct;
import mindustry.world.blocks.distribution.DuctBridge;
import mindustry.world.blocks.sandbox.ItemVoid;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

public class ECDuctBridge extends DuctBridge {

    public static Config config = new Config().addConfigSimple(null, "buildType")
            .scaleConfig("range")
            .linearConfig("itemCapacity");
    public DuctBridge root;
    public int level;
    public float outputMultiple;

    public ECDuctBridge(DuctBridge root, int level) throws IllegalAccessException {
        super("c" + level + "-" + root.name);

        this.root = root;
        this.level = level;
        this.outputMultiple = Mathf.pow(ECSetting.LINEAR_MULTIPLIER,level);
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
    }


    @Override
    public void setStats(){
        super.setStats();
        stats.remove(Stat.itemsMoved);
        stats.add(Stat.itemsMoved, 60f / speed * outputMultiple, StatUnit.itemsSecond);
    }

    public class ECDuctBridgeBuild extends DuctBridgeBuild {

        @Override
        public void updateTile(){
            var link = lastLink = findLink();
            if(link != null){
                link.occupied[rotation % 4] = this;
                if(items.any() && link.items.total() < link.block.itemCapacity){
                    progress += edelta();

                    while(progress > speed){
                        Item next = items.first();
                        if(next != null && link.items.total() < link.block.itemCapacity){
                            int num = (int) Math.min(outputMultiple,items.get(next));
                            num = Math.min(num,link.acceptStack(next,num,this));
                            link.handleStack(next, num,this);
                            this.removeStack(next,num);

                        }
                        progress -= speed;
                    }

                }
            }

            if(link == null && items.any()){
                Item next = items.first();
                if(moveForward(next)){
                }
            }

            for(int i = 0; i < 4; i++){
                if(occupied[i] == null || occupied[i].rotation != i || !occupied[i].isValid() || occupied[i].lastLink != this){
                    occupied[i] = null;
                }
            }
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            if (source==this)return true;
            //only accept if there's an output point.
            if(findLink() == null) return false;

            int rel = this.relativeToEdge(source.tile);
            return items.total() < itemCapacity && rel != rotation && occupied[(rel + 2) % 4] == null;
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
                }else {
                    return false;
                }

            } else {
                return false;
            }
        }


    }
}
