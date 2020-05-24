package moe.langua.lab.minecraft.auth.v2.client.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import moe.langua.lab.minecraft.auth.v2.client.core.utils.VerificationResult;
import moe.langua.lab.minecraft.auth.v2.client.exception.VerificationFailedException;
import moe.langua.lab.minecraft.auth.v2.client.json.ClientConfig;
import moe.langua.lab.minecraft.auth.v2.client.json.VerificationNotice;
import moe.langua.lab.security.otp.MelonTOTP;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.logging.Logger;

public class MelonAuthClient {
    private static final Gson prettyGson;
    private static final long TRUNCATE_VALUE = 0x100000000L;
    private static final long OTP_EXPIRATION = 30000;

    static {
        prettyGson = new GsonBuilder().setPrettyPrinting().create();
    }

    private final MelonTOTP otpServer;
    private final ClientConfig config;

    public MelonAuthClient(File dataRoot, Logger logger, boolean firstStart) throws IOException, VerificationFailedException {
        if (!dataRoot.mkdir() && !dataRoot.isDirectory()) {
            throw new IOException(dataRoot.getAbsolutePath() + " should be a directory, but found a fine.");
        }
        File configFile = new File(dataRoot.getAbsolutePath() + "/config.json");
        ClientConfig config;
        if (configFile.createNewFile()) {
            logger.info("Initializing config...");
            config = ClientConfig.getDefault();
        } else if (configFile.isFile()) {
            logger.info("Loading config...");
            config = prettyGson.fromJson(new FileReader(configFile), ClientConfig.class);
            config.check();
        } else {
            throw new IOException(configFile.getAbsolutePath() + " should be a file, but found a directory.");
        }
        this.config = config;
        FileWriter writer = new FileWriter(configFile, false);
        writer.write(prettyGson.toJson(config));
        writer.flush();
        writer.close();

        if (!firstStart) {
            //initialize otp server
            this.otpServer = new MelonTOTP(config.getClientKey().getBytes(StandardCharsets.UTF_8), TRUNCATE_VALUE, OTP_EXPIRATION);
            //check connectivity
            logger.info("Checking server connectivity...");
            UUID nonUniqueID = UUID.fromString("00000000-0000-0000-0000-000000000000");
            getVerify(nonUniqueID);
        } else {
            logger.warning("MelonAuth2-Plugin has been initialized. Please configure the settings before starting the server next time.");
            this.otpServer = null;
            return;
        }

        logger.info("Done, All modules have started.");
    }

    public ClientConfig getConfig() {
        return config;
    }

    public VerificationResult getVerify(UUID uniqueID) throws VerificationFailedException, IOException {
        URL getURL;
        getURL = new URL(config.getApiURL() + "/get/uuid/" + uniqueID.toString());

        HttpURLConnection connection = (HttpURLConnection) getURL.openConnection();
        connection.setRequestProperty("Authorization", Long.toHexString(otpServer.getPassNow()));
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(10000);
        connection.setRequestMethod("GET");
        connection.setUseCaches(false);
        int rCode = connection.getResponseCode();
        switch (rCode) {
            case 204:
                return new VerificationResult(true, null);
            case 200:
                VerificationNotice notice = prettyGson.fromJson(new BufferedReader(new InputStreamReader(connection.getInputStream())), VerificationNotice.class);
                return new VerificationResult(false, notice);
            case 403:
                throw new VerificationFailedException("API server refused the request. It may caused by wrong clientKey settings or incorrect system time settings");
            default:
                throw new IOException("MelonAuth2 API returned abnormal responseCode " + rCode + ", please check the server side for more information.");
        }
    }
}
