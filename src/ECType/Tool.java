package ECType;

import arc.Core;
import arc.graphics.Pixmap;
import arc.graphics.Texture;
import arc.graphics.TextureData;
import arc.graphics.g2d.TextureAtlas;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.ctype.UnlockableContent;
import mindustry.type.CellLiquid;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static ECType.ECSetting.LINEAR_MULTIPLIER;
import static ECType.ECSetting.SCALE_MULTIPLIER;

public class Tool {


    public static  <T extends UnlockableContent> void compress(T from, T to, int num, ObjectMap<String,Float> intValue,ObjectMap<String,Float> floatValue,Seq<String> jump) throws IllegalAccessException {
        String fClass = from.getClass().getSuperclass().getSimpleName();
        String tClass = to.getClass().getSuperclass().getSimpleName();
        //Log.info(from.localizedName+" : "+fClass+" -> "+tClass);

        if (!fClass.equals(tClass)) {
             Log.err("A and B haven't the same class\n" + "A : " + fClass + "\n" + "B : " + tClass);
        }

        Seq<Field> fields = getFields(to.getClass());


        //遍历全部属性
        for (Field field : fields) {

            //if (!field.over) continue;

            //允许通过反射访问私有变量
            //field.setAccessible(true);
            //获取属性名
            String name = field.getName();
            if (!Modifier.isPublic(field.getModifiers())) continue;
            if (Modifier.isFinal(field.getModifiers())) continue;
            //获取原物品属性的属性值
            var value = field.get(from);
            //将新物品的属性设置为和原物品相同
            if (value == null) continue;

            boolean shouldContinue = false;

            for (String s:intValue.keys()){
                if (!s.equals(name)) continue;
                field.set(to,compressValue((int)value,intValue.get(s),num));
                shouldContinue = true;
                break;
            }
            if (shouldContinue) continue;

            for (String s:floatValue.keys()){
                if (!s.equals(name)) continue;
                field.set(to,compressValue((float)value,floatValue.get(s),num));
                shouldContinue = true;
                break;
            }
            if (shouldContinue) continue;









            for (String j : jump) {
                if (!j.equals(name)) continue;
                shouldContinue = true;
                break;
            }
            if (shouldContinue) continue;


            field.set(to, value);


        }


    }

    public static  <T extends UnlockableContent> void compress(T from, T to, int num, ObjectMap<String,Float> intValue,ObjectMap<String,Float> floatValue) throws IllegalAccessException {
        compress(from,to,num,intValue,floatValue,new Seq<>());
    }

    /*/

    public static  <T extends UnlockableContent> void compress(T from, T to, int num, String[] linearInters, String[] linearFloats, String[] scaleInters, String[] scaleFloats,String[] jump) throws IllegalAccessException {
        String fClass = from.getClass().getSuperclass().getSimpleName();
        String tClass = to.getClass().getSuperclass().getSimpleName();

        if (!fClass.equals(tClass)) {
            throw new IllegalArgumentException("A and B must have the same class\n" + "A : " + fClass + "\n" + "B : " + tClass);

        }

        Seq<Field> fields = getFields(from.getClass());


        //遍历全部属性
        for (Field field : fields) {

            //if (!field.over) continue;

            //允许通过反射访问私有变量
            //field.setAccessible(true);
            //获取属性名
            String name = field.getName();
            if (!Modifier.isPublic(field.getModifiers())) continue;
            if (Modifier.isFinal(field.getModifiers())) continue;
            //获取原物品属性的属性值
            var value = field.get(from);
            //将新物品的属性设置为和原物品相同
            if (value == null) continue;

            boolean shouldContinue = false;
            if (linearInters == null) linearInters = new String[0];
            if (linearFloats == null) linearFloats = new String[0];
            if (scaleInters == null) scaleInters = new String[0];
            if (scaleFloats == null) scaleFloats = new String[0];
            if (jump == null) jump = new String[0];

            for (String Inter : linearInters) {
                if (!Inter.equals(name)) continue;
                field.set(to, compressValue((int) value, LINEAR_MULTIPLIER, num));
                shouldContinue = true;
                break;
            }
            if (shouldContinue) continue;

            for (String Float : linearFloats) {
                if (!Float.equals(name)) continue;
                field.set(to, compressValue((float) value, LINEAR_MULTIPLIER, num));
                shouldContinue = true;
                break;
            } if (shouldContinue) continue;

            for (String Inter : scaleInters) {
                if (!Inter.equals(name)) continue;
                field.set(to, compressValue((int) value, SCALE_MULTIPLIER, num));
                shouldContinue = true;
                break;
            }
            if (shouldContinue) continue;

            for (String Float : scaleFloats) {
                if (!Float.equals(name)) continue;
                field.set(to, compressValue((float) value, SCALE_MULTIPLIER, num));
                shouldContinue = true;
                break;
            }
            if (shouldContinue) continue;

            for (String j : jump) {
                if (!j.equals(name)) continue;
                shouldContinue = true;
                break;
            }
            if (shouldContinue) continue;


            field.set(to, value);

        }


    }

    public static  <T extends UnlockableContent> void compress(T from, T to, int num, String[] linearInters, String[] linearFloats, String[] scaleInters, String[] scaleFloats) throws IllegalAccessException{
        compress(from,to,num,linearInters,linearFloats,scaleInters,scaleFloats,new String[0]);
    }
    //*/

    public static Seq<Field> getFields(Class<?> clazz, Seq<Field> fields) {
        if (clazz.getSimpleName().equals("UnlockableContent")) return fields;
        //Log.info(clazz.getSimpleName());
        fields.add(clazz.getDeclaredFields());
        return getFields(clazz.getSuperclass(), fields);
    }

    public static Seq<Field> getFields(Class<?> clazz) {
        Seq<Field> fields = new Seq<>();
        return getFields(clazz.getSuperclass(), fields);
    }

    public static float compressValue(float v, float MULTIPLIER, int num) {
        return v * Mathf.pow(MULTIPLIER, num);
    }

    public static int compressValue(int v, float MULTIPLIER, int num) {
        return (int) compressValue((float) v, MULTIPLIER, num);
    }


    public static TextureRegion combineRegions(TextureRegion A, TextureAtlas.AtlasRegion B) {
        // 提取A和B的Pixmap，处理旋转和区域
        Pixmap pixmapA = extractRegionPixmap(A);
        Pixmap pixmapB = extractRegionPixmap(B);

        // 确保尺寸一致
        /*/
        if (pixmapA.getWidth() != pixmapB.getWidth() || pixmapA.getHeight() != pixmapB.getHeight()) {
            throw new IllegalArgumentException("A and B must have the same dimensions");
        }
        //*/

        // 创建新Pixmap并绘制叠加效果
        Pixmap result = new Pixmap(pixmapB.getWidth(), pixmapB.getHeight());
        result.draw(pixmapA, 0, 0); // 绘制A
        //result.setBlending(Pixmap.Blending.SourceOver); // 启用Alpha混合
        result.draw(pixmapB, 3 , -3 ,true); // 叠加B
        //result.setBlending(Pixmap.Blending.None); // 恢复默认

        // 生成纹理并清理资源
        Texture combinedTex = new Texture(result);
        //combinedTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear); // 可选滤波
        TextureRegion combinedRegion = new TextureRegion(combinedTex);

        // 释放Pixmap资源
        pixmapA.dispose();
        pixmapB.dispose();
        result.dispose();

        return combinedRegion;
    }

    public static TextureRegion combineRegions(TextureRegion A, int num){
        return combineRegions(A, Core.atlas.find("ec-num-"+num));
    }

    public static Pixmap extractRegionPixmap(TextureRegion region) {
        Texture tex = region.texture;
        TextureData data = tex.getTextureData();
        if (!data.isPrepared()) data.prepare();
        Pixmap full = data.consumePixmap();

        // 获取区域参数
        int x = region.getX(), y = region.getY();
        int width = region.width, height = region.height;
        boolean isRotated = (region instanceof TextureAtlas.AtlasRegion) && ((TextureAtlas.AtlasRegion)region).rotate;

        // 计算实际纹理中的区域尺寸
        int srcW = isRotated ? height : width;
        int srcH = isRotated ? width : height;

        // 提取原始区域
        Pixmap extracted = new Pixmap(srcW, srcH);
        extracted.draw(full, 0, 0, x, y, srcW, srcH);
        data.disposePixmap(); // 释放原纹理数据

        // 处理旋转
        /*/
        if (isRotated) {
            Pixmap rotated = new Pixmap(width, height);
            for (int px = 0; px < srcW; px++) {
                for (int py = 0; py < srcH; py++) {
                    rotated.set(py, width - px - 1, extracted.get(px, py));
                }
            }
            extracted.dispose();
            extracted = rotated;
        }
        //*/

        //Log.info("height : "+extracted.height+" , width : "+extracted.width);
        return extracted;
    }

    public static void putAllTo(ObjectMap<String,Float> ObjectMap,String[] Strings,float M){
        for (String s:Strings){
            ObjectMap.put(s,M);
        }
    }



}
