package ECType.ECBlockTypes;

import ECConfig.Config;
import ECConfig.ECData;
import ECConfig.ECTool;
import arc.Core;
import arc.graphics.g2d.TextureRegion;
import mindustry.ctype.UnlockableContent;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.world.Block;
import mindustry.world.blocks.heat.HeatProducer;
import mindustry.world.blocks.power.ConsumeGenerator;
import mindustry.world.blocks.production.HeatCrafter;
import mindustry.world.consumers.*;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawLiquidOutputs;
import mindustry.world.draw.DrawMulti;

public class ECConsumeGenerator extends ECMultiCrafter{

    public ConsumeGenerator root;


    public ECConsumeGenerator(ConsumeGenerator root) throws IllegalAccessException {
        super("compress-"+root.name);


        this.root = root;

        requirements(root.category, root.buildVisibility, ECTool.compressItemStack(root.requirements,1));
        localizedName = Core.bundle.get("Compression.localizedName") + root.localizedName;
        description = root.description;
        details = root.details;

        size = root.size;
        health = root.health;
        multiDrawer = true;


        ECTool.loadCompressContentRegion(root, this);
        ECTool.setIcon(root, this, 0);
        ECData.register(root,this,1);
    }


    @Override
    public void init() {
        createRecipe();
        super.init();
    }


    public void createRecipe() {
        Recipe recipe = new Recipe() {{

            crafterTime = root.itemDuration;


            //绘制方法套用
            if (root.drawer instanceof DrawMulti drawMulti) {
                drawer = new DrawMulti();
                ((DrawMulti) drawer).drawers = new DrawBlock[drawMulti.drawers.length];
                for (int i = 0 ; i < drawMulti.drawers.length;i++){

                    if (drawMulti.drawers[i] instanceof DrawLiquidOutputs){
                        (((DrawMulti) drawer).drawers)[i] = new DrawLiquidOutputs(){
                            public ECMultiCrafter expectMultiCrafter(Block block){
                                if(!(block instanceof ECMultiCrafter crafter))
                                    throw new ClassCastException("This drawer requires the block to be a MultiCrafter. Use a different drawer.");
                                return crafter;
                            }
                            @Override
                            public void load(Block block) {
                                ECMultiCrafter crafter = this.expectMultiCrafter(block);
                                for (Recipe r:crafter.recipes){

                                    if (r.outputLiquids != null) {
                                        this.liquidOutputRegions = new TextureRegion[2][r.outputLiquids.length];

                                        for(int i = 0; i < r.outputLiquids.length; ++i) {
                                            for(int j = 1; j <= 2; ++j) {
                                                this.liquidOutputRegions[j - 1][i] = Core.atlas.find(block.name + "-" + r.outputLiquids[i].liquid.name + "-output" + j);
                                            }
                                        }

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


            if (root.outputLiquid != null) {
                outputLiquids = new LiquidStack[]{new LiquidStack(root.outputLiquid.liquid,root.outputLiquid.amount*crafterTime)};
            }


            if (root.powerProduction > 0){
                outputPower = root.powerProduction * 1.5f;
            }

            for (Consume cons : root.consumers) {
                if (cons instanceof ConsumeItems consumer) {
                    inputItems = consumer.items;
                }
                if (cons instanceof ConsumeLiquid consumer) {
                    inputLiquids = new LiquidStack[]{new LiquidStack(consumer.liquid, consumer.amount*crafterTime)};
                }
                if (cons instanceof ConsumeLiquids consumer) {
                    inputLiquids = new LiquidStack[consumer.liquids.length];
                    for (int i = 0 ; i < inputLiquids.length;i++){
                        inputLiquids[i] = new LiquidStack(consumer.liquids[i].liquid,consumer.liquids[i].amount*crafterTime);
                    }
                }
                if (cons instanceof ConsumePower consumer) {
                    inputPower = consumer.usage;
                }
            }


        }};
        recipes.add(recipe);
        for (int i = 1 ; i <=9;i++){
            recipes.add(recipe.createCompressedRecipe(i));
        }
    }
}
