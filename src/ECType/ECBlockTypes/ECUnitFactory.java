package ECType.ECBlockTypes;

import ECConfig.Config;
import ECConfig.ECData;
import ECConfig.ECSetting;
import ECConfig.ECTool;
import arc.Core;
import arc.Events;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.Vars;
import mindustry.ai.UnitCommand;
import mindustry.ctype.UnlockableContent;
import mindustry.game.EventType;
import mindustry.gen.Unit;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.type.UnitType;
import mindustry.world.Block;
import mindustry.world.blocks.payloads.PayloadBlock;
import mindustry.world.blocks.payloads.UnitPayload;
import mindustry.world.blocks.production.Drill;
import mindustry.world.blocks.production.HeatCrafter;
import mindustry.world.blocks.units.UnitFactory;
import mindustry.world.consumers.*;

public class ECUnitFactory extends UnitFactory {


    public UnitFactory root;

    public static Config config = new Config().addConfigSimple(null,"buildType","configurations");


    public ECUnitFactory(UnitFactory root) throws IllegalAccessException {
        super("compression-" + root.name);

        this.root = root;

        ECTool.compress(root,this, PayloadBlock.class,UnlockableContent.class , config, 0);
        ECTool.loadCompressContentRegion(root, this);
        ECTool.setIcon(root, this, 0);
        requirements(root.category, root.buildVisibility, ECTool.compressItemStack(root.requirements,1));

        localizedName = Core.bundle.get("Compression.localizedName") + root.localizedName;
        description = root.description;
        details = root.details;

        //*/
        for (Consume cons : consumeBuilder){
            if (cons instanceof  ConsumePower c){
                consumePower(c.usage);
            }
        }
        //*/

        configurable = true;

        config(Integer.class, (UnitFactoryBuild build, Integer i) -> {
            if(!configurable) return;

            if(build.currentPlan == i) return;
            build.currentPlan = i < 0 || i >= plans.size ? -1 : i;
            build.progress = 0;
            if(build.command != null && (build.unit() == null || !build.unit().commands.contains(build.command))){
                build.command = null;
            }
        });

        config(UnitType.class, (UnitFactoryBuild build, UnitType val) -> {
            if(!configurable) return;

            int next = plans.indexOf(p -> p.unit == val);
            if(build.currentPlan == next) return;
            build.currentPlan = next;
            build.progress = 0;
            if(build.command != null && !val.commands.contains(build.command)){
                build.command = null;
            }
        });

        config(UnitCommand.class, (UnitFactoryBuild build, UnitCommand command) -> build.command = command);
        configClear((UnitFactoryBuild build) -> build.command = null);

        ECData.register(root,this,1);

    }

    @Override
    public void init() {

        plans = new Seq<>();
        for (UnitFactory.UnitPlan unitPlan : root.plans){

            if (!ECData.hasECContent(unitPlan.unit)) continue;

            for (int i = 0 ; i <= ECSetting.MAX_LEVEL;i++){

                UnitType unit = ECData.get(unitPlan.unit,i);
                ItemStack[] r = ECTool.compressItemStack(unitPlan.requirements,i);
                float time = unitPlan.time;

                plans.add(new UnitPlan(unit,time,r));
            }
        }
        super.init();
    }

    public class ECUnitFactoryBuild extends UnitFactoryBuild{


        @Override
        public void updateTile() {
            efficiency *= power.status;
            super.updateTile();
        }
    }
}
