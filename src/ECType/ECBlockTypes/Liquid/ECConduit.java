package ECType.ECBlockTypes.Liquid;

import ECConfig.Config;
import ECConfig.ECData;
import ECConfig.ECSetting;
import ECConfig.ECTool;
import arc.Core;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.entities.Puddles;
import mindustry.gen.Building;
import mindustry.type.Liquid;
import mindustry.world.Tile;
import mindustry.world.blocks.liquid.Conduit;

public class ECConduit extends Conduit{

    public Conduit root;

    public int level;

    public float outputMultiple;

    public static Config config = new Config().addConfigSimple(null, "buildType")
            .scaleConfig().linearConfig("liquidCapacity");

    public ECConduit(Conduit root, int level) throws IllegalAccessException {
        super("c" + level + "-" + root.name);
        this.root = root;
        this.level = level;
        this.outputMultiple = Mathf.pow(ECSetting.LINEAR_MULTIPLIER,level);
        ECTool.compress(root, this, config, level);
        ECTool.loadCompressContentRegion(root, this);
        ECTool.setIcon(root, this, level);
        ECTool.loadHealth(this,root,level);
        requirements(root.category, root.buildVisibility, ECTool.compressItemStack(root.requirements,level));

        localizedName = level + Core.bundle.get("num-Compression.localizedName") + root.localizedName;
        description = root.description;
        details = root.details;

        ECData.register(root,this,level);
    }


    @Override
    public void init() {
        super.init();
    }

    public class ECConduitBuild extends ConduitBuild{
        @Override
        public void updateTile() {
            smoothLiquid = Mathf.lerpDelta(smoothLiquid, liquids.currentAmount() / liquidCapacity, 0.05f);

            if(liquids.currentAmount() > 0.0001f && timer(timerFlow, 1)){
                this.moveLiquidForward(leaks, liquids.current());
                noSleep();
            }else{
                sleep();
            }
        }

        @Override
        public float moveLiquidForward(boolean leaks, Liquid liquid) {
            Tile next = this.tile.nearby(this.rotation);
            if (next == null) {
                return 0.0F;
            } else if (next.build != null) {
                return this.moveLiquid(next.build, liquid);
            } else {
                if (leaks && !next.block().solid && !next.block().hasLiquids) {
                    float leakAmount = this.liquids.get(liquid) / 1.5F;
                    Puddles.deposit(next, this.tile, liquid, leakAmount, true, true);
                    this.liquids.remove(liquid, leakAmount);
                }

                return 0.0F;
            }
        }

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
