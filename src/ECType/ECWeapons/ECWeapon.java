package ECType.ECWeapons;

import ECConfig.Config;
import ECConfig.ECTool;
import mindustry.type.Weapon;

public class ECWeapon extends Weapon {

    public Weapon root;

    public int level;

    public static Config config = new Config().addConfigSimple(null,"bullet");



    public ECWeapon(Weapon root,int level) throws IllegalAccessException {
        super("c"+level+"-"+root.name);
        this.root = root;
        this.level = level;
        ECTool.compress(root,this,Object.class,config,level);
        bullet = ECTool.compressBulletType(root.bullet,level);
    }


}
