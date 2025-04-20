package ECType;

import ECConfig.Config;
import ECConfig.ECTool;
import arc.Core;
import arc.util.Log;
import mindustry.world.blocks.production.Drill;

public class errECDrill extends Drill {
    public static Config config = new Config().addConfigSimple(null, "buildType")
            //.scaleConfig("drillEffectChance", "rotateSpeed").linearConfig("itemCapacity")
            ;
    public Drill root;
    public int level;

    public errECDrill(Drill root, int level) throws IllegalAccessException {
        super("c" + level + "-" + root.name);
        this.root = root;
        this.level = level;
        ECTool.compress(root, this, config, level);
        ECTool.loadCompressContentRegion(root, this);
        ECTool.setIcon(root, this, level);
        localizedName = level + Core.bundle.get("num-Compression.localizedName") + root.localizedName;
        description = root.description;
        details = root.details;
    }


    public class ECDrillBuild extends DrillBuild {


        @Override
        public void updateTile() {

            //Log.info(lastDrillSpeed +" * 60 * " + timeScale() + " = " + lastDrillSpeed * 60 * timeScale());
            Log.info(((Drill)block).drillTime);
            super.updateTile();
            /*/



            if (timer(timerDump, dumpTime)&&dominantItem != null && items.has(dominantItem)) {
                dump(dominantItem);
            }

            if (dominantItem == null) {
                return;
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
                int amount = (int) (progress / delay);
                amount = Math.min(getMaximumAccepted(dominantItem) - items.get(dominantItem), amount);

               // amount *= (int) Math.pow(ECSetting.SCALE_MULTIPLIER,level);

                items.add(dominantItem,amount);
                progress %= delay;

                if (wasVisible && Mathf.chanceDelta(drillEffectChance * warmup))
                    drillEffect.at(x + Mathf.range(drillEffectRnd), y + Mathf.range(drillEffectRnd), dominantItem.color);
            }
            //*/
        }

        /*/
        public boolean dump(Item todump) {
            if (block.hasItems && items.total() != 0 && proximity.size != 0 && (todump == null || items.has(todump))) {
                int dump = cdump;
                Seq<Item> allItems = Vars.content.items();
                int itemSize = allItems.size;
                Object[] itemArray = allItems.items;
                int i;
                Building other;
                if (todump == null) {
                    for(i = 0; i < proximity.size; ++i) {
                        other = (Building)proximity.get((i + dump) % proximity.size);

                        for(int ii = 0; ii < itemSize; ++ii) {
                            if (items.has(ii)) {
                                Item item = (Item)itemArray[ii];
                                if (other.acceptItem(this, item) && canDump(other, item)) {

                                    dump(other,todump);


                                    incrementDump(proximity.size);
                                    return true;
                                }
                            }
                        }

                        incrementDump(proximity.size);
                    }
                } else {
                    for(i = 0; i < proximity.size; ++i) {
                        other = (Building)proximity.get((i + dump) % proximity.size);
                        if (other.acceptItem(this, todump) && canDump(other, todump)) {

                            dump(other,todump);

                            incrementDump(proximity.size);
                            return true;
                        }

                        incrementDump(proximity.size);
                    }
                }

                return false;
            } else {
                return false;
            }
        }

        public void dump(Building other,Item item){
            if (other instanceof ItemVoid.ItemVoidBuild){
                other.flowItems().add(item,items.get(item));
                items.set(item,0);
                return;
            }else if (other instanceof Conveyor.ConveyorBuild b){
                for (int i = 0 ; i < items.get(item); i++ ){
                    if (!b.acceptItem(this,item)) return;
                    b.handleItem(this,item);
                    items.remove(item,1);
                }
                return;
            }
            int amount = Math.min(other.getMaximumAccepted(item) - other.items.get(item),items.get(item));
            amount = Math.max(0,amount);
            items.remove(item,amount);
            other.items.add(item,amount);
        }
//*/
    }
}
