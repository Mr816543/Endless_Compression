package ECType;

import ECConfig.Config;
import ECConfig.ECSetting;
import ECConfig.ECTool;
import arc.Core;
import mindustry.type.Liquid;

public class ECLiquid extends Liquid {

    public Liquid root;

    public int level;

    public static Config config = new Config().linearConfig("heatCapacity").scaleConfig("flammability","temperature","viscosity","explosiveness");

    public ECLiquid(Liquid root,int level) throws IllegalAccessException {
        super("c" + level + "-" + root.name);
        this.root = root;
        this.level = level;
        ECTool.compress(this.root,this,config,level);
        ECTool.setIcon(root,this,level);
        localizedName = level + Core.bundle.get("num-Compression.localizedName") + root.localizedName;
        description = root.description;
        details = root.details;
        color = ECTool.Color(root.color,level,true);

    }
}
