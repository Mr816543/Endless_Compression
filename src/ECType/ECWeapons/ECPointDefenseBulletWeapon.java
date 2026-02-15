package ECType.ECWeapons;

import ECConfig.Config;
import ECConfig.EC;
import ECConfig.ECTool;
import mindustry.type.Weapon;
import mindustry.type.weapons.PointDefenseBulletWeapon;

public class ECPointDefenseBulletWeapon extends PointDefenseBulletWeapon implements EC {
    public static Config config = new Config().addConfigSimple(null, "bullet").scaleConfig("damageTargetWeight");
    public Weapon root;
    public int level;

    public ECPointDefenseBulletWeapon(Weapon root, int level) throws IllegalAccessException {
        super("c" + level + "-" + root.name);
        this.root = root;
        this.level = level;
        ECTool.compress(root, this, Object.class, config, level);
        bullet = ECTool.compressBulletType(root.bullet, level);
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
