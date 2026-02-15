package ECType.ECBlockTypes.Defend;

import ECConfig.Config;
import ECConfig.EC;
import ECConfig.ECData;
import ECConfig.ECTool;
import arc.Core;
import mindustry.world.blocks.defense.OverdriveProjector;

public class ECOverdriveProjector extends OverdriveProjector implements EC {
    public static Config config = new Config().addConfigSimple(null, "buildType")
            .scaleConfig("range", "phaseRangeBoost").linearConfig("useTime");
    public OverdriveProjector root;
    public int level;

    public ECOverdriveProjector(OverdriveProjector root, int level) throws IllegalAccessException {
        super("c" + level + "-" + root.name);
        this.root = root;
        this.level = level;
        ECTool.compress(root, this, config, level);
        ECTool.loadCompressContentRegion(root, this);
        ECTool.setIcon(root, this, level);
        ECTool.loadHealth(this, root, level);
        requirements(root.category, root.buildVisibility, ECTool.compressItemStack(root.requirements, level));

        speedBoost = root.speedBoost + level * (root.speedBoost - 1);

        localizedName = level + Core.bundle.get("num-Compression.localizedName") + root.localizedName;
        description = root.description;
        details = root.details;

        ECData.register(root, this, level);
    }


    @Override
    public void init() {
        consumeBuilder = ECTool.consumeBuilderCopy(root, level);
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


}
