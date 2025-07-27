package ECType.ECBlockTypes.SandBox;

import ECConfig.Config;
import ECConfig.ECData;
import ECConfig.ECSetting;
import ECConfig.ECTool;
import arc.Core;
import mindustry.entities.bullet.MassDriverBolt;
import mindustry.world.blocks.sandbox.PowerSource;
import mindustry.world.meta.BuildVisibility;

public class ECPowerSource extends PowerSource {

    public PowerSource root;


    public static Config config = new Config().addConfigSimple(null, "buildType")
            .scaleConfig().linearConfig();

    public ECPowerSource(PowerSource root) throws IllegalAccessException {
        super("compress-" + root.name);
        this.root = root;
        ECTool.compress(root, this, config, 0);
        ECTool.loadCompressContentRegion(root, this);
        ECTool.setIcon(root, this, 0);
        requirements(root.category, BuildVisibility.sandboxOnly, ECTool.compressItemStack(root.requirements, 0));

        powerProduction = Float.MAX_VALUE;
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
}
