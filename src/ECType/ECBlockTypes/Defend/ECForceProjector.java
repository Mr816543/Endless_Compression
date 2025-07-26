package ECType.ECBlockTypes.Defend;

import ECConfig.Config;
import ECConfig.ECData;
import ECConfig.ECSetting;
import ECConfig.ECTool;
import arc.Core;
import arc.math.Mathf;
import mindustry.type.ItemStack;
import mindustry.world.blocks.defense.ForceProjector;
import mindustry.world.consumers.Consume;
import mindustry.world.consumers.ConsumeCoolant;
import mindustry.world.consumers.ConsumeItems;
import mindustry.world.consumers.ConsumePower;

public class ECForceProjector extends ForceProjector {
    public static Config config = new Config().addConfigSimple(null, "buildType")
            .scaleConfig("phaseRadiusBoost","radius").linearConfig("shieldHealth","phaseShieldBoost","cooldownBrokenBase");
    public ForceProjector root;
    public int level;

    public ECForceProjector(ForceProjector root, int level) throws IllegalAccessException {
        super("c" + level + "-" + root.name);
        this.root = root;
        this.level = level;
        ECTool.compress(root, this, config, level);
        ECTool.loadCompressContentRegion(root, this);
        ECTool.setIcon(root, this, level);
        ECTool.loadHealth(this, root, level);
        requirements(root.category, root.buildVisibility, ECTool.compressItemStack(root.requirements, level));

        consumeBuilder.clear();

        if (consumeCoolant) {
            consume(coolantConsumer = new ConsumeCoolant(coolantConsumption) {{
                maxTemp = 0.5f * Mathf.pow(1 / ECSetting.SCALE_MULTIPLIER, level);
                maxFlammability = 0.1f * Mathf.pow(1 / ECSetting.SCALE_MULTIPLIER, level);
            }}).boost().update(false);
        }

        localizedName = level + Core.bundle.get("num-Compression.localizedName") + root.localizedName;
        description = root.description;
        details = root.details;

        ECData.register(root, this, level);
    }

    @Override
    public void init() {
        try {
            for (Consume consume : root.consumers) {
                if (consume instanceof ConsumeItems r) {
                    ConsumeItems c = new ConsumeItems(new ItemStack[r.items.length]);
                    ECTool.compress(consume, c, Object.class, Config.NULL, 0);
                    for (int i = 0; i < c.items.length; i++) {
                        c.items[i] = new ItemStack(ECData.get(r.items[i].item, level), r.items[i].amount);
                    }
                    this.itemConsumer = this.consume(c);
                }else if (consume instanceof ConsumePower) {
                    ConsumePower c = new ConsumePower(0, 0, false);
                    ECTool.compress(consume, c, Object.class, Config.NULL, 0);
                    if (c.usage > 0) {
                        c.usage *= Mathf.pow(ECSetting.LINEAR_MULTIPLIER, level);
                    }
                    this.consume(c);
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }


        super.init();
    }
}
