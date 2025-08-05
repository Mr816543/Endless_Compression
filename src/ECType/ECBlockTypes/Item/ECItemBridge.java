package ECType.ECBlockTypes.Item;

import ECConfig.Config;
import ECConfig.ECData;
import ECConfig.ECSetting;
import ECConfig.ECTool;
import ECType.ECBlockTypes.Liquid.ECPump;
import arc.Core;
import arc.math.Mathf;
import arc.util.Nullable;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.Building;
import mindustry.gen.Teamc;
import mindustry.type.Item;
import mindustry.world.blocks.distribution.BufferedItemBridge;
import mindustry.world.blocks.distribution.Conveyor;
import mindustry.world.blocks.distribution.ItemBridge;
import mindustry.world.modules.ItemModule;

import static mindustry.Vars.content;
import static mindustry.Vars.world;

public class ECItemBridge extends ItemBridge {

    public static Config config = new Config().addConfigSimple(null, "buildType")
            .scaleConfig("range")
            .linearConfig("itemCapacity").addConfigSimple(1f/ ECSetting.LINEAR_MULTIPLIER,"transportTime");
    public ItemBridge root;
    public int level;

    public ECItemBridge(ItemBridge root, int level) throws IllegalAccessException {
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

    public ECItemBridge(BufferedItemBridge root, int level) throws IllegalAccessException {
        super("c" + level + "-" + root.name);

        this.root = root;
        this.level = level;

        ECTool.compress(root, this,ItemBridge.class, UnlockableContent.class ,config, level);
        ECTool.loadCompressContentRegion(root, this);
        ECTool.setIcon(root, this, level);
        ECTool.loadHealth(this,root,level);
        requirements(root.category, root.buildVisibility, ECTool.compressItemStack(root.requirements, level));

        transportTime = (60f/root.displayedSpeed) * Mathf.pow(1f/ECSetting.LINEAR_MULTIPLIER,level);

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

    public class ECItemBridgeBuild extends ItemBridgeBuild{


        public int takeRotation = 0;
        public @Nullable Item take( ){
            for (int i = 0 ; i < items.length();i++){
                int index = i + takeRotation;
                if(index >= items.length()) index -= items.length();
                if (items.get(index)>0){
                    takeRotation = index + 1;
                    return content.item(index);
                }
            }
            return null;
        }

        @Override
        public void updateTransport(Building other){
            transportCounter += edelta();
            if (transportCounter >= transportTime){
                int amount = (int) (transportCounter / transportTime);
                Item item = take();

                if (item!=null){
                    amount = Math.min(amount,items.get(item));
                    if (amount > 0 && other.acceptStack(item,amount,this)>0){
                        amount = other.acceptStack(item,amount,this);
                        items.remove(item,amount);
                        other.handleStack(item,amount,this);
                        moved = true;
                    }else if (amount > 0 && other.acceptItem(this,item)){
                        for (int i = 0 ; i < amount&&i<10;i++){
                            items.remove(item,1);
                            other.handleItem(this, item);
                        }
                        moved = true;
                    }else {
                        items.handleFlow(item,-amount);
                    }
                }

                transportCounter %= transportTime;
            }
        }

        @Override
        public boolean dump(Item todump) {
            return ECTool.dump(this,todump);
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            return hasItems && team == source.team && items.total() < itemCapacity &&
                    (checkAccept(source, world.tile(link))|| source.block == block);
        }
    }

}
