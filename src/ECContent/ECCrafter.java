package ECContent;

import ECType.MultiCrafter;
import mindustry.content.Items;
import mindustry.type.*;

import static ECType.ECSetting.*;
import static ECContent.ECItems.*;
import static ECContent.ECLiquids.*;
import static mindustry.type.ItemStack.with;

public class ECCrafter {
    public static void load(){
        for (int i = 1 ; i <= MAX_LEVEL; i++){

            compressCrafter(i);

        }
    }

    public static void compressCrafter(int num){

        new MultiCrafter("c" + num + " compressor"){{
            requirements(Category.crafting, with(
                    ECItems.get(Items.silicon).get(num-1),5
            ));
            size = 2;

            for (Item item:items){
                if (ECItems.get(item)==null||ECItems.get(item).size!=10) continue;

                recipes.add(new Recipe(){{

                    inputItems = new ItemStack[]{
                            new ItemStack(ECItems.get(item).get(num-1),9)
                    };

                    outputItems = new ItemStack[]{
                            new ItemStack(ECItems.get(item).get(num),1)
                    };

                    crafterTime = 60f;

                }});


            }


            for (Liquid liquid:liquids){


                if (ECLiquids.get(liquid)==null||ECLiquids.get(liquid).size!=10) continue;

                recipes.add(new Recipe(){{

                    inputLiquids = new LiquidStack[]{
                            new LiquidStack(ECLiquids.get(liquid).get(num-1),9)
                    };
                    outputLiquids = new LiquidStack[]{
                            new LiquidStack(ECLiquids.get(liquid).get(num),1)
                    };


                    crafterTime = 60f;

                }});


            }





        }};



    }


}
