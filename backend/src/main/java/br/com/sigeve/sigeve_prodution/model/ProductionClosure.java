package br.com.sigeve.sigeve_prodution.model;

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
 * Entidade de Fechamento de Produção
 * Consolida e totaliza os custos da ordem de produção
 */
@Entity
@Table(name = "tab_production_closures", indexes = {
    @Index(name = "idx_production_closures_company", columnList = "company_id"),
    @Index(name = "idx_production_closures_exported", columnList = "exported_to_financial, company_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductionClosure {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "production_closure_id", columnDefinition = "uuid")
    private UUID id;

    @NotNull
    @Column(name = "tenant_id", nullable = false, columnDefinition = "uuid")
    private UUID tenantId;

    @NotNull
    @Column(name = "company_id", nullable = false, columnDefinition = "uuid")
    private UUID companyId;

    // Ordem de produção (único)
    @NotNull
    @Column(name = "production_order_id", nullable = false, unique = true, columnDefinition = "uuid")
    private UUID productionOrderId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "production_order_id", insertable = false, updatable = false)
    private ProductionOrder productionOrder;

    // Totais por tipo
    @DecimalMin(value = "0.00")
    @Digits(integer = 15, fraction = 2)
    @Column(name = "total_material", precision = 15, scale = 2)
    private BigDecimal totalMaterial = BigDecimal.ZERO;

    @DecimalMin(value = "0.00")
    @Digits(integer = 15, fraction = 2)
    @Column(name = "total_service", precision = 15, scale = 2)
    private BigDecimal totalService = BigDecimal.ZERO;

    @DecimalMin(value = "0.00")
    @Digits(integer = 15, fraction = 2)
    @Column(name = "total_labor", precision = 15, scale = 2)
    private BigDecimal totalLabor = BigDecimal.ZERO;

    @DecimalMin(value = "0.00")
    @Digits(integer = 15, fraction = 2)
    @Column(name = "total_indirect", precision = 15, scale = 2)
    private BigDecimal totalIndirect = BigDecimal.ZERO;

    @NotNull
    @DecimalMin(value = "0.00")
    @Digits(integer = 15, fraction = 2)
    @Column(name = "total_cost", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalCost;

    // Datas
    @NotNull
    @Column(name = "closure_date", nullable = false)
    private LocalDate closureDate;

    @NotNull
    @Column(name = "closed_at", nullable = false)
    private LocalDateTime closedAt;

    @NotNull
    @Column(name = "closed_by", nullable = false)
    private String closedBy;

    // Integração financeira
    @NotNull
    @Column(name = "exported_to_financial", nullable = false)
    private Boolean exportedToFinancial = false;

    @Column(name = "financial_export_date")
    private LocalDateTime financialExportDate;

    @Column(name = "financial_document_id", columnDefinition = "uuid")
    private UUID financialDocumentId;

    // Observações
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // Auditoria simples
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.closedAt == null) {
            this.closedAt = LocalDateTime.now();
        }
        if (this.closureDate == null) {
            this.closureDate = LocalDate.now();
        }
        calculateTotalCost();
    }

    // Métodos auxiliares
    public void calculateTotalCost() {
        BigDecimal material = totalMaterial != null ? totalMaterial : BigDecimal.ZERO;
        BigDecimal service = totalService != null ? totalService : BigDecimal.ZERO;
        BigDecimal labor = totalLabor != null ? totalLabor : BigDecimal.ZERO;
        BigDecimal indirect = totalIndirect != null ? totalIndirect : BigDecimal.ZERO;
        
        this.totalCost = material.add(service).add(labor).add(indirect);
    }

    public boolean isExportedToFinancial() {
        return Boolean.TRUE.equals(this.exportedToFinancial);
    }

    public void exportToFinancial(UUID financialDocumentId) {
        this.exportedToFinancial = true;
        this.financialExportDate = LocalDateTime.now();
        this.financialDocumentId = financialDocumentId;
    }
}
