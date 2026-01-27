package ECType.ECBlockTypes.Power;

import ECConfig.Config;
import ECConfig.ECData;
import ECConfig.ECSetting;
import ECConfig.ECTool;
import arc.Core;
import arc.math.Mathf;
import mindustry.world.Block;
import mindustry.world.blocks.power.NuclearReactor;
import mindustry.world.blocks.power.PowerNode;
import mindustry.world.meta.Stat;

public class ECNuclearReactor extends NuclearReactor {

    public NuclearReactor root;

    public int level;

    public static Config config = new Config().addConfigSimple(null, "buildType")
            .scaleConfig("explosionRadius").linearConfig("explosionDamage").addConfigSimple(9f,"powerProduction");


    public ECNuclearReactor(NuclearReactor root,int level) throws IllegalAccessException {
        super("c" + level + "-" + root.name);
        this.root = root;
        this.level = level;
        ECTool.compress(root, this,root.getClass(), Block.class, config, level);

        this.size = root.size;


        ECTool.loadCompressContentRegion(root, this);
        ECTool.setIcon(root, this, level);
        ECTool.loadHealth(this,root,level);
        requirements(root.category, root.buildVisibility, ECTool.compressItemStack(root.requirements,level));

        fuelItem = ECData.get(root.fuelItem,level);

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

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.damage,explosionDamage);
        stats.add(Stat.range,explosionRadius);
    }
}
