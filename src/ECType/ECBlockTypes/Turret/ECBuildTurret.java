package ECType.ECBlockTypes.Turret;

import ECConfig.Config;
import ECConfig.ECData;
import ECConfig.ECTool;
import arc.Core;
import arc.util.Log;
import mindustry.world.blocks.defense.BuildTurret;
import mindustry.world.blocks.defense.RegenProjector;

public class ECBuildTurret extends BuildTurret {

    public static Config config = new Config().addConfigSimple(null, "buildType","unitType")
            .scaleConfig("range")
            .linearConfig("buildSpeed");
    public BuildTurret root;
    public int level;


    public ECBuildTurret(BuildTurret root, int level) throws IllegalAccessException {
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


    public class ECBuildTurretBuild extends BuildTurretBuild {
    }
}
