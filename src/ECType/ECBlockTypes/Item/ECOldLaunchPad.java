package ECType.ECBlockTypes.Item;

import ECConfig.Config;
import ECConfig.ECData;
import ECConfig.ECSetting;
import ECConfig.ECTool;
import arc.Core;
import arc.math.Mathf;
import mindustry.world.Block;
import mindustry.world.blocks.power.NuclearReactor;

public class ECOldLaunchPad extends OldLaunchPad{

    public OldLaunchPad root;

    public int level;

    public static Config config = new Config().addConfigSimple(null, "buildType")
            .scaleConfig("minLaunchCapacity").linearConfig("itemCapacity").addConfigSimple(1/ ECSetting.SCALE_MULTIPLIER,"launchTime");

    public ECOldLaunchPad(OldLaunchPad root,int level,Block iconRoot) throws IllegalAccessException {
        super("c" + level + "-" + root.name);
        this.root = root;
        this.level = level;
        ECTool.compress(root, this, config, level);
        ECTool.loadCompressContentRegion(iconRoot, this);
        ECTool.setIcon(iconRoot, this, level);
        ECTool.loadHealth(this,root,level);
        requirements(root.category, root.buildVisibility, ECTool.compressItemStack(root.requirements,level));

        localizedName = level + Core.bundle.get("num-Compression.localizedName") + root.localizedName;
        description = root.description;
        details = root.details;

        ECData.register(root,this,level);
    }

    @Override
    public void init() {
        consumeBuilder = ECTool.consumeBuilderCopy(root,level);
        super.init();
    }
}
