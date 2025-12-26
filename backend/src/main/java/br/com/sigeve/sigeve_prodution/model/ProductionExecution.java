package br.com.sigeve.sigeve_prodution.model;

import br.com.sigeve.sigeve_prodution.enums.QualityStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade de Execução de Produção
 * Registra a execução de cada etapa da ordem de produção
 */
@Entity
@Table(name = "tab_production_executions", indexes = {
    @Index(name = "idx_production_executions_order", columnList = "production_order_id"),
    @Index(name = "idx_production_executions_company_order", columnList = "company_id, production_order_id"),
    @Index(name = "idx_production_executions_step", columnList = "step_id"),
    @Index(name = "idx_production_executions_employee", columnList = "employee_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductionExecution extends AuditFull {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "production_execution_id", columnDefinition = "uuid")
    private UUID id;

    @NotNull
    @Column(name = "tenant_id", nullable = false, columnDefinition = "uuid")
    private UUID tenantId;

    @NotNull
    @Column(name = "company_id", nullable = false, columnDefinition = "uuid")
    private UUID companyId;

    // Ordem e etapa
    @NotNull
    @Column(name = "production_order_id", nullable = false, columnDefinition = "uuid")
    private UUID productionOrderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "production_order_id", insertable = false, updatable = false)
    private ProductionOrder productionOrder;

    @NotNull
    @Column(name = "step_id", nullable = false, columnDefinition = "uuid")
    private UUID stepId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "step_id", insertable = false, updatable = false)
    private ProductionStep step;

    // Tempo
    @NotNull
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    // Quantidades
    @NotNull
    @DecimalMin(value = "0.0000")
    @Digits(integer = 15, fraction = 4)
    @Column(name = "quantity_done", nullable = false, precision = 15, scale = 4)
    private BigDecimal quantityDone;

    @DecimalMin(value = "0.0000")
    @Digits(integer = 15, fraction = 4)
    @Column(name = "loss_quantity", precision = 15, scale = 4)
    private BigDecimal lossQuantity = BigDecimal.ZERO;

    // Responsável
    @Column(name = "employee_id", columnDefinition = "uuid")
    private UUID employeeId;

    // Máquina/Equipamento
    @Column(name = "machine_id", columnDefinition = "uuid")
    private UUID machineId;

    // Qualidade
    @Enumerated(EnumType.STRING)
    @Column(name = "quality_status")
    private QualityStatus qualityStatus;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    // Observações
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // Métodos auxiliares
    public boolean isCompleted() {
        return endTime != null;
    }

    public boolean isApproved() {
        return QualityStatus.APPROVED.equals(this.qualityStatus);
    }

    public boolean isRejected() {
        return QualityStatus.REJECTED.equals(this.qualityStatus);
    }

    public boolean needsRework() {
        return QualityStatus.REWORK.equals(this.qualityStatus);
    }

    public Long getDurationMinutes() {
        if (startTime == null || endTime == null) {
            return null;
        }
        return java.time.Duration.between(startTime, endTime).toMinutes();
    }

    public BigDecimal getEffectiveQuantity() {
        if (quantityDone == null) return BigDecimal.ZERO;
        if (lossQuantity == null) return quantityDone;
        return quantityDone.subtract(lossQuantity);
    }

    public BigDecimal getLossPercentage() {
        if (quantityDone == null || quantityDone.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        if (lossQuantity == null || lossQuantity.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return lossQuantity.divide(quantityDone, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }
}
