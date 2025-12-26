package br.com.sigeve.sigeve_prodution.model;

import br.com.sigeve.sigeve_prodution.enums.UnitType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Entidade de Produto de Produção
 * Pode diferir do produto comercial (Gestor)
 */
@Entity
@Table(name = "tab_production_products", indexes = {
    @Index(name = "idx_production_products_company", columnList = "company_id"),
    @Index(name = "idx_production_products_sku", columnList = "company_id, sku"),
    @Index(name = "idx_production_products_product", columnList = "product_id")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uq_production_products_company_sku", columnNames = {"company_id", "sku"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductionProduct extends AuditFull {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "production_product_id", columnDefinition = "uuid")
    private UUID id;

    @NotNull
    @Column(name = "tenant_id", nullable = false, columnDefinition = "uuid")
    private UUID tenantId;

    @NotNull
    @Column(name = "company_id", nullable = false, columnDefinition = "uuid")
    private UUID companyId;

    // Referência ao produto comercial (opcional)
    @Column(name = "product_id", columnDefinition = "uuid")
    private UUID productId;

    // Identificação
    @Size(max = 50)
    @Column(name = "sku", length = 50)
    private String sku;

    @Size(max = 50)
    @Column(name = "barcode", length = 50)
    private String barcode;

    @NotBlank
    @Size(max = 500)
    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @Size(max = 50)
    @Column(name = "size", length = 50)
    private String size;

    @Size(max = 50)
    @Column(name = "color", length = 50)
    private String color;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "unit_type", nullable = false)
    private UnitType unitType = UnitType.UN;

    // Imagem e observações
    @Size(max = 500)
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // Controle
    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Version
    @Column(name = "version")
    private Integer version = 1;

    // Métodos auxiliares
    public String getName() {
        return this.description;
    }

    public boolean isActive() {
        return Boolean.TRUE.equals(this.isActive);
    }

    public String getFullDescription() {
        StringBuilder sb = new StringBuilder(description);
        if (size != null && !size.isEmpty()) {
            sb.append(" - Tam: ").append(size);
        }
        if (color != null && !color.isEmpty()) {
            sb.append(" - Cor: ").append(color);
        }
        return sb.toString();
    }
}
