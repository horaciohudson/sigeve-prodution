package br.com.sigeve.sigeve_prodution.model;

import br.com.sigeve.sigeve_prodution.enums.PriorityLevel;
import br.com.sigeve.sigeve_prodution.enums.ProductionOrderStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade de Ordem de Produção
 * Centro do módulo de produção
 */
@Entity
@Table(name = "tab_production_orders", indexes = {
    @Index(name = "idx_production_orders_company_status", columnList = "company_id, status"),
    @Index(name = "idx_production_orders_tenant_company", columnList = "tenant_id, company_id"),
    @Index(name = "idx_production_orders_product", columnList = "product_id"),
    @Index(name = "idx_production_orders_dates", columnList = "start_date, end_date"),
    @Index(name = "idx_production_orders_priority", columnList = "company_id, priority, status")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uq_production_orders_company_code", columnNames = {"company_id", "code"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductionOrder extends AuditFull {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @NotNull
    @Column(name = "tenant_id", nullable = false, columnDefinition = "uuid")
    private UUID tenantId;

    @NotNull
    @Column(name = "company_id", nullable = false, columnDefinition = "uuid")
    private UUID companyId;

    // Identificação
    @NotBlank
    @Size(max = 50)
    @Column(name = "code", nullable = false, length = 50)
    private String code;

    // Produto
    @NotNull
    @Column(name = "product_id", nullable = false, columnDefinition = "uuid")
    private UUID productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private ProductionProduct product;

    // Quantidades
    @NotNull
    @DecimalMin(value = "0.0001")
    @Digits(integer = 15, fraction = 4)
    @Column(name = "quantity_planned", nullable = false, precision = 15, scale = 4)
    private BigDecimal quantityPlanned;

    @DecimalMin(value = "0.0000")
    @Digits(integer = 15, fraction = 4)
    @Column(name = "quantity_produced", precision = 15, scale = 4)
    private BigDecimal quantityProduced = BigDecimal.ZERO;

    // Status e prioridade
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProductionOrderStatus status = ProductionOrderStatus.PLANNED;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private PriorityLevel priority = PriorityLevel.MEDIUM;

    // Datas
    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "deadline")
    private LocalDate deadline;

    // Relacionamentos
    @Column(name = "customer_id", columnDefinition = "uuid")
    private UUID customerId;

    @Column(name = "order_id", columnDefinition = "uuid")
    private UUID orderId;

    // Custos
    @DecimalMin(value = "0.00")
    @Digits(integer = 15, fraction = 2)
    @Column(name = "cost_total", precision = 15, scale = 2)
    private BigDecimal costTotal = BigDecimal.ZERO;

    // Observações
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Version
    @Column(name = "version")
    private Integer version = 1;

    // Aprovação
    @Column(name = "approved_by")
    private String approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    // Finalização
    @Column(name = "finished_by")
    private String finishedBy;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    // Cancelamento
    @Column(name = "canceled_reason", columnDefinition = "TEXT")
    private String canceledReason;

    // Métodos auxiliares
    public boolean isPlanned() {
        return ProductionOrderStatus.PLANNED.equals(this.status);
    }

    public boolean isInProgress() {
        return ProductionOrderStatus.IN_PROGRESS.equals(this.status);
    }

    public boolean isFinished() {
        return ProductionOrderStatus.FINISHED.equals(this.status);
    }

    public boolean isCanceled() {
        return ProductionOrderStatus.CANCELED.equals(this.status);
    }

    public boolean canStart() {
        return isPlanned();
    }

    public boolean canFinish() {
        return isInProgress();
    }

    public boolean canCancel() {
        return !isFinished() && !isCanceled();
    }

    public void start() {
        if (canStart()) {
            this.status = ProductionOrderStatus.IN_PROGRESS;
            if (this.startDate == null) {
                this.startDate = LocalDate.now();
            }
        }
    }

    public void finish(String finishedBy) {
        if (canFinish()) {
            this.status = ProductionOrderStatus.FINISHED;
            this.finishedBy = finishedBy;
            this.finishedAt = LocalDateTime.now();
            if (this.endDate == null) {
                this.endDate = LocalDate.now();
            }
        }
    }

    public void cancel(String reason) {
        if (canCancel()) {
            this.status = ProductionOrderStatus.CANCELED;
            this.canceledReason = reason;
        }
    }

    public BigDecimal getProductionPercentage() {
        if (quantityPlanned == null || quantityPlanned.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        if (quantityProduced == null) {
            return BigDecimal.ZERO;
        }
        return quantityProduced.divide(quantityPlanned, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    public boolean isOverdue() {
        if (deadline == null || isFinished() || isCanceled()) {
            return false;
        }
        return LocalDate.now().isAfter(deadline);
    }
}
