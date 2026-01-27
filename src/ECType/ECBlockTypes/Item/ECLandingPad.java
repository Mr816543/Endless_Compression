package ECType.ECBlockTypes.Item;

import ECConfig.Config;
import ECConfig.ECData;
import ECConfig.ECSetting;
import ECConfig.ECTool;
import arc.Core;
import arc.math.Interp;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.gen.Call;
import mindustry.type.Item;
import mindustry.world.blocks.campaign.LandingPad;
import mindustry.world.blocks.defense.RegenProjector;
import mindustry.world.consumers.Consume;
import mindustry.world.consumers.ConsumePower;

import static mindustry.Vars.headless;
import static mindustry.Vars.state;

public class ECLandingPad extends LandingPad {

    public static Config config = new Config().addConfigSimple(null, "buildType")
            .scaleConfig("itemCapacity")
            .linearConfig();
    public LandingPad root;
    public int level;


    public ECLandingPad(LandingPad root, int level) throws IllegalAccessException {
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

        consumeLiquid = ECData.get(root.consumeLiquid,level);

        ECData.register(root, this, level);
    }


    @Override
    public void init() {
        if (Core.settings.getBool("simpleLaunch")){
            Seq<Consume> consumes = new Seq<>();
            try {
                for (Consume consume : root.consumers) {
                    if (consume instanceof ConsumePower) {

                        ConsumePower c = new ConsumePower(0, 0, false);
                        ECTool.compress(consume, c, Object.class, Config.NULL, 0);
                        if (c.usage > 0) {
                            c.usage *= Mathf.pow(ECSetting.LINEAR_MULTIPLIER, level);
                        }
                        if (c.capacity > 0){
                            c.capacity *= Mathf.pow(ECSetting.LINEAR_MULTIPLIER, level);
                        }
                        consumes.add(c);
                    }
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            consumeBuilder = consumes;
        }else {
            consumeBuilder = ECTool.consumeBuilderCopy(root, level);
        }
        super.init();
    }


    public class ECLandingPadBuild extends LandingPadBuild {


        @Override
        public void updateTile() {
            super.updateTile();
            if (Core.settings.getBool("simpleLaunch")){
                liquids.set(consumeLiquid,liquidCapacity);
            }
        }

        @Override
        public boolean dump(Item item) {
            return ECTool.dump(this, item);
        }
    }
}
