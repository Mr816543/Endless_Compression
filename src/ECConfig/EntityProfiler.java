package ECConfig;

import arc.Core;
import arc.Events;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.world.Block;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;

import static arc.Core.settings;

/**
 * 实体性能分析插件 - 修复版（无update字段依赖）
 */
public class EntityProfiler {

    /** 采样间隔（帧） */
    private int sampleInterval = 60;
    /** 采样次数（帧） */
    private int sampleTimes = 0;
    /** 帧计数器 */
    private int frameCounter = 0;
    /** 当前帧是否采样 */
    private boolean isSamplingTick = false;

    /** 已包装的实体列表 */
    private final Seq<Building> wrappedBuildings = new Seq<>();
    /** 实体 -> 原始update()方法句柄 映射 */
    private final ObjectMap<Building, MethodHandle> entityMap = new ObjectMap<>();

    private final ObjectMap<Block,Float> updateTime = new ObjectMap<>();

    private int maxTimes = 10;

    public void init() {
        // 绑定游戏主循环更新事件
        Events.run(EventType.Trigger.update, this::onUpdate);
    }

    private void onUpdate() {
        if (Vars.state.isPaused()||!Core.settings.getBool("entityProfiler"))return;
        frameCounter++;
        if (frameCounter >= sampleInterval) {
            frameCounter -= sampleInterval;
            isSamplingTick = true;
            sampleTimes++;
            // 采样帧：清空旧数据，重新收集所有建筑
            wrappedBuildings.clear();
            entityMap.clear();
            Groups.build.each(this::wrapEntityUpdateIfNeeded);
            // 执行所有实体的代理更新逻辑
            Groups.build.each(this::executeProfiledUpdate);
            if (sampleTimes >= settings.getInt("sampleTimes",5)){
                show();
                sampleTimes = 0;
            }
        } else {
            isSamplingTick = false;
        }
    }

    private void show() {
        Vars.ui.showSmall(Core.bundle.get("setting.entityProfiler.name"),showUpdateTime());
        Core.settings.put("entityProfiler",false);
        updateTime.clear();
    }

    /**
     * 包装实体update()方法，存储原始方法句柄
     */
    private void wrapEntityUpdateIfNeeded(Building entity) {
        if (entity == null || wrappedBuildings.contains(entity)) return;

        try {
            // 递归查找实体的update()无参方法（含父类）
            Method updateMethod = findUpdateMethod(entity.getClass());
            if (updateMethod == null) {
                Log.warn("No update() method found for class: " + entity.getClass().getName());
                return;
            }
            updateMethod.setAccessible(true);
            MethodHandle originalHandle = MethodHandles.lookup().unreflect(updateMethod);

            // 存储实体与原始方法的映射
            entityMap.put(entity, originalHandle);
            wrappedBuildings.add(entity);

        } catch (IllegalAccessException e) {
            Log.err("Failed to wrap update method for: " + entity.getClass().getName(), e);
        }
    }

    /**
     * 递归查找类及其父类的update()无参方法
     */
    private Method findUpdateMethod(Class<?> clazz) {
        if (clazz == null || clazz == Object.class) return null;
        try {
            return clazz.getDeclaredMethod("update");
        } catch (NoSuchMethodException e) {
            return findUpdateMethod(clazz.getSuperclass());
        }
    }

    /**
     * 执行带耗时统计的更新逻辑
     */
    private void executeProfiledUpdate(Building entity) {
        // 无原始方法则跳过
        if (!entityMap.containsKey(entity)) {
            return;
        }
        MethodHandle originalMethod = entityMap.get(entity);

        // 非采样帧：直接执行原始方法
        if (!isSamplingTick) {
            try {
                originalMethod.invokeExact(entity);
            } catch (Throwable e) {
                Log.err("Direct update failed for: " + entity.getClass().getName(), e);
            }
            return;
        }

        // 采样帧：统计耗时
        long startTime = System.nanoTime();
        try {
            originalMethod.invokeExact(entity);
        } catch (Throwable e) {
            Log.err("Profiled update failed for: " + entity.getClass().getName(), e);
        } finally {
            long durationNs = System.nanoTime() - startTime;
            float durationMs = durationNs / 1_000_000f;
            if (!updateTime.containsKey(entity.block)){
                updateTime.put(entity.block,durationMs);
            }else {
                float t = updateTime.get(entity.block);
                updateTime.remove(entity.block);
                updateTime.put(entity.block,t+durationMs);
            }
            //Log.info("[Profiler] " + entity.block.localizedName + ": " + String.format("%.3fms", durationMs));
        }
    }

    private String showUpdateTime() {
        StringBuilder s = new StringBuilder();
        int size = updateTime.size;

        // 1. 初始化数组并填充数据
        float[] timeArray = new float[size];
        Block[] blockArray = new Block[size];
        int index = 0;
        // 遍历 ObjectMap，将键值对分别存入两个数组
        for (ObjectMap.Entry<Block, Float> entry : updateTime) {
            blockArray[index] = entry.key;
            timeArray[index] = entry.value;
            index++;
        }

        // 2. 创建索引数组，用于记录排序后的位置（避免直接排序导致键值对应关系错乱）
        Integer[] indices = new Integer[size];
        for (int i = 0; i < size; i++) {
            indices[i] = i;
        }

        // 3. 按时间值从大到小排序索引数组
        Arrays.sort(indices, (i1, i2) -> {
            // 降序排序：后一个时间 - 前一个时间，若为正则交换位置
            return Float.compare(timeArray[i2], timeArray[i1]);
        });

        // 4. 按排序后的索引拼接字符串
        for (int i = 0; i < size; i++) {
            int sortedIndex = indices[i];
            Block block = blockArray[sortedIndex];
            float time = timeArray[sortedIndex]/sampleTimes;

            s.append(block.localizedName).append(":").append(((int) (time*1000f))/1000f).append("ms\n");
        }

        return s.toString();
    }
}