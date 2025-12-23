package br.com.sigeve.sigeve_prodution.model;

import br.com.sigeve.sigeve_prodution.enums.BuyServiceStatus;
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
 * Entidade de Compra de Serviço
 * Representa serviços terceirizados (costura, lavagem, bordado, etc.)
 */
@Entity
@Table(name = "tab_buy_services", indexes = {
    @Index(name = "idx_buy_services_company_status", columnList = "company_id, status"),
    @Index(name = "idx_buy_services_supplier", columnList = "supplier_id")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uq_buy_services_company_code", columnNames = {"company_id", "code"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BuyService extends AuditFull {

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

    // Fornecedor
    @NotNull
    @Column(name = "supplier_id", nullable = false, columnDefinition = "uuid")
    private UUID supplierId;

    // Identificação
    @NotBlank
    @Size(max = 50)
    @Column(name = "code", nullable = false, length = 50)
    private String code;

    @NotBlank
    @Size(max = 200)
    @Column(name = "service_name", nullable = false, length = 200)
    private String serviceName;

    @Size(max = 100)
    @Column(name = "reference", length = 100)
    private String reference;

    // Datas
    @NotNull
    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;

    @Column(name = "delivery_date")
    private LocalDate deliveryDate;

    // Valores
    @NotNull
    @DecimalMin(value = "0.00")
    @Digits(integer = 15, fraction = 2)
    @Column(name = "base_value", nullable = false, precision = 15, scale = 2)
    private BigDecimal baseValue = BigDecimal.ZERO;

    @NotNull
    @DecimalMin(value = "0.00")
    @Digits(integer = 15, fraction = 2)
    @Column(name = "total_value", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalValue = BigDecimal.ZERO;

    // Condições
    @Size(max = 200)
    @Column(name = "payment_terms", length = 200)
    private String paymentTerms;

    // Status
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BuyServiceStatus status = BuyServiceStatus.OPEN;

    // Observações
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // Aprovação
    @Column(name = "approved_by")
    private String approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    // Fechamento
    @Column(name = "closed_by")
    private String closedBy;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    // Métodos auxiliares
    public boolean isOpen() {
        return BuyServiceStatus.OPEN.equals(this.status);
    }

    public boolean isApproved() {
        return BuyServiceStatus.APPROVED.equals(this.status);
    }

    public boolean isClosed() {
        return BuyServiceStatus.CLOSED.equals(this.status);
    }

    public void approve(String approvedBy) {
        this.status = BuyServiceStatus.APPROVED;
        this.approvedBy = approvedBy;
        this.approvedAt = LocalDateTime.now();
    }

    public void close(String closedBy) {
        this.status = BuyServiceStatus.CLOSED;
        this.closedBy = closedBy;
        this.closedAt = LocalDateTime.now();
    }
}
