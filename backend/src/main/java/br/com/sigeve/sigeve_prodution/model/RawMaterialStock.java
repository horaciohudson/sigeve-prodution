package br.com.sigeve.sigeve_prodution.model;

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
 * Entidade de Estoque de Matéria-Prima
 * Controla a quantidade disponível de cada material
 */
@Entity
@Table(name = "tab_raw_material_stocks", indexes = {
    @Index(name = "idx_raw_material_stocks_material", columnList = "raw_material_id")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uq_stock_material_warehouse", columnNames = {"company_id", "raw_material_id", "warehouse_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RawMaterialStock {

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

    // Material
    @NotNull
    @Column(name = "raw_material_id", nullable = false, columnDefinition = "uuid")
    private UUID rawMaterialId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raw_material_id", insertable = false, updatable = false)
    private RawMaterial rawMaterial;

    // Localização (opcional)
    @Column(name = "warehouse_id", columnDefinition = "uuid")
    private UUID warehouseId;

    // Quantidades
    @NotNull
    @DecimalMin(value = "0.0000")
    @Digits(integer = 15, fraction = 4)
    @Column(name = "quantity", nullable = false, precision = 15, scale = 4)
    private BigDecimal quantity = BigDecimal.ZERO;

    @NotNull
    @DecimalMin(value = "0.0000")
    @Digits(integer = 15, fraction = 4)
    @Column(name = "reserved_quantity", nullable = false, precision = 15, scale = 4)
    private BigDecimal reservedQuantity = BigDecimal.ZERO;

    // Quantidade disponível (calculada)
    @Column(name = "available_quantity", precision = 15, scale = 4, insertable = false, updatable = false)
    private BigDecimal availableQuantity;

    // Controle
    @Column(name = "last_movement_date")
    private LocalDateTime lastMovementDate;

    // Auditoria simples
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Métodos auxiliares
    public BigDecimal getAvailableQuantity() {
        if (quantity == null) return BigDecimal.ZERO;
        if (reservedQuantity == null) return quantity;
        return quantity.subtract(reservedQuantity);
    }

    public boolean hasAvailableStock(BigDecimal requiredQuantity) {
        if (requiredQuantity == null || requiredQuantity.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        return getAvailableQuantity().compareTo(requiredQuantity) >= 0;
    }

    public void addQuantity(BigDecimal amount) {
        if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0) {
            this.quantity = this.quantity.add(amount);
        }
    }

    public void removeQuantity(BigDecimal amount) {
        if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0) {
            this.quantity = this.quantity.subtract(amount);
        }
    }

    public void reserveQuantity(BigDecimal amount) {
        if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0) {
            this.reservedQuantity = this.reservedQuantity.add(amount);
        }
    }

    public void releaseReservation(BigDecimal amount) {
        if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0) {
            this.reservedQuantity = this.reservedQuantity.subtract(amount);
            if (this.reservedQuantity.compareTo(BigDecimal.ZERO) < 0) {
                this.reservedQuantity = BigDecimal.ZERO;
            }
        }
    }
}
