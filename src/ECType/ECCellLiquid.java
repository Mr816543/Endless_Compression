package ECType;

import ECConfig.Config;
import ECConfig.ECData;
import ECConfig.ECSetting;
import ECConfig.ECTool;
import arc.Core;
import arc.math.Mathf;
import mindustry.type.CellLiquid;
import mindustry.type.Liquid;

public class ECCellLiquid extends CellLiquid {

    public CellLiquid root;

    public int level;

    public Config config = new Config().linearConfig("heatCapacity","spreadDamage").scaleConfig("flammability","explosiveness");

    public ECCellLiquid(CellLiquid root,int level) throws IllegalAccessException {
        super("c" + level + "-" + root.name);
        this.root = root;
        this.level = level;
        if (root.temperature > 0.5){
            config.scaleConfig("temperature");
        }else {
            config.addConfigSimple(1/ ECSetting.SCALE_MULTIPLIER,"temperature");
        }




        ECTool.compress(this.root,this,config,level);
        ECTool.setIcon(root,this,level);
        localizedName = level + Core.bundle.get("num-Compression.localizedName") + root.localizedName;
        description = root.description;
        details = root.details;
        color = ECTool.Color(root.color,level,true);
        viscosity = 0.9f - (1-root.viscosity)/ Mathf.pow(9,level);
        ECData.register(root,this,level);
    }
}
