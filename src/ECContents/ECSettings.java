package ECContents;

import arc.Core;
import arc.Events;
import arc.Settings;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.ui.dialogs.SettingsMenuDialog;

public class ECSettings {



    public static void init(){
        Events.on(EventType.ClientLoadEvent.class, e -> Vars.ui.settings.game.checkPref("Compress-Waves", false));
        Core.settings.getBool("Compress-Waves");
        Events.on(EventType.ClientLoadEvent.class, e -> Vars.ui.settings.game.checkPref("ECSync", true));
        Core.settings.getBool("ECSync");
        Events.on(EventType.ClientLoadEvent.class, e -> Vars.ui.settings.game.checkPref("asFrame", false));
        Core.settings.getBool("asFrame");
    }
}
