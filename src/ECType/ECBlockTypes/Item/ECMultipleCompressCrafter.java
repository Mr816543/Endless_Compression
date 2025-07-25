package ECType.ECBlockTypes.Item;

import ECConfig.ECData;
import ECConfig.ECTool;
import ECContents.ECBlocks;
import ECType.ECBlockTypes.Crafter.ECMultiCrafter;
import arc.Core;
import arc.graphics.Pixmap;
import arc.graphics.Texture;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import mindustry.Vars;
import mindustry.content.Items;
import mindustry.content.TechTree;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.Building;
import mindustry.type.*;
import mindustry.world.Block;
import mindustry.world.draw.DrawRegion;

import static ECContents.ECItems.items;
import static ECContents.ECLiquids.liquids;
import static mindustry.type.ItemStack.with;

public class ECMultipleCompressCrafter extends ECMultiCrafter {
    public int level;

    public ECMultipleCompressCrafter(int level) {
        super("c" + level + "-MultipleCompressor");
        requirements(Category.crafting, with(ECData.get(Items.silicon,level-1), 100));

        size = 3;
        this.level = level;
        multiDrawer = true;
        localizedName = level + Core.bundle.get("ECMultipleCompressCrafter.name");
    }


    @Override
    public void init() {

        initRecipes();
        super.init();
        initTechTree();
    }

    public void initRecipes() {
        recipes.clear();

        int need = Mathf.pow(9,level);

        for (Item item : items) {

            if (!ECData.hasECContent(item)) continue;

            recipes.add(new Recipe() {{

                inputItems = new ItemStack[]{new ItemStack(ECData.get(item,0), need)};
                outputItems = new ItemStack[]{new ItemStack(ECData.get(item,level), 1)};
                inputPower = 0.2f * Mathf.pow(5f,level);
                crafterTime = 12f;
                drawer = new DrawRegion() {
                    @Override
                    public void load(Block block) {
                        region = Core.atlas.find("ec-MultipleCompressor");
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

                        Draw.rect(Core.atlas.find("ec-MultipleCompressor-top" + level), build.x + x, build.y + y, build.totalProgress() * rotateSpeed + rotation + (buildingRotate ? build.rotdeg() : 0));


                        Draw.z(z);
                    }

                };
            }});
            recipes.add(new Recipe() {{
                inputItems = new ItemStack[]{new ItemStack(ECData.get(item,level), 1)};
                outputItems = new ItemStack[]{new ItemStack(ECData.get(item,level-1), 9)};
                inputPower = 0.2f * Mathf.pow(5f,level);
                crafterTime = 12f;
                drawer = new DrawRegion() {
                    @Override
                    public void load(Block block) {
                        region = Core.atlas.find("ec-MultipleCompressor");
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

                        Draw.rect(Core.atlas.find("ec-MultipleCompressor-top" + level), build.x + x, build.y + y, build.totalProgress() * rotateSpeed + rotation + (buildingRotate ? build.rotdeg() : 0));


                        Draw.z(z);
                    }

                };
            }});


        }
        for (Liquid liquid : liquids) {

            if (!ECData.hasECContent(liquid)) continue;

            recipes.add(new Recipe() {{
                inputLiquids = new LiquidStack[]{new LiquidStack(ECData.get(liquid,0), need)};
                outputLiquids = new LiquidStack[]{new LiquidStack(ECData.get(liquid,level), 1)};
                inputPower = 0.2f * Mathf.pow(5f,level);
                crafterTime = 1f;
                drawer = new DrawRegion() {
                    @Override
                    public void load(Block block) {
                        region = Core.atlas.find("ec-MultipleCompressor");
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

                        Draw.rect(Core.atlas.find("ec-MultipleCompressor-top" + num), build.x + x, build.y + y, build.totalProgress() * rotateSpeed + rotation + (buildingRotate ? build.rotdeg() : 0));


                        Draw.z(z);
                    }

                };
            }});
            recipes.add(new Recipe() {{
                inputLiquids = new LiquidStack[]{new LiquidStack(ECData.get(liquid,level), 1)};
                outputLiquids = new LiquidStack[]{new LiquidStack(ECData.get(liquid,level-1), 9)};
                inputPower = 0.2f * Mathf.pow(5f,level);
                crafterTime = 1f;
                drawer = new DrawRegion() {
                    @Override
                    public void load(Block block) {
                        region = Core.atlas.find("ec-MultipleCompressor");
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

                        Draw.rect(Core.atlas.find("ec-MultipleCompressor-top" + num), build.x + x, build.y + y, build.totalProgress() * rotateSpeed + rotation + (buildingRotate ? build.rotdeg() : 0));


                        Draw.z(z);
                    }

                };
            }});


        }

        initCapacity();
    }

    public void initTechTree(){

        UnlockableContent root = level==1?Items.silicon:ECBlocks.ecMultipleCompressCrafters.get(level-2);

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

        Pixmap A = new Texture(Vars.mods.getMod("ec").root.child("sprites").child("ECMultipleCompressCrafter").child("MultipleCompressor.png")).getTextureData().getPixmap();
        Pixmap B = ECTool.numberPixmap[size-1][level];

        fullIcon = uiIcon = ECTool.mergeRegions(A,B);
    }

}
