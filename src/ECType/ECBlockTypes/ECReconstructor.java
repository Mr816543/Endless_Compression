package ECType.ECBlockTypes;

import ECConfig.Config;
import ECConfig.ECData;
import ECConfig.ECSetting;
import ECConfig.ECTool;
import arc.Core;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.ai.UnitCommand;
import mindustry.ctype.UnlockableContent;
import mindustry.type.UnitType;
import mindustry.world.blocks.payloads.PayloadBlock;
import mindustry.world.blocks.units.Reconstructor;
import mindustry.world.consumers.Consume;
import mindustry.world.consumers.ConsumeItems;
import mindustry.world.consumers.ConsumePower;

public class ECReconstructor extends Reconstructor {

    public Reconstructor root ;

    public int level;

    public static Config config = new Config().addConfigSimple(null,"buildType","configurations","upgrades");

    public ECReconstructor(Reconstructor root,int level) throws IllegalAccessException {
        super("c"+level+"-" + root.name);

        this.root = root;
        this.level = level;

        ECTool.compress(root,this, UnlockableContent.class , config, level);
        ECTool.loadCompressContentRegion(root, this);
        ECTool.setIcon(root, this, level);
        requirements(root.category, root.buildVisibility, ECTool.compressItemStack(root.requirements,level));

        localizedName = level + Core.bundle.get("num-Compression.localizedName") + root.localizedName;
        description = root.description;
        details = root.details;

        upgrades = new Seq<>();
        for (UnitType[] p : root.upgrades){
            if (!ECData.hasECContent(p[0])) continue;
            if (!ECData.hasECContent(p[1])) continue;
            UnitType[] c = new UnitType[]{ECData.get(p[0],level),ECData.get(p[1],level)};
            upgrades.add(c);
        }


        configurable = true;
        config(UnitCommand.class, (ReconstructorBuild build, UnitCommand command) -> build.command = command);
        configClear((ReconstructorBuild build) -> build.command = null);

        ECData.register(root,this,level);

    }

    @Override
    public void init() {
        if (consPower == null){
            consumePower(0);
        }else{
            consume(consPower);
        }
        consumeBuilder = ECTool.consumeBuilderCopy(root,level);
        super.init();
    }

    public class ECReconstructorBuild extends ReconstructorBuild{


        @Override
        public void updateTile() {
            super.updateTile();
        }



    }


}
