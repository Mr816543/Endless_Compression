package ECType;

import ECConfig.Config;
import ECConfig.ECData;
import ECConfig.ECSetting;
import ECConfig.ECTool;
import arc.Core;
import mindustry.type.Liquid;

public class ECLiquid extends Liquid {

    public Liquid root;

    public int level;

    public Config config = new Config().linearConfig("heatCapacity").scaleConfig("flammability","viscosity","explosiveness");

    public ECLiquid(Liquid root,int level) throws IllegalAccessException {
        super("c" + level + "-" + root.name);
        this.root = root;
        this.level = level;
        ECTool.compress(this.root,this,config,level);
        ECTool.setIcon(root,this,level);
        if (root.temperature > 0.5){
            config.scaleConfig("temperature");
        }else {
            config.addConfigSimple(1/ECSetting.SCALE_MULTIPLIER,"temperature");
        }
        localizedName = level + Core.bundle.get("num-Compression.localizedName") + root.localizedName;
        description = root.description;
        details = root.details;
        color = ECTool.Color(root.color,level,true);
        ECData.register(root,this,level);
    }
}
