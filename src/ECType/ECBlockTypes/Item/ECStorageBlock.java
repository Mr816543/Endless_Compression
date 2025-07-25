package ECType.ECBlockTypes.Item;

import ECConfig.Config;
import ECConfig.ECData;
import ECConfig.ECTool;
import arc.Core;
import arc.math.Mathf;
import mindustry.world.blocks.storage.StorageBlock;

public class ECStorageBlock extends StorageBlock {

    public StorageBlock root;

    public int level;

    public static Config config = new Config().addConfigSimple(null, "buildType")
            .scaleConfig().linearConfig("itemCapacity");


    public ECStorageBlock(StorageBlock root,int level) throws IllegalAccessException {
        super("c" + level + "-" + root.name);
        this.root = root;
        this.level = level;
        ECTool.compress(root, this, config, level);
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
        super.init();
    }
}
