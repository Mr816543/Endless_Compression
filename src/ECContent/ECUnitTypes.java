package ECContent;

import ECType.ECItem;
import ECType.ECUnitType;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.type.Item;
import mindustry.type.UnitType;

import static ECConfig.ECSetting.MAX_LEVEL;

public class ECUnitTypes {
    public static ObjectMap<UnitType, Seq<UnitType>> ECUnitTypes = new ObjectMap<>();
    public static Seq<UnitType> unitTypes;

    public static void load() throws IllegalAccessException {
        unitTypes = Vars.content.units().copy();
        for (UnitType unitType : unitTypes) {
            if (!unitType.isVanilla()) continue;
            if (unitType.isHidden()) continue;
            compressUnitType(unitType);
        }
    }

    public static void compressUnitType(UnitType root) throws IllegalAccessException {
        ECUnitTypes.put(root, new Seq<>());
        ECUnitTypes.get(root).add(root);
        for (int i = 1; i <= MAX_LEVEL; i++) {
            ECUnitType child = new ECUnitType(root, i);
            ECUnitTypes.get(root).add(child);
        }
    }


}
