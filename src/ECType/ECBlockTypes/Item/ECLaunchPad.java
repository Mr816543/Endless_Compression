package ECType.ECBlockTypes.Item;

import ECConfig.Config;
import ECConfig.ECData;
import ECConfig.ECSetting;
import ECConfig.ECTool;
import arc.Core;
import arc.math.Mathf;
import arc.struct.Seq;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.gen.LaunchPayload;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.world.blocks.campaign.LaunchPad;
import mindustry.world.blocks.defense.RegenProjector;
import mindustry.world.consumers.*;

public class ECLaunchPad extends LaunchPad {

    public static Config config = new Config().addConfigSimple(null, "buildType")
            .scaleConfig("itemCapacity").addConfigSimple(1f/ECSetting.SCALE_MULTIPLIER,"launchTime")
            .linearConfig();
    public LaunchPad root;
    public int level;


    public ECLaunchPad(LaunchPad root, int level) throws IllegalAccessException {
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
        if (Core.settings.getBool("simpleLaunch")){
            Seq<Consume> consumes = new Seq<>();
            try {
                for (Consume consume : root.consumers) {
                    if (consume instanceof ConsumePower) {

                        ConsumePower c = new ConsumePower(0, 0, false);
                        ECTool.compress(consume, c, Object.class, Config.NULL, 0);
                        if (c.usage > 0) {
                            c.usage *= Mathf.pow(ECSetting.LINEAR_MULTIPLIER, level);
                        }
                        if (c.capacity > 0){
                            c.capacity *= Mathf.pow(ECSetting.LINEAR_MULTIPLIER, level);
                        }
                        consumes.add(c);
                    }
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            consumeBuilder = consumes;
        }else {
            consumeBuilder = ECTool.consumeBuilderCopy(root, level);
        }
        super.init();
    }


    public class ECLaunchPadBuild extends LaunchPadBuild {

    }
}
