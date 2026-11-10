package com.dymbia.sumaqagro.platform.iam.application.internal.commandservices;

import com.dymbia.sumaqagro.platform.iam.application.commandservices.UserCommandService;
import com.dymbia.sumaqagro.platform.iam.application.internal.outboundservices.hashing.HashingService;
import com.dymbia.sumaqagro.platform.iam.application.internal.outboundservices.tokens.TokenService;
import com.dymbia.sumaqagro.platform.iam.domain.model.aggregates.User;
import com.dymbia.sumaqagro.platform.iam.domain.model.commands.SignInCommand;
import com.dymbia.sumaqagro.platform.iam.domain.model.commands.SignUpCommand;
import com.dymbia.sumaqagro.platform.iam.domain.repositories.RoleRepository;
import com.dymbia.sumaqagro.platform.iam.domain.repositories.UserRepository;
import com.dymbia.sumaqagro.platform.profiles.interfaces.acl.ProfilesContextFacade;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

/**
 * Coordinates user registration and sign-in through the IAM repository,
 * password hashing and token service contracts.
 */
@Service
public class UserCommandServiceImpl implements UserCommandService {

    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_BCRYPT_PASSWORD_BYTES = 72;
    private static final ApplicationError INVALID_CREDENTIALS = new ApplicationError("INVALID_CREDENTIALS", "Invalid email or password");
    private final ProfilesContextFacade profilesContextFacade;

    private final UserRepository userRepository;
    private final HashingService hashingService;
    private final TokenService tokenService;
    private final RoleRepository roleRepository;

    public UserCommandServiceImpl(
            UserRepository userRepository,
            HashingService hashingService,
            TokenService tokenService,
            RoleRepository roleRepository,
            ProfilesContextFacade profilesContextFacade
    ) {
        this.userRepository = userRepository;
        this.hashingService = hashingService;
        this.tokenService = tokenService;
        this.roleRepository = roleRepository;
        this.profilesContextFacade = profilesContextFacade;
    }

    /**
     * Looks up the account by email, verifies the password hash and issues a JWT.
     *
     * @param command the sign-in credentials
     * @return the authenticated user and token, or an invalid credentials error
     */
    @Override
    public Result<ImmutablePair<User, String>, ApplicationError> handle(SignInCommand command) {
        var password = command.password();

        if (command.email() == null
                || password == null
                || password.isBlank()
                || password.getBytes(StandardCharsets.UTF_8).length > MAX_BCRYPT_PASSWORD_BYTES) {
            return Result.failure(INVALID_CREDENTIALS);
        }

        var user = userRepository.findByEmail(command.email());
        if (user.isEmpty()) {
            return Result.failure(INVALID_CREDENTIALS);
        }

        var authenticatedUser = user.get();
        if (!hashingService.matches(password, authenticatedUser.getPasswordHash())) {
            return Result.failure(INVALID_CREDENTIALS);
        }

        var token = tokenService.generateToken(authenticatedUser.getEmail().value());
        return Result.success(ImmutablePair.of(authenticatedUser, token));
    }

    /**
     * Resolves the requested role, hashes the password and persists the account and its initial profile.
     *
     * @param command the registration data
     * @return the persisted user or an application error
     */
    @Override
    @Transactional
    public Result<User, ApplicationError> handle(SignUpCommand command) {
        if (command.email() == null) {
            return Result.failure(
                    ApplicationError.validationError("email", "Email is required")
            );
        }

        if (command.requestedRole() == null) {
            return Result.failure(
                    ApplicationError.validationError("role", "A role is required")
            );
        }

        var password = command.password();
        if (password == null || password.isBlank()
                || password.length() < MIN_PASSWORD_LENGTH
                || password.getBytes(StandardCharsets.UTF_8).length > MAX_BCRYPT_PASSWORD_BYTES) {
            return Result.failure(ApplicationError.validationError(
                    "password",
                    "Use at least 8 characters and at most 72 UTF-8 bytes"
            ));
        }

        if (userRepository.existsByEmail(command.email())) {
            return Result.failure(
                    ApplicationError.conflict("User", "Email already exists")
            );
        }

        var role = roleRepository.findByName(command.requestedRole());
        if (role.isEmpty()) {
            return Result.failure(
                    ApplicationError.notFound("Role", command.requestedRole().name())
            );
        }

        var passwordHash = hashingService.encode(password);
        var user = new User(command.email(), passwordHash, role.get());

        var savedUser = userRepository.save(user);
        profilesContextFacade.createProfile(
                savedUser.getId(),
                command.firstName(),
                command.lastName()
        );

        return Result.success(savedUser);
    }
}