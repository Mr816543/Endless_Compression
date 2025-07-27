package ECContents;

import ECConfig.ECData;
import ECConfig.ECTool;
import ECType.ECUnitType;
import arc.Core;
import arc.Events;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.game.SpawnGroup;
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

        Events.on(EventType.WorldLoadEvent.class,e->{
            //ECTool.print("WorldLoadEvent");
            Seq<SpawnGroup> spawns = Vars.state.rules.spawns.copy();
            Seq<SpawnGroup> newSpawns = new Seq<>();
            for (SpawnGroup spawn : spawns){
                newSpawns.addAll(compressWaves(spawn));
            }
            Vars.state.rules.spawns = newSpawns;
        });


    }

    public static Seq<SpawnGroup> compressWaves(SpawnGroup root){
        StringBuilder out = new StringBuilder();
        Seq<SpawnGroup> spawnGroups = new Seq<>();

        if (!Core.settings.getBool("Compress-Waves")||root.max < 5 || (root.end - root.begin + root.spawn) < 5 || !ECData.hasECContent(root.type)) {
            spawnGroups.add(root.copy());
            out.append(root).append("\n no change");
            //ECTool.print(out.toString());
            return spawnGroups;
        }

        out.append(root).append("\n to \n");
        for (int i = 0;i<=9;i++){
            SpawnGroup ecSpawnGroup = root.copy();
            int begin = Mathf.pow(2,1+i);
            int end = begin * 2;
            if (begin > ecSpawnGroup.end || end < ecSpawnGroup.begin) continue;
            int spawn = Math.max(ecSpawnGroup.spawn + (begin - ecSpawnGroup.begin) - 5,1);

            ecSpawnGroup.begin = Math.max(begin, ecSpawnGroup.begin);
            if (i==0) ecSpawnGroup.begin = root.begin;
            ecSpawnGroup.end = Math.min(end,ecSpawnGroup.end);
            ecSpawnGroup.spawn = spawn;
            ecSpawnGroup.type = ECData.get(ecSpawnGroup.type,i);

            spawnGroups.add(ecSpawnGroup);
            out.append(ecSpawnGroup).append("\n");

        }
        //ECTool.print(out.toString());

        return spawnGroups;






    }




    public static void compressUnitType(UnitType root) throws IllegalAccessException {
        for (int i = 1; i <= MAX_LEVEL; i++) {
            new ECUnitType(root, i);
        }
    }


}
