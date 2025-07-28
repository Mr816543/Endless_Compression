package ECContents;

import arc.Core;
import arc.Events;
import arc.Settings;
import arc.scene.Element;
import arc.scene.event.Touchable;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.CheckBox;
import arc.scene.ui.Label;
import arc.scene.ui.Slider;
import arc.scene.ui.layout.Table;
import mindustry.Vars;
import mindustry.ai.UnitCommand;
import mindustry.game.EventType;
import mindustry.gen.Icon;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.ui.dialogs.SettingsMenuDialog;

import static arc.Core.settings;
import static mindustry.Vars.ui;

public class ECSettings {



    public static void init(){


        if(ui != null) {
            if (ui.settings != null) {

                ui.settings.addCategory(Core.bundle.get("set.menu"), "ec-icon", settingsTable -> {

                    settingsTable.checkPref("ECSync", true);
                    Core.settings.getBool("ECSync");
                    settingsTable.checkPref("Compress-Waves", false);
                    Core.settings.getBool("Compress-Waves");
                    settingsTable.checkPref("asFrame", false);
                    Core.settings.getBool("asFrame");
                    settingsTable.checkPref("banContent", false);
                    Core.settings.getBool("banContent");
                    settingsTable.checkPref("oldContent", false);
                    Core.settings.getBool("oldContent");
                    settingsTable.checkPref("testContent", false);
                    Core.settings.getBool("testContent");


                    /*/
                    settingsTable.pref(new SettingsMenuDialog.SettingsTable.Setting("min-zoom"){
                        final int def = 10;
                        final float min = 1f, max = 50f, step = 1f;

                        @Override
                        public void add(SettingsMenuDialog.SettingsTable table) {
                            Core.settings.defaults(name, def);
                            Slider slider = new Slider(min, max, step, false);
                            slider.setValue((float)Core.settings.getInt(this.name));
                            Label value = new Label("", Styles.outlineLabel);
                            Table content = new Table();
                            content.add(this.title, Styles.outlineLabel).left().growX().wrap();
                            content.add(value).padLeft(10.0F).right();
                            content.margin(3.0F, 33.0F, 3.0F, 33.0F);
                            content.touchable = Touchable.disabled;
                            slider.update(() -> {
                                float v = Vars.renderer.minZoom * 10;
                                slider.setValue(v);
                                String st = v > max ? Core.bundle.get("zoom-over") : v < min ? Core.bundle.get("zoom-below") : "";
                                value.setText(st + v/10 + "x");
                            });
                            slider.changed(() -> {
                                Core.settings.put(this.name, (int)slider.getValue());
                                if(!Vars.headless) Vars.renderer.minZoom = slider.getValue()/10f;
                            });
                            slider.change();
                            this.addDesc(table.stack(new Element[]{slider, content}).width(Math.min((float)Core.graphics.getWidth() / 1.2F, 460.0F)).left().padTop(4.0F).get());
                            table.row();
                        }
                    });

                    settingsTable.pref(new SettingsMenuDialog.SettingsTable.Setting("max-zoom"){
                        final int def = 10;
                        final float min = 5f, max = 50f, step = 1f;

                        @Override
                        public void add(SettingsMenuDialog.SettingsTable table) {
                            Core.settings.defaults(name, def);
                            Slider slider = new Slider(min, max, step, false);
                            slider.setValue((float)Core.settings.getInt(this.name));
                            Label value = new Label("", Styles.outlineLabel);
                            Table content = new Table();
                            content.add(this.title, Styles.outlineLabel).left().growX().wrap();
                            content.add(value).padLeft(10.0F).right();
                            content.margin(3.0F, 33.0F, 3.0F, 33.0F);
                            content.touchable = Touchable.disabled;
                            slider.update(() -> {
                                float v = Vars.renderer.maxZoom;
                                slider.setValue(v);
                                String st = v > max ? Core.bundle.get("zoom-over") : v < min ? Core.bundle.get("zoom-below") : "";
                                value.setText(st + v + "x");
                            });
                            slider.changed(() -> {
                                Core.settings.put(this.name, (int)slider.getValue());
                                if(!Vars.headless) Vars.renderer.maxZoom = slider.getValue();
                            });
                            slider.change();
                            this.addDesc(table.stack(new Element[]{slider, content}).width(Math.min((float)Core.graphics.getWidth() / 1.2F, 460.0F)).left().padTop(4.0F).get());
                            table.row();
                        }
                    });

                    settingsTable.checkPref("use-eu-cursor", true);
                    settingsTable.checkPref("eu-show-version", true);
                    settingsTable.checkPref("eu-WTMF-open", false);

                    settingsTable.pref(new SettingsMenuDialog.SettingsTable.CheckSetting("eu-plug-in-mode", false, null) {
                        @Override
                        public void add(SettingsMenuDialog.SettingsTable table) {
                            CheckBox box = new CheckBox(title);

                            box.update(() -> box.setChecked(settings.getBool(name)));

                            box.changed(() -> {
                                settings.put(name, box.isChecked());
                                settings.remove("eu-hard-mode");
                                dialog.show();
                            });
                            box.left();
                            addDesc(table.add(box).left().padTop(3f).get());
                            table.row();
                        }
                    });

                    if(!onlyPlugIn) {
                        settingsTable.checkPref("eu-reset-core-to-V7", true);
                        settingsTable.checkPref("eu-reset-core-to-all", false);
                        settingsTable.checkPref("eu-show-miner-point", true);
                        settingsTable.checkPref("eu-show-hole-acc-disk", true);
                        settingsTable.checkPref("eu-show-rust-range", true);

                        settingsTable.checkPref("eu-first-load", true);
                        if(!EUVerUnChange((String) settings.get("eu-version", ""))){
                            settings.put("eu-first-load", true);
                            settings.put("eu-version", EU.meta.version);
                        }
                        settingsTable.pref(new SettingsMenuDialog.SettingsTable.Setting(Core.bundle.get("eu-show-me-now")) {
                            @Override
                            public void add(SettingsMenuDialog.SettingsTable table) {
                                table.button(name, ExtraUtilitiesMod::toShow).margin(14).width(200f).pad(6);
                                table.row();
                            }
                        });

                        settingsTable.checkPref("eu-override-unit", false);

                        settingsTable.checkPref("eu-override-unit-missile", true);

                        settingsTable.pref(new SettingsMenuDialog.SettingsTable.CheckSetting("eu-hard-mode", false, null) {
                            @Override
                            public void add(SettingsMenuDialog.SettingsTable table) {
                                CheckBox box = new CheckBox(title);

                                box.update(() -> box.setChecked(settings.getBool(name)));

                                box.changed(() -> {
                                    if (!onlyPlugIn) {
                                        settings.put(name, box.isChecked());
                                        settings.put("eu-open-hard", hardMod);
                                        dialog.show();
                                    }
                                });
                                box.left();
                                addDesc(table.add(box).left().padTop(3f).get());
                                table.row();
                            }
                        });

                        settingsTable.pref(new SettingsMenuDialog.SettingsTable.Setting(Core.bundle.get("eu-show-donor-and-develop")) {
                            @Override
                            public void add(SettingsMenuDialog.SettingsTable table) {
                                table.button(name, eui.ddItemsList::toShow).margin(14).width(200f).pad(6);
                                table.row();
                            }
                        });
                    }


                    //*/
                });
            }
        }




    }
}
