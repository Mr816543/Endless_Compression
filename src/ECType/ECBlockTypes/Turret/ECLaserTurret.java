package ECType.ECBlockTypes.Turret;

import ECConfig.Config;
import ECConfig.ECData;
import ECConfig.ECTool;
import arc.Core;
import mindustry.world.blocks.defense.RegenProjector;
import mindustry.world.blocks.defense.turrets.LaserTurret;

public class ECLaserTurret extends LaserTurret {

    public static Config config = new Config().addConfigSimple(null, "buildType")
            .scaleConfig("range")
            .linearConfig();
    public LaserTurret root;
    public int level;


    public ECLaserTurret(LaserTurret root, int level) throws IllegalAccessException {
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
        consumeBuilder = ECTool.consumeBuilderCopy(root, level,true);
        try {
            shootType = ECTool.compressBulletType(root.shootType,level);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        super.init();
    }


    public class ECLaserTurretBuild extends LaserTurretBuild {

    }
}
