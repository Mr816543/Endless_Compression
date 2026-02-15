package ECType.ECBlockTypes.Item;

import ECConfig.*;
import arc.Core;
import mindustry.entities.bullet.MassDriverBolt;
import mindustry.type.Item;
import mindustry.world.blocks.distribution.MassDriver;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatCat;
import mindustry.world.meta.StatUnit;

public class ECMassDriver extends MassDriver implements EC {

    public static Config config = new Config().addConfigSimple(null, "buildType", "bullet")
            .scaleConfig("range", "rotateSpeed", "minDistribute", "bulletSpeed").linearConfig("itemCapacity")
            .addConfigSimple(1 / ECSetting.SCALE_MULTIPLIER, "reload");
    public MassDriver root;
    public int level;

    public ECMassDriver(MassDriver root, int level) throws IllegalAccessException {
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


        bullet = (MassDriverBolt) ECTool.compressBulletType(root.bullet, level);

        /*/
        configurations.clear();
        config(Point2.class, (MassDriverBuild tile, Point2 point) -> tile.link = Point2.pack(point.x + tile.tileX(), point.y + tile.tileY()));
        config(Integer.class, (MassDriverBuild tile, Integer point) -> tile.link = point);
        //*/
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
        stats.add(new Stat("maxspeed", StatCat.function), itemCapacity * 60f / reload, StatUnit.perSecond);
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public Object getRoot() {
        return root;
    }


    public class ECMassDriverBuild extends MassDriverBuild {

        @Override
        public boolean dump(Item todump) {
            return ECTool.dump(this, todump);
        }
    }

}
