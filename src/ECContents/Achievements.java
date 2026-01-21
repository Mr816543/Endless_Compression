package ECContents;

import ECType.Achievement;
import ECType.ECBlockTypes.Crafter.ECBeamDrill;
import ECType.ECBlockTypes.Crafter.ECBurstDrill;
import ECType.ECBlockTypes.Crafter.ECDrill;
import ECType.ECBlockTypes.Crafter.ECWallCrafter;
import ECType.ECBlockTypes.Defend.ECCoreBlock;
import ECType.ECBlockTypes.Liquid.ECPump;
import ECType.ECBlockTypes.Power.ECNuclearReactor;
import ECType.ECBlockTypes.Power.ECSolarGenerator;
import ECType.ECItem;
import ECType.ECUnitType;
import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.scene.ui.Label;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Align;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.content.UnitTypes;
import mindustry.ctype.UnlockableContent;
import mindustry.game.EventType;
import mindustry.game.Gamemode;
import mindustry.type.UnitType;
import mindustry.world.Block;
import mindustry.world.meta.Stat;

import static mindustry.game.EventType.UnlockEvent;
import static mindustry.game.EventType.WorldLoadEvent;

public class Achievements {

    public static Seq<Achievement> achievements = new Seq<>();

    public static Achievement
            startGame, c1, c2, c3, c4, c5, c6, c7, c8, c9,
            drillStrengthen,beamDrillStrengthen,burstDrillStrengthen,wallCrafterStrengthen,
            pumpStrengthen,
            explosiveArt, explosiveArtBig, explosiveArtMax, cleanPower, killer, compressCore;


    public static int drillMinLevel = 4;


    public static void load() {
        startGame = new Achievement("startGame") {{
            root = Items.copper;
            setEvent(WorldLoadEvent.class, e -> {
                unlock();
            });
        }};
        c1 = new Achievement("c1") {{
            root = startGame;
            iconFrom = Items.copper;
            index = 1;
            setEvent(UnlockEvent.class, e -> {
                if (e.content instanceof ECItem item && item.level >= 1) {
                    unlock();
                }
            });
        }};
        c2 = new Achievement("c2") {{
            root = c1;
            iconFrom = Items.copper;
            index = 2;
            setEvent(UnlockEvent.class, e -> {
                if (e.content instanceof ECItem item && item.level >= 2) {
                    unlock();
                }
            });
        }};
        c3 = new Achievement("c3") {{
            root = c2;
            iconFrom = Items.copper;
            index = 3;
            setEvent(UnlockEvent.class, e -> {
                if (e.content instanceof ECItem item && item.level >= 3) {
                    unlock();
                }
            });
        }};
        c4 = new Achievement("c4") {{
            root = c3;
            iconFrom = Items.copper;
            index = 4;
            setEvent(UnlockEvent.class, e -> {
                if (e.content instanceof ECItem item && item.level >= 4) {
                    unlock();
                }
            });
        }};
        c5 = new Achievement("c5") {{
            root = c4;
            iconFrom = Items.copper;
            index = 5;
            setEvent(UnlockEvent.class, e -> {
                if (e.content instanceof ECItem item && item.level >= 5) {
                    unlock();
                }
            });
        }};
        c6 = new Achievement("c6") {{
            root = c5;
            iconFrom = Items.copper;
            index = 6;
            setEvent(UnlockEvent.class, e -> {
                if (e.content instanceof ECItem item && item.level >= 6) {
                    unlock();
                }
            });
        }};
        c7 = new Achievement("c7") {{
            root = c6;
            iconFrom = Items.copper;
            index = 7;
            setEvent(UnlockEvent.class, e -> {
                if (e.content instanceof ECItem item && item.level >= 7) {
                    unlock();
                }
            });
        }};
        c8 = new Achievement("c8") {{
            root = c7;
            iconFrom = Items.copper;
            index = 8;
            setEvent(UnlockEvent.class, e -> {
                if (e.content instanceof ECItem item && item.level >= 8) {
                    unlock();
                }
            });
        }};
        c9 = new Achievement("c9") {{
            root = c8;
            iconFrom = Items.copper;
            index = 9;
            setEvent(UnlockEvent.class, e -> {
                if (e.content instanceof ECItem item && item.level >= 9) {
                    unlock();
                }
            });
        }};
        drillStrengthen = new Achievement("drillStrengthen") {
            {
                root = c4;
                iconFrom = Blocks.mechanicalDrill;
                index = 0;
                hasAward = true;
                setEvent(UnlockEvent.class, e -> {
                    if (e.content instanceof ECDrill drill && drill.level >= drillMinLevel) {
                        boolean unlocked = true;
                        for (Block block : Vars.content.blocks()) {
                            if (block instanceof ECDrill d && d.level <= drillMinLevel && d.root.isVanilla()) {
                                if (d.locked()) {
                                    unlocked = false;
                                    break;
                                }
                            }
                        }
                        if (unlocked) {
                            unlock();
                        }
                    }
                });
                setEvent(WorldLoadEvent.class, e -> {
                    boolean unlocked = true;
                    for (Block block : Vars.content.blocks()) {
                        if (block instanceof ECDrill d && d.level <= drillMinLevel && d.root.isVanilla()) {
                            if (d.locked()) {
                                unlocked = false;
                                break;
                            }
                        }
                    }
                    if (unlocked) {
                        unlock();
                    }
                });
            }

            @Override
            public boolean working(UnlockableContent content) {
                return super.working(content) &&
                        content instanceof ECDrill drill && drill.level >= 2;
            }
        };
        beamDrillStrengthen = new Achievement("beamDrillStrengthen") {
            {
                root = drillStrengthen;
                iconFrom = Blocks.plasmaBore;
                index = 0;
                hasAward = true;
                setEvent(UnlockEvent.class, e -> {
                    if (e.content instanceof ECBeamDrill drill && drill.level >= drillMinLevel) {
                        boolean unlocked = true;
                        for (Block block : Vars.content.blocks()) {
                            if (block instanceof ECBeamDrill d && d.level <= drillMinLevel && d.root.isVanilla()) {
                                if (d.locked()) {
                                    unlocked = false;
                                    break;
                                }
                            }
                        }
                        if (unlocked) {
                            unlock();
                        }
                    }
                });
                setEvent(WorldLoadEvent.class, e -> {
                    boolean unlocked = true;
                    for (Block block : Vars.content.blocks()) {
                        if (block instanceof ECBeamDrill d && d.level <= drillMinLevel && d.root.isVanilla()) {
                            if (d.locked()) {
                                unlocked = false;
                                break;
                            }
                        }
                    }
                    if (unlocked) {
                        unlock();
                    }
                });
            }

            @Override
            public boolean working(UnlockableContent content) {
                return super.working(content) &&
                        content instanceof ECBeamDrill drill && drill.level >= 2;
            }
        };
        burstDrillStrengthen = new Achievement("burstDrillStrengthen") {
            {
                root = drillStrengthen;
                iconFrom = Blocks.plasmaBore;
                index = 0;
                hasAward = true;
                setEvent(UnlockEvent.class, e -> {
                    if (e.content instanceof ECBurstDrill drill && drill.level >= drillMinLevel) {
                        boolean unlocked = true;
                        for (Block block : Vars.content.blocks()) {
                            if (block instanceof ECBurstDrill d && d.level <= drillMinLevel && d.root.isVanilla()) {
                                if (d.locked()) {
                                    unlocked = false;
                                    break;
                                }
                            }
                        }
                        if (unlocked) {
                            unlock();
                        }
                    }
                });
                setEvent(WorldLoadEvent.class, e -> {
                    boolean unlocked = true;
                    for (Block block : Vars.content.blocks()) {
                        if (block instanceof ECBurstDrill d && d.level <= drillMinLevel && d.root.isVanilla()) {
                            if (d.locked()) {
                                unlocked = false;
                                break;
                            }
                        }
                    }
                    if (unlocked) {
                        unlock();
                    }
                });
            }

            @Override
            public boolean working(UnlockableContent content) {
                return super.working(content) &&
                        content instanceof ECBurstDrill drill && drill.level >= 2;
            }
        };
        wallCrafterStrengthen = new Achievement("wallCrafterStrengthen") {
            {
                root = drillStrengthen;
                iconFrom = Blocks.largeCliffCrusher;
                index = 0;
                hasAward = true;
                setEvent(UnlockEvent.class, e -> {
                    if (e.content instanceof ECWallCrafter drill && drill.level >= drillMinLevel) {
                        boolean unlocked = true;
                        for (Block block : Vars.content.blocks()) {
                            if (block instanceof ECWallCrafter d && d.level <= drillMinLevel && d.root.isVanilla()) {
                                if (d.locked()) {
                                    unlocked = false;
                                    break;
                                }
                            }
                        }
                        if (unlocked) {
                            unlock();
                        }
                    }
                });
                setEvent(WorldLoadEvent.class, e -> {
                    boolean unlocked = true;
                    for (Block block : Vars.content.blocks()) {
                        if (block instanceof ECWallCrafter d && d.level <= drillMinLevel && d.root.isVanilla()) {
                            if (d.locked()) {
                                unlocked = false;
                                break;
                            }
                        }
                    }
                    if (unlocked) {
                        unlock();
                    }
                });
            }

            @Override
            public boolean working(UnlockableContent content) {
                return super.working(content) &&
                        content instanceof ECWallCrafter drill && drill.level >= 2;
            }
        };




        pumpStrengthen = new Achievement("pumpStrengthen") {

            {
                root = c4;
                iconFrom = Blocks.mechanicalPump;
                index = 0;
                hasAward = true;
                setEvent(UnlockEvent.class, e -> {
                    if (e.content instanceof ECPump pump && pump.level >= drillMinLevel) {
                        boolean unlocked = true;
                        for (Block block : Vars.content.blocks()) {
                            if (block instanceof ECPump p && p.level <= drillMinLevel && p.root.isVanilla()) {
                                if (p.locked()) {
                                    unlocked = false;
                                    break;
                                }
                            }
                        }
                        if (unlocked) {
                            unlock();
                        }
                    }
                });
                setEvent(WorldLoadEvent.class, e -> {
                    boolean unlocked = true;
                    for (Block block : Vars.content.blocks()) {
                        if (block instanceof ECPump p && p.level <= drillMinLevel && p.root.isVanilla()) {
                            if (p.locked()) {
                                unlocked = false;
                                break;
                            }
                        }
                    }
                    if (unlocked) {
                        unlock();
                    }
                });

            }

            @Override
            public boolean working(UnlockableContent content) {
                return super.working(content) &&
                        content instanceof ECPump pump && pump.level >= 2;
            }
        };
        explosiveArt = new Achievement("explosiveArt") {{
            root = c2;
            iconFrom = Blocks.thoriumReactor;
            index = 2;
            setEvent(EventType.BlockDestroyEvent.class, e -> {
                if (e.tile.block() instanceof ECNuclearReactor reactor && reactor.level >= 2 && reactor.root == Blocks.thoriumReactor) {
                    unlock();
                }
            });
        }};
        explosiveArtBig = new Achievement("explosiveArtBig") {{
            root = explosiveArt;
            iconFrom = Blocks.thoriumReactor;
            index = 5;
            setEvent(EventType.BlockDestroyEvent.class, e -> {
                if (e.tile.block() instanceof ECNuclearReactor reactor && reactor.level >= 5 && reactor.root == Blocks.thoriumReactor) {
                    unlock();
                }
            });
        }};
        explosiveArtMax = new Achievement("explosiveArtMax") {{
            root = explosiveArt;
            iconFrom = Blocks.thoriumReactor;
            index = 9;
            setEvent(EventType.BlockDestroyEvent.class, e -> {
                if (e.tile.block() instanceof ECNuclearReactor reactor && reactor.level >= 9 && reactor.root == Blocks.thoriumReactor) {
                    unlock();
                }
            });
        }};
        cleanPower = new Achievement("cleanPower") {{
            root = c1;
            iconFrom = Blocks.solarPanel;
            index = 1;
            setEvent(EventType.BlockBuildEndEvent.class, e -> {
                if (e.tile.block() instanceof ECSolarGenerator solar) {
                    unlock();
                }
            });
        }};
        killer = new Achievement("killer") {
            {
                root = startGame;
                iconFrom = UnitTypes.crawler;
                index = 0;
                setEvent(EventType.UnitDestroyEvent.class, e -> {
                    if (e.unit.team != Vars.player.team() && (Vars.state.rules.mode() == Gamemode.survival) && e.unit.type instanceof ECUnitType) {
                        unlock();
                    }
                });
            }

            @Override
            public void init() {
                super.init();
                Events.on(EventType.UnitDestroyEvent.class, e -> {
                    if (e.unit.team != Vars.player.team()) {
                        if (Vars.state.rules.mode() == Gamemode.survival) {
                            UnitType unit = e.unit.type();
                            Core.settings.put(unit.name, Core.settings.getInt(unit.name, 0) + 1);
                        }
                    }
                });
            }

            @Override
            public void setStats() {
                super.setStats();
                if (unlockedNow()) stats.add(new Stat("kill"), table -> {
                    StringBuilder s = new StringBuilder("\n");
                    for (UnitType unit : Vars.content.units()) {
                        if (unit instanceof ECUnitType ecUnit) {
                            int amount = Core.settings.getInt(ecUnit.name, 0);
                            if (amount > 0) {
                                s.append(ecUnit.localizedName).append(":").append(amount).append("\n");
                            }
                        }
                    }
                    // 创建支持换行的标签
                    Label label = new Label(s.toString());
                    label.setWrap(true); // 启用自动换行
                    label.setAlignment(Align.topLeft); // 左上对齐
                    label.setColor(Color.lightGray);
                    label.setFontScale(1f);

                    // 创建包含标签的表格（用于控制内边距）
                    Table textTable = new Table();
                    float textPad = 15f; // 文本内边距
                    textTable.add(label).grow().pad(textPad).top().left();

                    // 滚动面板
                    ScrollPane pane = new ScrollPane(textTable);
                    pane.setFadeScrollBars(false);
                    pane.setScrollingDisabled(true, false); // 禁用水平滚动

                    table.add(pane).grow();

                });
            }
        };
        compressCore = new Achievement("compressCore") {{
            root = killer;
            iconFrom = Blocks.coreShard;
            index = 1;
            hasAward = true;
            setEvent(EventType.UnlockEvent.class, e -> {
                if (e.content instanceof ECCoreBlock) {
                    unlock();
                }
            });
        }};
    }

    public static void clearAllAchievements() {
        for (Achievement a : achievements) {
            a.clearUnlock();
        }
    }
}
