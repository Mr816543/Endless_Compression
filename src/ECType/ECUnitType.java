package ECType;


import ECConfig.Config;
import ECConfig.ECData;
import ECConfig.ECTool;
import ECType.ECWeapons.*;
import arc.Core;
import arc.math.Mathf;
import arc.struct.Seq;
import mindustry.ai.types.MinerAI;
import mindustry.content.UnitTypes;
import mindustry.gen.UnitEntity;
import mindustry.type.ItemStack;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import mindustry.type.weapons.*;

public class ECUnitType extends UnitType {

    public UnitType root;

    public int level;

    public static Config config = new Config().linearConfig("health","armor","buildSpeed","mineSpeed").scaleConfig("speed","maxRange","mineTier");

    public ECUnitType(UnitType root,int level) throws IllegalAccessException {
        super("c"+ level +"-"+root.name);
        this.root = root;
        this.level = level;
        ECTool.compress(this.root,this,config,level);
        ECTool.loadCompressContentRegion(root,this);
        ECTool.setIcon(root,this,level);
        localizedName = level + Core.bundle.get("num-Compression.localizedName") + root.localizedName;
        description = root.description;
        details = root.details;

        loadWeapons(root, level);

        ECData.register(root,this,level);
    }

    @Override
    public void init() {
        super.init();
        itemCapacity *= Mathf.pow(5,level);
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

    public static class ECUnitEntity extends UnitEntity{
        @Override
        public void update() {
            super.update();
        }
    }
}
