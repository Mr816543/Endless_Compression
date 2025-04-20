package ECContent;

import ECType.ECCompressCrafter;
import ECType.ECDrill;
import ECType.ECGenericCrafter;
import ECType.ECMultiCrafter;
import arc.Core;
import arc.graphics.g2d.Draw;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.content.Items;
import mindustry.gen.Building;
import mindustry.type.*;
import mindustry.world.Block;
import mindustry.world.blocks.production.BurstDrill;
import mindustry.world.blocks.production.Drill;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.draw.*;
import mindustry.world.meta.BuildVisibility;

import static ECContent.ECItems.ECItems;
import static ECContent.ECItems.items;
import static ECContent.ECLiquids.ECLiquids;
import static ECContent.ECLiquids.liquids;
import static ECConfig.ECSetting.MAX_LEVEL;
import static mindustry.type.ItemStack.with;

public class ECBlocks {
    public static Seq<Block> blocks;

    public static void load() throws IllegalAccessException {

        blocks = Vars.content.blocks().copy();

        for (int i = 1; i <= MAX_LEVEL; i++) {
            new ECCompressCrafter(i);
        }

        for (Block block : blocks) {
            if (block.buildVisibility == BuildVisibility.debugOnly) continue;
            if (block instanceof GenericCrafter b) {
                new ECGenericCrafter(b);
                continue;
            }
            else if (block instanceof Drill b){
                if (b instanceof BurstDrill){
                    continue;
                }

                for (int i = 1 ; i <=9 ; i ++){
                    new ECDrill(b,i);
                }
                continue;
            }


        }


    }


    /*/
    //生成压缩器
    public static void compressECCrafter(int num) {
        new ECMultiCrafter("c" + num + " Compressor") {
            {
                multiDrawer = true;

                requirements(Category.crafting, with(ECItems.get(Items.silicon).get(num - 1), 5));
                size = 2;

                for (Item item : items) {
                    if (ECItems.get(item) == null || ECItems.get(item).size != 10) continue;

                    recipes.add(new Recipe() {{

                        inputItems = new ItemStack[]{new ItemStack(ECItems.get(item).get(num - 1), 9)};
                        outputItems = new ItemStack[]{new ItemStack(ECItems.get(item).get(num), 1)};
                        crafterTime = 60f;
                        drawer = new DrawRegion() {
                            @Override
                            public void load(Block block) {
                                region = Core.atlas.find("ec-Compressor");
                                //Log.info("load "+ block.name+" region "+(region==null?"worry":"finish"));
                            }


                            @Override
                            public void draw(Building build) {
                                float z = Draw.z();
                                if (layer > 0) Draw.z(layer);

                                Draw.rect(region, build.x + x, build.y + y, build.totalProgress() * rotateSpeed + rotation + (buildingRotate ? build.rotdeg() : 0));
                                draw(build, 0);
                                draw(build, 1);
                                draw(build, 2);

                                Draw.z(z);
                            }

                            public void draw(Building build, int num) {

                                float z = Draw.z();
                                if (layer > 0) Draw.z(layer);

                                Draw.color(ECItems.get(item).get(num * 4).color);

                                Draw.rect(Core.atlas.find("ec-Compressor-top" + num), build.x + x, build.y + y, build.totalProgress() * rotateSpeed + rotation + (buildingRotate ? build.rotdeg() : 0));


                                Draw.z(z);
                            }

                        };
                    }});
                }


                for (Liquid liquid : liquids) {
                    if (ECLiquids.get(liquid) == null || ECLiquids.get(liquid).size != 10) continue;

                    recipes.add(new Recipe() {{
                        inputLiquids = new LiquidStack[]{new LiquidStack(ECLiquids.get(liquid).get(num - 1), 9)};
                        outputLiquids = new LiquidStack[]{new LiquidStack(ECLiquids.get(liquid).get(num), 1)};
                        crafterTime = 60f;
                        drawer = new DrawRegion() {
                            @Override
                            public void load(Block block) {
                                region = Core.atlas.find("ec-Compressor");
                            }

                            @Override
                            public void draw(Building build) {
                                float z = Draw.z();
                                if (layer > 0) Draw.z(layer);
                                Draw.rect(region, build.x + x, build.y + y, build.totalProgress() * rotateSpeed + rotation + (buildingRotate ? build.rotdeg() : 0));
                                draw(build, 0);
                                draw(build, 1);
                                draw(build, 2);
                                Draw.z(z);
                            }

                            public void draw(Building build, int num) {

                                float z = Draw.z();
                                if (layer > 0) Draw.z(layer);

                                Draw.color(ECLiquids.get(liquid).get(num * 4).color);

                                Draw.rect(Core.atlas.find("ec-Compressor-top" + num), build.x + x, build.y + y, build.totalProgress() * rotateSpeed + rotation + (buildingRotate ? build.rotdeg() : 0));


                                Draw.z(z);
                            }

                        };
                    }});
                }
            }

            @Override
            public void loadIcon() {
                if (Core.atlas.has("ec-Compressor")) {
                    fullIcon = uiIcon = Core.atlas.find("ec-Compressor");
                    //Log.info("finish");
                } else {
                    Core.app.post(this::loadIcon);
                }
            }
        };
    }

     //*/

}
