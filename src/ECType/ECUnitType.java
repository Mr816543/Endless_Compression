package ECType;


import ECConfig.*;
import ECType.ECWeapons.*;
import arc.Core;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Strings;
import mindustry.gen.Payloadc;
import mindustry.type.ItemStack;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import mindustry.type.weapons.*;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValues;

import static mindustry.Vars.tilesize;

public class ECUnitType extends UnitType implements EC {

    public static Config config = new Config().addConfigSimple(null).linearConfig("armor", "buildSpeed", "mineSpeed").scaleConfig("speed", "maxRange", "mineTier");
    public UnitType root;
    public int level;


    public ECUnitType(UnitType root, int level) throws IllegalAccessException {
        super("c" + level + "-" + root.name);
        this.root = root;
        this.level = level;
        ECTool.compress(this.root, this, config, level);
        ECTool.loadCompressContentRegion(root, this);
        ECTool.setIcon(root, this, level);
        localizedName = level + Core.bundle.get("num-Compression.localizedName") + root.localizedName;
        description = root.description;
        details = root.details;
        loadWeapons(root, level);


        ECData.register(root, this, level);
    }

    @Override
    public void init() {
        super.init();
        health = compressHealth();
        itemCapacity *= Mathf.pow(5, level);
    }

    private float compressHealth() {
        float lastHealth = ECData.get(root, level - 1).health;
        float h = lastHealth >= Float.MAX_VALUE / ECSetting.LINEAR_MULTIPLIER ?
                Float.MAX_VALUE :
                lastHealth * ECSetting.LINEAR_MULTIPLIER;
        return h;
    }

    @Override
    public void setStats() {
        stats.add(Stat.health, health);

        stats.add(Stat.armor, armor);
        stats.add(Stat.speed, speed * 60f / tilesize, StatUnit.tilesSecond);
        stats.add(Stat.size, StatValues.squared(hitSize / tilesize, StatUnit.blocks));
        stats.add(Stat.itemCapacity, itemCapacity);
        stats.add(Stat.range, Strings.autoFixed(maxRange / tilesize, 1), StatUnit.blocks);

        if (crushDamage > 0) {
            stats.add(Stat.crushDamage, crushDamage * 60f * 5f, StatUnit.perSecond);
        }

        if (legSplashDamage > 0 && legSplashRange > 0) {
            this.stats.add(Stat.legSplashDamage, (table) -> {
                table.add(Core.bundle.format("bullet.splashdamage", Strings.autoFixed(this.legSplashDamage, 2), Strings.autoFixed(this.legSplashRange / 8.0F, 2)).replace("[stat]", "[white]") + " " + StatUnit.perLeg.localized());
            });
        }

        stats.add(Stat.targetsAir, targetAir);
        stats.add(Stat.targetsGround, targetGround);

        if (abilities.any()) {
            stats.add(Stat.abilities, StatValues.abilities(abilities));
        }

        stats.add(Stat.flying, flying);

        if (!flying) {
            stats.add(Stat.canBoost, canBoost);
        }

        if (mineTier >= 1) {
            stats.addPercent(Stat.mineSpeed, mineSpeed);
            stats.add(Stat.mineTier, StatValues.drillables(mineSpeed, 1f, 1, null, b ->
                    b.itemDrop != null &&
                            (b instanceof Floor f && (((f.wallOre && mineWalls) || (!f.wallOre && mineFloor))) ||
                                    (!(b instanceof Floor) && mineWalls)) &&
                            b.itemDrop.hardness <= mineTier && (!b.playerUnmineable || Core.settings.getBool("doubletapmine"))));
        }
        if (buildSpeed > 0) {
            stats.addPercent(Stat.buildSpeed, buildSpeed);
        }
        if (sample instanceof Payloadc) {
            stats.add(Stat.payloadCapacity, StatValues.squared(Mathf.sqrt(payloadCapacity / (tilesize * tilesize)), StatUnit.blocks));
        }

        var reqs = getFirstRequirements();

        if (reqs != null) {
            stats.add(Stat.buildCost, StatValues.items(reqs));
        }

        if (weapons.any()) {
            stats.add(Stat.weapons, StatValues.weapons(this, weapons));
        }

        if (immunities.size > 0) {
            stats.add(Stat.immunities, StatValues.statusEffects(immunities.toSeq().sort()));
        }
    }

    private void loadWeapons(UnitType root, int level) throws IllegalAccessException {
        weapons = new Seq<>();
        for (Weapon weapon : root.weapons) {
            if (weapon instanceof BuildWeapon) {
                weapons.add(new ECBuildWeapon(weapon, level));
            } else if (weapon instanceof MineWeapon) {
                weapons.add(new ECMineWeapon(weapon, level));
            } else if (weapon instanceof PointDefenseBulletWeapon) {
                weapons.add(new ECPointDefenseBulletWeapon(weapon, level));
            } else if (weapon instanceof PointDefenseWeapon) {
                weapons.add(new ECPointDefenseWeapon(weapon, level));
            } else if (weapon instanceof RepairBeamWeapon) {
                weapons.add(new ECRepairBeamWeapon(weapon, level));
            } else if (weapon.getClass().getSimpleName().equals("Weapon") || (weapon.getClass().getSimpleName().isEmpty() && weapon.getClass().getSuperclass().getSimpleName().equals("Weapon"))) {
                weapons.add(new ECWeapon(weapon, level));
            }


        }
    }


    @Override
    public ItemStack[] researchRequirements() {
        return ECTool.compressItemStack(root.researchRequirements(), level);
    }


    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public Object getRoot() {
        return root;
    }
}
