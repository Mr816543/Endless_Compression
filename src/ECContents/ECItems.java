package ECContents;

import ECConfig.ECData;
import ECConfig.ECTool;
import ECType.ECItem;
import arc.Core;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.type.Item;

import static ECConfig.ECSetting.MAX_LEVEL;

public class ECItems {
    public static Seq<Item> items;

    public static void load() throws IllegalAccessException {
        items = Vars.content.items().copy();
        for (Item root : items) {
            if (root.isModded()) continue;
            if (root.isHidden()) continue;
            compressItem(root);
            if (Core.settings.getBool("oldContent"))compressOldItem(root);
        }
    }

    public static void compressItem(Item root) throws IllegalAccessException {
        for (int i = 1; i <= MAX_LEVEL; i++) {
            new ECItem(root, i);
        }
    }
    public static void compressOldItem(Item root) throws IllegalAccessException {
        for (int i = 1; i <= MAX_LEVEL; i++) {
            Item old = new Item(root.name+i);
            ECTool.setIcon(root,old,i);
        }
    }


}

