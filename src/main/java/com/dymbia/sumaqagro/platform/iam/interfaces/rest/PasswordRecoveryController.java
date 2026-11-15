package com.dymbia.sumaqagro.platform.iam.interfaces.rest;

import com.dymbia.sumaqagro.platform.iam.application.commandservices.PasswordRecoveryCommandService;
import com.dymbia.sumaqagro.platform.iam.domain.model.commands.RequestPasswordResetCommand;
import com.dymbia.sumaqagro.platform.iam.domain.model.commands.ResetPasswordCommand;
import com.dymbia.sumaqagro.platform.iam.domain.model.valueobjects.EmailAddress;
import com.dymbia.sumaqagro.platform.iam.interfaces.rest.resources.RequestPasswordResetResource;
import com.dymbia.sumaqagro.platform.iam.interfaces.rest.resources.ResetPasswordResource;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;
import com.dymbia.sumaqagro.platform.shared.interfaces.rest.transform.ProblemDetailFromApplicationErrorAssembler;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Exposes password recovery for accounts that cannot sign in. */
@RestController
@RequestMapping("/api/v1/authentication/password-recovery")
public class PasswordRecoveryController {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(PasswordRecoveryController.class);

    private final PasswordRecoveryCommandService recovery;

    public PasswordRecoveryController(PasswordRecoveryCommandService recovery) {
        this.recovery = recovery;
    }

    /** Accepts a recovery request without disclosing account existence. */
    @PostMapping("/request")
    public ResponseEntity<Void> requestReset(
            @Valid @RequestBody RequestPasswordResetResource resource
    ) {
        var command = new RequestPasswordResetCommand(new EmailAddress(resource.email()));

        try {
            recovery.handle(command);
        } catch (RuntimeException exception) {
            LOGGER.warn("Password recovery request could not be processed");
        }

        return ResponseEntity.accepted().build();
    }

    /** Updates the password when the supplied token is still usable. */
    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(
            @Valid @RequestBody ResetPasswordResource resource
    ) {
        var result = recovery.handle(
                new ResetPasswordCommand(resource.token(), resource.newPassword())
        );

        return switch (result) {
            case Result.Success(var ignored) -> ResponseEntity.noContent().build();
            case Result.Failure(var error) ->
                    ProblemDetailFromApplicationErrorAssembler.toResponseFromApplicationError(error);
        };
    }
}