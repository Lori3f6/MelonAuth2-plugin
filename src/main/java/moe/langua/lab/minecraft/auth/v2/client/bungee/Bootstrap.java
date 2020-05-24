package moe.langua.lab.minecraft.auth.v2.client.bungee;

import moe.langua.lab.minecraft.auth.v2.client.bungee.listener.Login;
import moe.langua.lab.minecraft.auth.v2.client.core.MelonAuthClient;
import moe.langua.lab.minecraft.auth.v2.client.exception.VerificationFailedException;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class Bootstrap extends Plugin {
    @Override
    public void onEnable() {
        MelonAuthClient authClient;
        boolean firstStart = !new File(this.getDataFolder(), "config.json").exists();
        try {
            authClient = new MelonAuthClient(this.getDataFolder(), this.getLogger(), firstStart);
        } catch (IOException | VerificationFailedException e) {
            this.getLogger().log(Level.SEVERE, e.toString());
            this.getLogger().log(Level.SEVERE, "Failed to start up, Disabling...");
            this.onDisable();
            return;
        }
        if (firstStart) {
            this.onDisable();
            return;
        }
        this.getProxy().getPluginManager().registerListener(this,new Login(authClient,this));
    }
}
