package ECContents;

import ECConfig.ECTool;
import ECType.ECBlockTypes.Crafter.ECAttributeCrafter;
import ECType.ECBlockTypes.Crafter.ECDrill;
import ECType.ECBlockTypes.Crafter.ECGenericCrafter;
import ECType.ECBlockTypes.Defend.*;
import ECType.ECBlockTypes.Generator.ECConsumeGenerator;
import ECType.ECBlockTypes.Generator.ECConsumeItemFilterGenerator;
import ECType.ECBlockTypes.Item.*;
import ECType.ECBlockTypes.Liquid.*;
import ECType.ECBlockTypes.Power.*;
import ECType.ECBlockTypes.SandBox.ECItemSource;
import ECType.ECBlockTypes.SandBox.ECLiquidSource;
import ECType.ECBlockTypes.SandBox.ECPowerSource;
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
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.content.TechTree;
import mindustry.gen.Building;
import mindustry.type.Category;
import mindustry.type.Item;
import mindustry.world.Block;
import mindustry.world.Edges;
import mindustry.world.Tile;
import mindustry.world.blocks.defense.ForceProjector;
import mindustry.world.blocks.defense.MendProjector;
import mindustry.world.blocks.defense.OverdriveProjector;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.defense.turrets.LiquidTurret;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import mindustry.world.blocks.distribution.Conveyor;
import mindustry.world.blocks.distribution.MassDriver;
import mindustry.world.blocks.distribution.StackConveyor;
import mindustry.world.blocks.liquid.ArmoredConduit;
import mindustry.world.blocks.liquid.Conduit;
import mindustry.world.blocks.liquid.LiquidRouter;
import mindustry.world.blocks.power.*;
import mindustry.world.blocks.production.*;
import mindustry.world.blocks.sandbox.ItemSource;
import mindustry.world.blocks.sandbox.LiquidSource;
import mindustry.world.blocks.sandbox.PowerSource;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.blocks.storage.StorageBlock;
import mindustry.world.blocks.storage.Unloader;
import mindustry.world.blocks.units.Reconstructor;
import mindustry.world.blocks.units.RepairTurret;
import mindustry.world.blocks.units.UnitFactory;
import mindustry.world.meta.BuildVisibility;

import static ECConfig.ECSetting.MAX_LEVEL;
import static mindustry.type.ItemStack.with;

public class ECBlocks {

    public static Seq<Block> blocks;

    public static Seq<ECCompressCrafter> ecCompressCrafters = new Seq<>();
    public static Seq<ECMultipleCompressCrafter> ecMultipleCompressCrafters = new Seq<>();

    public static int unlockedLevel = 0;

    public static void load() throws IllegalAccessException {

        blocks = Vars.content.blocks().copy();




        for (int i = 1; i <= MAX_LEVEL; i++) {
            ECCompressCrafter compressCrafter = new ECCompressCrafter(i);
            if (Core.settings.getBool(compressCrafter.name + "-unlocked")) unlockedLevel += 1;
            ecCompressCrafters.add(compressCrafter);

            ECMultipleCompressCrafter multipleCompressCrafter = new ECMultipleCompressCrafter(i);
            ecMultipleCompressCrafters.add(multipleCompressCrafter);

        }


        for (Block root : blocks) {
            if (root.buildVisibility == BuildVisibility.debugOnly) continue;
            if (root.isModded()) continue;
            if (root.isHidden() && root.buildVisibility != BuildVisibility.sandboxOnly && root.buildVisibility != BuildVisibility.legacyLaunchPadOnly) continue;

            String cn = ECTool.getClassName(root.getClass());

            switch (cn) {
                //工厂
                case "GenericCrafter" -> new ECGenericCrafter((GenericCrafter) root);
                //环境工厂
                case "AttributeCrafter" -> new ECAttributeCrafter((AttributeCrafter) root);

                //消耗类发电厂
                case "ConsumeGenerator" -> {
                    Seq<String> CGNames = new Seq<>(new String[]{
                            "differential-generator", "chemical-combustion-chamber", "pyrolysis-generator"
                    });
                    if (CGNames.indexOf(root.name) != -1) {
                        new ECConsumeGenerator((ConsumeGenerator) root);
                    } else {
                        new ECConsumeItemFilterGenerator((ConsumeGenerator) root);
                    }
                }
                //钍反应堆
                case "NuclearReactor" -> {
                    for (int i = 1; i <= MAX_LEVEL; i++) new ECNuclearReactor((NuclearReactor) root,i);
                }
                //冲击反应堆
                case "ImpactReactor" -> {
                    for (int i = 1; i <= MAX_LEVEL; i++) new ECImpactReactor((ImpactReactor) root,i);
                }
                //地热
                case "ThermalGenerator" -> {
                    for (int i = 1; i <= MAX_LEVEL; i++) new ECThermalGenerator((ThermalGenerator) root,i);
                }

                //电力节点
                case "PowerNode" -> {
                    for (int i = 1; i <= MAX_LEVEL; i++) new ECPowerNode((PowerNode) root, i);
                }
                //电池
                case "Battery" -> {
                    for (int i = 1; i <= MAX_LEVEL; i++) new ECBattery((Battery) root, i);
                }


                //钻头
                case "Drill" -> {
                    for (int i = 1; i <= MAX_LEVEL; i++) new ECDrill((Drill) root, i);
                }

                //泵
                case "Pump" -> {
                    for (int i = 1; i <= MAX_LEVEL; i++) new ECPump((Pump) root, i);
                }
                //产液工厂
                case "SolidPump", "Fracker" -> {
                    for (int i = 1; i <= MAX_LEVEL; i++) new ECSolidPump((SolidPump) root, i);
                }
                //导管
                case "Conduit" -> {
                    for (int i = 1; i <= MAX_LEVEL; i++) new ECConduit((Conduit) root, i);
                }
                case "ArmoredConduit" -> {
                    for (int i = 1; i <= MAX_LEVEL; i++) new ECArmoredConduit((ArmoredConduit) root, i);
                }
                //液体路由器
                case "LiquidRouter" -> {
                    for (int i = 1; i <= MAX_LEVEL; i++) new ECLiquidRouter((LiquidRouter) root, i);
                }

                //物品仓库
                case "StorageBlock" -> {
                    for (int i = 1; i <= MAX_LEVEL; i++) new ECStorageBlock((StorageBlock) root, i);
                }
                //装卸器
                case "Unloader" -> {
                    for (int i = 1; i <= MAX_LEVEL; i++) new ECUnloader((Unloader) root, i);
                }
                //分类器
                //case "Sorter" -> new ECSorter((Sorter) root);
                //溢流门
                //case "OverflowGate" -> new ECOverflowGate((OverflowGate) root);
                //核心
                case "CoreBlock" -> {
                    if (!Core.settings.getBool("banContent"))
                        for (int i = 1; i <= MAX_LEVEL; i++) new ECCoreBlock((CoreBlock) root, i);
                }
                //质驱
                case "MassDriver" -> {
                    for (int i = 1; i <= MAX_LEVEL; i++) new ECMassDriver((MassDriver) root, i);
                }


                //生产源
                case "PowerSource" -> new ECPowerSource((PowerSource) root);
                case "ItemSource" -> new ECItemSource((ItemSource) root);
                case "LiquidSource" -> new ECLiquidSource((LiquidSource) root);


                //传送带
                case "Conveyor" -> {
                    for (int i = 1; i <= 5; i++) new ECConveyor((Conveyor) root, i);
                }
                case "StackConveyor" -> {
                        for (int i = 1; i <= MAX_LEVEL; i++) new ECStackConveyor((StackConveyor) root, i);
                }
                case "ArmoredConveyor" -> {
                        for (int i = 1; i <= 5; i++)
                            new ECConveyor((Conveyor) root, i) {
                                {
                                    noSideBlend = true;
                                }

                                @Override
                                public boolean blends(Tile tile, int rotation, int otherx, int othery, int otherrot, Block otherblock) {
                                    return (otherblock.outputsItems() && blendsArmored(tile, rotation, otherx, othery, otherrot, otherblock)) ||
                                            (lookingAt(tile, rotation, otherx, othery, otherblock) && otherblock.hasItems);
                                }

                                @Override
                                public boolean blendsArmored(Tile tile, int rotation, int otherx, int othery, int otherrot, Block otherblock) {
                                    return Point2.equals(tile.x + Geometry.d4(rotation).x, tile.y + Geometry.d4(rotation).y, otherx, othery)
                                            || ((!otherblock.rotatedOutput(otherx, othery, tile) && Edges.getFacingEdge(otherblock, otherx, othery, tile) != null &&
                                            Edges.getFacingEdge(otherblock, otherx, othery, tile).relativeTo(tile) == rotation) ||
                                            (otherblock instanceof Conveyor && otherblock.rotatedOutput(otherx, othery, tile) && Point2.equals(otherx + Geometry.d4(otherrot).x, othery + Geometry.d4(otherrot).y, tile.x, tile.y)));
                                }

                                public class ECArmoredConveyorBuild extends ECConveyorBuild {
                                    @Override
                                    public boolean acceptItem(Building source, Item item) {
                                        return super.acceptItem(source, item) && (source.block instanceof Conveyor || Edges.getFacingEdge(source.tile, tile).relativeTo(tile) == rotation);
                                    }
                                }
                            };
                }
                //发射台
                case "LaunchPad" ->{


                    if (Core.settings.getBool("testContent") && false){
                        ECTool.print(root.name);
                        if ("launch-pad".equals(root.name)) {


                            String localizedNameOld = root.localizedName;
                            String descriptionOld = root.description;
                            String detailsOld = root.details;


                            OldLaunchPad launchPad = new OldLaunchPad("launch-pad"){{
                                requirements(Category.effect, BuildVisibility.notLegacyLaunchPadOnly, with(Items.copper, 350, Items.silicon, 140, Items.lead, 200, Items.titanium, 150));
                                size = 3;
                                itemCapacity = 100;
                                launchTime = 60f * 20;
                                hasPower = true;

                                ECTool.loadCompressContentRegion(root, this);
                                ECTool.setIcon(root, this, 0);

                                localizedName = localizedNameOld;
                                description = descriptionOld;
                                details = detailsOld;

                                consumePower(4f);
                            }};
                            TechTree.TechNode node = TechTree.node(launchPad,() ->{});
                            node.parent = Blocks.advancedLaunchPad.techNode != null? Blocks.advancedLaunchPad.techNode:Blocks.advancedLaunchPad.techNodes.get(0);
                            node.parent.children.add(node);


                            for (int i = 1; i <= MAX_LEVEL; i++) new ECOldLaunchPad(launchPad, i , root);

                        }

                    }


                }



                //单位工厂
                case "UnitFactory" -> new ECUnitFactory((UnitFactory) root);
                //单位重构工厂
                case "Reconstructor" -> {
                    for (int i = 1; i <= MAX_LEVEL; i++) new ECReconstructor((Reconstructor) root, i);
                }


                //炮台
                case "ItemTurret" -> {
                    if (!Core.settings.getBool("banContent")) new ECItemTurret((ItemTurret) root);
                }
                case "LiquidTurret" -> new ECLiquidTurret((LiquidTurret) root);
                case "PowerTurret" -> new ECPowerTurret((PowerTurret) root);


                //城墙
                case "Wall" -> {
                    for (int i = 1; i <= MAX_LEVEL; i++) new ECWall((Wall) root, i);
                }
                //维修
                case "MendProjector" -> {
                    for (int i = 1; i <= MAX_LEVEL; i++) new ECMendProjector((MendProjector) root, i);
                }
                //维修点
                case "RepairTurret" -> {
                    for (int i = 1; i <= MAX_LEVEL; i++) new ECRepairTurret((RepairTurret) root, i);
                }
                //力墙
                case "ForceProjector" -> {
                    for (int i = 1; i <= MAX_LEVEL; i++) new ECForceProjector((ForceProjector) root, i);
                }
                //超速
                case "OverdriveProjector" -> {
                    for (int i = 1; i <= MAX_LEVEL; i++) new ECOverdriveProjector((OverdriveProjector) root, i);
                }
                case "" -> {
                }
            }


        }


    }


}
