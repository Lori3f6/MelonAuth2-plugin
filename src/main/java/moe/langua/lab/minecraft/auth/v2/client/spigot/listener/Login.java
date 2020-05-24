package moe.langua.lab.minecraft.auth.v2.client.spigot.listener;

import moe.langua.lab.minecraft.auth.v2.client.core.MelonAuthClient;
import moe.langua.lab.minecraft.auth.v2.client.core.utils.VerificationResult;
import moe.langua.lab.minecraft.auth.v2.client.exception.VerificationFailedException;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.io.IOException;
import java.util.List;

public class Login implements Listener {
    private final MelonAuthClient authClient;
    private final String promptTemple;
    private final String errorPromptTemple;

    public Login(MelonAuthClient authClient) {
        this.authClient = authClient;
        StringBuilder templeBuilder = new StringBuilder();
        List<Object> prompts = authClient.getConfig().getPromptTexts();
        for (int index = 0; index < prompts.size(); index++) {
            templeBuilder.append(prompts.get(index).toString());
            if (index != prompts.size() - 1) templeBuilder.append("\n");
        }
        promptTemple = templeBuilder.toString().replaceAll("&", "§").replaceAll("§§", "&");

        templeBuilder = new StringBuilder();
        List<Object> errorPrompts = authClient.getConfig().getErrorPrompt();
        for (int index = 0; index < errorPrompts.size(); index++) {
            templeBuilder.append(errorPrompts.get(index).toString());
            if (index != errorPrompts.size() - 1) templeBuilder.append("\n");
        }
        errorPromptTemple = templeBuilder.toString().replaceAll("&", "§").replaceAll("§§", "&");
    }

    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        VerificationResult container;
        try {
            container = authClient.getVerify(event.getUniqueId());
        } catch (VerificationFailedException | IOException e) {
            Bukkit.getLogger().warning(e.toString());
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, errorPromptTemple);
            return;
        }
        if (!container.isVerified()) {
            long expireIn = container.getNotice().getExpireIn();
            long days = expireIn / 86400000;
            long hours = (expireIn % 86400000) / 3600000;
            long minutes = (expireIn % 3600000) / 60000;
            long seconds = (expireIn % 60000) / 1000;
            String message = promptTemple.replace("{ExpireDay}", "" + days).replace("{ExpireHour}", "" + hours).replace("{ExpireMinute}", "" + minutes).replace("{ExpireSecond}", "" + seconds).replace("{VerificationCode}", "" + container.getNotice().getVerificationCode()).replace("{WebAppURL}", authClient.getConfig().getWebAppURL());
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, message);
        }
    }
}
