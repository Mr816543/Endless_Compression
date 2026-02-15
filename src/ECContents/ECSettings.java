package ECContents;

import arc.Core;

import static arc.Core.settings;
import static mindustry.Vars.ui;

public class ECSettings {


    public static void init() {


        if (ui != null) {
            if (ui.settings != null) {

                ui.settings.addCategory(Core.bundle.get("set.menu"), "ec-icon", settingsTable -> {

                    settingsTable.sliderPref("ECDifficulty", 2, 0, 3, 1, i -> Core.bundle.get("setting.ECDifficulty.level-" + i));
                    settings.getInt("ECDifficulty");
                    settingsTable.checkPref("ECSync", true);
                    Core.settings.getBool("ECSync");
                    settingsTable.checkPref("asFrame", false);
                    Core.settings.getBool("asFrame");
                    settingsTable.checkPref("banContent", false);
                    Core.settings.getBool("banContent");
                    settingsTable.checkPref("oldContent", false);
                    Core.settings.getBool("oldContent");
                    settingsTable.checkPref("testContent", false);
                    Core.settings.getBool("testContent");
                    settingsTable.checkPref("clearAchievements", false);
                    Core.settings.getBool("clearAchievements");
                    settingsTable.checkPref("achievementsWork", true);
                    Core.settings.getBool("achievementsWork");
                    settingsTable.checkPref("showDialog", true);
                    Core.settings.getBool("showDialog");
                    settingsTable.checkPref("simpleLaunch", false);
                    Core.settings.getBool("simpleLaunch");
                    //settingsTable.checkPref("autoUpdate", false);
                    //Core.settings.getBool("autoUpdate");
                    settingsTable.sliderPref("sampleTimes", 5, 1, 30, 1, i -> i + "s");
                    settings.getInt("sampleTimes");
                    settingsTable.checkPref("entityProfiler", false);
                    Core.settings.getBool("entityProfiler");
                });
            }
        }

    }

}
