package br.com.sigeve.sigeve_prodution.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tab_users", indexes = {
    @Index(name = "idx_user_tenant", columnList = "tenant_id"),
    @Index(name = "idx_user_username", columnList = "username"),
    @Index(name = "idx_user_email", columnList = "email"),
    @Index(name = "idx_user_status", columnList = "status")
})
public class User extends br.com.sigeve.sigeve_prodution.model.AuditFull {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id", columnDefinition = "uuid")
    private UUID id;

    @Column(name="tenant_id", nullable=false, columnDefinition = "uuid")
    private UUID tenantId;

    @Column(nullable=false, length=50)
    private String username;

    @Column(name="email", length=120)
    private String email;

    @Column(name="password_hash", nullable=false, length=255)
    private String passwordHash;

    @Column(name="full_name", nullable=false, length=100)
    private String fullName;

    @Column(nullable=false)
    @Enumerated(EnumType.STRING)
    private br.com.sigeve.sigeve_prodution.enums.UserStatus status = br.com.sigeve.sigeve_prodution.enums.UserStatus.ACTIVE;

    @Column(name="failed_attempts", nullable=false)
    private Integer failedAttempts;

    @Column(name="locked_until")
    private OffsetDateTime lockedUntil;

    @Column(name="last_login_at")
    private OffsetDateTime lastLoginAt;

    @Column(name="language", length=2)
    private String language;

    @Column(name="timezone", length=64)
    private String timezone;

    @Column(name="is_system_admin", nullable=false)
    private boolean systemAdmin;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "tab_user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

}