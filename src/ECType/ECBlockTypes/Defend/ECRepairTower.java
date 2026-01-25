package ECType.ECBlockTypes.Defend;

import ECConfig.Config;
import ECConfig.ECData;
import ECConfig.ECTool;
import arc.Core;
import mindustry.world.blocks.defense.RegenProjector;
import mindustry.world.blocks.units.RepairTower;

public class ECRepairTower extends RepairTower {

    public static Config config = new Config().addConfigSimple(null, "buildType")
            .scaleConfig("range")
            .linearConfig("healAmount");
    public RepairTower root;
    public int level;


    public ECRepairTower(RepairTower root, int level) throws IllegalAccessException {
        super("c" + level + "-" + root.name);
        this.root = root;
        this.level = level;
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
        consumeBuilder = ECTool.consumeBuilderCopy(root, level);
        super.init();
    }


    public class ECRepairTowerBuild extends RepairTowerBuild {
    }
}
