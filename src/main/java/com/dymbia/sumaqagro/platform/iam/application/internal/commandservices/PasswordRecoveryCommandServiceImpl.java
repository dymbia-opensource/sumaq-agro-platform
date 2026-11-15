package com.dymbia.sumaqagro.platform.iam.application.internal.commandservices;

import com.dymbia.sumaqagro.platform.iam.application.commandservices.PasswordRecoveryCommandService;
import com.dymbia.sumaqagro.platform.iam.application.internal.outboundservices.email.PasswordResetEmailSender;
import com.dymbia.sumaqagro.platform.iam.domain.model.commands.RequestPasswordResetCommand;
import com.dymbia.sumaqagro.platform.iam.domain.model.commands.ResetPasswordCommand;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/** Coordinates token delivery and password reset for IAM. */
@Service
public class PasswordRecoveryCommandServiceImpl implements PasswordRecoveryCommandService {
    private final PasswordResetRequestService requests;
    private final PasswordResetService resets;
    private final ObjectProvider<PasswordResetEmailSender> senders;
    private final URI frontendUrl;

    public PasswordRecoveryCommandServiceImpl(
            PasswordResetRequestService requests,
            PasswordResetService resets,
            ObjectProvider<PasswordResetEmailSender> senders,
            @Value("${iam.password-reset.frontend-url}") URI frontendUrl
    ) {
        this.requests = requests;
        this.resets = resets;
        this.senders = senders;
        this.frontendUrl = frontendUrl;
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void handle(RequestPasswordResetCommand command) {
        var sender = senders.getIfAvailable();
        if (sender == null) {
            return;
        }

        requests.handle(command).ifPresent(token -> {
            var link = UriComponentsBuilder.fromUri(frontendUrl)
                    .queryParam("token", token)
                    .build(true)
                    .toUri();
            sender.send(command.email(), link);
        });
    }

    @Override
    public Result<Long, ApplicationError> handle(ResetPasswordCommand command) {
        return resets.handle(command);
    }
}
