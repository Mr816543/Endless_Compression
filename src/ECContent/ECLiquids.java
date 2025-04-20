package ECContent;

import ECType.ECLiquid;
import arc.Core;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.type.CellLiquid;
import mindustry.type.Liquid;

import static ECConfig.ECSetting.*;
import static ECConfig.ECTool.mergeRegions;

public class ECLiquids {
    public static ObjectMap<Liquid, Seq<Liquid>> ECLiquids = new ObjectMap<>();
    public static Seq<Liquid> liquids;

    public static void load() throws IllegalAccessException {
        liquids = Vars.content.liquids().copy();
        for (Liquid liquid : liquids) {
            if (!liquid.isVanilla()) continue;
            if (liquid.isHidden()) continue;
            if (liquid instanceof CellLiquid) continue;
            compressLiquid(liquid);
        }
    }

    public static void compressLiquid(Liquid root) throws IllegalAccessException {
        ECLiquids.put(root,new Seq<>());
        ECLiquids.get(root).add(root);

        for (int i = 1; i <= MAX_LEVEL; i++) {
            ECLiquid child = new ECLiquid(root, i);
            ECLiquids.get(root).add(child);
        }
    }

    public static void setCompressionIcon(Liquid liquid, Liquid comppressLiquid,int num){
        if (liquid.uiIcon!=null){
            comppressLiquid.uiIcon = comppressLiquid.fullIcon = mergeRegions(liquid.uiIcon,num);
        }else {
            Core.app.post(() -> setCompressionIcon(liquid,comppressLiquid,num));
        }
    }

}
