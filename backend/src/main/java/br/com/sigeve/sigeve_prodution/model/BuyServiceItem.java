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
 * Entidade de Item de Compra de Serviço
 * Detalha cada serviço contratado
 */
@Entity
@Table(name = "tab_buy_service_items", indexes = {
    @Index(name = "idx_buy_service_items_service", columnList = "buy_service_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BuyServiceItem extends AuditFull {

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

    // Compra de serviço
    @NotNull
    @Column(name = "buy_service_id", nullable = false, columnDefinition = "uuid")
    private UUID buyServiceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buy_service_id", insertable = false, updatable = false)
    private BuyService buyService;

    // Descrição
    @NotNull
    @Min(1)
    @Column(name = "sequence", nullable = false)
    private Integer sequence = 1;

    @NotBlank
    @Size(max = 500)
    @Column(name = "description", nullable = false, length = 500)
    private String description;

    // Quantidade e valores
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "unit_type", nullable = false)
    private UnitType unitType;

    @NotNull
    @DecimalMin(value = "0.0001")
    @Digits(integer = 15, fraction = 4)
    @Column(name = "quantity", nullable = false, precision = 15, scale = 4)
    private BigDecimal quantity;

    @NotNull
    @DecimalMin(value = "0.0000")
    @Digits(integer = 15, fraction = 4)
    @Column(name = "unit_price", nullable = false, precision = 15, scale = 4)
    private BigDecimal unitPrice;

    @DecimalMin(value = "0.00")
    @Digits(integer = 15, fraction = 2)
    @Column(name = "discount", precision = 15, scale = 2)
    private BigDecimal discount = BigDecimal.ZERO;

    @NotNull
    @DecimalMin(value = "0.00")
    @Digits(integer = 15, fraction = 2)
    @Column(name = "total_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalPrice;

    // Entrega
    @Column(name = "delivery_date")
    private LocalDate deliveryDate;

    @DecimalMin(value = "0.0000")
    @Digits(integer = 15, fraction = 4)
    @Column(name = "quantity_received", precision = 15, scale = 4)
    private BigDecimal quantityReceived = BigDecimal.ZERO;

    // Observações
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // Métodos auxiliares
    public void calculateTotalPrice() {
        if (quantity != null && unitPrice != null) {
            BigDecimal subtotal = quantity.multiply(unitPrice);
            BigDecimal discountAmount = discount != null ? discount : BigDecimal.ZERO;
            this.totalPrice = subtotal.subtract(discountAmount);
        }
    }

    public boolean isFullyReceived() {
        if (quantity == null || quantityReceived == null) {
            return false;
        }
        return quantityReceived.compareTo(quantity) >= 0;
    }

    public BigDecimal getPendingQuantity() {
        if (quantity == null) return BigDecimal.ZERO;
        if (quantityReceived == null) return quantity;
        BigDecimal pending = quantity.subtract(quantityReceived);
        return pending.max(BigDecimal.ZERO);
    }

    @PrePersist
    @PreUpdate
    protected void onSave() {
        calculateTotalPrice();
    }
}
