package br.com.sigeve.sigeve_prodution.service;

import br.com.sigeve.sigeve_prodution.model.User;
import br.com.sigeve.sigeve_prodution.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class AccountLockoutService {

    @Value("${app.security.lockout.enabled:false}")
    private boolean lockoutEnabled;

    @Value("${app.security.lockout.maxAttempts:10}")
    private int maxAttempts;

    @Value("${app.security.lockout.lockoutDurationMinutes:15}")
    private int lockoutDurationMinutes;

    private final UserRepository userRepository;

    public AccountLockoutService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void recordFailedAttempt(UUID userId) {
        if (!lockoutEnabled) {
            return;
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return;
        }

        int currentAttempts = user.getFailedAttempts() != null ? user.getFailedAttempts() : 0;
        user.setFailedAttempts(currentAttempts + 1);

        if (user.getFailedAttempts() >= maxAttempts) {
            user.setLockedUntil(OffsetDateTime.now().plusMinutes(lockoutDurationMinutes));
        }

        userRepository.save(user);
    }

    public void resetFailedAttempts(UUID userId) {
        if (!lockoutEnabled) {
            return;
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return;
        }

        user.setFailedAttempts(0);
        user.setLockedUntil(null);
        userRepository.save(user);
    }

    public boolean isAccountLocked(User user) {
        if (!lockoutEnabled) {
            return false;
        }

        if (user.getLockedUntil() == null) {
            return false;
        }

        if (user.getLockedUntil().isAfter(OffsetDateTime.now())) {
            return true;
        }

        // Lockout period has expired, reset the lock
        user.setLockedUntil(null);
        user.setFailedAttempts(0);
        userRepository.save(user);
        return false;
    }
}