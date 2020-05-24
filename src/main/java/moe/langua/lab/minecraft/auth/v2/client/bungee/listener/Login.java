package moe.langua.lab.minecraft.auth.v2.client.bungee.listener;

import moe.langua.lab.minecraft.auth.v2.client.core.MelonAuthClient;
import moe.langua.lab.minecraft.auth.v2.client.core.utils.VerificationResult;
import moe.langua.lab.minecraft.auth.v2.client.exception.VerificationFailedException;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Login implements Listener {
    private final Plugin instance;
    private final MelonAuthClient authClient;
    private final String promptTemple;
    private final String errorPromptTemple;

    public Login(MelonAuthClient authClient, Plugin instance) {
        this.authClient = authClient;
        this.instance = instance;
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

    private final AtomicInteger counter = new AtomicInteger(0);
    @EventHandler
    public void onLogin(LoginEvent event){
        event.registerIntent(instance);
        Thread enquirer = new Thread(()->{
            VerificationResult container;
            try {
                container = authClient.getVerify(event.getConnection().getUniqueId());
            } catch (VerificationFailedException | IOException e) {
                Bukkit.getLogger().warning(e.toString());
                event.setCancelReason(new TextComponent(errorPromptTemple));
                event.setCancelled(true);
                return;
            }
            if (!container.isVerified()) {
                long expireIn = container.getNotice().getExpireIn();
                long days = expireIn / 86400000;
                long hours = (expireIn % 86400000) / 3600000;
                long minutes = (expireIn % 3600000) / 60000;
                long seconds = (expireIn % 60000) / 1000;
                String message = promptTemple.replace("{ExpireDay}", "" + days).replace("{ExpireHour}", "" + hours).replace("{ExpireMinute}", "" + minutes).replace("{ExpireSecond}", "" + seconds).replace("{VerificationCode}", "" + container.getNotice().getVerificationCode()).replace("{WebAppURL}", authClient.getConfig().getWebAppURL());
                event.setCancelReason(new TextComponent(message));
                event.setCancelled(true);
            }
            event.completeIntent(instance);
        },"MelonAuth2-Enquirer#"+counter.getAndAdd(1));
        enquirer.setDaemon(true);
        enquirer.start();
    }
}
