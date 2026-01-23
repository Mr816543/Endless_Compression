package ECType.ECBlockTypes.Defend;

import ECConfig.Config;
import ECConfig.ECData;
import ECConfig.ECTool;
import arc.Core;
import mindustry.content.Blocks;
import mindustry.world.blocks.defense.Radar;

public class ECRadar extends Radar {

    public static Config config = new Config().addConfigSimple(null, "buildType")
            .scaleConfig("fogRadius").linearConfig();
    public Radar root;
    public int level;

    public ECRadar(Radar root, int level) throws IllegalAccessException {
        super("c" + level + "-" + root.name);
        this.root = root;
        this.level = level;
        ECTool.compress(root, this, config, level);
        ECTool.loadCompressContentRegion(root, this);
        ECTool.setIcon(root, this, level);
        ECTool.loadHealth(this, root, level);
        requirements(root.category, ECTool.compressItemStack(root.requirements, level));


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

    public class ECRadarBuild extends RadarBuild {

    }
}
