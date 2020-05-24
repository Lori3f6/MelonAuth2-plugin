package moe.langua.lab.minecraft.auth.v2.client.core.utils;

import moe.langua.lab.minecraft.auth.v2.client.json.VerificationNotice;

public class VerificationResult {
    private final boolean verified;
    private final VerificationNotice notice;

    public VerificationResult(boolean verified, VerificationNotice notice) {
        this.verified = verified;
        this.notice = notice;
    }

    public boolean isVerified() {
        return verified;
    }

    public VerificationNotice getNotice() {
        return notice;
    }
}
