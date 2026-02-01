package ECType.ECBlockTypes.Turret;

import ECConfig.Config;
import ECConfig.ECData;
import ECConfig.ECTool;
import arc.Core;
import arc.struct.ObjectMap;
import mindustry.type.Liquid;
import mindustry.world.blocks.defense.RegenProjector;
import mindustry.world.blocks.defense.turrets.ContinuousLiquidTurret;

public class ECContinuousLiquidTurret extends ContinuousLiquidTurret {

    public static Config config = new Config().addConfigSimple(null, "buildType","ammoTypes")
            .scaleConfig("range")
            .linearConfig();
    public ContinuousLiquidTurret root;
    public int level;


    public ECContinuousLiquidTurret(ContinuousLiquidTurret root, int level) throws IllegalAccessException {
        super("c" + level + "-" + root.name);
        this.root = root;
        this.level = level;
        ECTool.compress(root, this, config, level);
        ECTool.loadCompressContentRegion(root, this);
        ECTool.setIcon(root, this, level);
        ECTool.loadHealth(this, root, level);
        requirements(root.category, root.buildVisibility, ECTool.compressItemStack(root.requirements, level));

        localizedName = level + Core.bundle.get("num-Compression.localizedName") + root.localizedName;
        description = root.description;
        details = root.details;


        ECData.register(root, this, level);
    }

    @Override
    public void init() {
        ammoTypes = new ObjectMap<>();
        for (Liquid l : root.ammoTypes.keys()){
            if (ECData.hasECContent(l)){
                try {
                    ammoTypes.put(ECData.get(l,level),ECTool.compressBulletType(root.ammoTypes.get(l),level));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }else {
                ammoTypes.put(l,root.ammoTypes.get(l));
            }
        }
        //consumeBuilder = ECTool.consumeBuilderCopy(root, level,true);
        super.init();
    }


    public class ECContinuousLiquidTurretBuild extends ContinuousLiquidTurretBuild {

        @Override
        public void updateTile() {
            super.updateTile();
        }
    }
}
