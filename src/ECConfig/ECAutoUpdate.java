package ECConfig;

import arc.Core;
import arc.util.Log;
import com.google.gson.Gson;
import mindustry.Vars;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import static mindustry.Vars.ui;

public class ECAutoUpdate {
    // 配置项（根据你的 GitHub 仓库修改）
    private static final String GITHUB_API_URL = "https://api.github.com/repos/Mr816543/Endless_Compression/releases/latest";
    private static final String LOCAL_MOD_VERSION = Vars.mods.locateMod("ec").meta.version; // 本地 Mod 当前版本（建议从配置/常量读取）
    private static final String MOD_FILE_SUFFIX = ".jar"; // Mod 文件后缀
    // Mindustry Mod 目录（自动适配不同系统）
    private static final String MOD_DIR = System.getProperty("user.home") + "/.mindustry/mods/";

    private static final OkHttpClient httpClient = new OkHttpClient();
    private static final Gson gson = new Gson();

    /**
     * 检测更新
     * @return 最新发布信息（无更新返回 null）
     */
    public static ECGitHubRelease checkUpdate() {
        try {
            // 构建 GitHub API 请求
            Request request = new Request.Builder()
                    .url(GITHUB_API_URL)
                    .header("User-Agent", "Mindustry-Mod-Updater") // GitHub API 要求必须带 User-Agent
                    .build();

            // 发送请求并解析响应
            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    ui.showErrorMessage(Core.bundle.get("err.updateFailed") + ":" + "HTTP:{" + response.code() +"}");
                    return null;
                }
                String responseBody = null;
                if (response.body() != null) {
                    responseBody = response.body().string();
                }
                ECGitHubRelease latestRelease = gson.fromJson(responseBody, ECGitHubRelease.class);

                // 跳过预发布版本
                if (latestRelease.isPreRelease()) {
                    ui.showInfo(Core.bundle.get("update.isPreRelease"));
                    return null;
                }

                // 对比版本
                boolean hasNewVersion = ECTool.Version(LOCAL_MOD_VERSION, latestRelease.getTagName());
                if (hasNewVersion) {
                    ui.showInfo(Core.bundle.get("update.hasNewVersion") + latestRelease.getTagName()
                            + "\n" + Core.bundle.get("update.nowVersion") + LOCAL_MOD_VERSION );
                    return latestRelease;
                } else {
                    ui.showInfo(Core.bundle.get("update.hasNotNewVersion") + LOCAL_MOD_VERSION);
                    return null;
                }
            }
        } catch (Exception e) {
            ui.showErrorMessage(Core.bundle.get("update.err")+":" + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 下载最新 Mod 文件
     * @param release 最新发布信息
     * @return 下载后的文件（失败返回 null）
     */
    public static File downloadLatestMod(ECGitHubRelease release) {
        if (release == null || release.getAssets().length == 0) {
            ui.showErrorMessage(Core.bundle.get("update.noFile"));
            return null;
        }

        // 筛选 Mod 包文件（匹配后缀）
        ECGitHubRelease.GitHubAsset targetAsset = null;
        for (ECGitHubRelease.GitHubAsset asset : release.getAssets()) {
            if (asset.getFileName().endsWith(MOD_FILE_SUFFIX)) {
                targetAsset = asset;
                break;
            }
        }
        if (targetAsset == null) {
            ui.showErrorMessage(Core.bundle.get("update.noMod"));
            return null;
        }

        // 确保 Mod 目录存在
        File modDir = new File(MOD_DIR);
        if (!modDir.exists() && !modDir.mkdirs()) {
            ui.showErrorMessage(Core.bundle.get("update.modDirFail") + MOD_DIR);
            return null;
        }

        // 下载文件
        try {
            URL downloadUrl = new URL(targetAsset.getDownloadUrl());
            ReadableByteChannel rbc = Channels.newChannel(downloadUrl.openStream());
            File outputFile = new File(MOD_DIR + targetAsset.getFileName());

            // 写入文件
            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            }
            ui.showInfo(Core.bundle.get("update.downloaded") + outputFile.getAbsolutePath());
            return outputFile;
        } catch (Exception e) {
            ui.showErrorMessage(Core.bundle.get("update.downloadFail")+ e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 一键检测并更新
     */
    public static void autoUpdate() {
        if(!Core.settings.getBool("autoUpdate",false))return;
        ECGitHubRelease latestRelease = checkUpdate();
        /*/
        if (latestRelease != null) {
            downloadLatestMod(latestRelease);
            ui.showInfo(Core.bundle.get("update.needRestart"));
        }
        //*/
    }
}