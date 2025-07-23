package ECType.ECBlockTypes.Turret;

import ECConfig.Config;
import ECConfig.ECData;
import ECConfig.ECSetting;
import ECConfig.ECTool;
import arc.Core;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import mindustry.ctype.UnlockableContent;
import mindustry.entities.bullet.BulletType;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.consumers.*;

public class ECItemTurret extends ItemTurret {

    public ItemTurret root;

    public static Config config = new Config().addConfigSimple(null,"buildType","ammoTypes").linearConfig("health");

    public ECItemTurret(ItemTurret root) throws IllegalAccessException {
        super("compression-"+root.name);

        this.root = root;

        ECTool.compress(root,this, UnlockableContent.class , config, 0);
        ECTool.loadCompressContentRegion(root, this);
        ECTool.setIcon(root, this, 0);
        requirements(root.category, root.buildVisibility, ECTool.compressItemStack(root.requirements,1));
        localizedName = Core.bundle.get("Compression.localizedName") + root.localizedName;
        description = root.description;
        details = root.details;

        ECData.register(root,this,1);
    }

    @Override
    public void init() {
        try {
            initAmmo();
            initCoolant();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        consumeBuilder = ECTool.consumeBuilderCopy(root,1);
        super.init();
    }

    public void initAmmo() throws IllegalAccessException {
        ObjectMap<Item, BulletType> rootAmmos = root.ammoTypes.copy();
        Seq<Item> items = rootAmmos.keys().toSeq();




        for (Item item : items){
            if (!ECData.hasECContent(item))continue;
            BulletType rootAmmo = rootAmmos.get(item);
            for (int i = 0 ; i <= ECSetting.MAX_LEVEL;i++){
                Item childItem = ECData.get(item,i);
                BulletType childAmmo = ECTool.compressBulletType(rootAmmo,i);
                ammoTypes.put(childItem,childAmmo);
            }




        }


    }

    boolean findLiquid(Seq<Liquid> liquids,Liquid liquid){

        for (Liquid l:liquids){
            if (l==liquid) return true;
        }

        return false;
    }

    public void initCoolant() throws IllegalAccessException {

        if (coolant instanceof ConsumeLiquid c){
            Liquid rLiquid = c.liquid;
            float amount = c.amount;

            if (!ECData.hasECContent(rLiquid)) return;

            Seq<Liquid> liquids = ECData.ECLiquids.get(rLiquid);



            coolant = new ConsumeCoolant(amount){{

                this.filter = liquid -> findLiquid(liquids,liquid) && liquid.coolant && (this.allowLiquid && !liquid.gas || this.allowGas && liquid.gas) && liquid.temperature <= maxTemp && liquid.flammability < maxFlammability;

            }};


        }



    }
}
