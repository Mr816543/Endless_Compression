package ECContent;

import ECType.Tool;
import arc.Core;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.Vars;
import mindustry.type.CellLiquid;
import mindustry.type.Item;
import mindustry.type.Liquid;

import static ECType.ECSetting.*;
import static ECType.Tool.*;

public class ECLiquids {
    public static ObjectMap<Liquid, Seq<Liquid>> ECLiquids = new ObjectMap<>();
    public static Seq<Liquid> liquids;

    public static void load() throws IllegalAccessException {
        liquids = Vars.content.liquids().copy();
        for (Liquid liquid : liquids) {
            if (!liquid.isVanilla()) continue;
            if (liquid.isHidden()) continue;
            if (liquid instanceof CellLiquid) continue;
            createCompressionLiquid(liquid);
        }
    }

    public static void createCompressionLiquid(Liquid L) throws IllegalAccessException {

        ECLiquids.put(L,new Seq<>());
        ECLiquids.get(L).add(L);

        for (int i = 1; i <= MAX_LEVEL; i++){
            int finalI = i;


            Liquid ECLiquid = new Liquid("c"+finalI+" "+L.name) {{
                localizedName = finalI + Core.bundle.get("Compression.localizedName") + L.localizedName;
                details = L.details;

                alwaysUnlocked = true;
                setCompressionIcon(L,this,finalI);
            }};

            String[] LI = new String[]{};
            String[] LF = new String[]{"heatCapacity"};
            String[] SI = new String[]{};
            String[] SF = new String[]{"flammability","temperature","viscosity","explosiveness"};

            ObjectMap<String,Float> intV = new ObjectMap<>();
            ObjectMap<String,Float> floatV = new ObjectMap<>();


            //配置
            putAllTo(intV,LI,LINEAR_MULTIPLIER);
            putAllTo(floatV,LF,LINEAR_MULTIPLIER);
            putAllTo(intV,SI,SCALE_MULTIPLIER);
            putAllTo(floatV,SF,SCALE_MULTIPLIER);

            compress(L,ECLiquid,finalI,intV,floatV);

            ECLiquids.get(L).add(ECLiquid);
        }

    }

    public static void setCompressionIcon(Liquid liquid, Liquid comppressLiquid,int num){
        if (liquid.uiIcon!=null){
            comppressLiquid.uiIcon = comppressLiquid.fullIcon = combineRegions(liquid.uiIcon,num);
        }else {
            Core.app.post(() -> setCompressionIcon(liquid,comppressLiquid,num));
        }
    }

}
