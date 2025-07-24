import ECConfig.GradualDisplayName;
import ECContents.*;
import ECConfig.ECTool;
import arc.Core;
import mindustry.content.Items;
import mindustry.mod.Mod;

public class EC extends Mod {

    @Override
    public void init() {
        /*/
        Events.on(EventType.ContentInitEvent.class, event -> {
            Log.info("ContentInitEvent");

        });

        //*/

        ECSettings.init();






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
            loadTime("ECTechTree");



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
