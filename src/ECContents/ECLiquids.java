package ECContents;

import ECConfig.ECData;
import ECType.ECCellLiquid;
import ECType.ECLiquid;
import arc.Core;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.type.CellLiquid;
import mindustry.type.Liquid;

import static ECConfig.ECSetting.*;
import static ECConfig.ECTool.mergeRegions;

public class ECLiquids {
    public static Seq<Liquid> liquids;

    public static void load() throws IllegalAccessException {
        liquids = Vars.content.liquids().copy();
        for (Liquid liquid : liquids) {
            if (liquid.isModded()) continue;
            if (liquid.isHidden()) continue;
            if (liquid instanceof CellLiquid cellLiquid) {
                compressCellLiquid(cellLiquid);
                continue;
            }
            compressLiquid(liquid);
        }
    }

    public static void compressLiquid(Liquid root) throws IllegalAccessException {

        for (int i = 1; i <= MAX_LEVEL; i++) {
            ECLiquid child = new ECLiquid(root, i);
        }
    }

    public static void compressCellLiquid(CellLiquid root) throws IllegalAccessException {

        for (int i = 1; i <= MAX_LEVEL; i++) {
            ECCellLiquid child = new ECCellLiquid(root, i);
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
