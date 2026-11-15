package com.dymbia.sumaqagro.platform.iam.application.internal.commandservices;

import com.dymbia.sumaqagro.platform.iam.application.internal.outboundservices.hashing.HashingService;
import com.dymbia.sumaqagro.platform.iam.application.internal.outboundservices.tokens.PasswordResetTokenCodec;
import com.dymbia.sumaqagro.platform.iam.domain.model.commands.ResetPasswordCommand;
import com.dymbia.sumaqagro.platform.iam.domain.repositories.PasswordResetTokenRepository;
import com.dymbia.sumaqagro.platform.iam.domain.repositories.UserRepository;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

/** Updates the account password and consumes its reset token atomically. */
@Service
public class PasswordResetService {
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_BCRYPT_PASSWORD_BYTES = 72;

    private final UserRepository users;
    private final PasswordResetTokenRepository tokens;
    private final PasswordResetTokenCodec tokenCodec;
    private final HashingService hashingService;

    public PasswordResetService(
            UserRepository users,
            PasswordResetTokenRepository tokens,
            PasswordResetTokenCodec tokenCodec,
            HashingService hashingService
    ) {
        this.users = users;
        this.tokens = tokens;
        this.tokenCodec = tokenCodec;
        this.hashingService = hashingService;
    }

    @Transactional
    public Result<Long, ApplicationError> handle(ResetPasswordCommand command) {
        String password = command.newPassword();
        if (password == null || password.isBlank() || password.length() < MIN_PASSWORD_LENGTH
                || password.getBytes(StandardCharsets.UTF_8).length > MAX_BCRYPT_PASSWORD_BYTES) {
            return Result.failure(ApplicationError.validationError(
                    "newPassword", "The password must have at least 8 characters and at most 72 UTF-8 bytes"));
        }

        String rawToken = command.token();
        if (rawToken == null || !rawToken.matches("[A-Za-z0-9_-]{43}")) {
            return invalidToken();
        }

        String tokenHash = tokenCodec.hash(rawToken);
        var token = tokens.findByTokenHash(tokenHash);
        if (token.isEmpty() || !token.get().canBeUsedAt(Instant.now())) {
            return invalidToken();
        }

        var user = users.findById(token.get().getUserId());
        if (user.isEmpty()) {
            return invalidToken();
        }

        String newPasswordHash = hashingService.encode(password);
        if (!tokens.consumeIfUsable(tokenHash, Instant.now())) {
            return invalidToken();
        }

        var account = user.get();
        account.changePasswordHash(newPasswordHash);
        users.save(account);
        return Result.success(account.getId());
    }

    private Result<Long, ApplicationError> invalidToken() {
        return Result.failure(ApplicationError.validationError(
                "token", "The password reset link is invalid or has expired"));
    }
}
