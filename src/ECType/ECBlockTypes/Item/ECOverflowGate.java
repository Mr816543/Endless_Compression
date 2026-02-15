package ECType.ECBlockTypes.Item;

import ECConfig.EC;
import ECConfig.ECData;
import ECConfig.ECTool;
import arc.Core;
import mindustry.gen.Teamc;
import mindustry.type.Item;
import mindustry.world.blocks.distribution.OverflowGate;

public class ECOverflowGate extends OverflowGate implements EC {

    public OverflowGate root;

    public ECOverflowGate(OverflowGate root) throws IllegalAccessException {
        super("compression-" + root.name);

        this.root = root;
        this.invert = root.invert;
        this.speed = root.speed;

        requirements(root.category, root.buildVisibility, ECTool.compressItemStack(root.requirements, 1));
        localizedName = Core.bundle.get("Compression.localizedName") + root.localizedName;
        description = root.description;
        details = root.details;

        size = root.size;

        ECTool.loadCompressContentRegion(root, this);

        ECTool.setIcon(root, this, 0);


        ECTool.loadHealth(this, root, 1);

        ECData.register(root, this, 1);
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public int getLevel() {
        return 1;
    }

    @Override
    public Object getRoot() {
        return root;
    }


    public class ECOverflowGateBuild extends OverflowGateBuild {

        @Override
        public int acceptStack(Item item, int amount, Teamc source) {
            if (getTileTarget(item, this, false) == null) return 0;
            return getTileTarget(item, this, false).acceptStack(item, amount, source);
        }

        @Override
        public void handleStack(Item item, int amount, Teamc source) {
            getTileTarget(item, this, true).handleStack(item, amount, source);
        }
    }

}
