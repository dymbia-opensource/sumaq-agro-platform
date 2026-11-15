package com.dymbia.sumaqagro.platform.iam.domain.model.commands;

/** Requests a password change using a recovery token. */
public record ResetPasswordCommand(String token, String newPassword) {
    @Override
    public String toString() {
        return "ResetPasswordCommand";
    }
}
