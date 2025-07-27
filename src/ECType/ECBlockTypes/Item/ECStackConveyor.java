package ECType.ECBlockTypes.Item;

import ECConfig.Config;
import ECConfig.ECData;
import ECConfig.ECSetting;
import ECConfig.ECTool;
import arc.Core;
import arc.math.Mathf;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.world.blocks.distribution.StackConveyor;
import mindustry.world.blocks.sandbox.ItemVoid;

public class ECStackConveyor extends StackConveyor {
    public StackConveyor root;

    public int level;

    public float outputMultiple;

    public static Config config = new Config().addConfigSimple(null, "buildType").linearConfig();


    public ECStackConveyor(StackConveyor root, int level) throws IllegalAccessException {
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

        float m = (level+1f)/2f;
        speed *= m;
        itemCapacity *= (int) (Mathf.pow(ECSetting.LINEAR_MULTIPLIER,level)/m);



        ECData.register(root,this,level);
    }

    @Override
    public void init() {
        consumeBuilder = ECTool.consumeBuilderCopy(root,level);
        super.init();
    }

    public class ECStackConveyorBuild extends StackConveyorBuild{

        @Override
        public void updateTile() {
            //the item still needs to be "reeled" in when disabled
            float eff = enabled ? (efficiency + baseEfficiency) : 1f;

            //reel in crater
            if(cooldown > 0f) cooldown = Mathf.clamp(cooldown - speed * eff * delta(), 0f, recharge);

            //indicates empty state
            if(link == -1) return;

            //crater needs to be centered
            if(cooldown > 0f) return;

            //get current item
            if(lastItem == null || !items.has(lastItem)){
                lastItem = items.first();
            }

            //do not continue if disabled, will still allow one to be reeled in to prevent visual stacking
            if(!enabled) return;

            if(state == stateUnload){ //unload
                if (lastItem != null){
                    if(!outputRouter){
                        moveForward(lastItem);
                    }else {
                        dump(lastItem);
                    }

                    if(!items.has(lastItem)){
                        poofOut();
                        lastItem = null;
                    }
                }
            }else{ //transfer
                if(state != stateLoad || (items.total() >= getMaximumAccepted(lastItem))){
                    if(front() instanceof StackConveyorBuild e && e.team == team){
                        //sleep if its occupied
                        if(e.link == -1){
                            e.items.add(items);
                            e.lastItem = lastItem;
                            e.link = tile.pos();
                            //▲ to | from ▼
                            link = -1;
                            items.clear();

                            cooldown = recharge;
                            e.cooldown = 1;
                        }
                    }
                }
            }
        }

        @Override
        public boolean moveForward(Item item) {
            Building other = this.front();
            if (other != null && other.team == this.team && other.acceptItem(this, item)) {


                if (other instanceof ItemVoid.ItemVoidBuild) {
                    other.flowItems().add(item,items.get(item));
                    items.set(item, 0);
                    return true;
                }


                int max = Math.min(other.acceptStack(item,items.get(item),this),items.get(item));
                other.handleStack(item,max,null);
                items.remove(lastItem, max);
                return true;
            } else {
                return false;
            }
        }

        @Override
        public boolean dump(Item todump) {
            return ECTool.dump(this,todump);
        }
    }
}
