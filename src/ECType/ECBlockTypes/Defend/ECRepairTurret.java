package ECType.ECBlockTypes.Defend;

import ECConfig.Config;
import ECConfig.EC;
import ECConfig.ECData;
import ECConfig.ECTool;
import arc.Core;
import mindustry.world.blocks.units.RepairTurret;

public class ECRepairTurret extends RepairTurret implements EC {
    public static Config config = new Config().addConfigSimple(null, "buildType")
            .scaleConfig("repairRadius").linearConfig("repairSpeed", "powerUse");
    public RepairTurret root;
    public int level;

    public ECRepairTurret(RepairTurret root, int level) throws IllegalAccessException {
        super("c" + level + "-" + root.name);
        this.root = root;
        this.level = level;
        ECTool.compress(root, this, config, level);
        ECTool.loadCompressContentRegion(root, this);
        ECTool.setIcon(root, this, level);
        ECTool.loadHealth(this, root, level);
        requirements(root.category, root.buildVisibility, ECTool.compressItemStack(root.requirements, level));

        //phaseRangeBoost = root.phaseRangeBoost * range / root.range;

        localizedName = level + Core.bundle.get("num-Compression.localizedName") + root.localizedName;
        description = root.description;
        details = root.details;

        ECData.register(root, this, level);
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public Object getRoot() {
        return root;
    }


    public class ECRepairTurretBuild extends RepairPointBuild {

    }


}
