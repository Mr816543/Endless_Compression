package ECConfig;


import ECContent.ECItems;
import ECContent.ECLiquids;
import ECType.ECLiquid;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.Pixmap;
import arc.graphics.Texture;
import arc.graphics.TextureData;
import arc.graphics.g2d.TextureAtlas;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.ctype.UnlockableContent;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.world.Block;
import mindustry.world.consumers.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static mindustry.content.Liquids.water;

public class ECTool {

    public static Pixmap[][] numberPixmap = new Pixmap[9][10];

    public static void compress(Object root , Object child , Class<?> clazz , Config c , int level) throws IllegalAccessException {
        //遍历全部属性
        for (Field field : getField(root,clazz)) {
            //允许通过反射访问私有变量
            //field.setAccessible(true);
            //获取属性名
            String name = field.getName();
            if (!Modifier.isPublic(field.getModifiers())) continue;
            if (Modifier.isFinal(field.getModifiers())) continue;
            //获取原物品属性的属性值
            var value = field.get(root);
            if (value == null) continue;

            boolean needContinue = false;

            for (String s : c.config.keys()){
                if (name.equals(s)) {
                    if (value instanceof Integer) {
                        field.set(child, (int) ((int) value * Mathf.pow(c.config.get(name), level)));
                        needContinue = true;
                    }
                    else if (value instanceof Float){
                        field.set(child, (float)value *  Mathf.pow(c.config.get(name),level));
                        needContinue = true;
                    }
                    else if (c.config.get(name)==null){
                        needContinue = true;
                    }
                }
            }

            //将新物品的属性设置为和原物品相同
            if (!needContinue) field.set(child, value);

            /*/

            for (String s : config.intConfig.keys()){
                if (name.equals(s)) {
                    field.set(child, (int)((int)value * Mathf.pow(config.intConfig.get(name),level)));
                }
            }

            for (String s : config.floatConfig.keys()){
                if (name.equals(s)) {
                    field.set(child, (float)value *  Mathf.pow(config.floatConfig.get(name),level));
                }
            }
            //*/


        }
    }

    public static <T extends UnlockableContent> void compress(T root , T child , Config c , int level) throws IllegalAccessException {
        compress(root,child, UnlockableContent.class,c,level);
    }

    public static Seq<Field> getField(Object root,Class<?> clas){
        Seq<Field> fields = new Seq<>();
        Class<?> clazz = root.getClass();
        while (!clazz.getSimpleName().equals(clas.getSimpleName())) {
            if (clazz.getSimpleName().isEmpty()) {
                clazz = clazz.getSuperclass();
                continue;
            }
            fields.add(clazz.getDeclaredFields());
            clazz = clazz.getSuperclass();

        }
        return fields;
    }

    public static <T extends UnlockableContent> Seq<Field> getField(T root) {
        return getField(root, UnlockableContent.class);
    }

    public static String fieldsToString(Field[] fields){
        StringBuilder string = new StringBuilder("[");
        for (Field f:fields){
            string.append(f.getName()).append(",");
        }
        string.append("]");
        return string.toString();
    }


    public static Color Color(Color color, int num, boolean deepen) {

        Color color0 = Color.rgb(255, 255, 255);

        if (deepen) {
            color0 = Color.rgb(0, 0, 0);
        }

        return color.cpy().lerp(color0, 0.035f * num);

    }

    //设置图标
    public static void setIcon(UnlockableContent root, UnlockableContent child, int num){
        if (root.uiIcon!=null){
            child.uiIcon = child.fullIcon = mergeRegions(root.uiIcon,num);
        }else {
            Core.app.post(() -> setIcon(root,child,num));
        }
    }


    //加载数字角标贴图
    public static void loadNumberPixmap(){
        for (int i = 0 ; i < 10 ; i++){
            numberPixmap[0][i] = new Texture(Vars.mods.getMod("ec").root.child("sprites").child("number").child("num-"+i+".png")).getTextureData().getPixmap();
        }
        for (int j = 2 ; j <= 9 ; j ++){
            int size = 32 * j ;
            for (int i = 0 ; i < 10;i++){
                Pixmap num = new Pixmap(size,size);
                num.draw(numberPixmap[0][i],0,0,size,size);
                numberPixmap[j-1][i] = num;
            }
        }

    }

    public static TextureRegion mergeRegions(Pixmap pixmapA, Pixmap pixmapB , int size){
        // 创建新Pixmap并绘制叠加效果
        Pixmap result = new Pixmap(size,size);
        result.draw(pixmapA, 0, 0); // 绘制A
        //result.setBlending(Pixmap.Blending.SourceOver); // 启用Alpha混合
        //result.draw(pixmapB, 3 , -3 ,true); // 叠加B
        result.draw(pixmapB, result.getWidth()-pixmapB.getWidth() , 0 ,true); // 叠加B
        //result.setBlending(Pixmap.Blending.None); // 恢复默认


        result.draw(pixmapB,1,1,1,1);




        // 生成纹理并清理资源
        Texture combinedTex = new Texture(result);
        //combinedTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear); // 可选滤波
        TextureRegion combinedRegion = new TextureRegion(combinedTex);

        // 释放Pixmap资源
        pixmapA.dispose();
        result.dispose();

        return combinedRegion;
    }

    public static TextureRegion mergeRegions(Pixmap pixmapA, Pixmap pixmapB){
        int sizeA = Math.max(pixmapA.getHeight(),pixmapA.getWidth());
        int sizeB = Math.max(pixmapB.getHeight(),pixmapB.getWidth());
        return mergeRegions(pixmapA,pixmapB,Math.max(sizeA,sizeB));
    }

    //为贴图绘制数字角标
    public static TextureRegion mergeRegions(TextureRegion A, int num){
        int sizeA = Math.max(A.width,A.height);
        int size = Math.max(sizeA / 32,1);
        return mergeRegions(extractRegionPixmap(A),numberPixmap[size-1][num]);
    }


    //截取贴图对应的Pixmap
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
        //*/
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


    //将原版贴图复制加载为压缩贴图
    public static void loadCompressContentRegion(UnlockableContent root,UnlockableContent child) throws IllegalAccessException {
        Seq<String> sprites = getSprites(root);
        loadCompressContentIcon(root,child);
        for (String sprite : sprites){
            loadCompressContentSpriteRegion(root,child,sprite);
        }
    }

    //贴图复制
    public static void loadCompressContentSpriteRegion(UnlockableContent root, UnlockableContent child, String sprite) {
        if (Core.atlas.has(root.name + sprite)) {
            Core.atlas.addRegion(child.name + sprite, Core.atlas.find(root.name + sprite));
        } else {
            Core.app.post(() -> loadCompressContentSpriteRegion(root, child, sprite));
        }
    }

    //获取后缀
    public static Seq<String> getSprites(UnlockableContent content) throws IllegalAccessException {
        Seq<String> sprites = new Seq<>();
        ObjectMap<String, TextureAtlas.AtlasRegion> regionmap =null;
        for (Field field: TextureAtlas.class.getDeclaredFields()){
            field.setAccessible(true);
            String name = field.getName();
            if (!name.equals("regionmap"))continue;
            regionmap = (ObjectMap<String, TextureAtlas.AtlasRegion>) field.get(Core.atlas);
        }
        if (regionmap==null) return sprites;
        for (String s:regionmap.keys()){
            if (!s.contains(content.name)) continue;
            String[] strings = s.split(content.name);
            if (strings.length==0) sprites.add("");
            else sprites.add(strings[strings.length-1]);
        }
        return sprites;
    }

    //ui图标复制
    public static void loadCompressContentIcon(UnlockableContent root, UnlockableContent child) {
        if (root.uiIcon != null) {
            child.fullIcon = child.uiIcon = root.uiIcon;
        } else {
            Core.app.post(() -> loadCompressContentIcon(root, child));
        }
    }


    //复制并增强消耗器
    public static Seq<Consume> consumeBuilderCopy(Block root,int level){
        Seq<Consume> consumes = new Seq<>();
        try {
            for (Consume consume : root.consumers) {
                if (consume instanceof ConsumeLiquid) {
                    if (ECLiquids.ECLiquids.get(((ConsumeLiquid)consume).liquid)==null) continue;
                    ConsumeLiquid c = new ConsumeLiquid(ECLiquids.ECLiquids.get(((ConsumeLiquid)consume).liquid).get(level), 1);
                    ECTool.compress(consume, c, Object.class, Config.NULL, 0);


                    consumes.add(c);



                } else if (consume instanceof ConsumeLiquids) {

                    ConsumeLiquids c = new ConsumeLiquids(new LiquidStack[0]);
                    ECTool.compress(consume, c, Object.class, Config.NULL, 0);
                    for (LiquidStack liquidStack : c.liquids){
                        if (ECLiquids.ECLiquids.get(liquidStack.liquid)==null) continue;
                        liquidStack.liquid = ECLiquids.ECLiquids.get(liquidStack.liquid).get(level);
                    }
                    consumes.add(c);

                } else if (consume instanceof ConsumeItems) {
                    ConsumeItems c = new ConsumeItems(new ItemStack[0]);
                    ECTool.compress(consume, c, Object.class, Config.NULL, 0);
                    for (ItemStack itemStack : c.items){

                        if (ECItems.ECItems.get(itemStack.item)==null) continue;
                        itemStack.item = ECItems.ECItems.get(itemStack.item).get(level);
                    }
                    consumes.add(c);
                } else if (consume instanceof ConsumePower) {

                    ConsumePower c = new ConsumePower(0,0,false);
                    ECTool.compress(consume, c, Object.class, Config.NULL, 0);
                    if (c.usage>0){
                        c.usage *= Mathf.pow(ECSetting.LINEAR_MULTIPLIER,level);
                    }
                    consumes.add(c);
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return consumes;
    }

}
