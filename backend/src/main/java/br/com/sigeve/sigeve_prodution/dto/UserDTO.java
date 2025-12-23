package br.com.sigeve.sigeve_prodution.dto;

import br.com.sigeve.sigeve_prodution.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    
    private UUID id;
    private UUID tenantId;
    private String username;
    private String email;
    private String fullName;
    private UserStatus status;
    private Integer failedAttempts;
    private OffsetDateTime lockedUntil;
    private OffsetDateTime lastLoginAt;
    private String language;
    private String timezone;
    private boolean systemAdmin;
    private Set<String> roles; // Apenas os nomes dos roles
}