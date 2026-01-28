package ECType;

import ECType.ECBlockTypes.Turret.ECPowerTurret;
import ECType.ECBlockTypes.Turret.ECTractorBeamTurret;
import mindustry.gen.Building;
import mindustry.world.consumers.ConsumePower;

public class ECConsumePower extends ConsumePower {
    public ECConsumePower(float usage, float capacity, boolean buffered) {
        super(usage, capacity, buffered);
    }

    @Override
    public float requestedPower(Building entity) {
        //ECTool.print("requestedPower");

        if (entity instanceof ECPowerTurret.ECPowerTurretBuild build) {
            return buffered ?
                    (1f - entity.power.status) * capacity :
                    ((ECPowerTurret) build.block).powerUse.get(build.index) * (entity.shouldConsume() ? 1f : 0f);
        } else if (entity instanceof ECTractorBeamTurret.ECTractorBeamTurretBuild build) {
            return buffered ?
                    (1f - entity.power.status) * capacity :
                    ((ECTractorBeamTurret) build.block).powerUse.get(build.index) * (entity.shouldConsume() ? 1f : 0f);
        }

        return super.requestedPower(entity);
    }
}
