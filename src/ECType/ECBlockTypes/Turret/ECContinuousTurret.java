package ECType.ECBlockTypes.Turret;

import ECConfig.Config;
import ECConfig.ECData;
import ECConfig.ECSetting;
import ECConfig.ECTool;
import arc.Core;
import arc.math.Mathf;
import arc.struct.ObjectMap;
import mindustry.entities.bullet.PointLaserBulletType;
import mindustry.type.Liquid;
import mindustry.world.blocks.defense.turrets.ContinuousLiquidTurret;
import mindustry.world.blocks.defense.turrets.ContinuousTurret;

public class ECContinuousTurret extends ContinuousTurret {

    public static Config config = new Config().addConfigSimple(null, "buildType","shootType")
            .scaleConfig("range")
            .linearConfig();
    public ContinuousTurret root;
    public int level;


    public ECContinuousTurret(ContinuousTurret root, int level) throws IllegalAccessException {
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
        try {
            shootType = ECTool.compressBulletType(root.shootType,level);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        consumeBuilder = ECTool.consumeBuilderCopy(root, level,true);
        super.init();
    }


    public class ECContinuousTurretBuild extends ContinuousTurretBuild {

        @Override
        public void updateTile() {
            super.updateTile();
        }
    }
}
