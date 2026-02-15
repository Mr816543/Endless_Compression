package ECContents;

import ECConfig.EC;
import ECConfig.ECData;
import ECConfig.ECSetting;
import ECType.ECUnitType;
import arc.Core;
import arc.Events;
import arc.graphics.Texture;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.game.Gamemode;
import mindustry.game.SpawnGroup;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.gen.Icon;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.type.ItemStack;
import mindustry.type.UnitType;
import mindustry.world.Block;
import mindustry.world.blocks.storage.CoreBlock;

import static ECConfig.ECSetting.MAX_LEVEL;
import static ECConfig.ECTool.numberPixmap;
import static ECContents.ECBlocks.unlockedLevel;

public class ECUnitTypes {
    public static Seq<UnitType> unitTypes;
    public static Seq<SpawnGroup> spawns;
    public static Seq<SpawnGroup> newSpawns;
    public static int maxLevel;
    public static TextureRegion[][] levelTextureRegions;
    public static int difficulty;
    public static int waveLevel;
    public static int n = 0;

    public static void load() throws IllegalAccessException {
        unitTypes = Vars.content.units().copy();
        for (UnitType root : unitTypes) {
            if (root.isModded()) continue;
            if (root.isHidden()) continue;
            compressUnitType(root);
        }

        difficulty = Core.settings.getInt("ECDifficulty", 2);

        Events.on(EventType.WorldLoadEndEvent.class, e -> {
            spawns = Vars.state.rules.spawns.copy();
            newSpawns = compressWaves(spawns);
            if (Core.settings.getInt("ECDifficulty") > 0) {
                Vars.state.rules.spawns = newSpawns;
            }
        });

        Events.on(EventType.SaveWriteEvent.class, e -> {
            Vars.state.rules.spawns = spawns;

            if (waveLevel != unlockedLevel) {
                newSpawns = compressWaves(spawns);
            }

            if (difficulty != Core.settings.getInt("ECDifficulty", -1)) {
                difficulty = Core.settings.getInt("ECDifficulty");
                newSpawns = compressWaves(spawns);
            }
            Time.run(0f, () -> {
                if (Core.settings.getInt("ECDifficulty") > 0) {
                    Vars.state.rules.spawns = newSpawns;
                }
            });
        });

        Events.on(EventType.WaveEvent.class, e -> {
            if (Core.settings.getInt("ECDifficulty") > 0) {
                int wave = Vars.state.wave - 2;
                int nowLevel = 0;
                for (int i = 0; i <= MAX_LEVEL; i++) {
                    int begin = i == 0 ? 0 : Mathf.pow(2, i + 1);
                    int end = Mathf.pow(2, i + 2) - 1;
                    if (wave >= begin && wave < end) {
                        nowLevel = i;
                        break;
                    }
                }

                int nextLevelBegin = Mathf.pow(2, nowLevel + 1 + 1);

                int diff = nextLevelBegin - wave;

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
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 11; j++) {
                levelTextureRegions[i][j] = new TextureRegion(new Texture(numberPixmap[i][j]));
            }
        }
    }

    public static Seq<SpawnGroup> compressWaves(Seq<SpawnGroup> spawns) {
        Seq<SpawnGroup> newSpawns = new Seq<>();
        maxLevel = -2 + difficulty;

        int maxCrafterLevel = unlockedLevel;
        waveLevel = unlockedLevel;
        maxLevel += maxCrafterLevel;
        maxLevel = Math.max(Math.min(maxLevel, 9), 0);

        for (SpawnGroup group : spawns) {
            for (int i = 0; i <= MAX_LEVEL; i++) {
                SpawnGroup newGroup = group.copy();
                int begin = i == 0 ? 0 : Mathf.pow(2, i + 1);
                int end = Mathf.pow(2, i + 2) - 1;
                if (begin > group.end || end < group.begin || !ECData.hasECContent(group.type)) continue;
                newGroup.begin = Math.max(begin, group.begin);
                newGroup.end = Math.min(end, group.end);

                newGroup.type = ECData.get(group.type, Math.min(i, maxLevel));


                newGroup.shields = group.shields * Mathf.pow(ECSetting.LINEAR_MULTIPLIER, Math.min(i, maxLevel - 1));
                if (group.payloads != null) {
                    newGroup.payloads = new Seq<>();
                    for (UnitType unitType : group.payloads) {
                        newGroup.payloads.add(ECData.get(unitType, i));
                    }
                }
                if (group.items != null) {
                    newGroup.items = new ItemStack(ECData.get(group.items.item, Math.min(i, maxLevel)), group.items.amount);
                }
                newSpawns.add(newGroup);
            }
        }
        return newSpawns;
    }

    public static void init() {

        Events.run(EventType.Trigger.draw, () -> {
            if (Core.camera == null) return;
            Groups.unit.each(unit -> {
                if (unit.dead || !unit.isValid()) return;
                if (unit.type instanceof ECUnitType) {
                    drawLevel(unit);
                }
            });
        });

        Events.on(EventType.WorldLoadEvent.class, e -> {
            if (Core.settings.getBool("compressedEnemyBuilding")) {
                if (Vars.state.rules.mode() == Gamemode.pvp) return;
                if (Vars.state.rules.waveTeam.core() instanceof EC) return;
                int playerLevel = Vars.player.team().core() instanceof EC ecC ? ecC.getLevel() : 0;

                updateEnemyCore(playerLevel);
                updateEnemyBuild(playerLevel);
            } else {
                downEnemyCore();
                downEnemyBuild();
            }


        });


    }

    private static void updateEnemyCore(int level) {
        Team et = Vars.state.rules.waveTeam;
        Seq<CoreBlock.CoreBuild> cores = et.cores().copy();
        for (CoreBlock.CoreBuild c : cores) {
            Block r = c.block instanceof EC ecB ? (Block) ecB.getRoot() : c.block;
            Block up = ECData.get(r, level);
            if (r == up) continue;
            c.tile.setBlock(up, et);
            Groups.unit.each(unit -> {
                if (unit.dead) return;
                if (unit.team != et) return;
                if (unit.type == ((CoreBlock) c.block).unitType) {
                    unit.killed();
                }
            });
        }
    }

    private static void updateEnemyBuild(int level) {
        Team et = Vars.state.rules.waveTeam;
        Seq<Building> builds = new Seq<>();
        Vars.world.tiles.eachTile(tile -> {
            if (tile.team() == et) builds.add(tile.build);
        });
        for (Building c : builds) {

            if (c instanceof CoreBlock.CoreBuild) continue;

            Block r = c.block instanceof EC ecB ? (Block) ecB.getRoot() : c.block;
            Block up = ECData.get(r, level);
            if (r == up) continue;
            c.tile.setBlock(up, et);
        }
    }

    private static void downEnemyCore() {
        Team et = Vars.state.rules.waveTeam;
        Seq<CoreBlock.CoreBuild> cores = et.cores().copy();
        for (CoreBlock.CoreBuild c : cores) {
            Block r = c.block instanceof EC ecB ? (Block) ecB.getRoot() : c.block;
            if (r == c.block) continue;
            c.tile.setBlock(r, et);
            Groups.unit.each(unit -> {
                if (unit.dead) return;
                if (unit.team != et) return;
                if (unit.type == ((CoreBlock) c.block).unitType) {
                    unit.killed();
                }
            });
        }
    }

    private static void downEnemyBuild() {
        Team et = Vars.state.rules.waveTeam;
        Seq<Building> builds = new Seq<>();
        Vars.world.tiles.eachTile(tile -> {
            if (tile.team() == et) builds.add(tile.build);
        });
        for (Building c : builds) {

            if (c instanceof CoreBlock.CoreBuild) continue;

            Block r = c.block instanceof EC ecB ? (Block) ecB.getRoot() : c.block;
            if (r == c.block) continue;
            c.tile.setBlock(r, et);
        }
    }

    public static void drawLevel(Unit unit) {
        if (unit.type instanceof ECUnitType ecUnitType) {
            float x = unit.x;
            float y = unit.y;
            int uiSize = Math.min(Math.max((int) (unit.type.hitSize / 8), 0), 8);
            int level = ecUnitType.level;
            TextureRegion levelTextureRegion = levelTextureRegions[uiSize][level];
            float z = Draw.z();
            Draw.z(Layer.flyingUnit + 0.1f);
            Draw.rect(levelTextureRegion, x, y, unit.type.hitSize * 1.5f, unit.type.hitSize * 1.5f);
            Draw.z(z);
            Draw.reset();
        }
    }


    public static String toString(Seq<SpawnGroup> spawns) {
        StringBuilder s = new StringBuilder();
        for (SpawnGroup group : spawns) {
            String spawn = "[" + group.type.localizedName + ":" + group.begin + "-" + group.end + "," + group.unitAmount + "/" + group.max + "]";
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
