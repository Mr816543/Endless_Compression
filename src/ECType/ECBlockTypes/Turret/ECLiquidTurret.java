package ECType.ECBlockTypes.Turret;

import ECConfig.Config;
import ECConfig.ECData;
import ECConfig.ECSetting;
import ECConfig.ECTool;
import ECType.ECItem;
import ECType.ECLiquid;
import arc.Core;
import arc.math.Mathf;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.ctype.UnlockableContent;
import mindustry.entities.bullet.BulletType;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.defense.turrets.LiquidTurret;

public class ECLiquidTurret extends LiquidTurret {

    public LiquidTurret root;

    public static Config config = new Config().addConfigSimple(null,"buildType","ammoTypes");

    public ECLiquidTurret(LiquidTurret root) throws IllegalAccessException {
        super("compression-"+root.name);

        this.root = root;

        ECTool.compress(root,this, UnlockableContent.class , config, 0);
        ECTool.loadCompressContentRegion(root, this);
        ECTool.setIcon(root, this, 0);
        ECTool.loadHealth(this,root,1);
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
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        consumeBuilder = ECTool.consumeBuilderCopy(root,1);
        super.init();
    }

    public void initAmmo() throws IllegalAccessException {
        ObjectMap<Liquid, BulletType> rootAmmos = root.ammoTypes.copy();
        Seq<Liquid> liquids = rootAmmos.keys().toSeq();




        for (Liquid liquid : liquids){
            if (!ECData.hasECContent(liquid))continue;
            BulletType rootAmmo = rootAmmos.get(liquid);
            for (int i = 0; i <= ECSetting.MAX_LEVEL; i++){
                Liquid childItem = ECData.get(liquid,i);
                BulletType childAmmo = ECTool.compressBulletType(rootAmmo,i);
                ammoTypes.put(childItem,childAmmo);
            }




        }


    }

    public class ECLiquidTurretBuild extends LiquidTurretBuild {
        @Override
        public float range(){
            if(peekAmmo() != null){

                int level = 0;

                for (Liquid liquid : ammoTypes.keys()){
                    if (ammoTypes.get(liquid) != peekAmmo()) continue;
                    if (liquid instanceof ECLiquid ecLiquid) {
                        level = ecLiquid.level;
                    }

                }
                return (range + peekAmmo().rangeChange) * Mathf.pow(ECSetting.SCALE_MULTIPLIER,level) ;
            }
            return range;
        }
    }


}
