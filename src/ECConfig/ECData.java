package ECConfig;

import arc.struct.ObjectMap;
import arc.struct.Seq;
import mindustry.ctype.UnlockableContent;
import mindustry.entities.Effect;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.type.UnitType;
import mindustry.world.Block;

public class ECData {
    public static ObjectMap<Item, Seq<Item>> ECItems = new ObjectMap<>();
    public static ObjectMap<Liquid, Seq<Liquid>> ECLiquids = new ObjectMap<>();
    public static ObjectMap<Effect,Seq<Effect>> ECEffects = new ObjectMap<>();
    public static ObjectMap<UnitType, Seq<UnitType>> ECUnitTypes = new ObjectMap<>();
    public static ObjectMap<Block, Seq<Block>> ECBlocks = new ObjectMap<>();



    public static void register(Object root, Object child, int level){
        ObjectMap map = getMap(root);
        if (map== null) return;
        if (map.get(root)==null){
            map.put(root,new Seq<>());
            ((Seq)map.get(root)).add(root);
        }
        Seq seq = ((Seq)map.get(root));
        if (seq.size == level){
            seq.add(child);
        }
    }


    public static UnlockableContent getEC(UnlockableContent root, int level){
        if (root instanceof Item) return get((Item)root,level);
        if (root instanceof Liquid) return get((Liquid) root,level);
        if (root instanceof UnitType) return get((UnitType) root,level);
        if (root instanceof Block) return get((Block) root,level);
        return root;
    }

    public static ObjectMap getMap(Object root){
        if (root instanceof Item) return ECItems;
        if (root instanceof Liquid) return ECLiquids;
        if (root instanceof Effect) return ECEffects;
        if (root instanceof UnitType) return ECUnitTypes;
        if (root instanceof Block) return ECBlocks;
        return null;
    }

    public static int getECSize(Object root){
        ObjectMap map = getMap(root);
        if (map== null) return 0;
        if (map.get(root) ==null) return 0;
        return ((Seq)map.get(root)).size;
    }

    public static Item get(Item root, int level){
        if (ECItems.get(root)==null) return root;
        return ECItems.get(root).get(level);
    }
    public static Liquid get(Liquid root, int level){
        if (ECLiquids.get(root)==null) return root;
        return ECLiquids.get(root).get(level);
    }
    public static Effect get(Effect root, int level){
        if (ECEffects.get(root)==null) return root;
        return ECEffects.get(root).get(level);
    }
    public static UnitType get(UnitType root, int level){
        if (ECUnitTypes.get(root)==null) return root;
        return ECUnitTypes.get(root).get(level);
    }
    public static Block get(Block root, int level){
        if (ECBlocks.get(root)==null) return root;
        return ECBlocks.get(root).get(level);
    }

    public static Seq<UnlockableContent> getAllContentKeys(){
        Seq<UnlockableContent> keys = new Seq<>();

        keys.add(ECItems.keys().toSeq());
        keys.add(ECLiquids.keys().toSeq());
        keys.add(ECUnitTypes.keys().toSeq());
        keys.add(ECBlocks.keys().toSeq());


        return keys;
    }

    public static boolean hasECContent(UnlockableContent root){
        return getECSize(root)==10;
    }


}
