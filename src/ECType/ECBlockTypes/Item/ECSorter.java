package ECType.ECBlockTypes.Item;

import ECConfig.ECData;
import ECConfig.ECTool;
import ECType.ECBlockTypes.Crafter.ECMultiCrafter;
import arc.Core;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import mindustry.content.Blocks;
import mindustry.gen.Teamc;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.world.Block;
import mindustry.world.blocks.distribution.Sorter;
import mindustry.world.blocks.heat.HeatProducer;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.production.HeatCrafter;
import mindustry.world.consumers.*;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawLiquidOutputs;
import mindustry.world.draw.DrawMulti;

public class ECSorter extends Sorter {

    public Sorter root;

    public ECSorter(Sorter root) throws IllegalAccessException {
        super("compression-"+root.name);

        this.root = root;
        this.cross = root.cross;
        this.invert = root.invert;

        requirements(root.category, root.buildVisibility, ECTool.compressItemStack(root.requirements,1));
        localizedName = Core.bundle.get("Compression.localizedName") + root.localizedName;
        description = root.description;
        details = root.details;

        size = root.size;

        ECTool.loadCompressContentRegion(root,this);

        ECTool.setIcon(root,this,0);


        ECTool.loadHealth(this,root,1);

        ECData.register(root,this,1);
    }

    @Override
    public void init() {
        super.init();
    }

    public class ECSorterBuild extends SorterBuild{

        @Override
        public int acceptStack(Item item, int amount, Teamc source) {
            if (getTileTarget(item,this,false) == null) return 0;
            return getTileTarget(item, this, false).acceptStack(item,amount,source);
        }

        @Override
        public void handleStack(Item item, int amount, Teamc source) {
            getTileTarget(item,this,true).handleStack(item,amount,source);
        }
    }

}
