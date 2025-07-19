package ECContents;

import ECConfig.ECData;
import ECConfig.ECSetting;
import ECConfig.ECTool;
import ECType.ECBlockTypes.*;
import ECType.ECTurretTypes.ECItemTurret;
import ECType.ECTurretTypes.ECLiquidTurret;
import ECType.ECTurretTypes.ECPowerTurret;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.world.Block;
import mindustry.world.Edges;
import mindustry.world.Tile;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.blocks.defense.turrets.BaseTurret;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.defense.turrets.LiquidTurret;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import mindustry.world.blocks.distribution.Conveyor;
import mindustry.world.blocks.distribution.StackConveyor;
import mindustry.world.blocks.production.AttributeCrafter;
import mindustry.world.blocks.production.BurstDrill;
import mindustry.world.blocks.production.Drill;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.units.Reconstructor;
import mindustry.world.blocks.units.UnitFactory;
import mindustry.world.meta.BuildVisibility;

import static ECConfig.ECSetting.MAX_LEVEL;

public class ECBlocks {

    public static Seq<Block> blocks;

    public static Seq<ECCompressCrafter> ecCompressCrafters = new Seq<>();

    public static void load() throws IllegalAccessException {

        blocks = Vars.content.blocks().copy();


        for (int i = 1; i <= MAX_LEVEL; i++) {
            ECCompressCrafter compressCrafter = new ECCompressCrafter(i);
            ecCompressCrafters.add(compressCrafter);
        }


        for (Block root : blocks) {
            if (root.buildVisibility == BuildVisibility.debugOnly) continue;
            if (root.isModded()) continue;
            if (root.isHidden()) continue;

            String cn = ECTool.getClassName(root.getClass());

            switch (cn){
                case "GenericCrafter" -> new ECGenericCrafter((GenericCrafter) root);

                case "Drill" -> {
                    for (int i = 1; i <= 9; i++) new ECDrill((Drill) root, i);
                }
                case "UnitFactory" -> new ECUnitFactory((UnitFactory) root);
                case "Reconstructor" -> {
                    for (int i = 1 ; i <=MAX_LEVEL;i++) new ECReconstructor((Reconstructor) root,i);
                }
                case "ItemTurret" -> new ECItemTurret((ItemTurret) root);
                case "LiquidTurret" -> new ECLiquidTurret((LiquidTurret) root);
                case "PowerTurret" -> new ECPowerTurret((PowerTurret) root);


                case "Conveyor" -> {
                    for (int i = 1 ; i <= MAX_LEVEL;i++) new ECConveyor((Conveyor) root, i);
                }
                case "StackConveyor" -> {
                    for (int i = 1 ; i <= MAX_LEVEL;i++) new ECStackConveyor((StackConveyor) root, i);
                }
                case "ArmoredConveyor" -> {
                    for (int i = 1 ; i <= MAX_LEVEL;i++) new ECConveyor((Conveyor) root, i){{

                        noSideBlend = true;
                    }
                        @Override
                        public boolean blends(Tile tile, int rotation, int otherx, int othery, int otherrot, Block otherblock){
                            return (otherblock.outputsItems() && blendsArmored(tile, rotation, otherx, othery, otherrot, otherblock)) ||
                                    (lookingAt(tile, rotation, otherx, othery, otherblock) && otherblock.hasItems);
                        }

                        @Override
                        public boolean blendsArmored(Tile tile, int rotation, int otherx, int othery, int otherrot, Block otherblock){
                            return Point2.equals(tile.x + Geometry.d4(rotation).x, tile.y + Geometry.d4(rotation).y, otherx, othery)
                                    || ((!otherblock.rotatedOutput(otherx, othery, tile) && Edges.getFacingEdge(otherblock, otherx, othery, tile) != null &&
                                    Edges.getFacingEdge(otherblock, otherx, othery, tile).relativeTo(tile) == rotation) ||
                                    (otherblock instanceof Conveyor && otherblock.rotatedOutput(otherx, othery, tile) && Point2.equals(otherx + Geometry.d4(otherrot).x, othery + Geometry.d4(otherrot).y, tile.x, tile.y)));
                        }

                        public class ECArmoredConveyorBuild extends ECConveyorBuild{
                            @Override
                            public boolean acceptItem(Building source, Item item){
                                return super.acceptItem(source, item) && (source.block instanceof Conveyor || Edges.getFacingEdge(source.tile, tile).relativeTo(tile) == rotation);
                            }
                        }
                    };
                }
                case "Wall" -> {
                    for (int i = 1 ; i <= MAX_LEVEL;i++) new ECWall((Wall) root, i);
                }
                case "" -> {}
            }



        }


    }

    public static void init(){

        int speed = 0;
        for (ECCompressCrafter crafter:ecCompressCrafters){
            if (crafter.unlocked()){
                speed += 1;
            }
        }

        for (ECCompressCrafter crafter:ecCompressCrafters){
            int speedPow = (int) Math.pow(9,speed- crafter.level);
            for (ECMultiCrafter.Recipe r : crafter.recipes){
                for (ItemStack itemStack : r.inputItems){
                    itemStack.amount *= speedPow;
                }
                for (ItemStack itemStack : r.outputItems){
                    itemStack.amount *= speedPow;
                }
                for (LiquidStack liquidStack : r.inputLiquids){
                    liquidStack.amount *= speedPow;
                }
                for (LiquidStack liquidStack : r.outputLiquids){
                    liquidStack.amount *= speedPow;
                }
            }
            crafter.initCapacity();
        }



    }


}
