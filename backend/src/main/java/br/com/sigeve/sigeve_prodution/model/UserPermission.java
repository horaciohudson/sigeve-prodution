package br.com.sigeve.sigeve_prodution.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "tab_user_permissions", indexes = {
        @Index(name = "idx_user_permission_user", columnList = "user_id"),
        @Index(name = "idx_user_permission_permission", columnList = "permission_id"),
        @Index(name = "idx_user_permission_tenant", columnList = "tenant_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPermission extends AuditFull {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_permission_id")
    private Long id;

    @Column(name = "user_id", nullable = false, columnDefinition = "uuid")
    private UUID userId;

    @Column(name = "permission_id", nullable = false)
    private Long permissionId;

    @Column(name = "tenant_id", nullable = false, columnDefinition = "uuid")
    private UUID tenantId;

    @Column(name = "granted", nullable = false)
    private Boolean granted = true;

    @Column(name = "notes", length = 255)
    private String notes;

    // 🔗 Relacionamentos
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id", insertable = false, updatable = false)
    private Permission permission;

    // ✅ Construtor de conveniência
    public UserPermission(UUID userId, Long permissionId, UUID tenantId, Boolean granted) {
        this.userId = userId;
        this.permissionId = permissionId;
        this.tenantId = tenantId;
        this.granted = granted;
    }

    // ✅ Outro construtor com notes opcional
    public UserPermission(UUID userId, Long permissionId, UUID tenantId, Boolean granted, String notes) {
        this.userId = userId;
        this.permissionId = permissionId;
        this.tenantId = tenantId;
        this.granted = granted;
        this.notes = notes;
    }
}