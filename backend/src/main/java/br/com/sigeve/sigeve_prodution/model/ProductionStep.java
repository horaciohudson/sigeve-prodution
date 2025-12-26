package br.com.sigeve.sigeve_prodution.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Entidade de Etapa de Produção
 * Define as etapas do processo produtivo (Corte, Costura, Acabamento, etc.)
 */
@Entity
@Table(name = "tab_production_steps", indexes = {
    @Index(name = "idx_production_steps_company", columnList = "company_id"),
    @Index(name = "idx_production_steps_sequence", columnList = "company_id, sequence")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductionStep extends AuditFull {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "production_step_id", columnDefinition = "uuid")
    private UUID id;

    @NotNull
    @Column(name = "tenant_id", nullable = false, columnDefinition = "uuid")
    private UUID tenantId;

    @NotNull
    @Column(name = "company_id", nullable = false, columnDefinition = "uuid")
    private UUID companyId;

    // Identificação
    @NotBlank
    @Size(max = 200)
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull
    @Min(1)
    @Column(name = "sequence", nullable = false)
    private Integer sequence = 1;

    // Tempo estimado (em minutos)
    @Min(0)
    @Column(name = "estimated_time")
    private Integer estimatedTime = 0;

    // Centro de custo
    @Column(name = "cost_center_id", columnDefinition = "uuid")
    private UUID costCenterId;

    // Controle
    @NotNull
    @Column(name = "is_outsourced", nullable = false)
    private Boolean isOutsourced = false;

    @NotNull
    @Column(name = "requires_approval", nullable = false)
    private Boolean requiresApproval = false;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Métodos auxiliares
    public boolean isOutsourced() {
        return Boolean.TRUE.equals(this.isOutsourced);
    }

    public boolean requiresApproval() {
        return Boolean.TRUE.equals(this.requiresApproval);
    }

    public boolean isActive() {
        return Boolean.TRUE.equals(this.isActive);
    }
}
