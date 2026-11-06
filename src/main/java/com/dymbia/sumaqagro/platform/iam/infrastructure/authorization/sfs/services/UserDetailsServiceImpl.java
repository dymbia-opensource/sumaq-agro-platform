package com.dymbia.sumaqagro.platform.iam.infrastructure.authorization.sfs.services;

import com.dymbia.sumaqagro.platform.iam.domain.model.entities.Role;
import com.dymbia.sumaqagro.platform.iam.domain.model.valueobjects.EmailAddress;
import com.dymbia.sumaqagro.platform.iam.domain.repositories.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Loads the current account and its role authorities for Spring Security.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads an account using its email as the security principal name.
     *
     * @param username the account email
     * @return the current account and role authorities
     * @throws UsernameNotFoundException if the account does not exist
     */
    @Override
    public UserDetails loadUserByUsername(String username) {
        var user = userRepository.findByEmail(new EmailAddress(username))
                .orElseThrow(() -> new UsernameNotFoundException("Account not found"));

        var authorities = user.getRoles().stream()
                .map(Role::getStringName)
                .toArray(String[]::new);

        return User.withUsername(user.getEmail().value())
                .password(user.getPasswordHash())
                .authorities(authorities)
                .build();
    }
}
