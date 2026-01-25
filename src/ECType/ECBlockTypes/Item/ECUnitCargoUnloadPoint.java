package ECType.ECBlockTypes.Item;

import ECConfig.Config;
import ECConfig.ECData;
import ECConfig.ECTool;
import arc.Core;
import mindustry.type.Item;
import mindustry.world.blocks.units.UnitCargoUnloadPoint;

public class ECUnitCargoUnloadPoint extends UnitCargoUnloadPoint {

    public static Config config = new Config().addConfigSimple(null, "buildType")
            .scaleConfig()
            .linearConfig("itemCapacity");
    public UnitCargoUnloadPoint root;
    public int level;


    public ECUnitCargoUnloadPoint(UnitCargoUnloadPoint root, int level) throws IllegalAccessException {
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


    public class ECUnitCargoUnloadPointBuild extends UnitCargoUnloadPointBuild {

        @Override
        public boolean dump(Item item) {
            return ECTool.dump(this, item);
        }
    }
}
