package ECType.ECBlockTypes.Defend;

import ECConfig.Config;
import ECConfig.EC;
import ECConfig.ECData;
import ECConfig.ECTool;
import arc.Core;
import mindustry.world.blocks.defense.MendProjector;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatCat;
import mindustry.world.meta.StatUnit;

import static mindustry.Vars.tilesize;

public class ECMendProjector extends MendProjector implements EC {
    public static Config config = new Config().addConfigSimple(null, "buildType")
            .scaleConfig("range", "phaseRangeBoost", "healPercent").linearConfig("phaseBoost");
    public MendProjector root;
    public int level;

    public ECMendProjector(MendProjector root, int level) throws IllegalAccessException {
        super("c" + level + "-" + root.name);
        this.root = root;
        this.level = level;
        ECTool.compress(root, this, config, level);
        ECTool.loadCompressContentRegion(root, this);
        ECTool.setIcon(root, this, level);
        ECTool.loadHealth(this, root, level);
        requirements(root.category, root.buildVisibility, ECTool.compressItemStack(root.requirements, level));

        //phaseRangeBoost = root.phaseRangeBoost * range / root.range;

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

    @Override
    public void setStats() {
        super.setStats();
        stats.remove(Stat.repairTime);
        stats.remove(Stat.range);
        stats.add(new Stat("healpercent", StatCat.function), healPercent, StatUnit.percent);
        stats.add(new Stat("reload", StatCat.function), ((int) (reload / 60f * 100f)) / 100f, StatUnit.seconds);
        stats.add(Stat.range, range / tilesize, StatUnit.blocks);

    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public Object getRoot() {
        return root;
    }

}
