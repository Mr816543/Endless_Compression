package ECType.ECBlockTypes.Defend;

import ECConfig.Config;
import ECConfig.ECData;
import ECConfig.ECTool;
import ECType.ECBlockTypes.Crafter.ECBurstDrill;
import arc.Core;
import mindustry.content.Blocks;
import mindustry.world.Block;
import mindustry.world.blocks.defense.Radar;
import mindustry.world.meta.BlockGroup;

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

    @Override
    public boolean canReplace(Block other){
        if(other.alwaysReplace) return true;
        if(other.privileged) return false;
        return other.replaceable &&
                (other != this || (rotate && quickRotate)) &&
                (((this.group != BlockGroup.none && other.group == this.group) || other == this)
                        || (other == root) || (other instanceof ECRadar d && d.root == this.root&&d.level<level))
                &&
                (size == other.size || (size >= other.size && ((subclass != null && subclass == other.subclass) || group.anyReplace)));
    }

    public class ECRadarBuild extends RadarBuild {

    }
}
