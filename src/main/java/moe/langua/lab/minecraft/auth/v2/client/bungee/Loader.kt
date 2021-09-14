package moe.langua.lab.minecraft.auth.v2.client.bungee

import moe.langua.lab.minecraft.auth.v2.client.OnLogin
import net.md_5.bungee.api.plugin.Plugin
import java.io.File
import java.io.IOException

class Loader : Plugin() {
    private val configFile = File(dataFolder, "config.json")
    override fun onEnable() {
        if (!dataFolder.mkdir() && !dataFolder.isDirectory)
            throw IOException("${dataFolder.absolutePath} should be a directory, but found a file.")
        OnLogin.register(configFile)
    }
}