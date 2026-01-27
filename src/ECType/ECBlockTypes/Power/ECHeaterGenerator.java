package ECType.ECBlockTypes.Power;

import ECConfig.Config;
import ECConfig.ECData;
import ECConfig.ECTool;
import arc.Core;
import mindustry.content.Liquids;
import mindustry.type.LiquidStack;
import mindustry.world.blocks.power.HeaterGenerator;
import mindustry.world.blocks.power.VariableReactor;

public class ECHeaterGenerator extends HeaterGenerator {

    public static Config config = new Config().addConfigSimple(null, "buildType")
            .scaleConfig("explosionRadius").linearConfig("explosionDamage").addConfigSimple(9f,"powerProduction","heatOutput","warmupRate");
    public HeaterGenerator root;
    public int level;

    public ECHeaterGenerator(HeaterGenerator root, int level) throws IllegalAccessException {
        super("c" + level + "-" + root.name);
        this.root = root;
        this.level = level;
        ECTool.compress(root, this, config, level);
        ECTool.loadCompressContentRegion(root, this);
        ECTool.setIcon(root, this, level);
        ECTool.loadHealth(this, root, level);
        requirements(root.category, root.buildVisibility, ECTool.compressItemStack(root.requirements, level));

        outputLiquid = new LiquidStack(ECData.get(root.outputLiquid.liquid,level), root.outputLiquid.amount);

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

    public class ECHeaterGeneratorBuild extends HeaterGeneratorBuild {

    }
}
