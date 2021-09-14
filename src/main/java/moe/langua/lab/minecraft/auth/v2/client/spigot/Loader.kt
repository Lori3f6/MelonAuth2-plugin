package moe.langua.lab.minecraft.auth.v2.client.spigot

import moe.langua.lab.minecraft.auth.v2.client.OnLogin
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.IOException

class Loader : JavaPlugin() {
    private val configFile = File(dataFolder, "config.json")

    override fun onEnable() {
        if (!dataFolder.mkdir() && !dataFolder.isDirectory)
            throw IOException("${dataFolder.absolutePath} should be a directory, but found a file.")
        OnLogin.register(configFile)
    }
}
