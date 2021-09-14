package moe.langua.lab.minecraft.auth.v2.client

import moe.langua.lab.minecraft.auth.v2.client.json.ClientConfig
import moe.langua.lab.minecraft.auth.v2.plugin.api.LoginResult
import moe.langua.lab.minecraft.auth.v2.plugin.api.MelonAuth2API
import moe.langua.lab.minecraft.auth.v2.plugin.api.gson.GsonBuilder
import moe.langua.lab.minecraft.auth.v2.plugin.api.json.ChallengeOverview
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.io.IOException

class OnLogin {
    companion object {
        private val gsonInstance = GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create()
        fun register(configFile: File) {
            val config = loadClientConfig(configFile)
            saveConfigToFile(configFile,config)
            var templeBuilder = StringBuilder()
            val prompts: List<String> = config.promptTexts
            for (index in prompts.indices) {
                templeBuilder.append(prompts[index])
                if (index != prompts.size - 1) templeBuilder.append("\n")
            }
            val promptTemple = templeBuilder.toString().replace("&".toRegex(), "§").replace("§§".toRegex(), "&")

            templeBuilder = StringBuilder()
            val errorPrompts: List<Any> = config.errorPrompt
            for (index in errorPrompts.indices) {
                templeBuilder.append(errorPrompts[index])
                if (index != errorPrompts.size - 1) templeBuilder.append("\n")
            }
            val errorPromptTemple = templeBuilder.toString().replace("&".toRegex(), "§").replace("§§".toRegex(), "&")

            MelonAuth2API.registerLoginEvent { uuid, playerStatus, loginResult ->
                if (playerStatus == null) {
                    refuseLoginByError(loginResult, errorPromptTemple)
                } else {
                    if (playerStatus.verified) {
                        loginResult.isAllowLogin = true
                    } else {
                        val challenge = MelonAuth2API.requireChallenge(uuid)
                        if (challenge == null) {
                            refuseLoginByError(loginResult, errorPromptTemple)
                        } else {
                            refuseLoginByChallenge(loginResult, challenge, promptTemple, MelonAuth2API.getWebAppURL().toString())
                        }
                    }
                }
            }
        }

        private fun refuseLoginByError(loginResult: LoginResult, promptTemple: String) {
            loginResult.isAllowLogin = false
            loginResult.kickedMessage = promptTemple
        }

        private fun refuseLoginByChallenge(
            loginResult: LoginResult,
            challengeOverview: ChallengeOverview,
            promptTemple: String,
            webAppURL: String
        ) {
            val expireIn = challengeOverview.expireIn
            val days = expireIn / 86400000
            val hours = expireIn % 86400000 / 3600000
            val minutes = expireIn % 3600000 / 60000
            val seconds = expireIn % 60000 / 1000
            val message = promptTemple.replace("{ExpireDay}", days.toString()).replace("{ExpireHour}", hours.toString())
                .replace("{ExpireMinute}", minutes.toString()).replace("{ExpireSecond}", seconds.toString())
                .replace("{VerificationCode}", challengeOverview.challengeID.toString().padStart(6, '0'))
                .replace("{WebAppURL}", webAppURL)
            loginResult.isAllowLogin = false
            loginResult.kickedMessage = message
        }

        private fun loadClientConfig(ClientConfigFile: File): ClientConfig {
            return when {
                ClientConfigFile.createNewFile() -> ClientConfig().check()
                ClientConfigFile.isDirectory -> throw IOException(ClientConfigFile.absolutePath + " should be a file, but found a directory.")
                else -> {
                    var clientConfigLoaded: ClientConfig?
                    val configFileReader = FileReader(ClientConfigFile, Charsets.UTF_8)
                    clientConfigLoaded = gsonInstance.fromJson(configFileReader, ClientConfig::class.java)
                    if (clientConfigLoaded == null ) {
                        clientConfigLoaded = ClientConfig()
                    }
                    clientConfigLoaded.check()
                }
            }
        }

        @Throws(IOException::class)
        private fun <T> saveConfigToFile(file: File, instance: T) {
            val fileOutputStream = FileOutputStream(file, false)
            fileOutputStream.write(gsonInstance.toJson(instance).toByteArray(Charsets.UTF_8))
            fileOutputStream.flush()
            fileOutputStream.close()
        }
    }
}