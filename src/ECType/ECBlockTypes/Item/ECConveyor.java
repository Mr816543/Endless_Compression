package ECType.ECBlockTypes.Item;

import ECConfig.Config;
import ECConfig.ECData;
import ECConfig.ECTool;
import arc.Core;
import arc.math.Mathf;
import mindustry.content.Blocks;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.world.blocks.distribution.Conveyor;
import mindustry.world.blocks.distribution.DuctBridge;
import mindustry.world.blocks.distribution.ItemBridge;

public class ECConveyor extends Conveyor {

    public static Config config = new Config().addConfigSimple(null, "buildType")
            .linearConfig("speed", "displayedSpeed");
    public Conveyor root;
    public int level;

    public ECConveyor(Conveyor root, int level) throws IllegalAccessException {
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
        super.init();
        junctionReplacement = ECData.get(Blocks.invertedSorter,level);
        bridgeReplacement = ECData.get(Blocks.itemBridge,level);
    }

    public class ECConveyorBuild extends ConveyorBuild {


        private static final float ITEM_SPACE = 0.4f;
        private static final int CAPACITY = 3;

        @Override
        public void updateTile() {
            minitem = 1f;
            mid = 0;

            //skip updates if possible
            if(len == 0 && Mathf.equal(timeScale, 1f)){
                clogHeat = 0f;
                sleep();
                return;
            }

            float nextMax = aligned ? 1f - Math.max(ITEM_SPACE - nextc.minitem, 0) : 1f;
            float moved = speed * edelta();

            for(int i = len - 1; i >= 0; i--){
                float nextpos = (i == len - 1 ? 100f : ys[i + 1]) - ITEM_SPACE;
                float maxmove = Mathf.clamp(nextpos - ys[i], 0, moved);

                ys[i] += maxmove;

                if(ys[i] > 0.5 && i > 0) mid = i - 1;
                xs[i] = Mathf.approach(xs[i], 0, moved*2);

                if (ys[i]>=2f){

                    Building lastBuild = navigate(ys[i], this, ids[i]);
                    if (lastBuild!=null&&lastBuild!=this){
                        lastBuild.handleItem(this,ids[i]);
                        items.remove(ids[i], len - i);
                        len = Math.min(i, len);
                        if (lastBuild instanceof ConveyorBuild lb && rotation == lb.rotation) {
                            lb.xs[lb.lastInserted] = xs[i];
                        }
                    }

                    ys[i] = 0;
                }else {

                    if(ys[i] > nextMax) ys[i] = nextMax;
                }


                if(ys[i] >= 1f && pass(ids[i])){
                    //align X position if passing forwards
                    if(aligned){
                        nextc.xs[nextc.lastInserted] = xs[i];
                    }
                    //remove last item
                    items.remove(ids[i], len - i);
                    len = Math.min(i, len);
                }else if(ys[i] < minitem){
                    minitem = ys[i];
                }
            }

            if(minitem < ITEM_SPACE + (blendbits == 1 ? 0.3f : 0f)){
                clogHeat = Mathf.approachDelta(clogHeat, 1f, 1f / 60f);
            }else{
                clogHeat = 0f;
            }

            noSleep();
        }

        @Override
        public void handleItem(Building source, Item item) {
            super.handleItem(source, item);
        }

        public Building navigate(float length, Building from, Item item) {
            if (length < 0) return null;
            /*/

            if (length<1f) return this;
            if (next == null) return this;
            if (next instanceof ECConveyorNewBuild nextC) return nextC.navigate(length-1f,from,item);
            if (!next.acceptItem(from,item)) return this;
            return next;
            //*/
            Building[] path = new Building[(int) length + 1];
            path[0] = this;
            for (int i = 1; i < path.length; i++) {
                if (path[i - 1] == null) break;
                if (path[i - 1] instanceof ConveyorBuild c) {
                    path[i] = c.next;
                }
            }

            for (int i = path.length - 1; i >= 0; i--) {
                if (path[i] == null) continue;
                if (path[i].acceptItem(from, item)) return path[i];
            }
            return path[0];


        }
    }
}
