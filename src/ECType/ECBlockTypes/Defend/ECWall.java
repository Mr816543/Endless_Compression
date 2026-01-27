package ECType.ECBlockTypes.Defend;

import ECConfig.Config;
import ECConfig.ECData;
import ECConfig.ECSetting;
import ECConfig.ECTool;
import arc.Core;
import arc.math.Mathf;
import arc.util.Log;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

public class ECWall extends Wall {

    public Wall root;

    public int level;

    public static Config config = new Config().addConfigSimple(null, "buildType").
            linearConfig("lightningDamage","armor").
            scaleConfig("lightningChance","lightningLength","chanceDeflect");

    public float IFR ;


    public ECWall(Wall root,int level) throws IllegalAccessException {
        super("c"+level+"-"+root.name);
        this.root = root;
        this.level = level;
        this.IFR = Mathf.pow(1/ECSetting.LINEAR_MULTIPLIER,level);
        ECTool.compress(root, this, config, level);
        ECTool.loadCompressContentRegion(root, this);
        ECTool.setIcon(root, this, level);
        requirements(root.category, root.buildVisibility, ECTool.compressItemStack(root.requirements,level));
        localizedName = level + Core.bundle.get("num-Compression.localizedName") + root.localizedName;
        description = root.description;
        details = root.details;
        ECData.register(root,this,level);

    }

    @Override
    public void setStats() {
        super.setStats();
        float ifr = (1 - IFR)*100;
        stats.add(new Stat("IFR"),ifr,StatUnit.percent);
        int echealth = (int) (health*Mathf.pow(ECSetting.LINEAR_MULTIPLIER,level));
        stats.add(new Stat("echealth"), echealth == Integer.MAX_VALUE ? Core.bundle.get("infinite"):echealth+"",StatUnit.none);
    }

    public class ECWallBuild extends WallBuild{

        @Override
        public float handleDamage(float amount) {
            return amount * IFR;
        }

        @Override
        public void heal(float amount) {
            this.health += amount * IFR;
            this.clampHealth();
            this.healthChanged();
        }

        @Override
        public void healFract(float amount) {
            this.health += amount * this.maxHealth;
            this.clampHealth();
            this.healthChanged();
        }

        @Override
        public float maxHealth() {
            return super.maxHealth() / IFR;
        }

    }
}
