package ECContent;

import ECType.ECSetting;
import ECType.Tool;
import arc.Core;
import arc.graphics.Pixmap;
import arc.graphics.Texture;
import arc.graphics.TextureData;
import arc.graphics.g2d.TextureAtlas;
import arc.graphics.g2d.TextureRegion;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.Vars;
import mindustry.type.Item;

import static ECType.ECSetting.*;
import static ECType.Tool.*;

public class ECItems {
    public static ObjectMap<Item, Seq<Item>> ECItems = new ObjectMap<>();
    public static Seq<Item> items;

    public static void load() throws IllegalAccessException {
        items = Vars.content.items().copy();
        for (Item item : items) {
            if (!item.isVanilla()) continue;
            if (item.isHidden()) continue;
            createCompressionItem(item);
            Log.info(item.localizedName);
        }
    }

    public static void createCompressionItem(Item I) throws IllegalAccessException {

        ECItems.put(I,new Seq<>());
        ECItems.get(I).add(I);

        for (int i = 1; i <= MAX_LEVEL; i++){
            int finalI = i;

            Item ECItem = new Item("c"+finalI+" "+I.name) {{
                /*/
                color = I.color;
                explosiveness = I.explosiveness;
                flammability = I.flammability;
                radioactivity = I.radioactivity;
                charge = I.charge;
                hardness = I.hardness;
                cost = I.cost;
                healthScaling = I.healthScaling;
                lowPriority = I.lowPriority;
                frames = I.frames;
                transitionFrames = I.transitionFrames;
                frameTime = I.frameTime;
                buildable = I.buildable;
                hidden = I.hidden;
                hiddenOnPlanets = I.hiddenOnPlanets;

                //*/
                localizedName = finalI +Core.bundle.get("Compression.localizedName") + I.localizedName;
                details = I.details;

                alwaysUnlocked = true;
                setCompressionIcon(I,this,finalI);
            }};

            //初始化
            String[] LI = new String[]{};
            String[] LF = new String[]{"explosiveness","flammability","radioactivity","charge"};
            String[] SI = new String[]{"hardness"};
            String[] SF = new String[]{"cost","healthScaling"};

            ObjectMap<String,Float> intV = new ObjectMap<>();
            ObjectMap<String,Float> floatV = new ObjectMap<>();


            //配置
            putAllTo(intV,LI,LINEAR_MULTIPLIER);
            putAllTo(floatV,LF,LINEAR_MULTIPLIER);
            putAllTo(intV,SI,SCALE_MULTIPLIER);
            putAllTo(floatV,SF,SCALE_MULTIPLIER);

            compress(I,ECItem,finalI,intV,floatV);

            ECItems.get(I).add(ECItem);
        }

    }

    public static void setCompressionIcon(Item item, Item comppressItem,int num){
        if (item.uiIcon!=null){
            comppressItem.uiIcon = comppressItem.fullIcon = combineRegions(item.uiIcon,num);
        }else {
            Core.app.post(() -> setCompressionIcon(item,comppressItem,num));
        }
    }

}

