package ECType.ECBlockTypes.Liquid;

import ECConfig.*;
import arc.Core;
import arc.math.Mathf;
import mindustry.world.blocks.liquid.ArmoredConduit;

public class ECArmoredConduit extends ArmoredConduit implements EC {

    public static Config config = new Config().addConfigSimple(null, "buildType")
            .scaleConfig().linearConfig("liquidCapacity", "liquidPressure");
    public ArmoredConduit root;
    public int level;
    public float outputMultiple;

    public ECArmoredConduit(ArmoredConduit root, int level) throws IllegalAccessException {
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
        if (root.junctionReplacement != null) junctionReplacement = ECData.get(root.junctionReplacement, level);
        if (root.bridgeReplacement != null) bridgeReplacement = ECData.get(root.bridgeReplacement, level);
        if (root.rotBridgeReplacement != null) rotBridgeReplacement = ECData.get(root.rotBridgeReplacement, level);
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public Object getRoot() {
        return root;
    }

}
