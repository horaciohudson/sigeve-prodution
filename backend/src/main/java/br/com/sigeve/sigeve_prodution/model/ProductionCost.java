package br.com.sigeve.sigeve_prodution.model;

import br.com.sigeve.sigeve_prodution.enums.ProductionCostType;
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
 * Entidade de Custo de Produção
 * Registra os custos reais apontados na produção
 */
@Entity
@Table(name = "tab_production_costs", indexes = {
    @Index(name = "idx_production_costs_order", columnList = "production_order_id"),
    @Index(name = "idx_production_costs_company_type", columnList = "company_id, cost_type"),
    @Index(name = "idx_production_costs_date", columnList = "cost_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductionCost extends AuditFull {

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

    // Ordem de produção
    @NotNull
    @Column(name = "production_order_id", nullable = false, columnDefinition = "uuid")
    private UUID productionOrderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "production_order_id", insertable = false, updatable = false)
    private ProductionOrder productionOrder;

    // Tipo de custo
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "cost_type", nullable = false)
    private ProductionCostType costType;

    @Column(name = "reference_id", columnDefinition = "uuid")
    private UUID referenceId;  // ID do material, serviço, funcionário, etc.

    // Data
    @NotNull
    @Column(name = "cost_date", nullable = false)
    private LocalDate costDate;

    // Quantidade e valores
    @DecimalMin(value = "0.0000")
    @Digits(integer = 15, fraction = 4)
    @Column(name = "quantity", precision = 15, scale = 4)
    private BigDecimal quantity;

    @DecimalMin(value = "0.0000")
    @Digits(integer = 15, fraction = 4)
    @Column(name = "unit_cost", precision = 15, scale = 4)
    private BigDecimal unitCost;

    @NotNull
    @DecimalMin(value = "0.00")
    @Digits(integer = 15, fraction = 2)
    @Column(name = "total_cost", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalCost;

    // Observações
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // Aprovação
    @Column(name = "approved_by")
    private String approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    // Métodos auxiliares
    public boolean isMaterial() {
        return ProductionCostType.MATERIAL.equals(this.costType);
    }

    public boolean isService() {
        return ProductionCostType.SERVICE.equals(this.costType);
    }

    public boolean isLabor() {
        return ProductionCostType.LABOR.equals(this.costType);
    }

    public boolean isIndirect() {
        return ProductionCostType.INDIRECT.equals(this.costType);
    }

    public boolean isApproved() {
        return approvedBy != null && approvedAt != null;
    }

    public void approve(String approvedBy) {
        this.approvedBy = approvedBy;
        this.approvedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        super.onCreate();
        if (this.costDate == null) {
            this.costDate = LocalDate.now();
        }
        // Calcula total_cost se não foi informado
        if (this.totalCost == null && this.unitCost != null && this.quantity != null) {
            this.totalCost = this.unitCost.multiply(this.quantity);
        }
    }
}
