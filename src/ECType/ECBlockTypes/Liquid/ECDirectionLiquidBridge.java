package ECType.ECBlockTypes.Liquid;

import ECConfig.Config;
import ECConfig.ECData;
import ECConfig.ECSetting;
import ECConfig.ECTool;
import arc.Core;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.world.blocks.distribution.DirectionLiquidBridge;
import mindustry.world.blocks.distribution.DuctBridge;
import mindustry.world.blocks.liquid.LiquidBlock;
import mindustry.world.blocks.sandbox.ItemVoid;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

public class ECDirectionLiquidBridge extends DirectionLiquidBridge {

    public static Config config = new Config().addConfigSimple(null, "buildType")
            .scaleConfig("range")
            .linearConfig("liquidCapacity");
    public DirectionLiquidBridge root;
    public int level;
    public float outputMultiple;

    public ECDirectionLiquidBridge(DirectionLiquidBridge root, int level) throws IllegalAccessException {
        super("c" + level + "-" + root.name);
        this.root = root;
        this.level = level;
        this.outputMultiple = Mathf.pow(ECSetting.LINEAR_MULTIPLIER,level);
        ECTool.compress(root, this, config, level);
        ECTool.loadCompressContentRegion(root, this);
        ECTool.setIcon(root, this, level);
        ECTool.loadHealth(this,root,level);
        requirements(root.category, root.buildVisibility, ECTool.compressItemStack(root.requirements, level));

        localizedName = level + Core.bundle.get("num-Compression.localizedName") + root.localizedName;
        description = root.description;
        details = root.details;



        ECData.register(root, this, level);
    }

    @Override
    public void init() {
        consumeBuilder = ECTool.consumeBuilderCopy(root,level);
        super.init();
    }


    @Override
    public void setStats(){
        super.setStats();
    }

    public class ECDirectionLiquidBridgeBuild extends DuctBridgeBuild {


        @Override
        public float moveLiquid(Building next, Liquid liquid) {
            if (next == null) {
                return 0.0F;
            } else {
                next = next.getLiquidDestination(this, liquid);
                if (next.team == this.team && next.block.hasLiquids && this.liquids.get(liquid) > 0.0F) {
                    float ofract = next.liquids.get(liquid) / next.block.liquidCapacity;
                    float fract = this.liquids.get(liquid) / this.block.liquidCapacity * this.block.liquidPressure;
                    float flow = Math.min(Mathf.clamp(fract - ofract) * this.block.liquidCapacity, this.liquids.get(liquid));
                    flow = Math.min(flow, next.block.liquidCapacity - next.liquids.get(liquid));
                    if (flow > 0.0F && ofract <= fract && next.acceptLiquid(this, liquid)) {

                        float max = Math.min(next.block.liquidCapacity-next.liquids.get(liquid) ,this.liquids.get(liquid) );




                        next.handleLiquid(this, liquid, flow);
                        this.liquids.remove(liquid, flow);
                        return flow;
                    }

                    if (!next.block.consumesLiquid(liquid) && next.liquids.currentAmount() / next.block.liquidCapacity > 0.1F && fract > 0.1F) {
                        float fx = (this.x + next.x) / 2.0F;
                        float fy = (this.y + next.y) / 2.0F;
                        Liquid other = next.liquids.current();
                        if (other.blockReactive && liquid.blockReactive) {
                            if ((!(other.flammability > 0.3F) || !(liquid.temperature > 0.7F)) && (!(liquid.flammability > 0.3F) || !(other.temperature > 0.7F))) {
                                if (liquid.temperature > 0.7F && other.temperature < 0.55F || other.temperature > 0.7F && liquid.temperature < 0.55F) {
                                    this.liquids.remove(liquid, Math.min(this.liquids.get(liquid), 0.7F * Time.delta * outputMultiple));
                                    if (Mathf.chanceDelta(0.20000000298023224)) {
                                        Fx.steam.at(fx, fy);
                                    }
                                }
                            } else {
                                this.damageContinuous(1.0F);
                                next.damageContinuous(1.0F);
                                if (Mathf.chanceDelta(0.1)) {
                                    Fx.fire.at(fx, fy);
                                }
                            }
                        }
                    }
                }

                return 0.0F;
            }
        }
    }
}
