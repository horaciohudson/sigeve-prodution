package br.com.sigeve.sigeve_prodution.model;

import br.com.sigeve.sigeve_prodution.enums.MovementOrigin;
import br.com.sigeve.sigeve_prodution.enums.StockMovementType;
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
 * Entidade de Movimento de Estoque
 * Registra todas as movimentações (entradas e saídas) de matérias-primas
 */
@Entity
@Table(name = "tab_raw_material_movements", indexes = {
    @Index(name = "idx_raw_material_movements_material_date", columnList = "raw_material_id, movement_date"),
    @Index(name = "idx_raw_material_movements_company_date", columnList = "company_id, movement_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RawMaterialMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "raw_material_movement_id", columnDefinition = "uuid")
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

    // Tipo de movimento
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false)
    private StockMovementType movementType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "movement_origin", nullable = false)
    private MovementOrigin movementOrigin;

    @Column(name = "origin_id", columnDefinition = "uuid")
    private UUID originId;  // ID da compra, OP, etc.

    // Documento
    @Size(max = 100)
    @Column(name = "document_number", length = 100)
    private String documentNumber;

    @NotNull
    @Column(name = "movement_date", nullable = false)
    private LocalDateTime movementDate;

    // Quantidade e custo
    @NotNull
    @DecimalMin(value = "0.0001")
    @Digits(integer = 15, fraction = 4)
    @Column(name = "quantity", nullable = false, precision = 15, scale = 4)
    private BigDecimal quantity;

    @DecimalMin(value = "0.0000")
    @Digits(integer = 15, fraction = 4)
    @Column(name = "unit_cost", precision = 15, scale = 4)
    private BigDecimal unitCost;

    @DecimalMin(value = "0.0000")
    @Digits(integer = 15, fraction = 4)
    @Column(name = "total_cost", precision = 15, scale = 4)
    private BigDecimal totalCost;

    // Responsável
    @Column(name = "user_id", columnDefinition = "uuid")
    private UUID userId;

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
        if (this.movementDate == null) {
            this.movementDate = LocalDateTime.now();
        }
        // Calcula total_cost se não foi informado
        if (this.totalCost == null && this.unitCost != null && this.quantity != null) {
            this.totalCost = this.unitCost.multiply(this.quantity);
        }
    }

    // Métodos auxiliares
    public boolean isEntry() {
        return StockMovementType.IN.equals(this.movementType);
    }

    public boolean isExit() {
        return StockMovementType.OUT.equals(this.movementType);
    }

    public BigDecimal getSignedQuantity() {
        if (quantity == null) return BigDecimal.ZERO;
        return isEntry() ? quantity : quantity.negate();
    }
}
