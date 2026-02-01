package ECType.ECBlockTypes.Crafter;

import ECConfig.Config;
import ECConfig.ECData;
import ECConfig.ECTool;
import arc.Core;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.world.Block;
import mindustry.world.blocks.defense.RegenProjector;
import mindustry.world.blocks.payloads.Constructor;
import mindustry.world.blocks.storage.CoreBlock;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static ECConfig.ECTool.getECBlockLevel;
import static mindustry.Vars.state;

public class ECConstructor extends Constructor {

    public static Config config = new Config().addConfigSimple(null, "buildType")
            .scaleConfig()
            .linearConfig("buildSpeed");
    public Constructor root;
    public int level;


    public ECConstructor(Constructor root, int level) throws IllegalAccessException {
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
        consumeBuilder = ECTool.consumeBuilderCopy(root, level,true);



        super.init();
        if (!root.filter.isEmpty()){
            Seq<Block> f = new Seq<>();
            for (Block b : root.filter){
                f.add(ECData.get(b,level));
            }
            filter = f;
        }
    }

    @Override
    public boolean canProduce(Block b){
        return b.isVisible() && b.size >= minBlockSize && b.size <= maxBlockSize && !(b instanceof CoreBlock) && !state.rules.isBanned(b) && b.environmentBuildable() && (filter.isEmpty() || filter.contains(b)) && getECBlockLevel(b) <= level;
    }


    public class ECConstructorBuild extends ConstructorBuild {

    }
}
