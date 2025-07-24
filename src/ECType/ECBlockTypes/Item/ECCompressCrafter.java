package ECType.ECBlockTypes.Item;

import ECConfig.ECData;
import ECConfig.ECTool;
import ECContents.ECBlocks;
import ECType.ECBlockTypes.Crafter.ECMultiCrafter;
import arc.Core;
import arc.Events;
import arc.graphics.Pixmap;
import arc.graphics.Texture;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import mindustry.Vars;
import mindustry.content.Items;
import mindustry.content.TechTree;
import mindustry.ctype.UnlockableContent;
import mindustry.game.EventType;
import mindustry.gen.Building;
import mindustry.type.*;
import mindustry.world.Block;
import mindustry.world.draw.DrawRegion;

import static ECContents.ECItems.items;
import static ECContents.ECLiquids.liquids;
import static mindustry.type.ItemStack.with;

public class ECCompressCrafter extends ECMultiCrafter {

    public int level;

    public ECCompressCrafter(int level) {
        super("c" + level + "-Compressor");
        requirements(Category.crafting, with(ECData.get(Items.silicon,level-1), 10));

        size = 2;
        this.level = level;
        multiDrawer = true;
        localizedName = level + Core.bundle.get("ECCompressCrafter.name");


    }

    @Override
    public void onUnlock() {
        super.onUnlock();
        for (ECCompressCrafter compressCrafter:ECBlocks.ecCompressCrafters){
            if (compressCrafter.level <= level){
                for (Recipe r : compressCrafter.recipes){
                    if (r.name.equals(Core.bundle.get("ECType.Recipe.ECname"))){
                        for (ItemStack stack:r.inputItems){
                            if (stack.amount==0) {
                                stack.amount = 1;
                                continue;
                            }
                            stack.amount *= 9;
                        }
                        for (ItemStack stack:r.outputItems){
                            if (stack.amount==0) {
                                stack.amount = 1;
                                continue;
                            }
                            stack.amount *= 9;
                        }
                        for (LiquidStack stack:r.inputLiquids){
                            if (stack.amount==0) {
                                stack.amount = 1;
                                continue;
                            }
                            stack.amount *= 9;
                        }
                        for (LiquidStack stack:r.outputLiquids){
                            if (stack.amount==0) {
                                stack.amount = 1;
                                continue;
                            }
                            stack.amount *= 9;
                        }
                    }
                }
                compressCrafter.initCapacity();
            }
        }
    }

    @Override
    public void init() {

        initRecipes();
        super.init();
        initTechTree();
    }

    public void initRecipes() {
        recipes.clear();

        int MultipleLevel = ECBlocks.unlockedLevel-level;

        int recipeMultiple = Mathf.pow(9,MultipleLevel);

        for (Item item : items) {

            if (!ECData.hasECContent(item)) continue;

            recipes.add(new Recipe() {{

                inputItems = new ItemStack[]{new ItemStack(ECData.get(item,level-1), 9)};
                outputItems = new ItemStack[]{new ItemStack(ECData.get(item,level), 1)};
                crafterTime = 60f;
                drawer = new DrawRegion() {
                    @Override
                    public void load(Block block) {
                        region = Core.atlas.find("ec-ECCompressCrafter");
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

                    public void draw(Building build, int level) {

                        float z = Draw.z();
                        if (layer > 0) Draw.z(layer);

                        Draw.color(ECData.get(item,level*4).color);

                        Draw.rect(Core.atlas.find("ec-Compressor-top" + level), build.x + x, build.y + y, build.totalProgress() * rotateSpeed + rotation + (buildingRotate ? build.rotdeg() : 0));


                        Draw.z(z);
                    }

                };
            }});

            recipes.add(new Recipe() {{

                name = Core.bundle.get("ECType.Recipe.ECname");
                    inputItems = new ItemStack[]{new ItemStack(ECData.get(item,level-1), 9*recipeMultiple)};
                    outputItems = new ItemStack[]{new ItemStack(ECData.get(item,level), 1*recipeMultiple)};
                    crafterTime = 60f;
                    drawer = new DrawRegion() {
                        @Override
                        public void load(Block block) {
                            region = Core.atlas.find("ec-ECCompressCrafter");
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

                        public void draw(Building build, int level) {

                            float z = Draw.z();
                            if (layer > 0) Draw.z(layer);

                            Draw.color(ECData.get(item,level*4).color);

                            Draw.rect(Core.atlas.find("ec-Compressor-top" + level), build.x + x, build.y + y, build.totalProgress() * rotateSpeed + rotation + (buildingRotate ? build.rotdeg() : 0));


                            Draw.z(z);
                        }

                    };
                }});

        }
        for (Liquid liquid : liquids) {

            if (!ECData.hasECContent(liquid)) continue;

            recipes.add(new Recipe() {{
                inputLiquids = new LiquidStack[]{new LiquidStack(ECData.get(liquid,level-1), 9)};
                outputLiquids = new LiquidStack[]{new LiquidStack(ECData.get(liquid,level), 1)};
                crafterTime = 60f;
                drawer = new DrawRegion() {
                    @Override
                    public void load(Block block) {
                        region = Core.atlas.find("ec-ECCompressCrafter");
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

                        Draw.color(ECData.get(liquid,num*4).color);

                        Draw.rect(Core.atlas.find("ec-Compressor-top" + num), build.x + x, build.y + y, build.totalProgress() * rotateSpeed + rotation + (buildingRotate ? build.rotdeg() : 0));


                        Draw.z(z);
                    }

                };
            }});

            recipes.add(new Recipe() {{
                name = Core.bundle.get("ECType.Recipe.ECname");
                    inputLiquids = new LiquidStack[]{new LiquidStack(ECData.get(liquid,level-1), 9*recipeMultiple)};
                    outputLiquids = new LiquidStack[]{new LiquidStack(ECData.get(liquid,level), 1*recipeMultiple)};
                    crafterTime = 60f;
                    drawer = new DrawRegion() {
                        @Override
                        public void load(Block block) {
                            region = Core.atlas.find("ec-ECCompressCrafter");
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

                            Draw.color(ECData.get(liquid,num*4).color);

                            Draw.rect(Core.atlas.find("ec-Compressor-top" + num), build.x + x, build.y + y, build.totalProgress() * rotateSpeed + rotation + (buildingRotate ? build.rotdeg() : 0));


                            Draw.z(z);
                        }

                    };
                }});

        }

        initCapacity();
    }

    public void initTechTree(){

        UnlockableContent root = level==1?Items.silicon:ECBlocks.ecCompressCrafters.get(level-2);

        //遍历根物品的全部科技节点
        for (TechTree.TechNode rootNode : root.techNodes){

            //待解锁的内容
            UnlockableContent content = this;
            //创建新节点
            TechTree.TechNode node = TechTree.node(content,()->{});
            node.parent = rootNode;
            rootNode.children.add(node);

        }



    }

    @Override
    public void loadIcon() {
        /*/
        if (Core.atlas.has("ec-Compressor")) {
            fullIcon = uiIcon = Core.atlas.find("ec-Compressor");
        } else {
            Core.app.post(this::loadIcon);
        }
        //*/

        Pixmap A = new Texture(Vars.mods.getMod("ec").root.child("sprites").child("ECCompressCrafter").child("ECCompressCrafter.png")).getTextureData().getPixmap();
        Pixmap B = ECTool.numberPixmap[size-1][level];

        fullIcon = uiIcon = ECTool.mergeRegions(A,B);
    }







}
