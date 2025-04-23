package ECType.ECWeapons;

import ECConfig.Config;
import ECConfig.ECTool;
import mindustry.type.Weapon;
import mindustry.type.weapons.BuildWeapon;

public class ECBuildWeapon extends BuildWeapon {
    public Weapon root;

    public int level;

    public static Config config = new Config().addConfigSimple(null,"bullet");

    public ECBuildWeapon(Weapon root,int level) throws IllegalAccessException {
        super("c"+level+"-"+root.name);
        this.root = root;
        this.level = level;
        ECTool.compress(root,this,Object.class,config,level);
        bullet = ECTool.compressBulletType(root.bullet,level);
    }

}
