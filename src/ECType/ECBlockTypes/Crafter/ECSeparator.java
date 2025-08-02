package ECType.ECBlockTypes.Crafter;

import ECConfig.Config;
import ECConfig.ECData;
import ECConfig.ECSetting;
import ECConfig.ECTool;
import arc.Core;
import arc.math.Mathf;
import mindustry.world.Block;
import mindustry.world.blocks.production.Drill;
import mindustry.world.blocks.production.Separator;
import mindustry.world.meta.BlockGroup;

public class ECSeparator extends Separator {


    public static Config config = new Config().addConfigSimple(null, "buildType")
            .scaleConfig().linearConfig();
    public Separator root;
    public int level;

    public ECSeparator(Separator root,int level) throws IllegalAccessException {
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

        results = ECTool.compressItemStack(root.results,level);

        ECData.register(root, this, level);
    }

    @Override
    public boolean canReplace(Block other){
        if(other.alwaysReplace) return true;
        if(other.privileged) return false;
        return other.replaceable &&
                (other != this || (rotate && quickRotate)) &&
                ((this.group != BlockGroup.none && other.group == this.group) || other == this.root) &&
                (size == other.size || (size >= other.size && ((subclass != null && subclass == other.subclass) || group.anyReplace)));
    }

    @Override
    public void init() {
        consumeBuilder = ECTool.consumeBuilderCopy(root, level);
        super.init();
    }

    public class ECSeparatorBuild extends SeparatorBuild{

    }
}
