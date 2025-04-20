package ECType;

import arc.Core;
import arc.func.Func;
import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.ui.Image;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.layout.Table;
import arc.struct.IntSet;
import arc.struct.OrderedMap;
import arc.struct.Seq;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.gen.Icon;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.type.*;
import mindustry.ui.Bar;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.world.Block;
import mindustry.world.blocks.heat.HeatBlock;
import mindustry.world.blocks.heat.HeatConsumer;
import mindustry.world.consumers.ConsumePower;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;
import mindustry.world.meta.BlockStatus;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValues;

import static ECContent.ECItems.*;
import static ECContent.ECLiquids.*;


public class ECMultiCrafter extends Block {
    public boolean multiDrawer = false;
    public Seq<Recipe> recipes = new Seq<>();//配方
    public DrawBlock drawer = new DrawDefault();//绘制器
    public boolean splitHeat = false;//热分裂
    public float visualMaxHeat;//视觉最大热量

    //构造函数
    public ECMultiCrafter(String name) {
        super(name);

        //基础设置
        update = true;//需要更新
        solid = true;//固体方块
        hasItems = true;//容纳物品
        hasLiquids = true;//容纳液体
        hasPower = true;//通电
        sync = true;//多人游戏时需要同步
        visualMaxHeat = 15f;//视觉热量上限
        saveConfig = true;//保存设置
        copyConfig = true;//复制设置
        configurable = true;//允许设置
        consumesPower = true;//消耗电力
        outputsPower = true;//输出电力
        consPower = new ConsumePower(0, 1f, false);//电力消耗器
        consumeBuilder.add(consPower);//添加

        //drawer = new DrawMulti(new DrawDefault(), new DrawHeatOutput());
        rotateDraw = false;
        rotate = true;
        canOverdrive = false;
        drawArrow = true;


        config(Integer.class, (MultiCrafterBuild tile, Integer index) -> tile.index = index);
        configClear((MultiCrafterBuild tile) -> tile.index = 0);
    }


    @Override
    public void init() {
        super.init();
        initHeat();
        loadDrawer();
        initCapacity();
    }

    public void initCapacity() {

        int maxItem = 0;
        float maxLiquid = 0f;

        for (Recipe r : recipes) {
            for (ItemStack s : r.inputItems) {
                maxItem = Math.max(maxItem, s.amount);
            }
            for (ItemStack s : r.outputItems) {
                maxItem = Math.max(maxItem, s.amount);
            }


            for (LiquidStack s : r.inputLiquids) {
                maxLiquid = Math.max(maxLiquid, s.amount);
            }
            for (LiquidStack s : r.outputLiquids) {
                maxLiquid = Math.max(maxLiquid, s.amount);
            }
        }

        itemCapacity = Math.max(itemCapacity, maxItem * 2);
        liquidCapacity = Math.max(liquidCapacity, maxLiquid * 2f);
    }

    public void initHeat() {
        boolean needDrawArrow = false;
        for (Recipe r : recipes) {
            if (r.outputHeat > 0) {
                needDrawArrow = true;
                break;
            }
        }
        drawArrow = needDrawArrow;
    }

    @Override
    public void setBars() {
    }

    @Override
    public void setStats() {

        stats.add(Stat.size, "@x@", size, size);

        if (synthetic()) {
            stats.add(Stat.health, health, StatUnit.none);
            if (armor > 0) {
                stats.add(Stat.armor, armor, StatUnit.none);
            }
        }

        if (canBeBuilt() && requirements.length > 0) {
            stats.add(Stat.buildTime, buildTime / 60, StatUnit.seconds);
            stats.add(Stat.buildCost, StatValues.items(false, requirements));
        }

        if (instantTransfer) {
            stats.add(Stat.maxConsecutive, 2, StatUnit.none);
        }

        if (hasLiquids) stats.add(Stat.liquidCapacity, liquidCapacity, StatUnit.liquidUnits);
        if (hasItems && itemCapacity > 0) stats.add(Stat.itemCapacity, itemCapacity, StatUnit.items);

        setRecipesStats();
    }

    //设置配方信息
    public void setRecipesStats() {
        stats.add(Stat.output, table -> {
            table.row();
            // 创建滚动面板
            ScrollPane pane = new ScrollPane(new Table(t -> {
                for (Recipe r : recipes) {
                    t.add(getRecipeDisplay(r)).growX().left();
                    t.row();
                }
            }));

            table.add(pane).grow().height(200f);
        });
    }

    // 配方可视化工具方法
    public Table getRecipeDisplay(Recipe r) {
        Table t = new Table().left();


        t.add(r.name + " : ");

        //*/
        for (Object o : imageForRecipes(r)) {
            if (o instanceof TextureRegion textureRegion) {
                t.add(new Image(textureRegion)).size(32f);
            } else if (o instanceof String string) {
                t.add(string).fontScale(0.8f);
            } else t.add(new Image(Core.atlas.find("error")));
        }
        //*/
        /*/
        // 输入部分
        addRecipeComponents(t, r.inputItems, r.inputLiquids,r.inputUnits,r.inputHeat , r.inputPower);

        // 箭头分隔符
        //noinspection SpellCheckingInspection
        t.add("[lightgray]->[]").pad(10f);

        // 输出部分
        addRecipeComponents(t, r.outputItems, r.outputLiquids,r.outputUnits,r.outputHeat, r.outputPower);

        //*/

        return t;
    }

    // 通用组件添加方法
    /*/
    private void addRecipeComponents(Table t, ItemStack[] items, LiquidStack[] liquids,UnitStack[] units,float heat,float power) {

        for (int i = 0; i < items.length; i++) {
            ItemStack stack = items[i];
            if (i == items.length - 1 && liquids.length == 0 && power == 0) {
                t.add((stack.amount == 1 ? "" : stack.amount) + " " + stack.item.emoji());
            } else {
                t.add((stack.amount == 1 ? "" : stack.amount) + " " + stack.item.emoji() + " + ");
            }
        }


        for (int i = 0; i < liquids.length; i++) {
            LiquidStack stack = liquids[i];
            if (i == liquids.length - 1 && power == 0) {
                t.add(((stack.amount - (int) stack.amount) <= 0.1f ? (int) stack.amount : stack.amount) + " " + stack.liquid.emoji());
            } else {
                t.add(((stack.amount - (int) stack.amount) <= 0.1f ? (int) stack.amount : stack.amount) + " " + stack.liquid.emoji() + " + ");
            }
        }
        if (power > 0) {
            //noinspection UnnecessaryUnicodeEscape
            t.add(" " + power + "\u26A1" + "/s").color(Pal.power);
        }
    }
    //*/

    public Seq<Object> imageForRecipes(Recipe r) {
        Seq<Object> regions = new Seq<>();

        imageForRecipes(regions, r.inputItems, r.inputLiquids, r.inputUnits, r.inputHeat, r.inputPower);

        regions.add(" -(" + r.crafterTime / 60f + "s)-> ");

        imageForRecipes(regions, r.outputItems, r.outputLiquids, r.outputUnits, r.outputHeat, r.outputPower);

        return regions;
    }

    public void imageForRecipes(Seq<Object> regions, ItemStack[] inputItems, LiquidStack[] inputLiquids, UnitStack[] inputUnits, float inputHeat, float inputPower) {
        //确认加号个数
        int num1 = 0;
        num1 += inputItems.length + inputLiquids.length + inputUnits.length + (inputHeat > 0 ? 1 : 0) + (inputPower > 0 ? 1 : 0) - 1;
        num1 = Math.max(num1, 0);

        for (ItemStack input : inputItems) {
            if (input.amount != 1) regions.add(Integer.toString(input.amount));
            regions.add(input.item.uiIcon);
            if (num1 > 0) {
                regions.add(" + ");
                num1 -= 1;
            }
        }

        for (LiquidStack input : inputLiquids) {
            boolean isInt = input.amount - (int) input.amount < 0.01F || input.amount - (int) input.amount > 0.99F;
            if (isInt) {
                if ((int) input.amount != 1) regions.add(Integer.toString((int) input.amount));
            } else regions.add(Float.toString(input.amount));
            regions.add(input.liquid.uiIcon);
            if (num1 > 0) {
                regions.add(" + ");
                num1 -= 1;
            }
        }

        for (UnitStack input : inputUnits) {
            if (input.amount != 1) regions.add(Integer.toString(input.amount));
            regions.add(input.unitType.uiIcon);
            if (num1 > 0) {
                regions.add(" + ");
                num1 -= 1;
            }
        }

        if (inputHeat > 0) {
            boolean isInt = inputHeat - (int) inputHeat < 0.01F || inputHeat - (int) inputHeat > 0.99F;
            if (isInt) {
                regions.add(Integer.toString((int) inputHeat));
            } else regions.add(Float.toString(inputHeat));
            regions.add(Icon.waves.getRegion());
            if (num1 > 0) {
                regions.add(" + ");
                num1 -= 1;
            }
        }

        if (inputPower > 0) {
            boolean isInt = inputPower - (int) inputPower < 0.01F || inputPower - (int) inputPower > 0.99F;
            if (isInt) {
                regions.add(Integer.toString((int) inputPower * 60));
            } else regions.add(Float.toString(inputPower*60));
            regions.add(Icon.power.getRegion());
        }

    }

    public void loadDrawer() {
        int i = 1;
        if (multiDrawer && recipes.size > 0) {
            drawer = recipes.get(0).drawer;
        }

        for (Recipe r : recipes) {
            r.drawer.load(this);
            r.name = r.name + i;
            i++;
        }
    }


    //配方类
    public static class Recipe {
        public String name;
        public ItemStack[] inputItems, outputItems;
        public LiquidStack[] inputLiquids, outputLiquids;
        public UnitStack[] inputUnits, outputUnits;
        public float inputHeat, outputHeat;
        public float inputPower, outputPower;//每帧
        public float crafterTime;//帧
        public float warmupRate;//热量升高时间
        public DrawBlock drawer;

        public Recipe() {
            name = Core.bundle.get("ECType.Recipe.name");
            inputItems = outputItems = new ItemStack[0];
            inputLiquids = outputLiquids = new LiquidStack[0];
            inputUnits = outputUnits = new UnitStack[0];
            inputHeat = outputHeat = 0f;
            inputPower = outputPower = 0f;
            crafterTime = 60f;
            warmupRate = 0.15f;
            drawer = new DrawDefault();
        }

        public Recipe(Recipe r) {

            name = r.name;
            inputItems = r.inputItems.clone();
            outputItems = r.outputItems.clone();
            inputLiquids = r.inputLiquids.clone();
            outputLiquids = r.outputLiquids.clone();
            inputUnits = r.inputUnits.clone();
            outputUnits = r.outputUnits.clone();
            inputHeat = r.inputHeat;
            outputHeat = r.outputHeat;
            inputPower = r.inputPower;
            outputPower = r.outputPower;
            crafterTime = r.crafterTime;
            warmupRate = r.warmupRate;
            drawer = r.drawer;
        }

        public boolean isUnlocked() {
            for (ItemStack c : inputItems) {
                if (!c.item.unlockedNow()) return false;
            }
            for (LiquidStack c : inputLiquids) {
                if (!c.liquid.unlockedNow()) return false;
            }
            for (UnitStack c : inputUnits) {
                if (!c.unitType.unlockedNow()) return false;
            }
            return true;
        }

        public Recipe copy() {
            return new Recipe(this);
        }

        public Recipe createCompressedRecipe(int num){
            Recipe r = this.copy();

            for (int i = 0 ; i < r.inputItems.length;i++){
                r.inputItems[i] = new ItemStack(ECItems.get(r.inputItems[i].item).get(num),r.inputItems[i].amount);
            }

            for (int i = 0 ; i < r.outputItems.length;i++){
                r.outputItems[i] = new ItemStack(ECItems.get(r.outputItems[i].item).get(num),r.outputItems[i].amount);
            }
            for (int i = 0 ; i < r.inputLiquids.length;i++){
                r.inputLiquids[i] = new LiquidStack(ECLiquids.get(r.inputLiquids[i].liquid).get(num),r.inputLiquids[i].amount);
            }
            for (int i = 0 ; i < r.outputLiquids.length;i++){
                r.outputLiquids[i] = new LiquidStack(ECLiquids.get(r.outputLiquids[i].liquid).get(num),r.outputLiquids[i].amount);
            }



            return r;
        }

    }

    //单位堆叠类
    public static class UnitStack {
        public UnitType unitType;
        public int amount;

        public UnitStack(UnitType unitType, int amount) {
            this.unitType = unitType;
            this.amount = amount;
        }
    }

    //配置面板
    public class RecipeDialog extends BaseDialog {
        public MultiCrafterBuild build;
        public ECMultiCrafter block;

        public RecipeDialog(MultiCrafterBuild build) {
            super(Core.bundle.get("ECType.Recipe.RecipeDialog"));
            this.build = build;
            this.block = (ECMultiCrafter) build.block;
            setup();
        }


        public void setup() {
            cont.clear();

            Table table = new Table();
            //table.defaults().growX().height(50f).pad(10f);
            table.defaults().growX().uniformX().fillX().height(50f).pad(5f);
            ScrollPane pane = new ScrollPane(table);

            pane.setScrollingDisabled(false, false);
            int buttonsPerRow = Vars.android ? 2 : 3; // 每行显示的按钮数量
            if (recipes.size == 1 || recipes.size == 2) buttonsPerRow = recipes.size;

            int count = 0;

            for (int i = 0; i < recipes.size; i++) {
                Recipe r = recipes.get(i);
                int finalI = i;
                table.button(b -> {
                    //b.add(r.name).left();
                    //b.row();
                    //b.add(new Image(imageForRecipe(r))).size(32f);

                    b.add(new Table(t -> {
                        if (finalI == build.index) {
                            t.add(Core.bundle.get("ECType.Recipe.RecipeDialog.now") + " ").color(Color.yellow);
                        }
                        t.add(r.name + " : ");

                        for (Object o : block.imageForRecipes(r)) {
                            if (o instanceof TextureRegion textureRegion) {
                                t.add(new Image(textureRegion)).size(32f);
                            } else if (o instanceof String string) {
                                t.add(string).fontScale(0.8f);
                            } else t.add(new Image(Core.atlas.find("error")));
                        }

                        //t.add(new Image(imageForRecipe(r))).size(40f);
                    })).grow().left();
                }, Styles.cleart, () -> {//触发事件
                    build.index = finalI;
                    build.progress = 0f;
                    build.updateBar();
                    hide();

                }).get().setDisabled(!r.isUnlocked());


                count++;
                // 每行添加指定数量按钮后换行
                if (count % buttonsPerRow == 0) {
                    table.row();
                }

            }

            // 处理最后一行不满的情况
            if (count % buttonsPerRow != 0) {
                // 填充剩余单元格以保持布局
                int remaining = buttonsPerRow - (count % buttonsPerRow);
                for (int i = 0; i < remaining; i++) {
                    table.add().growX(); // 添加空单元格
                }
            }


            cont.add(pane).grow();
        }

    }

    //实体类
    public class MultiCrafterBuild extends Building implements HeatBlock, HeatConsumer {
        public float[] sideHeat = new float[4];//热方向
        public float heat = 0f;//热量
        public IntSet cameFrom = new IntSet();//不知道
        public long lastHeatUpdate = -1;//最后一次热更新
        public int index = 0;//当前配方索引
        public float progress = 0f;//生产进度
        public boolean needUpdateBar = true;
        public OrderedMap<String, Func<Building, Bar>> barMap = new OrderedMap<>();
        public boolean canConsume = false;

        @Override
        public void displayBars(Table table) {
            for (Func<Building, Bar> bar : barMap.values()) {
                Bar result = bar.get(this);
                if (result != null) {
                    table.add(result).growX();
                    table.row();
                }
            }

        }

        @Override
        public void updateTile() {
            Recipe r = recipes.get(index);

            if (needUpdateBar) {
                updateBar();
            }
            updateHeat();

            canConsume = canConsume(r);

            if (canConsume) {
                progress += delta() / r.crafterTime;
                //Log.info("working");
                for (int i = 0; progress >= 1f && i < 9; i++) {
                    //Log.info("finish");
                    consumeRecipe(r);
                    handleRecipe(r);
                    unitCraft(r);
                    progress -= 1f;
                }
            }

            dumpRecipe(r);

        }

        @Override
        public Object config() {
            return index;
        }

        public boolean canConsume(Recipe r) {
            Building b = this;
            //被逻辑禁用
            if (!enabled) return false;
            for (ItemStack input : r.inputItems) {
                if (input.amount > b.items.get(input.item)) {
                    //Log.info(input.item.localizedName + ":" + b.items.get(input.item) + "/" + input.amount);
                    return false;
                }
            }
            for (ItemStack output : r.outputItems) {
                if (b.items.get(output.item) + output.amount > b.getMaximumAccepted(output.item)) {
                    //Log.info("cannot dump " + output.item.localizedName);
                    return false;
                }
            }

            for (LiquidStack input : r.inputLiquids) {
                if (input.amount > b.liquids.get(input.liquid)) {
                    //Log.info(input.liquid.localizedName + ":" + b.liquids.get(input.liquid) + "/" + input.amount);
                    return false;
                }
            }
            for (LiquidStack output : r.outputLiquids) {
                if (b.liquids.get(output.liquid) + output.amount > b.block.liquidCapacity) {
                    //Log.info("cannot dump " + output.liquid.localizedName);
                    return false;
                }
            }
            if (power != null && power.graph.getLastPowerStored() < r.inputPower / 60f * delta()) {
                if (power.graph.getPowerBalance() < r.inputPower / 60f * delta()) {
                    return false;
                }
            }

            if (r.inputHeat > 0 && r.inputHeat > heat) {
                return false;
            }

            // 单位输入检测（使用Groups.unit替代indexer）
            for (UnitStack inputUnit : r.inputUnits) {
                int count = 0;
                for (Unit u : Groups.unit) {
                    if (u.team == team && u.type == inputUnit.unitType && u.isValid() && isNearly(u)) {
                        count++;
                    }
                }
                if (count < inputUnit.amount) return false;

            }
            /*/
            for(UnitFactory.UnitPlan plan : r.inputUnits){
                int count = 0;
                for(Unit u : Groups.unit){
                    if( u.team == team && u.type == plan && u.isValid() && isNearly(u) ){
                        count++;
                    }
                    if(count >= 1) break; // 只需要存在至少1个单位
                }
                if(count < 1) return false;
            }

            //*/
            return true;
        }

        public Boolean isNearly(Unit u) {
            //Log.info(" ( " + tileX()+" , "+tileY()+" ) " + " ( " + u.tileX()+" , "+u.tileY()+" ) "  );
            if (u.tileX() - tileX() > size || u.tileX() - tileX() < -size) return false;
            if (u.tileY() - tileY() > size || u.tileY() - tileY() < -size) return false;
            return true;
        }

        @Override
        public boolean shouldConsume() {
            Recipe r = recipes.get(index);
            Building b = this;

            //输出的物品和液体有一个满了就不应该消耗
            for (ItemStack output : r.outputItems) {
                if (b.items.get(output.item) + output.amount > b.getMaximumAccepted(output.item)) {
                    return false;
                }
            }
            for (LiquidStack output : r.outputLiquids) {
                if (b.liquids.get(output.liquid) + output.amount > b.block.liquidCapacity) {
                    return false;
                }
            }


            return true;
        }

        public void consumeRecipe(Recipe r) {
            Building b = this;
            for (ItemStack input : r.inputItems) {
                b.items.remove(input.item, input.amount);
            }
            for (LiquidStack input : r.inputLiquids) {
                b.liquids.remove(input.liquid, input.amount);
            }
            /*/
            if (r.inputPower > 0) {
                power.status -= (r.inputPower / 60f * delta()) / this.block.consPower.capacity;
            }
            new ConsumePower(r.inputPower / 60, 0, false).trigger(this);

             //*/


        }

        public void handleRecipe(Recipe r) {
            Building b = this;
            for (ItemStack output : r.outputItems) {
                b.items.add(output.item, output.amount);
            }
            for (LiquidStack output : r.outputLiquids) {
                b.liquids.add(output.liquid, output.amount);
            }
        }

        public void unitCraft(Recipe r) {
            float x = x() + size;
            float y = y() + size;
            float rotation = this.rotation;
            for (UnitStack input : r.inputUnits) {
                int amount = input.amount;
                for (Unit u : Groups.unit) {
                    if (amount == 0) break;
                    if (u.team == team && u.type == input.unitType && u.isValid() && isNearly(u)) {
                        x = u.x;
                        y = u.y;
                        rotation = u.rotation;
                        u.remove();
                        amount -= 1;
                    }
                }
            }

            for (UnitStack output : r.outputUnits) {
                for (int i = 1; i <= output.amount; i++) {
                    Unit u = output.unitType.spawn(team, x, y);
                    u.rotation = rotation;
                }
            }


        }

        @Override
        public float getPowerProduction() {
            Recipe r = recipes.get(index);
            if (canConsume) {
                if (r.outputPower > 0) {
                    return r.outputPower;
                }
                if (r.inputPower > 0) {
                    return (0.001f - r.inputPower);
                }
            }
            return 0f;
        }

        public void dumpRecipe(Recipe r) {
            Building b = this;
            for (ItemStack output : r.outputItems) {
                for (int i = 0; i < 9 && b.items.get(output.item) > 0; i++) {
                    dump(output.item);
                }
            }
            for (LiquidStack output : r.outputLiquids) {
                for (int i = 0; i < 9 && b.liquids.get(output.liquid) > 0f; i++) {
                    dumpLiquid(output.liquid, b.liquids.get(output.liquid));
                }
            }
        }

        public void updateHeat() {
            if (lastHeatUpdate == Vars.state.updateId) return;
            lastHeatUpdate = Vars.state.updateId;
            Recipe r = recipes.get(index);
            if (r.inputHeat > 0) {
                heat = calculateHeat(sideHeat, cameFrom);
                return;
            }
            if (r.outputHeat > 0) {
                if (canConsume) {
                    heat = Mathf.approachDelta(heat, r.outputHeat, r.warmupRate * delta());
                } else {
                    heat = Mathf.approachDelta(heat, 0, r.warmupRate * delta());
                }
            }

        }

        public void updateBar() {
            Recipe r = recipes.get(index);
            barMap.clear();
            setBars();
            if (r.inputPower > 0 || r.outputPower > 0) {
                addBar("power", entity -> new Bar(() -> Core.bundle.get("bar.power"), () -> Pal.powerBar, () -> Mathf.zero(consPower.requestedPower(entity)) && entity.power.graph.getPowerProduced() + entity.power.graph.getBatteryStored() > 0f ? 1f : entity.power.status));
            }
            if (r.inputHeat > 0 || r.outputHeat > 0) {
                addBar("heat", (MultiCrafterBuild entity) -> new Bar(() -> Core.bundle.get("bar.heat") + " : " + entity.heat + " / " + Math.max(r.inputHeat, r.outputHeat), () -> Pal.lightOrange, () -> entity.heat / Math.max(r.inputHeat, r.outputHeat)));
            }
            for (ItemStack input : r.inputItems) {
                addBar(input.item.name, entity -> new Bar(() -> input.item.localizedName + " : " + entity.items.get(input.item) + " / " + itemCapacity, () -> Pal.items, () -> (float) entity.items.get(input.item) / itemCapacity));
            }
            for (LiquidStack input : r.inputLiquids) {
                addBar(input.liquid.name, entity -> new Bar(() -> input.liquid.localizedName + " : " + entity.liquids.get(input.liquid) + " / " + liquidCapacity, () -> input.liquid.color, () -> entity.liquids.get(input.liquid) / liquidCapacity));
            }
            for (UnitStack input : r.inputUnits) {
                addBar(input.unitType.name, entity -> new Bar(() -> input.unitType.localizedName + " : " + getUnits(input.unitType) + " / " + input.amount, () -> Pal.items, () -> (float) getUnits(input.unitType) / input.amount));
            }

            needUpdateBar = false;
        }

        public void setBars() {
            addBar("health", entity -> new Bar("stat.health", Pal.health, entity::healthf).blink(Color.white));
        }

        public <T extends Building> void addBar(String name, Func<T, Bar> sup) {
            barMap.put(name, (Func<Building, Bar>) sup);
        }

        public int getUnits(UnitType unitType) {
            int amount = 0;
            for (Unit u : Groups.unit) {
                if (u.team == team && u.type == unitType && u.isValid() && isNearly(u)) {
                    amount += 1;
                }
            }
            return amount;
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            Recipe r = recipes.get(index);
            for (ItemStack input : r.inputItems) {
                if (item == input.item && this.items.get(item) < this.getMaximumAccepted(item)) return true;
            }
            return false;
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid) {
            Recipe r = recipes.get(index);
            for (LiquidStack input : r.inputLiquids) {
                if (liquid == input.liquid && this.liquids.get(liquid) < this.block.liquidCapacity) return true;
            }
            return false;
        }

        @Override//配置菜单
        public void buildConfiguration(Table table) {
            table.button(Icon.pencil, Styles.clearTogglei, () -> new RecipeDialog(this).show()).size(40f);
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.i(index);//保存当前配方索引
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            index = read.i();//读取保存的索引
        }

        @Override
        public BlockStatus status() {
            if (!this.enabled) {
                return BlockStatus.logicDisable;
            }
            if (canConsume) return BlockStatus.active;
            if (!shouldConsume()) return BlockStatus.noOutput;
            return BlockStatus.noInput;
        }

        @Override
        public void draw() {
            if (!((ECMultiCrafter) block).multiDrawer) {
                drawer.draw(this);
                return;
            }
            recipes.get(index).drawer.draw(this);
        }

        @Override
        public void drawLight() {
            super.drawLight();
            if (!((ECMultiCrafter) block).multiDrawer) {
                drawer.draw(this);
                return;
            }
            recipes.get(index).drawer.drawLight(this);
        }


        //*/
        @Override
        public float[] sideHeat() {
            return sideHeat;
        }

        @Override
        public float heatRequirement() {
            return recipes.get(index).inputHeat;
        }
        //*/

        @Override
        public float warmup() {
            return heat;
        }

        @Override
        public float heat() {
            //如果需求热量,就不应该往外输出热量
            if (recipes.get(index).inputHeat > 0) return 0f;
            return heat;
        }

        @Override
        public float heatFrac() {
            Recipe r = recipes.get(index);
            if (r.inputHeat > 0) return (heat / r.inputHeat);
            if (r.outputHeat > 0) return (heat / r.outputHeat);

            return (heat / visualMaxHeat) / (splitHeat ? 3f : 1);
        }
    }

}
