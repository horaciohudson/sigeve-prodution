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
 * Entidade de Composição (BOM - Bill of Materials)
 * Define os materiais e serviços necessários para produzir um produto
 */
@Entity
@Table(name = "tab_compositions", indexes = {
    @Index(name = "idx_compositions_company_product", columnList = "company_id, production_product_id"),
    @Index(name = "idx_compositions_active", columnList = "company_id, is_active")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uq_composition_product_version", columnNames = {"company_id", "production_product_id", "version"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Composition extends AuditFull {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "composition_id", columnDefinition = "uuid")
    private UUID id;

    @NotNull
    @Column(name = "tenant_id", nullable = false, columnDefinition = "uuid")
    private UUID tenantId;

    @NotNull
    @Column(name = "company_id", nullable = false, columnDefinition = "uuid")
    private UUID companyId;

    // Produto
    @NotNull
    @Column(name = "production_product_id", nullable = false, columnDefinition = "uuid")
    private UUID productionProductId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "production_product_id", insertable = false, updatable = false)
    private ProductionProduct productionProduct;

    // Identificação
    @NotBlank
    @Size(max = 200)
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @NotNull
    @Min(1)
    @Column(name = "version", nullable = false)
    private Integer version = 1;

    // Vigência
    @Column(name = "effective_date")
    private LocalDate effectiveDate;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    // Controle
    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // Aprovação
    @Column(name = "approved_by")
    private String approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    // Custo
    @DecimalMin(value = "0.0000")
    @Digits(integer = 15, fraction = 4)
    @Column(name = "total_cost", precision = 15, scale = 4)
    private BigDecimal totalCost = BigDecimal.ZERO;

    // Métodos auxiliares
    public boolean isActive() {
        return Boolean.TRUE.equals(this.isActive);
    }

    public boolean isEffective() {
        return isEffective(LocalDate.now());
    }

    public boolean isEffective(LocalDate date) {
        if (!isActive()) return false;
        if (effectiveDate != null && date.isBefore(effectiveDate)) return false;
        if (expirationDate != null && date.isAfter(expirationDate)) return false;
        return true;
    }

    public boolean isApproved() {
        return approvedBy != null && approvedAt != null;
    }

    public void approve(String approvedBy) {
        this.approvedBy = approvedBy;
        this.approvedAt = LocalDateTime.now();
    }
}
