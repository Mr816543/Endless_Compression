package ECConfig;

import com.google.gson.annotations.SerializedName;

public class ECGitHubRelease {
    // 版本标签（如 v1.0.1）
    @SerializedName("tag_name")
    private String tagName;
    // 是否为预发布
    @SerializedName("prerelease")
    private boolean preRelease;
    // 发布资产（Mod 包文件）
    @SerializedName("assets")
    private GitHubAsset[] assets;

    // Getter
    public String getTagName() { return tagName; }
    public boolean isPreRelease() { return preRelease; }
    public GitHubAsset[] getAssets() { return assets; }

    // 资产文件模型
    public static class GitHubAsset {
        @SerializedName("browser_download_url")
        private String downloadUrl;
        @SerializedName("name")
        private String fileName;

        public String getDownloadUrl() { return downloadUrl; }
        public String getFileName() { return fileName; }
    }
}