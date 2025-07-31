package ECType;

import ECConfig.ECData;
import ECConfig.ECTool;
import ECContents.Achievements;
import ECType.ECBlockTypes.Crafter.ECDrill;
import arc.Core;
import arc.Events;
import arc.func.Cons;
import arc.math.Interp;
import arc.scene.actions.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.struct.Seq;
import mindustry.content.Items;
import mindustry.ctype.ContentType;
import mindustry.ctype.UnlockableContent;
import mindustry.game.EventType;
import mindustry.game.Objectives;
import mindustry.gen.Sounds;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.ui.Styles;
import mindustry.world.meta.Stat;

import static mindustry.content.TechTree.*;

public class Achievement extends Item {
    // 可配置变量
    public float windowHeight = 80f;         // 弹窗固定高度
    public float pad = 10f;                  // 内边距
    public float imageSize = 50f;            // 图标大小
    public float slideDuration = 0.3f;       // 滑动动画时间
    public float showDuration = 5f;          // 显示持续时间
    public Color titleColor = Color.yellow;  // 标题颜色
    public Color textColor = Color.white;    // 正文颜色
    public float titleScale = 1.3f;          // 标题字体缩放
    public float textScale = 0.9f;           // 正文字体缩放
    public float offsetY = 20f;              // Y轴偏移量

    public String title;

    public String message;

    public TextureRegion icon;

    public UnlockableContent iconFrom;

    public UnlockableContent root;

    public int index = 10;//贴图索引

    public static int showing = 0;

    public Achievement(String name) {
        super(name);
        title = localizedName = Core.bundle.get("string.achievement") + ":" + Core.bundle.get("achievement."+name+".title",name);
        message = description = Core.bundle.get("achievement."+name+".message",localizedName);
        details = Core.bundle.get("achievement."+name+".details",localizedName);
        setIcon();
        Achievements.achievements.add(this);
    }

    public void initTechNode() {
        if (root == null||root==this) return;
        for (TechNode r : root.techNodes){
            TechNode node = node(this, () -> {});
            node.parent = r;
            r.children.add(node);
        }
    }

    @Override
    public void init() {
        hidden = true;
        initTechNode();
    }

    public <T> void setEvent(Class<T> type, Cons<T> listener){
        if (locked()) Events.on(type,listener);
    }

    public void setIcon() {
        if (iconFrom == null){
            iconFrom = Items.copper;
            index = 0;
        }

        if (iconFrom.uiIcon != null) {
            icon = uiIcon = fullIcon = ECTool.mergeRegions(iconFrom.uiIcon, index);
        } else {
            Core.app.post(this::setIcon);
        }
    }

    public boolean working(UnlockableContent content){
        return Core.settings.getBool("achievementsWork");
    }

    @Override
    public void setStats() {
    }

    @Override
    public ItemStack[] researchRequirements() {
        return new ItemStack[]{new ItemStack(this,1)};
    }

    @Override
    public void unlock() {
        if (locked()) show();
        super.unlock();
    }

    public void show() {
        // 创建弹窗主体
        Table toast = new Table();
        toast.background(Styles.black6);
        toast.margin(pad);
        toast.setHeight(windowHeight);

        // 左侧图标（居中）
        toast.add(new Image(icon))
                .size(imageSize)
                .padRight(pad)
                .center();

        // 右侧文本区域
        Table textTable = new Table();
        textTable.left().top();

        // 标题文本（大号黄色）
        Label titleLabel = new Label(title, Styles.outlineLabel);
        titleLabel.setColor(titleColor);
        titleLabel.setFontScale(titleScale);

        // 消息文本（小号白色）
        Label msgLabel = new Label(message, Styles.outlineLabel);
        msgLabel.setColor(textColor);
        msgLabel.setFontScale(textScale);

        textTable.add(titleLabel).growX().left().row();
        textTable.add(msgLabel).growX().left().padTop(2f);

        toast.add(textTable).grow();

        // 计算初始位置（屏幕右侧外）
        toast.pack();
        float startX = Core.graphics.getWidth();
        float endX = Core.graphics.getWidth() - toast.getWidth() - pad;
        float yPos = Core.graphics.getHeight() - toast.getHeight() - offsetY;

        // 设置初始位置
        toast.setPosition(startX, yPos + showing);

        // 添加到场景
        Core.scene.add(toast);

        // 执行动画序列
        toast.actions(

                Actions.run(()-> Sounds.message.play()),

                // 滑入动画
                Actions.moveTo(endX, yPos, slideDuration, Interp.smooth),

                // 显示等待
                Actions.delay(showDuration),

                // 滑出动画
                Actions.moveTo(startX, yPos, slideDuration, Interp.smooth),

                // 移除元素
                Actions.remove()
        );
    }

}