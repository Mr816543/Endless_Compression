package ECContent;

import ECType.MultiCrafter;
import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.struct.Seq;
import arc.util.Eachable;
import mindustry.Vars;
import mindustry.content.Items;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.type.*;
import mindustry.world.Block;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.draw.DrawMulti;
import mindustry.world.draw.DrawRegion;

import static ECContent.ECItems.ECItems;
import static ECContent.ECItems.items;
import static ECContent.ECLiquids.ECLiquids;
import static ECContent.ECLiquids.liquids;
import static ECType.ECSetting.MAX_LEVEL;
import static mindustry.type.ItemStack.with;

public class ECCrafter {
    public static Seq<Block> blocks;

    public static void load() throws IllegalAccessException {

        blocks = Vars.content.blocks().copy();

        for (int i = 1; i <= MAX_LEVEL; i++) {
            compressECCrafter(i);
        }

        for (Block block : blocks) {
            if (!(block instanceof GenericCrafter)) continue;
            //Log.info(block.localizedName);
            compressGenericCrafter(block);

        }


    }

    public static void compressECCrafter(int num) {

        new MultiCrafter("c" + num + "-Compressor") {{

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
                        public void draw(Building build) {
                            float z = Draw.z();
                            if (layer > 0) Draw.z(layer);

                            Draw.rect(Core.atlas.find("ec-Compressor"), build.x + x, build.y + y, build.totalProgress() * rotateSpeed + rotation + (buildingRotate ? build.rotdeg() : 0));
                            draw(build, 0);
                            draw(build, 1);
                            draw(build, 2);

                            Draw.z(z);
                        }

                        public void draw(Building build, int num) {

                            float z = Draw.z();
                            if (layer > 0) Draw.z(layer);

                            Draw.color(ECItems.get(item).get(num*4).color);

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
                        public void draw(Building build) {
                            float z = Draw.z();
                            if (layer > 0) Draw.z(layer);

                            Draw.rect(Core.atlas.find("ec-Compressor"), build.x + x, build.y + y, build.totalProgress() * rotateSpeed + rotation + (buildingRotate ? build.rotdeg() : 0));
                            draw(build, 0);
                            draw(build, 1);
                            draw(build, 2);

                            Draw.z(z);
                        }

                        public void draw(Building build, int num) {

                            float z = Draw.z();
                            if (layer > 0) Draw.z(layer);

                            Draw.color(ECLiquids.get(liquid).get(num*4).color);

                            Draw.rect(Core.atlas.find("ec-Compressor-top" + num), build.x + x, build.y + y, build.totalProgress() * rotateSpeed + rotation + (buildingRotate ? build.rotdeg() : 0));


                            Draw.z(z);
                        }

                    };
                }});


            }


        }};


    }

    public static void compressGenericCrafter(Block b) throws IllegalAccessException {
        MultiCrafter compressGenericCrafter = new MultiCrafter("compression-" + b.name) {{

            requirements(b.category, b.buildVisibility, b.requirements);
            localizedName = Core.bundle.get("Compression.localizedName") + b.localizedName;
            description = b.description;
            details = b.details;

            size = b.size;
            health = b.health;


        }};


        //配置

        //Log.info(compressGenericCrafter.localizedName);
        //compress(block, compressGenericCrafter, Block.class);
    }
}
