package ECContents;

import ECType.Achievement;
import ECType.ECBlockTypes.Crafter.ECDrill;
import ECType.ECBlockTypes.Crafter.ECSeparator;
import ECType.ECBlockTypes.Liquid.ECPump;
import ECType.ECBlockTypes.Power.ECNuclearReactor;
import ECType.ECBlockTypes.Power.ECSolarGenerator;
import ECType.ECItem;
import arc.Core;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.ctype.UnlockableContent;
import mindustry.game.EventType;
import mindustry.world.Block;

import static mindustry.game.EventType.UnlockEvent;
import static mindustry.game.EventType.WorldLoadEvent;

public class Achievements {

    public static Seq<Achievement> achievements = new Seq<>();

    public static Achievement
            startGame, c1 ,c2,c3,c4,c5,c6,c7,c8,c9,drillStrengthen, pumpStrengthen,
            explosiveArt,explosiveArtBig,explosiveArtMax,cleanPower
            ;


    public static int drillMinLevel = 5;


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

        drillStrengthen = new Achievement("drillStrengthen"){
            @Override
            public boolean working(UnlockableContent content){
                return Core.settings.getBool("achievementsWork") && unlockedNow() &&
                        content instanceof ECDrill drill && drill.level>=2;
            }
            {
            root = c5;
            iconFrom = Blocks.mechanicalDrill;
            index = 0;
            setEvent(UnlockEvent.class,e->{
                if (e.content instanceof ECDrill drill && drill.level >= drillMinLevel){
                    boolean unlocked = true;
                    for (Block block: Vars.content.blocks()){
                        if (block instanceof ECDrill d && d.level <= drillMinLevel){
                            if (d.locked()){
                                unlocked = false;
                                break;
                            }
                        }
                    }
                    if (unlocked){
                        unlock();
                    }
                }
            });
            setEvent(WorldLoadEvent.class,e->{
                boolean unlocked = true;
                for (Block block: Vars.content.blocks()){
                    if (block instanceof ECDrill d && d.level <= drillMinLevel){
                        if (d.locked()){
                            unlocked = false;
                            break;
                        }
                    }
                }
                if (unlocked){
                    unlock();
                }
            });
        }};

        pumpStrengthen = new Achievement("pumpStrengthen"){

            @Override
            public boolean working(UnlockableContent content){
                return Core.settings.getBool("achievementsWork") && unlockedNow() &&
                        content instanceof ECPump pump && pump.level>=2;
            }
            {
                root = c5;
                iconFrom = Blocks.mechanicalPump;
                index = 0;

                setEvent(UnlockEvent.class,e->{
                    if (e.content instanceof ECPump pump && pump.level >= drillMinLevel){
                        boolean unlocked = true;
                        for (Block block: Vars.content.blocks()){
                            if (block instanceof ECPump p && p.level <= drillMinLevel){
                                if (p.locked()){
                                    unlocked = false;
                                    break;
                                }
                            }
                        }
                        if (unlocked){
                            unlock();
                        }
                    }
                });
                setEvent(WorldLoadEvent.class,e->{
                    boolean unlocked = true;
                    for (Block block: Vars.content.blocks()){
                        if (block instanceof ECPump p && p.level <= drillMinLevel){
                            if (p.locked()){
                                unlocked = false;
                                break;
                            }
                        }
                    }
                    if (unlocked){
                        unlock();
                    }
                });

            }};

        explosiveArt = new Achievement("explosiveArt"){{
            root = c2;
            iconFrom = Blocks.thoriumReactor;
            index = 2;
            setEvent(EventType.BlockDestroyEvent.class,e->{
                if (e.tile.block() instanceof ECNuclearReactor reactor && reactor.level >= 2 && reactor.root == Blocks.thoriumReactor){
                    unlock();
                }
            });
        }};

        explosiveArtBig = new Achievement("explosiveArtBig"){{
            root = explosiveArt;
            iconFrom = Blocks.thoriumReactor;
            index = 5;
            setEvent(EventType.BlockDestroyEvent.class,e->{
                if (e.tile.block() instanceof ECNuclearReactor reactor && reactor.level >= 5 && reactor.root == Blocks.thoriumReactor){
                    unlock();
                }
            });
        }};

        explosiveArtMax = new Achievement("explosiveArtMax"){{
            root = explosiveArt;
            iconFrom = Blocks.thoriumReactor;
            index = 9;
            setEvent(EventType.BlockDestroyEvent.class,e->{
                if (e.tile.block() instanceof ECNuclearReactor reactor && reactor.level >= 9 && reactor.root == Blocks.thoriumReactor){
                    unlock();
                }
            });
        }};

        cleanPower = new Achievement("cleanPower"){{
            root = c1;
            iconFrom = Blocks.solarPanel;
            index = 1;
            setEvent(EventType.BlockBuildEndEvent.class,e->{
                if (e.tile.block() instanceof ECSolarGenerator solar){
                    unlock();
                }
            });
        }};

    }

    public static void clearAllAchievements(){
        for (Achievement a:achievements){
            a.clearUnlock();
        }
    }
}
