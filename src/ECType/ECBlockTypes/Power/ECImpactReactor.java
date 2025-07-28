package ECType.ECBlockTypes.Power;

import ECConfig.Config;
import ECConfig.ECData;
import ECConfig.ECTool;
import arc.Core;
import mindustry.world.Block;
import mindustry.world.blocks.power.ImpactReactor;
import mindustry.world.blocks.power.NuclearReactor;

public class ECImpactReactor extends ImpactReactor {

    public ImpactReactor root;

    public int level;

    public static Config config = new Config().addConfigSimple(null, "buildType")
            .scaleConfig().linearConfig("powerProduction");


    public ECImpactReactor(ImpactReactor root,int level) throws IllegalAccessException {
        super("c" + level + "-" + root.name);
        this.root = root;
        this.level = level;
        ECTool.compress(root, this,root.getClass(), Block.class, config, level);

        this.size = root.size;


        ECTool.loadCompressContentRegion(root, this);
        ECTool.setIcon(root, this, level);
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
