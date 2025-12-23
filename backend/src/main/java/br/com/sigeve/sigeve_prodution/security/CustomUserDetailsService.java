package br.com.sigeve.sigeve_prodution.security;

import br.com.sigeve.sigeve_prodution.model.User;
import br.com.sigeve.sigeve_prodution.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPasswordHash())
                .authorities(getAuthorities(user))
                .accountExpired(false)
                .accountLocked(user.getLockedUntil() != null && user.getLockedUntil().isAfter(java.time.OffsetDateTime.now()))
                .credentialsExpired(false)
                .disabled(user.getStatus() != br.com.sigeve.sigeve_prodution.enums.UserStatus.ACTIVE)
                .build();
    }

    /**
     * Load user by username and tenant ID for multi-tenant authentication
     */
    public User loadByUsernameAndTenant(String username, UUID tenantId) {
        return userRepository.findByUsernameAndTenantId(username, tenantId)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username + " para o tenant: " + tenantId));
    }

    /**
     * Assert that the user is active
     */
    public void assertActive(User user) {
        if (user.getStatus() != br.com.sigeve.sigeve_prodution.enums.UserStatus.ACTIVE) {
            throw new RuntimeException("Usuário inativo: " + user.getUsername());
        }
    }

    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            return java.util.Collections.emptyList();
        }
        
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRole().name()))
                .collect(Collectors.toList());
    }
}
