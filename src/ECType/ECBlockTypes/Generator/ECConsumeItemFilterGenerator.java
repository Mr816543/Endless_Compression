package ECType.ECBlockTypes.Generator;

import ECConfig.Config;
import ECConfig.EC;
import ECConfig.ECData;
import ECConfig.ECTool;
import arc.Core;
import mindustry.ctype.UnlockableContent;
import mindustry.world.blocks.power.ConsumeGenerator;
import mindustry.world.consumers.Consume;
import mindustry.world.consumers.ConsumeItemExplode;

public class ECConsumeItemFilterGenerator extends ConsumeGenerator implements EC {

    public static Config config = new Config().addConfigSimple(null, "buildType");
    public ConsumeGenerator root;

    public ECConsumeItemFilterGenerator(ConsumeGenerator root) throws IllegalAccessException {
        super("compression-" + root.name);

        this.root = root;

        ECTool.compress(root, this, UnlockableContent.class, config, 0);
        ECTool.loadCompressContentRegion(root, this);
        ECTool.setIcon(root, this, 0);
        requirements(root.category, root.buildVisibility, ECTool.compressItemStack(root.requirements, 1));
        localizedName = Core.bundle.get("Compression.localizedName") + root.localizedName;
        description = root.description;
        details = root.details;

        ECData.register(root, this, 1);
    }

    @Override
    public void init() {

        consumeBuilder = ECTool.consumeBuilderCopy(root, 1, true);

        for (Consume consume : consumeBuilder) {
            if (consume instanceof ConsumeItemExplode c) {
                consumeBuilder.remove(c);
                consumeBuilder.add(new ConsumeItemExplode() {{
                    damage = 0;
                }});
            }


        }


        powerProduction *= 1.5f;


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

}
