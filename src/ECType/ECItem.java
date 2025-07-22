package ECType;

import ECConfig.Config;
import ECConfig.ECData;
import ECConfig.ECSetting;
import ECConfig.ECTool;
import arc.Core;
import mindustry.type.Item;
import mindustry.world.meta.Stat;


public class ECItem extends Item {

    public Item root;

    public int level;

    public static Config config = new Config().linearConfig("cost","radioactivity","charge","hardness"
    ).scaleConfig("healthScaling").addConfigSimple(9f,"explosiveness","flammability");

    public ECItem(Item root, int level) throws IllegalAccessException {
        super("c" + level + "-" + root.name);
        this.root = root;
        this.level = level;
        ECTool.compress(this.root,this,config,level);
        ECTool.setIcon(root,this,level);
        localizedName = level + Core.bundle.get("num-Compression.localizedName") + root.localizedName;
        description = root.description;
        details = root.details;
        color = ECTool.Color(root.color,level,true);
        ECData.register(root,this,level);
    }


    @Override
    public void setStats() {
        super.setStats();

        stats.add(new Stat("hardness"), hardness);
        stats.add(new Stat("cost"), cost);
        stats.addPercent(new Stat("healthscaling"), healthScaling);
    }
}
