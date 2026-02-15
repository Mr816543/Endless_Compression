package ECType.ECBlockTypes.Crafter;

import ECConfig.EC;
import ECConfig.ECData;
import ECConfig.ECTool;
import arc.Core;
import mindustry.game.Team;
import mindustry.world.Tile;
import mindustry.world.blocks.production.AttributeCrafter;

public class ECAttributeCrafter extends ECGenericCrafter implements EC {

    public ECAttributeCrafter(AttributeCrafter root) throws IllegalAccessException {
        super("compression-" + root.name);

        this.root = root;

        requirements(root.category, root.buildVisibility, ECTool.compressItemStack(root.requirements, 1));
        localizedName = Core.bundle.get("Compression.localizedName") + root.localizedName;
        description = root.description;
        details = root.details;

        size = root.size;
        health = root.health;
        multiDrawer = true;

        attribute = root.attribute;
        baseEfficiency = root.baseEfficiency;
        boostScale = root.boostScale;
        maxBoost = root.maxBoost;
        minEfficiency = root.minEfficiency;
        displayEfficiencyScale = root.displayEfficiencyScale;
        displayEfficiency = root.displayEfficiency;
        scaleLiquidConsumption = root.scaleLiquidConsumption;

        rotate = root.rotate;

        ECTool.loadCompressContentRegion(root, this);

        ECTool.setIcon(root, this, 0);

        ECData.register(root, this, 1);
    }


    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        //make sure there's enough efficiency at this location
        return baseEfficiency + tile.getLinkedTilesAs(this, tempTiles).sumf(other -> other.floor().attributes.get(attribute)) >= minEfficiency;
    }

}
