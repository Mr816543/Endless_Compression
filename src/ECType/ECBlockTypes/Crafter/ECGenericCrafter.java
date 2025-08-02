package ECType.ECBlockTypes.Crafter;

import ECConfig.ECData;
import ECConfig.ECTool;
import ECType.ECBlockTypes.Item.ECCompressCrafter;
import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Geometry;
import arc.struct.Seq;
import mindustry.content.Blocks;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.Building;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.world.Block;
import mindustry.world.blocks.heat.HeatProducer;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.production.HeatCrafter;
import mindustry.world.consumers.*;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawLiquidOutputs;
import mindustry.world.draw.DrawMulti;
import mindustry.world.meta.BlockGroup;

import static mindustry.Vars.tilesize;

public class ECGenericCrafter extends ECMultiCrafter {

    public GenericCrafter root;

    public ECGenericCrafter(GenericCrafter root) throws IllegalAccessException {
        super("compression-" + root.name);

        this.root = root;

        requirements(root.category, root.buildVisibility, ECTool.compressItemStack(root.requirements, 1));
        localizedName = Core.bundle.get("Compression.localizedName") + root.localizedName;
        description = root.description;
        details = root.details;

        size = root.size;
        health = root.health;
        multiDrawer = true;
        rotate = root.rotate;

        ECTool.loadCompressContentRegion(root, this);

        ECTool.setIcon(root, this, 0);

        ECData.register(root, this, 1);
    }

    public ECGenericCrafter(String name){
        super(name);
    }

    @Override
    public void init() {
        createRecipe();
        super.init();
    }

    public void createRecipe() {
        Recipe recipe = new Recipe() {{

            crafterTime = root.craftTime;
            liquidOutputDirections = root.liquidOutputDirections;
            //绘制方法套用
            if (root.drawer instanceof DrawMulti drawMulti) {
                drawer = new DrawMulti();
                ((DrawMulti) drawer).drawers = new DrawBlock[drawMulti.drawers.length];
                for (int i = 0; i < drawMulti.drawers.length; i++) {

                    if (drawMulti.drawers[i] instanceof DrawLiquidOutputs drawLiquidOutputs) {
                        int finalI = i;
                        drawLiquidOutputs.load(root);
                        TextureRegion[][] finalLiquidOutputRegions = drawLiquidOutputs.liquidOutputRegions;
                        (((DrawMulti) drawer).drawers)[i] = new DrawLiquidOutputs() {

                            public ECMultiCrafter expectMultiCrafter(Block block) {
                                if (!(block instanceof ECMultiCrafter crafter))
                                    throw new ClassCastException("This drawer requires the block to be a MultiCrafter. Use a different drawer.");
                                return crafter;
                            }

                            @Override
                            public void load(Block block) {
                                ECMultiCrafter crafter = expectMultiCrafter(block);
                                Recipe r = crafter.recipes.get(finalI);
                                if (r.outputLiquids != null) {
                                    liquidOutputRegions = finalLiquidOutputRegions;
                                }
                            }

                            @Override
                            public void draw(Building build) {
                                ECMultiCrafter crafter = expectMultiCrafter(build.block);

                                Recipe r = crafter.recipes.get(finalI);
                                if (r.outputLiquids == null || r.outputLiquids.length < 2) return;


                                for (int i = 0; i < r.outputLiquids.length; i++) {
                                    int side = i < liquidOutputDirections.length ? liquidOutputDirections[i] : -1;
                                    if (side != -1) {
                                        int realRot = (side + build.rotation) % 4;
                                        Draw.rect(liquidOutputRegions[realRot > 1 ? 1 : 0][i], build.x, build.y, realRot * 90);
                                    }
                                }
                            }


                        };
                        continue;
                    }

                    (((DrawMulti) drawer).drawers)[i] = drawMulti.drawers[i];
                }
            } else {
                drawer = root.drawer;
            }


            if (root.outputItems != null) {
                outputItems = root.outputItems;
            } else if (root.outputItem != null) {
                outputItems = new ItemStack[]{root.outputItem};
            }

            if (root.outputLiquids != null) {
                outputLiquids = new LiquidStack[root.outputLiquids.length];
                for (int i = 0; i < outputLiquids.length; i++) {
                    outputLiquids[i] = new LiquidStack(root.outputLiquids[i].liquid, root.outputLiquids[i].amount * crafterTime);
                }
            } else if (root.outputLiquid != null) {
                outputLiquids = new LiquidStack[]{new LiquidStack(root.outputLiquid.liquid, root.outputLiquid.amount * crafterTime)};
            }

            if (root instanceof HeatProducer h) {
                outputHeat = h.heatOutput;
            }


            for (Consume cons : root.consumers) {
                if (cons instanceof ConsumeItems consumer) {
                    inputItems = consumer.items;
                }
                if (cons instanceof ConsumeLiquid consumer) {
                    inputLiquids = new LiquidStack[]{new LiquidStack(consumer.liquid, consumer.amount * crafterTime)};
                }
                if (cons instanceof ConsumeLiquids consumer) {
                    inputLiquids = new LiquidStack[consumer.liquids.length];
                    for (int i = 0; i < inputLiquids.length; i++) {
                        inputLiquids[i] = new LiquidStack(consumer.liquids[i].liquid, consumer.liquids[i].amount * crafterTime);
                    }
                }
                if (cons instanceof ConsumePower consumer) {
                    inputPower = consumer.usage;
                }
            }

            if (root instanceof HeatCrafter h) {
                inputHeat = h.heatRequirement;
            }


        }};
        recipes.add(recipe);
        for (int i = 1; i <= 9; i++) {
            recipes.add(recipe.createCompressedRecipe(i));
        }
    }

    @Override
    public boolean canReplace(Block other){
        if(other.alwaysReplace) return true;
        if(other.privileged) return false;
        return other.replaceable &&
                (other != this || (rotate && quickRotate)) &&
                ((this.group != BlockGroup.none && other.group == this.group) || other == this.root) &&
                (size == other.size || (size >= other.size && ((subclass != null && subclass == other.subclass) || group.anyReplace)));
    }

    @Override
    public void drawOverlay(float x, float y, int rotation){
        if(recipes.get(0).outputLiquids != null){
            Recipe r = recipes.get(0);
            for(int i = 0; i <r.outputLiquids.length; i++){
                int dir = r.liquidOutputDirections.length > i ? r.liquidOutputDirections[i] : -1;

                if(dir != -1){
                    Draw.rect(
                            r.outputLiquids[i].liquid.fullIcon,
                            x + Geometry.d4x(dir + rotation) * (size * tilesize / 2f + 4),
                            y + Geometry.d4y(dir + rotation) * (size * tilesize / 2f + 4),
                            8f, 8f
                    );
                }
            }
        }
    }
    @Override
    public void drawOverlay(float x, float y, int rotation,int index){
        if(recipes.get(index).outputLiquids != null){
            Recipe r = recipes.get(index);
            for(int i = 0; i < r.outputLiquids.length; i++){
                int dir = r.liquidOutputDirections.length > i ? r.liquidOutputDirections[i] : -1;

                if(dir != -1){
                    Draw.rect(
                            r.outputLiquids[i].liquid.fullIcon,
                            x + Geometry.d4x(dir + rotation) * (size * tilesize / 2f + 4),
                            y + Geometry.d4y(dir + rotation) * (size * tilesize / 2f + 4),
                            8f, 8f
                    );
                }
            }
        }
    }

}
