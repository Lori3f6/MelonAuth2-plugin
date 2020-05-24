package moe.langua.lab.minecraft.auth.v2.client.spigot;

import moe.langua.lab.minecraft.auth.v2.client.core.MelonAuthClient;
import moe.langua.lab.minecraft.auth.v2.client.exception.VerificationFailedException;
import moe.langua.lab.minecraft.auth.v2.client.spigot.listener.Login;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class Bootstrap extends JavaPlugin {
    @Override
    public void onEnable() {
        MelonAuthClient authClient;
        boolean firstStart = !new File(this.getDataFolder(), "config.json").exists();
        try {
            authClient = new MelonAuthClient(this.getDataFolder(), this.getLogger(), firstStart);
        } catch (IOException | VerificationFailedException e) {
            this.getLogger().log(Level.SEVERE, e.toString());
            this.getLogger().log(Level.SEVERE, "Failed to start up, Disabling...");
            this.getPluginLoader().disablePlugin(this);
            return;
        }
        if (firstStart) {
            this.getPluginLoader().disablePlugin(this);
            return;
        }
        this.getServer().getPluginManager().registerEvents(new Login(authClient), this);
    }
}
