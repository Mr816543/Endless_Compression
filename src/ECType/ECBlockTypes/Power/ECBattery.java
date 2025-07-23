package ECType.ECBlockTypes.Power;

import ECConfig.Config;
import ECConfig.ECData;
import ECConfig.ECTool;
import arc.Core;
import arc.math.Mathf;
import mindustry.world.blocks.power.Battery;
import mindustry.world.blocks.storage.StorageBlock;
import mindustry.world.consumers.Consume;
import mindustry.world.consumers.ConsumePower;

public class ECBattery extends Battery {

    public Battery root;

    public int level;

    public static Config config = new Config().addConfigSimple(null, "buildType")
            .scaleConfig().linearConfig("itemCapacity");

    public ECBattery(Battery root,int level) throws IllegalAccessException {
        super("c" + level + "-" + root.name);
        this.root = root;
        this.level = level;
        ECTool.compress(root, this, config, level);
        ECTool.loadCompressContentRegion(root, this);
        ECTool.setIcon(root, this, level);
        requirements(root.category, root.buildVisibility, ECTool.compressItemStack(root.requirements,level));

        localizedName = level + Core.bundle.get("num-Compression.localizedName") + root.localizedName;
        description = root.description;
        details = root.details;

        ECData.register(root,this,level);
    }

    @Override
    public void init() {
        health = root.health * Mathf.pow(5,level);
        for (Consume consume:root.consumers){
            if (consume instanceof ConsumePower c && c.buffered){
                this.consumePowerBuffered(c.capacity * Mathf.pow(5,level));
            }
        }
        super.init();
    }
}
