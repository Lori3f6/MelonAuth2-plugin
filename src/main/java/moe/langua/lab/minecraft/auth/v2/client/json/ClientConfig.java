package moe.langua.lab.minecraft.auth.v2.client.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ClientConfig {

    @SerializedName("apiURL")
    @Expose
    private String apiURL;
    @SerializedName("webAppURL")
    @Expose
    private String webAppURL;
    @SerializedName("clientKey")
    @Expose
    private String clientKey;
    @SerializedName("promptTexts")
    @Expose
    private List<Object> promptTexts = null;
    @SerializedName("errorPrompt")
    @Expose
    private List<Object> errorPrompt = null;

    public static ClientConfig getDefault() {
        return new ClientConfig().check();
    }

    private static String removeSlashAtTheEnd(String target) {
        while (target.endsWith("/")) {
            target = target.substring(0, target.length() - 1);
        }
        return target;
    }

    public String getApiURL() {
        return apiURL;
    }

    public String getWebAppURL() {
        return webAppURL;
    }

    public String getClientKey() {
        return clientKey;
    }

    public List<Object> getPromptTexts() {
        return new ArrayList<>(promptTexts);
    }

    public List<Object> getErrorPrompt() {
        return new ArrayList<>(errorPrompt);
    }

    public ClientConfig check() {
        if (apiURL == null) apiURL = "https://APIURL";
        if (webAppURL == null) webAppURL = "https://WEBAPPURL";
        if (clientKey == null) clientKey = "CLIENT_KEY";
        if (promptTexts == null) {
            promptTexts = new ArrayList<>();
            promptTexts.add("&7--- &fAuthentication Required &7---");
            promptTexts.add("");
            promptTexts.add("&7Before joining the server, you are required to prove ownership of this Minecraft account.");
            promptTexts.add("&7Your verification code is:");
            promptTexts.add("");
            promptTexts.add("&7> &f&l{VerificationCode} &7<");
            promptTexts.add("");
            promptTexts.add("&7This verification code will expired after {ExpireMinute} minutes.");
            promptTexts.add("&7Please visit &n{WebAppURL}&r &7and follow the instructions to verify your account.");
        }
        if (errorPrompt == null) {
            errorPrompt = new ArrayList<>();
            errorPrompt.add("&7--- &fAuthentication Error &7---");
            errorPrompt.add("");
            errorPrompt.add("&7There is a Authentication error occurred, which prevent you to login the server.");
            errorPrompt.add("&7Please have a try in some seconds again.");
            errorPrompt.add("&7If this message continues to appear, please contact the server administrator.");
        }
        apiURL = removeSlashAtTheEnd(apiURL);
        webAppURL = removeSlashAtTheEnd(webAppURL);
        try {
            String apiURLProtocol = new URL(apiURL).getProtocol();
            if (!apiURLProtocol.equals("https") && !apiURLProtocol.equals("http"))
                apiURL = "https://APIURL";
        } catch (MalformedURLException e) {
            apiURL = "https://APIURL";
        }
        try {
            String apiURLProtocol = new URL(webAppURL).getProtocol();
            if (!apiURLProtocol.equals("https") && !apiURLProtocol.equals("http"))
                webAppURL = "https://WEBAPPURL";
        } catch (MalformedURLException e) {
            webAppURL = "https://WEBAPPURL";
        }
        return this;
    }


}