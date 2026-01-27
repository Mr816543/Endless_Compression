package ECType.ECBlockTypes.Item;

import ECConfig.Config;
import ECConfig.ECData;
import ECConfig.ECSetting;
import ECConfig.ECTool;
import arc.Core;
import arc.math.Mathf;
import arc.util.Log;
import arc.util.Nullable;
import mindustry.gen.Building;
import mindustry.gen.Teamc;
import mindustry.type.Item;
import mindustry.world.Edges;
import mindustry.world.blocks.distribution.Duct;
import mindustry.world.blocks.distribution.OverflowDuct;
import mindustry.world.blocks.sandbox.ItemVoid;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

public class ECOverflowDuct extends OverflowDuct {

    public static Config config = new Config().addConfigSimple(null, "buildType")
            .scaleConfig()
            .linearConfig("itemCapacity");
    public OverflowDuct root;
    public int level;

    public ECOverflowDuct(OverflowDuct root, int level) throws IllegalAccessException {
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
    }


    @Override
    public void setStats(){
        super.setStats();
        stats.remove(Stat.itemsMoved);
        stats.add(Stat.itemsMoved, 60f / speed * Mathf.pow(ECSetting.LINEAR_MULTIPLIER,level), StatUnit.itemsSecond);
    }

    public class ECOverflowDuctBuild extends OverflowDuctBuild {


        @Override
        public void updateTile(){
            progress += edelta() / speed * 2f;

            if(current != null){
                if(progress >= (1f - 1f/speed)) {
                    moveTimer=0;
                    move();
                    progress %= (1f - 1f/speed);
                }
            }else{
                progress = 0;
            }

            if(current == null && items.total() > 0){
                current = items.first();
            }
        }

        int moveTimer = 0;

        private void move() {
            Building target = target();
            if(target != null){

                if (target instanceof ItemVoid.ItemVoidBuild) {
                    target.flowItems().add(current,items.get(current));
                    items.set(current, 0);
                    current = null;
                }
                else {
                    int moves = target.acceptStack(current,items.get(current),this);
                    if (!target.acceptItem(this,current)) moves =0;
                    if (moves > 0){
                        target.handleStack(current,moves,this);
                        items.remove(current,moves);
                        if (items.get(current)==0) {
                            current = null;
                        }
                    }else if(target.acceptItem(this,current)) {
                        target.handleItem(this,current);
                        items.remove(current,1);
                        if (items.get(current)==0) {
                            current = null;
                        }
                    }
                }
                cdump = (byte)(cdump == 0 ? 2 : 0);
            }

            if (current != null && items.get(current)>0 && moveTimer <=4){
                moveTimer += 1 ;
                move();
            }
        }



        @Override
        public boolean acceptItem(Building source, Item item){
            if (source == this && (current == null || current == item)) return true;
            return (current == null||current==item) && items.total() < itemCapacity &&
                    (Edges.getFacingEdge(source.tile, tile).relativeTo(tile) == rotation);
        }


        @Override
        public void handleStack(Item item, int amount, Teamc source){
            items.add(item, amount);
            noSleep();
            current = item;
            progress = -1f;
        }

    }
}
