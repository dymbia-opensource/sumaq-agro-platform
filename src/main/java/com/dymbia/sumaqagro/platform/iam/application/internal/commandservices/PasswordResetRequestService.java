package com.dymbia.sumaqagro.platform.iam.application.internal.commandservices;

import com.dymbia.sumaqagro.platform.iam.application.internal.outboundservices.tokens.PasswordResetTokenCodec;
import com.dymbia.sumaqagro.platform.iam.domain.model.commands.RequestPasswordResetCommand;
import com.dymbia.sumaqagro.platform.iam.domain.model.entities.PasswordResetToken;
import com.dymbia.sumaqagro.platform.iam.domain.repositories.PasswordResetTokenRepository;
import com.dymbia.sumaqagro.platform.iam.domain.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

/** Issues password recovery tokens for internal delivery. */
@Service
public class PasswordResetRequestService {
    private static final Duration TOKEN_LIFETIME = Duration.ofMinutes(30);

    private final UserRepository users;
    private final PasswordResetTokenRepository tokens;
    private final PasswordResetTokenCodec tokenCodec;

    public PasswordResetRequestService(
            UserRepository users,
            PasswordResetTokenRepository tokens,
            PasswordResetTokenCodec tokenCodec
    ) {
        this.users = users;
        this.tokens = tokens;
        this.tokenCodec = tokenCodec;
    }

    /** Returns the sensitive raw token only for internal email delivery. */
    @Transactional
    public Optional<String> handle(RequestPasswordResetCommand command) {
        return users.findByEmail(command.email()).map(user -> {
            String rawToken = tokenCodec.generate();
            var token = new PasswordResetToken(
                    user.getId(), tokenCodec.hash(rawToken), Instant.now().plus(TOKEN_LIFETIME));
            tokens.insert(token);
            return rawToken;
        });
    }
}
