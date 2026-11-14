package com.dymbia.sumaqagro.platform.iam.infrastructure.hashing.bcrypt;

import com.dymbia.sumaqagro.platform.iam.application.internal.outboundservices.hashing.HashingService;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Exposes the BCrypt implementation through the IAM hashing contract
 * and the Spring Security password encoder contract.
 */
public interface BCryptHashingService extends HashingService, PasswordEncoder {
}