package ECContents;

import ECConfig.ECData;
import ECType.ECUnitType;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.type.UnitType;

import static ECConfig.ECSetting.MAX_LEVEL;

public class ECUnitTypes {
    public static Seq<UnitType> unitTypes;

    public static void load() throws IllegalAccessException {
        unitTypes = Vars.content.units().copy();
        for (UnitType root : unitTypes) {
            if (root.isModded()) continue;
            if (root.isHidden()) continue;
            compressUnitType(root);
        }
    }

    public static void compressUnitType(UnitType root) throws IllegalAccessException {
        for (int i = 1; i <= MAX_LEVEL; i++) {
            new ECUnitType(root, i);
        }
    }


}
