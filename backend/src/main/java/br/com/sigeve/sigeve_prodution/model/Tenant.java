package br.com.sigeve.sigeve_prodution.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import br.com.sigeve.sigeve_prodution.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.UUID;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tab_tenants", indexes = {
    @Index(name = "idx_tenant_code", columnList = "code"),
    @Index(name = "idx_tenant_status", columnList = "status")
})
public class Tenant extends AuditDouble {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "tenant_id", columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false, length = 50, unique = true)
    private String code;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.ACTIVE;

    // Campos de auditoria adicionais (não incluídos no AuditDouble)
    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    // Métodos de ciclo de vida para preencher campos de auditoria
    @PrePersist
    protected void onCreate() {
        super.onCreate(); // Chama o método do AuditDouble para createdAt e updatedAt
        
        // Preenche createdBy automaticamente se não foi definido
        if (this.createdBy == null) {
            this.createdBy = getCurrentUsername();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        super.onUpdate(); // Chama o método do AuditDouble para updatedAt
        
        // Preenche updatedBy automaticamente
        this.updatedBy = getCurrentUsername();
    }

    /**
     * Obtém o nome do usuário atual do contexto de segurança
     */
    private String getCurrentUsername() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() 
                && !"anonymousUser".equals(authentication.getName())) {
                return authentication.getName();
            }
        } catch (Exception e) {
            // Log do erro se necessário, mas não interrompe o processo
        }
        return null;
    }

}
