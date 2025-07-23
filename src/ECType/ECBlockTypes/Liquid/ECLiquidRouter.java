package ECType.ECBlockTypes.Liquid;

import ECConfig.Config;
import ECConfig.ECData;
import ECConfig.ECSetting;
import ECConfig.ECTool;
import arc.Core;
import arc.math.Mathf;
import mindustry.world.blocks.liquid.LiquidRouter;

public class ECLiquidRouter extends LiquidRouter{

    public LiquidRouter root;

    public int level;

    public float outputMultiple;

    public static Config config = new Config().addConfigSimple(null, "buildType")
            .scaleConfig().linearConfig("liquidCapacity");


    public ECLiquidRouter(LiquidRouter root, int level) throws IllegalAccessException {
        super("c" + level + "-" + root.name);
        this.root = root;
        this.level = level;
        this.outputMultiple = Mathf.pow(ECSetting.LINEAR_MULTIPLIER,level);
        ECTool.compress(root, this, config, level);
        ECTool.loadCompressContentRegion(root, this);
        ECTool.setIcon(root, this, level);
        requirements(root.category, root.buildVisibility, ECTool.compressItemStack(root.requirements,level));

        localizedName = level + Core.bundle.get("num-Compression.localizedName") + root.localizedName;
        description = root.description;
        details = root.details;

        ECData.register(root,this,level);
    }

    @Override
    public void init() {
        health = root.health * Mathf.pow(5,level);
        super.init();
    }

    public class ECLiquidRouterBuild extends LiquidRouterBuild{

        @Override
        public void updateTile() {
            ECTool.dumpLiquids(liquids.current(),2F / outputMultiple,-1,this);
        }
    }
}
