package ECContents;

import ECConfig.ECData;
import ECConfig.ECSetting;
import ECConfig.ECTool;
import ECType.ECUnitType;
import arc.Core;
import arc.Events;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Items;
import mindustry.game.EventType;
import mindustry.game.SpawnGroup;
import mindustry.type.ItemStack;
import mindustry.type.UnitType;

import static ECConfig.ECSetting.MAX_LEVEL;

public class ECUnitTypes {
    public static Seq<UnitType> unitTypes;
    public static Seq<SpawnGroup> spawns;
    public static Seq<SpawnGroup> newSpawns;

    public static void load() throws IllegalAccessException {
        unitTypes = Vars.content.units().copy();
        for (UnitType root : unitTypes) {
            if (root.isModded()) continue;
            if (root.isHidden()) continue;
            compressUnitType(root);
        }


        Events.on(EventType.WorldLoadEvent.class,e->{
            spawns = Vars.state.rules.spawns.copy();
            newSpawns = compressWaves(spawns);
            if (Core.settings.getBool("Compress-Waves")){
                Vars.state.rules.spawns = newSpawns;
            }
        });

        Events.on(EventType.SaveWriteEvent.class,e->{
            Vars.state.rules.spawns = spawns;
            Time.run(0f,()->{
                if (Core.settings.getBool("Compress-Waves")){
                    Vars.state.rules.spawns = newSpawns;
                }
            });
        });


    }

    public static Seq<SpawnGroup> compressWaves(Seq<SpawnGroup> spawns) {
        Seq<SpawnGroup> newSpawns = new Seq<>();
        int maxLevel = 0;
        for (SpawnGroup group : spawns){
            for (int i = 0 ; i <= MAX_LEVEL;i++){
                SpawnGroup newGroup = group.copy();
                int begin = i == 0 ? 0 : Mathf.pow(2,i+1);
                int end = Mathf.pow(2,i+2)-1;
                if (begin > group.end || end < group.begin || !ECData.hasECContent(group.type) ) continue;
                newGroup.begin = Math.max(begin,group.begin);
                newGroup.end = Math.min(end,group.end);
                if (i != 0 && ECData.get(Items.silicon,i-1).unlockedNow()) {
                    newGroup.type = ECData.get(group.type, i);
                    maxLevel = i;
                }else {
                    newGroup.type = ECData.get(group.type, maxLevel);
                }

                newGroup.shields = group.shields * Mathf.pow(ECSetting.LINEAR_MULTIPLIER,i);
                if (group.payloads!=null){
                    newGroup.payloads  = new Seq<>();
                    for (UnitType unitType:group.payloads){
                        newGroup.payloads.add(ECData.get(unitType,i));
                    }
                }
                if (group.items!=null){
                    newGroup.items = new ItemStack(ECData.get(group.items.item,i),group.items.amount);
                }
                newSpawns.add(newGroup);
            }
        }
        return newSpawns;
    }


    public static String toString(Seq<SpawnGroup> spawns){
        StringBuilder s = new StringBuilder();
        for (SpawnGroup group : spawns){
            String spawn = "[" + group.type.localizedName + ":" + group.begin+"-"+group.end+","+group.unitAmount+"/"+group.max+"]";
            s.append(spawn);
        }
        return s.toString();
    }




    public static void compressUnitType(UnitType root) throws IllegalAccessException {
        for (int i = 1; i <= MAX_LEVEL; i++) {
            new ECUnitType(root, i);
        }
    }


}
