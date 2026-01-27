package ECType;


import ECConfig.Config;
import ECConfig.ECData;
import ECConfig.ECSetting;
import ECConfig.ECTool;
import ECType.ECWeapons.*;
import arc.Core;
import arc.func.Prov;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Strings;
import mindustry.gen.Payloadc;
import mindustry.gen.Unit;
import mindustry.gen.UnitEntity;
import mindustry.type.ItemStack;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import mindustry.type.weapons.*;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValues;

import static mindustry.Vars.tilesize;

public class ECUnitType extends UnitType {

    public UnitType root;

    public int level;

    public static Config config = new Config().addConfigSimple(null,"constructor").linearConfig("armor","buildSpeed","mineSpeed").scaleConfig("speed","maxRange","mineTier");

    public float IFR ;

    public ECUnitType(UnitType root,int level) throws IllegalAccessException {
        super("c"+ level +"-"+root.name);
        this.root = root;
        this.level = level;
        this.IFR = Mathf.pow(1/ ECSetting.LINEAR_MULTIPLIER,level);
        ECTool.compress(this.root,this,config,level);
        ECTool.loadCompressContentRegion(root,this);
        ECTool.setIcon(root,this,level);
        localizedName = level + Core.bundle.get("num-Compression.localizedName") + root.localizedName;
        description = root.description;
        details = root.details;


        loadWeapons(root, level);

        constructor = (Prov<Unit>) ECUnitEntity::new;
        ECData.register(root,this,level);
    }

    @Override
    public void init() {
        super.init();
        itemCapacity *= Mathf.pow(5,level);
    }

    @Override
    public void setStats() {
        stats.add(Stat.health, health);

        float ifr = (1 - IFR)*100;
        stats.add(new Stat("IFR"),ifr, StatUnit.percent);
        int echealth = (int) (health*Mathf.pow(ECSetting.LINEAR_MULTIPLIER,level));
        stats.add(new Stat("echealth"), echealth == Integer.MAX_VALUE ? Core.bundle.get("infinite"):echealth+"",StatUnit.none);

        stats.add(Stat.armor, armor);
        stats.add(Stat.speed, speed * 60f / tilesize, StatUnit.tilesSecond);
        stats.add(Stat.size, StatValues.squared(hitSize / tilesize, StatUnit.blocks));
        stats.add(Stat.itemCapacity, itemCapacity);
        stats.add(Stat.range, Strings.autoFixed(maxRange / tilesize, 1), StatUnit.blocks);

        if(crushDamage > 0){
            stats.add(Stat.crushDamage, crushDamage * 60f * 5f, StatUnit.perSecond);
        }

        if(legSplashDamage > 0 && legSplashRange > 0){
            stats.add(Stat.legSplashDamage, legSplashDamage, StatUnit.perLeg);
            stats.add(Stat.legSplashRange, Strings.autoFixed(legSplashRange / tilesize, 1), StatUnit.blocks);
        }

        stats.add(Stat.targetsAir, targetAir);
        stats.add(Stat.targetsGround, targetGround);

        if(abilities.any()){
            stats.add(Stat.abilities, StatValues.abilities(abilities));
        }

        stats.add(Stat.flying, flying);

        if(!flying){
            stats.add(Stat.canBoost, canBoost);
        }

        if(mineTier >= 1){
            stats.addPercent(Stat.mineSpeed, mineSpeed);
            stats.add(Stat.mineTier, StatValues.drillables(mineSpeed, 1f, 1, null, b ->
                    b.itemDrop != null &&
                            (b instanceof Floor f && (((f.wallOre && mineWalls) || (!f.wallOre && mineFloor))) ||
                                    (!(b instanceof Floor) && mineWalls)) &&
                            b.itemDrop.hardness <= mineTier && (!b.playerUnmineable || Core.settings.getBool("doubletapmine"))));
        }
        if(buildSpeed > 0){
            stats.addPercent(Stat.buildSpeed, buildSpeed);
        }
        if(sample instanceof Payloadc){
            stats.add(Stat.payloadCapacity, StatValues.squared(Mathf.sqrt(payloadCapacity / (tilesize * tilesize)), StatUnit.blocks));
        }

        var reqs = getFirstRequirements();

        if(reqs != null){
            stats.add(Stat.buildCost, StatValues.items(reqs));
        }

        if(weapons.any()){
            stats.add(Stat.weapons, StatValues.weapons(this, weapons));
        }

        if(immunities.size > 0){
            stats.add(Stat.immunities, StatValues.statusEffects(immunities.toSeq().sort()));
        }
    }

    private void loadWeapons(UnitType root, int level) throws IllegalAccessException {
        weapons = new Seq<>();
        for (Weapon weapon: root.weapons){
            if (weapon instanceof BuildWeapon){
                weapons.add(new ECBuildWeapon(weapon, level));
            }
            else if (weapon instanceof MineWeapon){
                weapons.add(new ECMineWeapon(weapon, level));
            }
            else if (weapon instanceof PointDefenseBulletWeapon){
                weapons.add(new ECPointDefenseBulletWeapon(weapon, level));
            }
            else if (weapon instanceof PointDefenseWeapon){
                weapons.add(new ECPointDefenseWeapon(weapon, level));
            }
            else if (weapon instanceof RepairBeamWeapon){
                weapons.add(new ECRepairBeamWeapon(weapon, level));
            }
            else if (weapon.getClass().getSimpleName().equals("Weapon") || (weapon.getClass().getSimpleName().isEmpty()&&weapon.getClass().getSuperclass().getSimpleName().equals("Weapon"))){
                weapons.add(new ECWeapon(weapon, level));
            }




        }
    }


    @Override
    public ItemStack[] researchRequirements() {
        return ECTool.compressItemStack(root.researchRequirements(),level);
    }


    public class ECUnitEntity extends UnitEntity{
        @Override
        public void update() {
            super.update();
        }
        @Override
        public void rawDamage(float amount) {
            super.rawDamage(amount * IFR);
        }

        @Override
        public void heal(float amount) {
            super.heal(amount * IFR);
            Log.info(health+" : "+ "+"+amount * IFR);
        }


        @Override
        public void healFract(float amount) {
            this.health += amount * this.maxHealth;
            this.clampHealth();
        }

        @Override
        public float maxHealth() {
            return super.maxHealth()/IFR;
        }
    }
}
