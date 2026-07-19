package ECConfig;

import ECContents.ECBlocks;
import ECContents.ECItems;
import ECContents.ECLiquids;
import ECContents.ECUnitTypes;
import ECType.ECBlockTypes.Item.ECCompressCrafter;
import ECType.ECBlockTypes.Item.ECMultipleCompressCrafter;
import arc.Events;
import mindustry.game.EventType;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.type.UnitType;
import mindustry.world.Block;
import mindustry.world.meta.BuildVisibility;

import static arc.Core.settings;

public class VisibilityReplace {

    private static int min = 1;

    private static int max = 9;

    public static void init() {
        // 绑定游戏主循环更新事件
        Events.run(EventType.Trigger.update, VisibilityReplace::onUpdate);
    }

    private static void onUpdate() {
        if (settings.getInt("MinShowLevel") > settings.getInt("MaxShowLevel")) {
            settings.putInt("MaxShowLevel", settings.getInt("MinShowLevel"));
        }
        if (min != settings.getInt("MinShowLevel", 1) || max != settings.getInt("MaxShowLevel", 9)) {
            min = settings.getInt("MinShowLevel", 1);
            max = settings.getInt("MaxShowLevel", 9);

            for (Block root : ECBlocks.blocks) {
                if (root.isHidden()) continue;
                if (ECData.getECSize(root) <= 2) continue;

                for (int i = 1; i < ECData.getECSize(root); i++) {
                    Block b = ECData.get(root, i);
                    if (i < min || i > max) {
                        b.buildVisibility = BuildVisibility.hidden;
                    } else {
                        b.buildVisibility = root.buildVisibility;
                    }
                }

            }
            for (ECCompressCrafter c : ECBlocks.ecCompressCrafters) {
                if (c.getLevel() < min || c.getLevel() > max) {
                    c.buildVisibility = BuildVisibility.hidden;
                } else {
                    c.buildVisibility = BuildVisibility.shown;
                }
            }
            for (ECMultipleCompressCrafter c : ECBlocks.ecMultipleCompressCrafters) {
                if (c.getLevel() < min || c.getLevel() > max) {
                    c.buildVisibility = BuildVisibility.hidden;
                } else {
                    c.buildVisibility = BuildVisibility.shown;
                }
            }

            for (Item root : ECItems.items) {
                if (root.isHidden()) continue;
                if (ECData.getECSize(root) <= 2) continue;

                for (int i = 1; i < ECData.getECSize(root); i++) {
                    Item b = ECData.get(root, i);
                    if (i < min || i > max) {
                        b.hidden = true;
                    } else {
                        b.hidden = root.hidden;
                    }
                }

            }

            for (Liquid root : ECLiquids.liquids) {
                if (root.isHidden()) continue;
                if (ECData.getECSize(root) <= 2) continue;

                for (int i = 1; i < ECData.getECSize(root); i++) {
                    Liquid b = ECData.get(root, i);
                    if (i < min || i > max) {
                        b.hidden = true;
                    } else {
                        b.hidden = root.hidden;
                    }
                }

            }

            for (UnitType root : ECUnitTypes.unitTypes) {
                if (root.isHidden()) continue;
                if (ECData.getECSize(root) <= 2) continue;

                for (int i = 1; i < ECData.getECSize(root); i++) {
                    UnitType b = ECData.get(root, i);
                    if (i < min || i > max) {
                        b.hidden = true;
                    } else {
                        b.hidden = root.hidden;
                    }
                }

            }

        }

    }

}
