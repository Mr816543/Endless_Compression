package ECType.ECBlockTypes.Liquid;

import ECConfig.*;
import arc.Core;
import arc.math.Mathf;
import mindustry.world.blocks.liquid.LiquidRouter;

public class ECLiquidRouter extends LiquidRouter implements EC {

    public static Config config = new Config().addConfigSimple(null, "buildType")
            .scaleConfig().linearConfig("liquidCapacity");
    public LiquidRouter root;
    public int level;
    public float outputMultiple;


    public ECLiquidRouter(LiquidRouter root, int level) throws IllegalAccessException {
        super("c" + level + "-" + root.name);
        this.root = root;
        this.level = level;
        this.outputMultiple = Mathf.pow(ECSetting.LINEAR_MULTIPLIER, level);
        ECTool.compress(root, this, config, level);
        ECTool.loadCompressContentRegion(root, this);
        ECTool.setIcon(root, this, level);
        ECTool.loadHealth(this, root, level);
        requirements(root.category, root.buildVisibility, ECTool.compressItemStack(root.requirements, level));

        localizedName = level + Core.bundle.get("num-Compression.localizedName") + root.localizedName;
        description = root.description;
        details = root.details;

        ECData.register(root, this, level);
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public Object getRoot() {
        return root;
    }


    public class ECLiquidRouterBuild extends LiquidRouterBuild {

        @Override
        public void updateTile() {
            super.updateTile();
            ECTool.dumpLiquid(liquids.current(), 2F, -1, this);
        }
    }
}
