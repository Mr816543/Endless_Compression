package ECType.ECBlockTypes.Item;

import ECConfig.Config;
import ECConfig.EC;
import ECConfig.ECData;
import ECConfig.ECTool;
import ECContents.ECUnitTypes;
import arc.Core;
import mindustry.world.blocks.units.UnitCargoLoader;

public class ECUnitCargoLoader extends UnitCargoLoader implements EC {

    public static Config config = new Config().addConfigSimple(null, "buildType")
            .scaleConfig("polyStroke", "polyRadius", "polyRotateSpeed")
            .linearConfig("itemCapacity");
    public UnitCargoLoader root;
    public int level;


    public ECUnitCargoLoader(UnitCargoLoader root, int level) throws IllegalAccessException {
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
        if (root.unitType == ECData.get(root.unitType, level)) {
            try {
                ECUnitTypes.compressUnitType(root.unitType);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        unitType = ECData.get(root.unitType, level);
    }


    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public Object getRoot() {
        return root;
    }


    public class ECUnitCargoLoaderBuild extends UnitTransportSourceBuild {
    }
}
