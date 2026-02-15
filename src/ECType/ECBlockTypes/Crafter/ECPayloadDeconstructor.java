package ECType.ECBlockTypes.Crafter;

import ECConfig.Config;
import ECConfig.EC;
import ECConfig.ECData;
import ECConfig.ECTool;
import arc.Core;
import mindustry.type.Item;
import mindustry.world.blocks.payloads.PayloadDeconstructor;

public class ECPayloadDeconstructor extends PayloadDeconstructor implements EC {

    public static Config config = new Config().addConfigSimple(null, "buildType")
            .scaleConfig()
            .linearConfig("deconstructSpeed");
    public PayloadDeconstructor root;
    public int level;


    public ECPayloadDeconstructor(PayloadDeconstructor root, int level) throws IllegalAccessException {
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
    public int getLevel() {
        return level;
    }

    @Override
    public Object getRoot() {
        return root;
    }

    public class ECPayloadDeconstructorBuild extends PayloadDeconstructorBuild {

        @Override
        public void updateTile() {
            super.updateTile();
        }

        @Override
        public boolean dump(Item todump) {
            return ECTool.dump(this, todump);
        }
    }

}
