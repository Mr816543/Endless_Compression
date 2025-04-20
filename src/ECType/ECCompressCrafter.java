package ECType;

import ECConfig.ECTool;
import arc.Core;
import arc.graphics.Pixmap;
import arc.graphics.Texture;
import arc.graphics.g2d.Draw;
import mindustry.Vars;
import mindustry.content.Items;
import mindustry.gen.Building;
import mindustry.type.*;
import mindustry.world.Block;
import mindustry.world.draw.DrawRegion;

import static ECContent.ECItems.ECItems;
import static ECContent.ECItems.items;
import static ECContent.ECLiquids.ECLiquids;
import static ECContent.ECLiquids.liquids;
import static mindustry.type.ItemStack.with;

public class ECCompressCrafter extends ECMultiCrafter{

    int level;

    public ECCompressCrafter(int level) {
        super("c" + level + "-Compressor");
        requirements(Category.crafting, with(ECItems.get(Items.silicon).get(level - 1), 5));

        size = 2;
        this.level = level;
        multiDrawer = true;
        localizedName = level + Core.bundle.get("ECType.ECCompressCrafter.name");

    }

    @Override
    public void init() {
        for (Item item : items) {
            if (ECItems.get(item) == null || ECItems.get(item).size != 10) continue;

            recipes.add(new Recipe() {{

                inputItems = new ItemStack[]{new ItemStack(ECItems.get(item).get(level - 1), 9)};
                outputItems = new ItemStack[]{new ItemStack(ECItems.get(item).get(level), 1)};
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
                inputLiquids = new LiquidStack[]{new LiquidStack(ECLiquids.get(liquid).get(level - 1), 9)};
                outputLiquids = new LiquidStack[]{new LiquidStack(ECLiquids.get(liquid).get(level), 1)};
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

                        Draw.color(ECLiquids.get(liquid).get(num * 4).color);

                        Draw.rect(Core.atlas.find("ec-Compressor-top" + num), build.x + x, build.y + y, build.totalProgress() * rotateSpeed + rotation + (buildingRotate ? build.rotdeg() : 0));


                        Draw.z(z);
                    }

                };
            }});
        }
        super.init();
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
