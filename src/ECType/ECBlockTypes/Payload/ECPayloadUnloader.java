package ECType.ECBlockTypes.Payload;

import ECConfig.Config;
import ECConfig.ECData;
import ECConfig.ECTool;
import arc.Core;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.world.blocks.payloads.PayloadUnloader;

public class ECPayloadUnloader extends PayloadUnloader {

    public static Config config = new Config().addConfigSimple(null, "buildType")
            .scaleConfig()
            .linearConfig("itemCapacity","liquidCapacity","maxPowerUnload");

    public PayloadUnloader root;
    public int level;


    public ECPayloadUnloader(PayloadUnloader root, int level) throws IllegalAccessException {
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


    public class ECPayloadUnloaderBuild extends PayloadUnloaderBuild {

        @Override
        public void updateTile() {
            super.updateTile();
        }

        @Override
        public void dumpLiquid(Liquid liquid, float scaling, int outputDir) {
            ECTool.dumpLiquid(liquid, scaling, outputDir,this);
        }

        @Override
        public boolean dump(Item todump) {
            return ECTool.dump(this,todump);
        }
    }
}
