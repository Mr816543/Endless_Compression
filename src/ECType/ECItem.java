package ECType;

import ECConfig.Config;
import ECConfig.ECSetting;
import ECConfig.ECTool;
import arc.Core;
import mindustry.type.Item;
import mindustry.world.meta.Stat;


public class ECItem extends Item {

    public Item root;

    public int level;

    public static Config config = new Config().linearConfig("explosiveness","flammability","radioactivity","charge","hardness"
    ).scaleConfig("cost","healthScaling");

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
    }

    @Override
    public void setStats() {
        super.setStats();

        this.stats.addPercent(new Stat("hardness"), this.hardness);
    }
}
