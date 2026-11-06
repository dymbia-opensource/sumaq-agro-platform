package com.dymbia.sumaqagro.platform.iam.infrastructure.hashing.bcrypt.services;

import com.dymbia.sumaqagro.platform.iam.infrastructure.hashing.bcrypt.BCryptHashingService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Implements password hashing and verification using BCrypt.
 */
@Service
public class HashingServiceImpl implements BCryptHashingService {

    private final BCryptPasswordEncoder passwordEncoder;

    HashingServiceImpl() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * Generates a BCrypt hash for a raw password.
     *
     * @param rawPassword the password to hash
     * @return the BCrypt hash
     */
    @Override
    public String encode(CharSequence rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * Verifies a raw password against a stored BCrypt hash.
     *
     * @param rawPassword the password supplied by the user
     * @param encodedPassword the stored BCrypt hash
     * @return {@code true} if the password matches
     */
    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}