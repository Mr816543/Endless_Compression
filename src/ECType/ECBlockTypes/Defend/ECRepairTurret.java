package ECType.ECBlockTypes.Defend;

import ECConfig.Config;
import ECConfig.ECData;
import ECConfig.ECTool;
import arc.Core;
import mindustry.world.blocks.defense.MendProjector;
import mindustry.world.blocks.units.RepairTurret;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatCat;
import mindustry.world.meta.StatUnit;

import static mindustry.Vars.tilesize;

public class ECRepairTurret extends RepairTurret {
    public RepairTurret root;

    public int level;

    public static Config config = new Config().addConfigSimple(null, "buildType")
            .scaleConfig("repairRadius").linearConfig("repairSpeed","powerUse");

    public ECRepairTurret(RepairTurret root,int level) throws IllegalAccessException {
        super("c" + level + "-" + root.name);
        this.root = root;
        this.level = level;
        ECTool.compress(root, this, config, level);
        ECTool.loadCompressContentRegion(root, this);
        ECTool.setIcon(root, this, level);
        ECTool.loadHealth(this,root,level);
        requirements(root.category, root.buildVisibility, ECTool.compressItemStack(root.requirements,level));

        //phaseRangeBoost = root.phaseRangeBoost * range / root.range;

        localizedName = level + Core.bundle.get("num-Compression.localizedName") + root.localizedName;
        description = root.description;
        details = root.details;

        ECData.register(root,this,level);
    }

    public class ECRepairTurretBuild extends RepairPointBuild{

    }


}
