package ECType.ECBlockTypes.SandBox;

import ECConfig.Config;
import ECConfig.ECData;
import ECConfig.ECTool;
import arc.Core;
import mindustry.type.Liquid;
import mindustry.world.blocks.sandbox.LiquidSource;
import mindustry.world.meta.BuildVisibility;

public class ECLiquidSource extends LiquidSource {

    public LiquidSource root;

    public static Config config = new Config().addConfigSimple(null, "buildType")
            .scaleConfig().linearConfig();

    public ECLiquidSource(LiquidSource root) throws IllegalAccessException {
        super("compress-" + root.name);
        this.root = root;
        ECTool.compress(root, this, config, 0);
        ECTool.loadCompressContentRegion(root, this);
        ECTool.setIcon(root, this, 0);
        requirements(root.category, BuildVisibility.sandboxOnly, ECTool.compressItemStack(root.requirements, 0));

        liquidCapacity = Float.MAX_VALUE;
        alwaysUnlocked = true;

        localizedName = Core.bundle.get("Compression.localizedName") + root.localizedName;
        description = root.description;
        details = root.details;



        /*/
        configurations.clear();
        config(Point2.class, (MassDriverBuild tile, Point2 point) -> tile.link = Point2.pack(point.x + tile.tileX(), point.y + tile.tileY()));
        config(Integer.class, (MassDriverBuild tile, Integer point) -> tile.link = point);
        //*/
        ECData.register(root, this, 1);
    }

    public class ECLiquidSourceBuild extends LiquidSourceBuild {

        @Override
        public void dumpLiquid(Liquid liquid) {
            ECTool.dumpLiquid(liquid,this);
        }

        @Override
        public void dumpLiquid(Liquid liquid, float scaling, int outputDir) {
            super.dumpLiquid(liquid, scaling, outputDir);
        }
    }
}
