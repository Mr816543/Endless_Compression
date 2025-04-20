package ECType;


import ECConfig.Config;
import ECConfig.ECSetting;
import ECConfig.ECTool;
import arc.Core;
import arc.math.Mathf;
import arc.struct.Seq;
import mindustry.type.UnitType;
import mindustry.type.Weapon;

public class ECUnitType extends UnitType {

    public UnitType root;

    public int level;

    public static Config config = new Config().linearConfig("health","armor","itemCapacity","buildSpeed").scaleConfig("speed","maxRange","mineTier");

    public ECUnitType(UnitType root,int level) throws IllegalAccessException {
        super("c"+ level +"-"+root.name);this.root = root;
        this.level = level;
        if (this.itemCapacity < 0) {
            this.itemCapacity = Math.max(Mathf.round((int)(this.hitSize * 4.0F), 10), 10);
        }
        ECTool.compress(this.root,this,config,level);
        ECTool.loadCompressContentRegion(root,this);
        ECTool.setIcon(root,this,level);
        localizedName = level + Core.bundle.get("num-Compression.localizedName") + root.localizedName;
        description = root.description;
        details = root.details;
        weapons = new Seq<>();
        for (Weapon weapon:root.weapons){
            weapons.add(weapon.copy());
        }

    }
}
