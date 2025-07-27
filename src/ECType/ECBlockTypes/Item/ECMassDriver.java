package ECType.ECBlockTypes.Item;

import ECConfig.Config;
import ECConfig.ECData;
import ECConfig.ECSetting;
import ECConfig.ECTool;
import ECType.ECBlockTypes.Defend.ECCoreBlock;
import arc.Core;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.util.Time;
import mindustry.entities.bullet.MassDriverBolt;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.world.Tile;
import mindustry.world.blocks.distribution.MassDriver;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatCat;
import mindustry.world.meta.StatUnit;

import static mindustry.Vars.state;
import static mindustry.Vars.world;

public class ECMassDriver extends MassDriver {

    public static Config config = new Config().addConfigSimple(null, "buildType","bullet")
            .scaleConfig("range","rotateSpeed","minDistribute","bulletSpeed").linearConfig("itemCapacity")
            .addConfigSimple(1/ECSetting.SCALE_MULTIPLIER,"reload");
    public MassDriver root;
    public int level;
    public ECMassDriver(MassDriver root,int level) throws IllegalAccessException {
        super("c" + level + "-" + root.name);
        this.root = root;
        this.level = level;
        ECTool.compress(root, this, config, level);
        ECTool.loadCompressContentRegion(root, this);
        ECTool.setIcon(root, this, level);
        ECTool.loadHealth(this, root, level);
        requirements(root.category, ECTool.compressItemStack(root.requirements, level));


        localizedName = level + Core.bundle.get("num-Compression.localizedName") + root.localizedName;
        description = root.description;
        details = root.details;


        bullet = (MassDriverBolt) ECTool.compressBulletType(root.bullet,level);

        /*/
        configurations.clear();
        config(Point2.class, (MassDriverBuild tile, Point2 point) -> tile.link = Point2.pack(point.x + tile.tileX(), point.y + tile.tileY()));
        config(Integer.class, (MassDriverBuild tile, Integer point) -> tile.link = point);
        //*/
        ECData.register(root, this, level);
    }

    @Override
    public void init() {
        consumeBuilder = ECTool.consumeBuilderCopy(root,level);
        super.init();
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(new Stat("maxspeed", StatCat.function),itemCapacity * 60f / reload, StatUnit.perSecond);
    }

    public class ECMassDriverBuild extends MassDriverBuild{


        @Override
        public boolean dumpAccumulate(Item item) {
            boolean res = false;

            for(this.dumpAccum += this.delta(); this.dumpAccum >= 1.0F; --this.dumpAccum) {
                res |= ECTool.dump( this , item);
            }

            return res;
        }
    }

}
