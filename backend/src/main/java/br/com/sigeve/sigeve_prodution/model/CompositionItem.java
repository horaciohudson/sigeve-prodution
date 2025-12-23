package br.com.sigeve.sigeve_prodution.model;

import br.com.sigeve.sigeve_prodution.enums.CompositionItemType;
import br.com.sigeve.sigeve_prodution.enums.UnitType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Entidade de Item da Composição
 * Representa cada material ou serviço que compõe um produto
 */
@Entity
@Table(name = "tab_composition_items", indexes = {
    @Index(name = "idx_composition_items_composition", columnList = "composition_id"),
    @Index(name = "idx_composition_items_type_ref", columnList = "company_id, item_type, reference_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompositionItem extends AuditFull {

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

    // Composição
    @NotNull
    @Column(name = "composition_id", nullable = false, columnDefinition = "uuid")
    private UUID compositionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "composition_id", insertable = false, updatable = false)
    private Composition composition;

    // Tipo e referência
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false)
    private CompositionItemType itemType;

    @NotNull
    @Column(name = "reference_id", nullable = false, columnDefinition = "uuid")
    private UUID referenceId;  // ID da matéria-prima ou serviço

    // Quantidade
    @NotNull
    @Min(1)
    @Column(name = "sequence", nullable = false)
    private Integer sequence = 1;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "unit_type", nullable = false)
    private UnitType unitType;

    @NotNull
    @DecimalMin(value = "0.0001")
    @Digits(integer = 15, fraction = 4)
    @Column(name = "quantity", nullable = false, precision = 15, scale = 4)
    private BigDecimal quantity;

    @DecimalMin(value = "0.00")
    @DecimalMax(value = "100.00")
    @Digits(integer = 3, fraction = 2)
    @Column(name = "loss_percentage", precision = 5, scale = 2)
    private BigDecimal lossPercentage = BigDecimal.ZERO;

    // Custos (snapshot)
    @DecimalMin(value = "0.0000")
    @Digits(integer = 15, fraction = 4)
    @Column(name = "unit_cost", precision = 15, scale = 4)
    private BigDecimal unitCost;

    @DecimalMin(value = "0.0000")
    @Digits(integer = 15, fraction = 4)
    @Column(name = "total_cost", precision = 15, scale = 4)
    private BigDecimal totalCost;

    // Controle
    @NotNull
    @Column(name = "is_optional", nullable = false)
    private Boolean isOptional = false;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // Métodos auxiliares
    public boolean isRawMaterial() {
        return CompositionItemType.RAW_MATERIAL.equals(this.itemType);
    }

    public boolean isService() {
        return CompositionItemType.SERVICE.equals(this.itemType);
    }

    public boolean isOptional() {
        return Boolean.TRUE.equals(this.isOptional);
    }

    public BigDecimal getQuantityWithLoss() {
        if (quantity == null) return BigDecimal.ZERO;
        if (lossPercentage == null || lossPercentage.compareTo(BigDecimal.ZERO) == 0) {
            return quantity;
        }
        BigDecimal lossMultiplier = BigDecimal.ONE.add(lossPercentage.divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_HALF_UP));
        return quantity.multiply(lossMultiplier);
    }

    public void calculateTotalCost() {
        if (unitCost != null && quantity != null) {
            BigDecimal quantityWithLoss = getQuantityWithLoss();
            this.totalCost = unitCost.multiply(quantityWithLoss);
        }
    }

    @PrePersist
    @PreUpdate
    protected void onSave() {
        calculateTotalCost();
    }
}
