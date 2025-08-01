import ECConfig.GradualDisplayName;
import ECContents.*;
import ECConfig.ECTool;
import ECType.CustomDialog;
import arc.Core;
import arc.Events;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.game.EventType;
import mindustry.mod.Mod;
import mindustry.mod.Mods;

public class EC extends Mod {

    @Override
    public void init() {
        /*/
        Events.on(EventType.ContentInitEvent.class, event -> {
            Log.info("ContentInitEvent");

        });

        //*/


        ECSettings.init();

        if (Core.settings.getBool("clearAchievements")) {
            Achievements.clearAllAchievements();
        }

        CustomDialog c = new CustomDialog();
        if (isUpdatedMod()){
            c.show();
        }else if (Core.settings.getBool("showDialog")){
            c.show();
        }


        Core.settings.put("ECVersion",Vars.mods.locateMod("ec").meta.version);

    }

    public boolean isUpdatedMod(){

        Mods.LoadedMod mod = Vars.mods.locateMod("ec");
        String[] old = Core.settings.getString("ECVersion","0.0.0").split("\\.");
        String[] now = mod.meta.version.split("\\.");
        for (int i = 0 ; i < old.length&&i<now.length;i++){
            if (Integer.parseInt(now[i]) > Integer.parseInt(old[i])) return true;
            if (Integer.parseInt(now[i]) < Integer.parseInt(old[i])) return false;
        }
        if (now.length > old.length) return true;
        return false;

    }

    private void checkIconAvailability() {
        if (Items.copper.uiIcon != null) {




            //Log.info("Copper UI icon is loaded!");
            //Log.info(Core.atlas.find("ec-num-1")==null?"":"ec-num-1");


        } else {
            Core.app.post(this::checkIconAvailability); // 延迟到下一帧继续检查
        }
    }

    @Override
    public void loadContent() {
        GradualDisplayName.load();
        StartTime();
        Mods.LoadedMod mod = Vars.mods.locateMod("ec");
        String version = mod.meta.version;
        ECTool.print("[EC]Loading...\nversion:"+version);

        ECTool.loadNumberPixmap();
        loadTime("NumberPixmap");

        try {
            ECItems.load();
            loadTime("ECItems");

            ECLiquids.load();
            loadTime("ECLiquids");

            ECEffects.load();
            loadTime("ECEffects");

            ECUnitTypes.load();
            loadTime("ECUnitTypes");

            ECBlocks.load();
            loadTime("ECBlocks");

            ECTechTrees.load();
            loadTime("ECTechTrees");

            Achievements.load();
            loadTime("ECAchievements");


        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        /*/
        new ECMultiCrafter("test"){{
            requirements(Category.crafting, with(Items.titanium, 100, Items.silicon, 25, Items.lead, 100, Items.graphite, 50));
            alwaysUnlocked = false;
            size = 2;
            aiRecipe = true;
            recipes.addAll(
                    new Recipe(){{
                        inputItems = new ItemStack[]{
                                new ItemStack(copper,2),
                                new ItemStack(lead,2)
                        };
                        outputItems = new ItemStack[]{
                                new ItemStack(graphite,1),
                                new ItemStack(titanium,1)
                        };
                    }},
                    new Recipe(){{
                        inputLiquids = new LiquidStack[]{
                                new LiquidStack(oil,2f)
                        };
                        outputLiquids = new LiquidStack[]{
                                new LiquidStack(water,1f)
                        };
                    }},
                    new Recipe(){{
                        inputItems = new ItemStack[]{
                                new ItemStack(coal,1)
                        };
                        outputPower = 60f;
                    }},
                    new Recipe(){{
                        inputPower = 60f;
                        outputItems = new ItemStack[]{
                                new ItemStack(coal,1)
                        };
                    }},
                    new Recipe(){{
                        inputHeat = 3f;
                        outputItems = new ItemStack[]{
                                new ItemStack(copper,1)
                        };
                    }},
                    new Recipe(){{
                        outputHeat = 3f;
                        inputItems = new ItemStack[]{
                                new ItemStack(copper,1)
                        };
                    }},
                    new Recipe(){{
                        inputUnits = new UnitStack[]{
                                new UnitStack(dagger,1)
                        };

                        outputUnits = new UnitStack[]{
                                new UnitStack(crawler,1)
                        };
                    }},
                    new Recipe(){{
                        inputUnits = new UnitStack[]{
                                new UnitStack(crawler,1)
                        };

                        outputUnits = new UnitStack[]{
                                new UnitStack(dagger,1)
                        };
                    }}

            );


        }};
        //*/



        //Log.info(Items.copper.uiIcon.texture.getTextureData().getPixmap());


    }

    public long StartTime;

    public void StartTime(){
        StartTime = System.nanoTime();
    }

    public void loadTime(String s){
        long NowTime = System.nanoTime();
        long time = (NowTime-StartTime) / 1000000;
        float msTime = ((float) ((int)(time*1000))) / 1000;
        ECTool.print(s + " : " + msTime +" ms");
        StartTime();
    }



}
