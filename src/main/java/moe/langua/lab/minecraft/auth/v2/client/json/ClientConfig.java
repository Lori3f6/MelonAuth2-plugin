package moe.langua.lab.minecraft.auth.v2.client.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ClientConfig {
    @SerializedName("promptTexts")
    @Expose
    private List<String> promptTexts = null;
    @SerializedName("errorPrompt")
    @Expose
    private List<String> errorPrompt = null;

    public static ClientConfig getDefault() {
        return new ClientConfig().check();
    }

    private static String removeSlashAtTheEnd(String target) {
        while (target.endsWith("/")) {
            target = target.substring(0, target.length() - 1);
        }
        return target;
    }

    public List<String> getPromptTexts() {
        return new ArrayList<>(promptTexts);
    }

    public List<String> getErrorPrompt() {
        return new ArrayList<>(errorPrompt);
    }

    public ClientConfig check() {
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
        return this;
    }


}