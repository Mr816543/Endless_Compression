package ECContents;

import ECConfig.ECTool;
import ECType.ECBlockTypes.*;
import ECType.ECTurretTypes.ECItemTurret;
import ECType.ECTurretTypes.ECLiquidTurret;
import ECType.ECTurretTypes.ECPowerTurret;
import arc.Events;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.game.EventType;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.world.Block;
import mindustry.world.Edges;
import mindustry.world.Tile;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.defense.turrets.LiquidTurret;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import mindustry.world.blocks.distribution.Conveyor;
import mindustry.world.blocks.distribution.StackConveyor;
import mindustry.world.blocks.liquid.ArmoredConduit;
import mindustry.world.blocks.liquid.Conduit;
import mindustry.world.blocks.liquid.LiquidRouter;
import mindustry.world.blocks.power.ConsumeGenerator;
import mindustry.world.blocks.production.Drill;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.production.Pump;
import mindustry.world.blocks.production.SolidPump;
import mindustry.world.blocks.units.Reconstructor;
import mindustry.world.blocks.units.UnitFactory;
import mindustry.world.consumers.Consume;
import mindustry.world.consumers.ConsumeItemFilter;
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
                //工厂
                case "GenericCrafter" -> new ECGenericCrafter((GenericCrafter) root);
                //消耗类发电厂
                case "ConsumeGenerator" -> {
                    Seq<String> CGNames = new Seq<>(new String[]{
                            "differential-generator","chemical-combustion-chamber","pyrolysis-generator"
                    });
                    if (CGNames.indexOf(root.name)!=-1){
                        new ECConsumeGenerator((ConsumeGenerator)root);
                    }else {
                        new ECConsumeItemFilterGenerator((ConsumeGenerator) root);
                    }
                }


                //钻头
                case "Drill" -> {
                    for (int i = 1; i <= 9; i++) new ECDrill((Drill) root, i);
                }

                //泵
                case "Pump" -> {
                    for(int i = 1;i <= 9;i++) new ECPump((Pump) root,i);
                }
                //产液工厂\
                case "SolidPump" , "Fracker" -> {
                    for (int i = 1 ; i <= 9 ; i++) new ECSolidPump((SolidPump) root,i);
                }
                case "Conduit" ->{
                    for (int i = 1 ; i <= 9 ; i++) new ECConduit((Conduit) root,i);
                }
                case "ArmoredConduit"-> {
                    for (int i = 1 ; i <= 9 ; i++) new ECArmoredConduit((ArmoredConduit) root,i);
                }
                case "LiquidRouter" -> {
                    for (int i = 1 ; i <= 9 ; i++) new ECLiquidRouter((LiquidRouter) root,i);
                }

                //单位工厂
                case "UnitFactory" -> new ECUnitFactory((UnitFactory) root);
                //单位重构工厂
                case "Reconstructor" -> {
                    for (int i = 1 ; i <=MAX_LEVEL;i++) new ECReconstructor((Reconstructor) root,i);
                }



                //炮台
                case "ItemTurret" -> new ECItemTurret((ItemTurret) root);
                case "LiquidTurret" -> new ECLiquidTurret((LiquidTurret) root);
                case "PowerTurret" -> new ECPowerTurret((PowerTurret) root);

                //传送带
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

                //城墙
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
