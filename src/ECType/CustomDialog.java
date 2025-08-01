package ECType;

import arc.Core;
import arc.graphics.g2d.TextureRegion;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.scene.*;
import mindustry.Vars;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.mod.Mods;
import mindustry.ui.dialogs.*;
import arc.graphics.*;
import arc.input.*;
import arc.scene.style.*;
import arc.util.*;
import static mindustry.Vars.*;

public class CustomDialog extends BaseDialog {
    // 尺寸变量
    float screenWidth,screenHeight, dialogHeight, dialogWidth , titleHeight,contentHeight , footerHeight; // 底部区域高度

    // 间距变量
    float padOuter = 20f; // 外间距
    float padInner = 15f; // 内间距
    float elementSpacing = 10f; // 元素间距

    // 颜色变量
    Color bgColor = Color.white;
            //new Color(255f, 255f, 255f, 255f); // 背景
    Color titleColor = Color.lightGray;
                    //new Color(0.3f, 0.3f, 0.7f, 1f); // 标题背景色
    Color borderColor = Color.valueOf("404049"); // 边框颜色

    // 纹理变量（使用游戏内置图标）
    TextureRegion icon = Icon.infoSmall.getRegion();
    float iconSize = titleHeight * 0.7f; // 图标尺寸

    public CustomDialog() {
        super("");
        icon = Core.atlas.find("ec-icon");
        screenWidth = Core.graphics.getWidth();
        screenHeight = Core.graphics.getHeight();
        dialogHeight = screenHeight * 0.6f; // 75%屏幕高度
        dialogWidth = Math.min(screenWidth * 0.6f, dialogHeight * 1.25f); // 80%宽度且不超过高度的125%
        titleHeight = dialogHeight * 0.15f; // 标题区高度
        contentHeight = dialogHeight * 0.65f; // 内容区高度
        footerHeight = dialogHeight * 0.2f; // 底部区域高度
        setup();
    }

    void setup() {

        Mods.LoadedMod mod = mods.locateMod("ec");
        String name = mod.meta.displayName;
        String version = mod.meta.version;
        String description = mod.meta.description;

        // 正确设置对话框背景颜色
        cont.background(Tex.buttonEdge3);
        cont.setColor(bgColor);

        // 主容器设置
        cont.table(main -> {
            main.defaults().grow();

            // 标题区域
            main.table(title -> {
                // 正确设置标题背景
                title.background(Tex.buttonEdge3);
                title.setColor(titleColor);
                title.defaults().pad(padInner).left();

                // 左侧图标
                title.image(icon).size(iconSize).padRight(padInner * 2);

                // 右侧文本区域
                title.table(texts -> {
                    texts.defaults().left().growX();
                    // 主标题
                    texts.add(name)
                            .color(Color.lightGray)
                            .fontScale(1f)
                            .row();
                    // 副标题
                    texts.add(toText("dialog.showWhenUpdate"))
                            .color(Color.gray)
                            .fontScale(1f);
                }).grow();
            }).size(dialogWidth, titleHeight).row();

            // 内容区域
            /*/
            main.table(content -> {
                // 正确设置内容背景
                content.background(Tex.buttonEdge3);
                content.setColor(Color.valueOf("262626"));

                // 滚动面板
                ScrollPane pane = new ScrollPane(new Table(){{
                    // 添加示例文本
                    add(description,Align.left).color(Color.lightGray).fontScale(1f).padBottom(5f).row();
                }});

                pane.setFadeScrollBars(false);
                pane.setScrollingDisabled(true, false); // 禁用水平滚动
                content.add(pane).grow();
            }).size(dialogWidth, contentHeight).padTop(padInner).row();


            //*/

            main.table(content -> {
                // 正确设置内容背景
                content.background(Tex.buttonEdge3);
                content.setColor(Color.valueOf("262626"));

                // 创建支持换行的标签
                Label label = new Label(description);
                label.setWrap(true); // 启用自动换行
                label.setAlignment(Align.topLeft); // 左上对齐
                label.setColor(Color.lightGray);
                label.setFontScale(1f);
                label.setHeight(content.getHeight()-padInner);
                label.setWidth(content.getWidth()-padInner);

                // 创建包含标签的表格（用于控制内边距）
                Table textTable = new Table();
                float textPad = 15f; // 文本内边距
                textTable.add(label).grow().pad(textPad).top().left();

                // 滚动面板
                ScrollPane pane = new ScrollPane(textTable);
                pane.setFadeScrollBars(false);
                pane.setScrollingDisabled(true, false); // 禁用水平滚动

                content.add(pane).grow();
            }).size(dialogWidth, contentHeight).padTop(padInner).row();


            // 底部区域
            main.table(footer -> {
                footer.defaults().pad(elementSpacing).center();

                // 左侧复选框
                footer.check(toText("dialog.notShow"), false, value -> {

                    Core.settings.put("showDialog",false);

                }).left();

                // 右侧退出按钮
                footer.button(toText("dialog.exit"), this::hide)
                        .size(150f, 60f)
                        .right()
                        .get().setColor(Color.scarlet);
            }).size(dialogWidth, footerHeight).padTop(padInner);
        }).size(dialogWidth, dialogHeight);

        // ESC键关闭监听
        keyDown(KeyCode.escape, this::hide);
    }



    @Override
    public Dialog show() {
        // 每次显示时更新尺寸
        updateSizes();
        return super.show();
    }

    void updateSizes() {
        screenWidth = Core.graphics.getWidth();
        screenHeight = Core.graphics.getHeight();

        dialogHeight = screenHeight * 0.75f;
        dialogWidth = Math.min(screenWidth * 0.8f, dialogHeight * 1.25f);

        // 更新对话框位置（居中）
        setPosition((screenWidth - dialogWidth) / 2, (screenHeight - dialogHeight) / 2);
    }

    String toText(String key){
        return Core.bundle.get(key,key);
    }
}