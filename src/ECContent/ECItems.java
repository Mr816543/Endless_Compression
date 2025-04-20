package ECContent;

import ECType.ECItem;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.type.Item;

import static ECConfig.ECSetting.MAX_LEVEL;

public class ECItems {
    public static ObjectMap<Item, Seq<Item>> ECItems = new ObjectMap<>();
    public static Seq<Item> items;

    public static void load() throws IllegalAccessException {
        items = Vars.content.items().copy();
        for (Item item : items) {
            if (!item.isVanilla()) continue;
            if (item.isHidden()) continue;
            compressItem(item);
        }
    }

    public static void compressItem(Item I) throws IllegalAccessException {
        ECItems.put(I, new Seq<>());
        ECItems.get(I).add(I);
        for (int i = 1; i <= MAX_LEVEL; i++) {
            ECItem child = new ECItem(I, i);
            ECItems.get(I).add(child);
        }
    }


}

