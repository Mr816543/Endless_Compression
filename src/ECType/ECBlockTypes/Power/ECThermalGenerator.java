package ECType.ECBlockTypes.Power;

import ECConfig.Config;
import ECConfig.ECData;
import ECConfig.ECSetting;
import ECConfig.ECTool;
import arc.Core;
import arc.math.Mathf;
import mindustry.ctype.UnlockableContent;
import mindustry.type.LiquidStack;
import mindustry.world.Block;
import mindustry.world.blocks.power.ImpactReactor;
import mindustry.world.blocks.power.ThermalGenerator;

public class ECThermalGenerator extends ThermalGenerator {

    public ThermalGenerator root;

    public int level;

    public static Config config = new Config().addConfigSimple(null, "buildType")
            .scaleConfig().linearConfig("powerProduction","liquidCapacity");


    public ECThermalGenerator(ThermalGenerator root,int level) throws IllegalAccessException {
        super("c" + level + "-" + root.name);
        this.root = root;
        this.level = level;
        ECTool.compress(root, this, config, level);

        ECTool.loadCompressContentRegion(root, this);
        ECTool.setIcon(root, this, level);
        ECTool.loadHealth(this,root,level);
        requirements(root.category, root.buildVisibility, ECTool.compressItemStack(root.requirements,level));

        if (root.outputLiquid!=null){
            outputLiquid = new LiquidStack(root.outputLiquid.liquid,root.outputLiquid.amount* Mathf.pow(ECSetting.LINEAR_MULTIPLIER,level));
        }

        localizedName = level + Core.bundle.get("num-Compression.localizedName") + root.localizedName;
        description = root.description;
        details = root.details;

        ECData.register(root,this,level);
    }

    @Override
    public void init() {
        consumeBuilder = ECTool.consumeBuilderCopy(root,level);
        super.init();
    }
}
