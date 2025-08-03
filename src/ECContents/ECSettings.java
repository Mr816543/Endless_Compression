package ECContents;

import arc.Core;
import arc.Events;
import arc.Settings;
import arc.math.Interp;
import arc.scene.Element;
import arc.scene.actions.Actions;
import arc.scene.event.Touchable;
import arc.scene.style.Drawable;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.CheckBox;
import arc.scene.ui.Label;
import arc.scene.ui.Slider;
import arc.scene.ui.layout.Table;
import arc.util.Align;
import mindustry.Vars;
import mindustry.ai.UnitCommand;
import mindustry.ai.WaveSpawner;
import mindustry.content.Blocks;
import mindustry.game.EventType;
import mindustry.gen.Icon;
import mindustry.gen.Sounds;
import mindustry.gen.Tex;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.ui.dialogs.SettingsMenuDialog;

import static arc.Core.settings;
import static mindustry.Vars.state;
import static mindustry.Vars.ui;

public class ECSettings {



    public static void init(){


        if(ui != null) {
            if (ui.settings != null) {

                ui.settings.addCategory(Core.bundle.get("set.menu"), "ec-icon", settingsTable -> {

                    settingsTable.sliderPref("ECDifficulty",2,0,3,1,i->Core.bundle.get("setting.ECDifficulty.level-"+i));
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

                });
            }
        }

    }

}
