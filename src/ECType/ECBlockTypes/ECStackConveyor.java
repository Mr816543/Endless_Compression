package ECType.ECBlockTypes;

import ECConfig.Config;
import ECConfig.ECData;
import ECConfig.ECSetting;
import ECConfig.ECTool;
import arc.Core;
import arc.math.Mathf;
import mindustry.world.blocks.distribution.StackConveyor;
import mindustry.world.blocks.production.Drill;

public class ECStackConveyor extends StackConveyor {
    public StackConveyor root;

    public int level;

    public float outputMultiple;

    public static Config config = new Config().addConfigSimple(null, "buildType");


    public ECStackConveyor(StackConveyor root, int level) throws IllegalAccessException {
        super("c" + level + "-" + root.name);
        this.root = root;
        this.level = level;
        this.outputMultiple = Mathf.pow(ECSetting.LINEAR_MULTIPLIER,level);
        ECTool.compress(root, this, config, level);
        ECTool.loadCompressContentRegion(root, this);
        ECTool.setIcon(root, this, level);
        requirements(root.category, root.buildVisibility, ECTool.compressItemStack(root.requirements,level));

        localizedName = level + Core.bundle.get("num-Compression.localizedName") + root.localizedName;
        description = root.description;
        details = root.details;

        float m = (level+1f)/2f;
        speed *= m;
        itemCapacity *= (int) (Mathf.pow(ECSetting.LINEAR_MULTIPLIER,level)/m);



        ECData.register(root,this,level);
    }
}
