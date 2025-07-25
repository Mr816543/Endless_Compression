package ECContents;

import ECConfig.ECTool;
import ECType.ECBlockTypes.*;
import ECType.ECBlockTypes.Crafter.ECDrill;
import ECType.ECBlockTypes.Crafter.ECGenericCrafter;
import ECType.ECBlockTypes.Crafter.ECMultiCrafter;
import ECType.ECBlockTypes.Generator.ECConsumeGenerator;
import ECType.ECBlockTypes.Generator.ECConsumeItemFilterGenerator;
import ECType.ECBlockTypes.Item.*;
import ECType.ECBlockTypes.Liquid.*;
import ECType.ECBlockTypes.Power.ECBattery;
import ECType.ECBlockTypes.Power.ECPowerNode;
import ECType.ECBlockTypes.Turret.ECItemTurret;
import ECType.ECBlockTypes.Turret.ECLiquidTurret;
import ECType.ECBlockTypes.Turret.ECPowerTurret;
import ECType.ECBlockTypes.Unit.ECReconstructor;
import ECType.ECBlockTypes.Unit.ECUnitFactory;
import arc.Core;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.struct.Seq;
import mindustry.Vars;
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
import mindustry.world.blocks.distribution.OverflowGate;
import mindustry.world.blocks.distribution.Sorter;
import mindustry.world.blocks.distribution.StackConveyor;
import mindustry.world.blocks.liquid.ArmoredConduit;
import mindustry.world.blocks.liquid.Conduit;
import mindustry.world.blocks.liquid.LiquidRouter;
import mindustry.world.blocks.power.Battery;
import mindustry.world.blocks.power.ConsumeGenerator;
import mindustry.world.blocks.power.PowerNode;
import mindustry.world.blocks.production.Drill;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.production.Pump;
import mindustry.world.blocks.production.SolidPump;
import mindustry.world.blocks.storage.StorageBlock;
import mindustry.world.blocks.storage.Unloader;
import mindustry.world.blocks.units.Reconstructor;
import mindustry.world.blocks.units.UnitFactory;
import mindustry.world.meta.BuildVisibility;

import static ECConfig.ECSetting.MAX_LEVEL;

public class ECBlocks {

    public static Seq<Block> blocks;

    public static Seq<ECCompressCrafter> ecCompressCrafters = new Seq<>();
    public static Seq<ECMultipleCompressCrafter> ecMultipleCompressCrafters = new Seq<>();

    public static int unlockedLevel = 0;

    public static void load() throws IllegalAccessException {

        blocks = Vars.content.blocks().copy();


        for (int i = 1; i <= MAX_LEVEL; i++) {
            ECCompressCrafter compressCrafter = new ECCompressCrafter(i);
            if (Core.settings.getBool(compressCrafter.name + "-unlocked")) unlockedLevel+=1;
            ecCompressCrafters.add(compressCrafter);

            ECMultipleCompressCrafter multipleCompressCrafter = new ECMultipleCompressCrafter(i);
            ecMultipleCompressCrafters.add(multipleCompressCrafter);

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

                //电力节点
                case "PowerNode" -> {
                    for (int i = 1 ; i <= 9 ; i++) new ECPowerNode((PowerNode) root,i);
                }
                //电池
                case "Battery" -> {
                    for (int i = 1 ; i <=9;i++) new ECBattery((Battery) root,i);
                }


                //钻头
                case "Drill" -> {
                    for (int i = 1; i <= 9; i++) new ECDrill((Drill) root, i);
                }

                //泵
                case "Pump" -> {
                    for(int i = 1;i <= 9;i++) new ECPump((Pump) root,i);
                }
                //产液工厂
                case "SolidPump" , "Fracker" -> {
                    for (int i = 1 ; i <= 9 ; i++) new ECSolidPump((SolidPump) root,i);
                }
                //导管
                case "Conduit" ->{
                    for (int i = 1 ; i <= 9 ; i++) new ECConduit((Conduit) root,i);
                }
                case "ArmoredConduit"-> {
                    for (int i = 1 ; i <= 9 ; i++) new ECArmoredConduit((ArmoredConduit) root,i);
                }
                //液体路由器
                case "LiquidRouter" -> {
                    for (int i = 1 ; i <= 9 ; i++) new ECLiquidRouter((LiquidRouter) root,i);
                }

                //物品仓库
                case "StorageBlock" -> {
                    for (int i = 1 ; i <= 9 ; i++) new ECStorageBlock((StorageBlock) root,i);
                }
                //装卸器
                case "Unloader" -> {
                    for (int i = 1; i <= 9 ; i ++) new ECUnloader((Unloader)root,i);
                }
                //分类器
                //case "Sorter" -> new ECSorter((Sorter) root);
                //溢流门
                //case "OverflowGate" -> new ECOverflowGate((OverflowGate) root);

                //传送带
                case "Conveyor" -> {
                    for (int i = 1 ; i <= 5;i++) new ECConveyor((Conveyor) root, i);
                }
                case "StackConveyor" -> {
                    for (int i = 1 ; i <= MAX_LEVEL;i++) new ECStackConveyor((StackConveyor) root, i);
                }
                case "ArmoredConveyor" -> {
                    for (int i = 1 ; i <= MAX_LEVEL;i++) new ECConveyorOld((Conveyor) root, i){{

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


                //城墙
                case "Wall" -> {
                    for (int i = 1 ; i <= MAX_LEVEL;i++) new ECWall((Wall) root, i);
                }



                case "" -> {}
            }



        }


    }





}
