package br.com.sigeve.sigeve_prodution.model;

import br.com.sigeve.sigeve_prodution.enums.UnitType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Entidade de Matéria-Prima
 * Representa os materiais utilizados na produção
 */
@Entity
@Table(name = "tab_raw_materials", indexes = {
    @Index(name = "idx_raw_materials_company_code", columnList = "company_id, code"),
    @Index(name = "idx_raw_materials_supplier", columnList = "supplier_id"),
    @Index(name = "idx_raw_materials_category", columnList = "category_id")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uq_raw_materials_company_code", columnNames = {"company_id", "code"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RawMaterial extends AuditFull {

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

    @NotBlank
    @Size(max = 200)
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "unit_type", nullable = false)
    private UnitType unitType;

    // Fornecedor
    @Column(name = "supplier_id", columnDefinition = "uuid")
    private UUID supplierId;

    // Custos
    @DecimalMin(value = "0.0000")
    @Digits(integer = 15, fraction = 4)
    @Column(name = "average_cost", precision = 15, scale = 4)
    private BigDecimal averageCost = BigDecimal.ZERO;

    @DecimalMin(value = "0.0000")
    @Digits(integer = 15, fraction = 4)
    @Column(name = "last_purchase_price", precision = 15, scale = 4)
    private BigDecimal lastPurchasePrice;

    @Column(name = "last_purchase_date")
    private LocalDate lastPurchaseDate;

    // Controle de estoque
    @NotNull
    @Column(name = "stock_control", nullable = false)
    private Boolean stockControl = true;

    @DecimalMin(value = "0.0000")
    @Digits(integer = 15, fraction = 4)
    @Column(name = "min_stock", precision = 15, scale = 4)
    private BigDecimal minStock = BigDecimal.ZERO;

    @DecimalMin(value = "0.0000")
    @Digits(integer = 15, fraction = 4)
    @Column(name = "max_stock", precision = 15, scale = 4)
    private BigDecimal maxStock;

    @DecimalMin(value = "0.0000")
    @Digits(integer = 15, fraction = 4)
    @Column(name = "reorder_point", precision = 15, scale = 4)
    private BigDecimal reorderPoint;

    @Min(0)
    @Column(name = "lead_time_days")
    private Integer leadTimeDays = 0;

    // Categoria
    @Column(name = "category_id", columnDefinition = "uuid")
    private UUID categoryId;

    // Controle
    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Version
    @Column(name = "version")
    private Integer version = 1;

    // Métodos auxiliares
    public boolean isStockControlled() {
        return Boolean.TRUE.equals(this.stockControl);
    }

    public boolean isActive() {
        return Boolean.TRUE.equals(this.isActive);
    }

    public boolean needsReorder(BigDecimal currentStock) {
        if (reorderPoint == null || currentStock == null) {
            return false;
        }
        return currentStock.compareTo(reorderPoint) <= 0;
    }

    public boolean isBelowMinStock(BigDecimal currentStock) {
        if (minStock == null || currentStock == null) {
            return false;
        }
        return currentStock.compareTo(minStock) < 0;
    }

    public boolean isAboveMaxStock(BigDecimal currentStock) {
        if (maxStock == null || currentStock == null) {
            return false;
        }
        return currentStock.compareTo(maxStock) > 0;
    }
}
