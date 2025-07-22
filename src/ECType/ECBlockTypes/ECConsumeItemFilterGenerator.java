package ECType.ECBlockTypes;

import ECConfig.Config;
import ECConfig.ECData;
import ECConfig.ECTool;
import arc.Core;
import mindustry.ctype.UnlockableContent;
import mindustry.world.blocks.power.ConsumeGenerator;
import mindustry.world.consumers.Consume;
import mindustry.world.consumers.ConsumeItemExplode;

public class ECConsumeItemFilterGenerator extends ConsumeGenerator{

    public ConsumeGenerator root;

    public static Config config = new Config().addConfigSimple(null,"buildType");

    public ECConsumeItemFilterGenerator(ConsumeGenerator root) throws IllegalAccessException {
        super("compression-"+root.name);

        this.root = root;

        ECTool.compress(root,this, UnlockableContent.class , config, 0);
        ECTool.loadCompressContentRegion(root, this);
        ECTool.setIcon(root, this, 0);
        requirements(root.category, root.buildVisibility, ECTool.compressItemStack(root.requirements,1));
        localizedName = Core.bundle.get("Compression.localizedName") + root.localizedName;
        description = root.description;
        details = root.details;

        ECData.register(root,this,1);
    }

    @Override
    public void init() {

        consumeBuilder = ECTool.consumeBuilderCopy(root,1,true);

        for (Consume consume : consumeBuilder){
            if (consume instanceof ConsumeItemExplode c){
                consumeBuilder.remove(c);
                consumeBuilder.add(new ConsumeItemExplode(){{
                    damage=0;
                }});
            }





        }


        powerProduction *= 1.5f;




        super.init();
    }
}
