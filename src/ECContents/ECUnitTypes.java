package ECContents;

import ECConfig.ECData;
import ECConfig.ECSetting;
import ECConfig.ECTool;
import ECType.ECBlockTypes.Item.ECCompressCrafter;
import ECType.ECBlockTypes.Item.ECMultipleCompressCrafter;
import ECType.ECUnitType;
import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.Texture;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Items;
import mindustry.content.UnitTypes;
import mindustry.core.UI;
import mindustry.game.EventType;
import mindustry.game.SpawnGroup;
import mindustry.gen.Groups;
import mindustry.gen.Icon;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.UnitType;

import static ECConfig.ECSetting.MAX_LEVEL;
import static ECConfig.ECTool.numberPixmap;
import static ECConfig.ECTool.print;

public class ECUnitTypes {
    public static Seq<UnitType> unitTypes;
    public static Seq<SpawnGroup> spawns;
    public static Seq<SpawnGroup> newSpawns;
    public static int maxLevel;
    public static TextureRegion[][] levelTextureRegions;
    public static int difficulty;

    public static void load() throws IllegalAccessException {
        unitTypes = Vars.content.units().copy();
        for (UnitType root : unitTypes) {
            if (root.isModded()) continue;
            if (root.isHidden()) continue;
            compressUnitType(root);
        }

        difficulty = Core.settings.getInt("ECDifficulty",2);

        Events.on(EventType.WorldLoadEvent.class,e->{
            spawns = Vars.state.rules.spawns.copy();
            newSpawns = compressWaves(spawns);
            if (Core.settings.getInt("ECDifficulty")>0){
                Vars.state.rules.spawns = newSpawns;
            }
        });

        Events.on(EventType.SaveWriteEvent.class,e->{
            Vars.state.rules.spawns = spawns;

            if (difficulty != Core.settings.getInt("ECDifficulty",-1)){
                difficulty = Core.settings.getInt("ECDifficulty");
                newSpawns = compressWaves(spawns);
            }
            Time.run(0f,()->{
                if (Core.settings.getInt("ECDifficulty")>0){
                    Vars.state.rules.spawns = newSpawns;
                }
            });
        });

        Events.on(EventType.WaveEvent.class,e->{
            if (Core.settings.getInt("ECDifficulty")>0){
                int wave = Vars.state.wave-2;
                int nowLevel=0;
                for (int i = 0;i <=MAX_LEVEL;i++){
                    int begin = i == 0 ? 0 : Mathf.pow(2,i+1);
                    int end = Mathf.pow(2,i+2)-1;
                    if (wave>=begin&&wave<end) {
                        nowLevel = i;
                        break;
                    }
                }

                int nextLevelBegin = Mathf.pow(2,nowLevel+1+1);

                int diff =nextLevelBegin - wave;

                if (nowLevel < maxLevel) {
                    if (diff == 1 || diff == 2 || diff == 5 || diff == 10) {
                        Vars.ui.hudfrag.showToast(Icon.warning, (nowLevel + 1) + Core.bundle.format("wave.compressWave" + (diff == 1 ? ".one" : ""), diff));
                    }
                }

            }
        });


        loadLevelTexture();

    }

    public static void loadLevelTexture() {
        levelTextureRegions = new TextureRegion[9][11];
        for (int i = 0 ; i < 9;i++){
            for (int j = 0;j<11;j++){
                levelTextureRegions[i][j] = new TextureRegion(new Texture(numberPixmap[i][j]));
            }
        }
    }

    public static Seq<SpawnGroup> compressWaves(Seq<SpawnGroup> spawns) {
        Seq<SpawnGroup> newSpawns = new Seq<>();
        maxLevel = -2 + difficulty;

        int maxCrafterLevel = 0;
        for (ECCompressCrafter crafter:ECBlocks.ecCompressCrafters){
            if (crafter.level>maxCrafterLevel&&crafter.unlockedNow()) maxCrafterLevel = crafter.level;
        }
        for (ECMultipleCompressCrafter crafter:ECBlocks.ecMultipleCompressCrafters){
            if (crafter.level>maxCrafterLevel&&crafter.unlockedNow()) maxCrafterLevel = crafter.level;
        }
        maxLevel += maxCrafterLevel;
        maxLevel = Math.max(Math.min(maxLevel,9),0);

        for (SpawnGroup group : spawns){
            for (int i = 0 ; i <= MAX_LEVEL;i++){
                SpawnGroup newGroup = group.copy();
                int begin = i == 0 ? 0 : Mathf.pow(2,i+1);
                int end = Mathf.pow(2,i+2)-1;
                if (begin > group.end || end < group.begin || !ECData.hasECContent(group.type) ) continue;
                newGroup.begin = Math.max(begin,group.begin);
                newGroup.end = Math.min(end,group.end);

                newGroup.type = ECData.get(group.type, Math.min(i, maxLevel));


                newGroup.shields = group.shields * Mathf.pow(ECSetting.LINEAR_MULTIPLIER, Math.min(i, maxLevel-1));
                if (group.payloads!=null){
                    newGroup.payloads  = new Seq<>();
                    for (UnitType unitType:group.payloads){
                        newGroup.payloads.add(ECData.get(unitType,i));
                    }
                }
                if (group.items!=null){
                    newGroup.items = new ItemStack(ECData.get(group.items.item, Math.min(i, maxLevel)),group.items.amount);
                }
                newSpawns.add(newGroup);
            }
        }
        return newSpawns;
    }

    public static void init(){

        Events.run(EventType.Trigger.draw,()->{
            if (Core.camera == null) return;
            Groups.unit.each(unit -> {
                if (unit.dead || !unit.isValid())return;
                if (unit.type instanceof ECUnitType) {
                    drawLevel(unit);
                }
            });
        });



    }

    public static void drawLevel(Unit unit) {
        if (unit.type instanceof ECUnitType ecUnitType){
            float x = unit.x;
            float y = unit.y;
            int uiSize = Math.min(Math.max((int) (unit.type.hitSize/8) ,0),8);
            int level = ecUnitType.level;
            TextureRegion levelTextureRegion = levelTextureRegions[uiSize][level];
            float z = Draw.z();
            Draw.z(Layer.flyingUnit+0.1f);
            Draw.rect(levelTextureRegion,x,y,unit.type.hitSize*1.5f,unit.type.hitSize*1.5f);
            Draw.z(z);
            Draw.reset();
        }
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
