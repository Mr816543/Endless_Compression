package ECContents;

import ECConfig.ECData;
import ECConfig.ECSetting;
import arc.graphics.Color;
import arc.graphics.g2d.Fill;
import arc.struct.Seq;
import mindustry.content.Fx;
import mindustry.content.UnitTypes;
import mindustry.entities.Effect;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;

import static arc.graphics.g2d.Draw.color;
import static arc.math.Angles.randLenVectors;

public class ECEffects {
    
    public static void load(){
        Seq<Effect> all = Effect.all.copy();
        for (Effect effect : all){
            compressEffect(effect);
        }
    }
    
    
    public static void compressEffect(Effect root){


        if (root == Fx.shootSmallFlame) {
            for (int i = 1 ; i <= ECSetting.MAX_LEVEL;i++){
                float sizeBase = (float) Math.pow(ECSetting.SCALE_MULTIPLIER, i);
                Effect effect = new Effect(root.lifetime,root.clip,e->{
                    color(Pal.lightFlame, Pal.darkFlame, Color.gray, e.fin());

                    randLenVectors(e.id, (int) Math.min(8 * sizeBase, 1024), e.finpow() * 60f * sizeBase, e.rotation, (float) Math.toDegrees(Math.atan(Math.toRadians(10f)) / sizeBase), (x, y) -> {
                        Fill.circle(e.x + x, e.y + y, 0.65f + e.fout() * 1.5f);
                    });
                });
                ECData.register(root,effect,i);
            }
        }
        else if (root == Fx.shootPyraFlame){
            for (int i = 1 ; i <= ECSetting.MAX_LEVEL;i++){
                float sizeBase = (float) Math.pow(ECSetting.SCALE_MULTIPLIER, i);
                Effect effect = new Effect(root.lifetime,root.clip,e->{
                    color(Pal.lightPyraFlame, Pal.darkPyraFlame, Color.gray, e.fin());

                    randLenVectors(e.id, (int) Math.min(10 * sizeBase, 2048), e.finpow() * 70f * sizeBase, e.rotation, (float) Math.toDegrees(Math.atan(Math.toRadians(10f)) / sizeBase), (x, y) -> {
                        Fill.circle(e.x + x, e.y + y, 0.65f + e.fout() * 1.6f);
                    });
                });
                ECData.register(root,effect,i);
            }
        }
        else if (root == UnitTypes.oxynoe.weapons.get(0).bullet.shootEffect){
            for (int i = 1 ; i <= ECSetting.MAX_LEVEL;i++){
                float sizeBase = (float) Math.pow(ECSetting.SCALE_MULTIPLIER, i);
                Effect effect = new Effect(root.lifetime,root.clip,e->{
                    color(Color.white, Pal.heal, Color.gray, e.fin());

                    randLenVectors(e.id, (int) Math.min(8 * sizeBase, 1024), e.finpow() * 60f * sizeBase, e.rotation, (float) Math.toDegrees(Math.atan(Math.toRadians(10f)) / sizeBase), (x, y) -> {
                        Fill.circle(e.x + x, e.y + y, 0.65f + e.fout() * 1.5f);
                        Drawf.light(e.x + x, e.y + y, 16f * e.fout(), Pal.heal, 0.6f);
                    });
                });
                ECData.register(root,effect,i);
            }
        }
        else if (false){
            for (int i = 1 ; i <= ECSetting.MAX_LEVEL;i++){
                float sizeBase = (float) Math.pow(ECSetting.SCALE_MULTIPLIER, i);
                Effect effect = new Effect(root.lifetime,root.clip,e->{});
                ECData.register(root,effect,i);
            }
        }



        
        
        
        
    }

    
}
