package ECConfig;

import arc.struct.ObjectMap;

public class Config{

    public static Config NULL = new Config();

    public ObjectMap<String, Float> config = new ObjectMap<>();

    public Config(){
    }

    public Config addConfig(Object... objects){
        for (int i = 0 ; i < objects.length/2;i++){
            config.put((String) objects[i*2],(Float) objects[i*2+1]);
        }
        return this;
    }

    public Config addConfigSimple(Float MULTIPLIER, String... strings){
        for (String s :strings ){
            config.put(s,MULTIPLIER);
        }
        return this;
    }

    public Config linearConfig(String... strings){
        return addConfigSimple(ECSetting.LINEAR_MULTIPLIER,strings);
    }
    public Config scaleConfig(String... strings){
        return addConfigSimple(ECSetting.SCALE_MULTIPLIER,strings);
    }
}