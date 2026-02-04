package ECType.ECBlockTypes.Payload;

import ECConfig.Config;
import ECConfig.ECData;
import ECConfig.ECSetting;
import ECConfig.ECTool;
import arc.Core;
import arc.math.Mathf;
import arc.util.Log;
import mindustry.world.blocks.defense.RegenProjector;
import mindustry.world.blocks.payloads.PayloadLoader;
import mindustry.world.consumers.Consume;
import mindustry.world.consumers.ConsumePower;

public class ECPayloadLoader extends PayloadLoader {

    public static Config config = new Config().addConfigSimple(null, "buildType")
            .scaleConfig()
            .linearConfig("itemsLoaded","itemCapacity","liquidsLoaded","liquidCapacity","maxPowerConsumption")
            .addConfigSimple(1f/ ECSetting.SCALE_MULTIPLIER,"loadTime");
    public PayloadLoader root;
    public int level;


    public ECPayloadLoader(PayloadLoader root, int level) throws IllegalAccessException {
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
        consumeBuilder.removeAll(b -> b instanceof ConsumePower);
        consPower = null;
        try {
            Object o = ECTool.get(root,"basePowerUse");
            if (o==null){
                consumePower(200f);
            }else {
                consumePower((float) o * Mathf.pow(ECSetting.LINEAR_MULTIPLIER,level));
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        super.init();
    }

    @Override
    public void setStats() {
        super.setStats();
    }

    public class ECPayloadLoaderBuild extends PayloadLoaderBuild {

        @Override
        public void updateTile() {
            super.updateTile();
        }
    }
}
