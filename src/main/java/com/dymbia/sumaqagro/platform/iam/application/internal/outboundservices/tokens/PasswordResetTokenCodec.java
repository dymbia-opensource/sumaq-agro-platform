package com.dymbia.sumaqagro.platform.iam.application.internal.outboundservices.tokens;

/** Provides opaque password recovery tokens and their storage digests. */
public interface PasswordResetTokenCodec {
    String generate();
    String hash(String token);
}
