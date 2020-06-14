package moe.langua.lab.minecraft.auth.v2.client.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VerificationNotice {

    @SerializedName("challengeID")
    @Expose
    private int challengeID;
    @SerializedName("expireIn")
    @Expose
    private Long expireIn;

    public VerificationNotice(int verificationCode, Long expireIn) {
        this.challengeID = verificationCode;
        this.expireIn = expireIn;
    }

    public VerificationNotice() {
    }

    public int getVerificationCode() {
        return challengeID;
    }

    public Long getExpireIn() {
        return expireIn;
    }
}
