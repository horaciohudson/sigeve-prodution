package br.com.sigeve.sigeve_prodution.dto;

import br.com.sigeve.sigeve_prodution.enums.UnitType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductionProductDTO {
    
    private UUID id;
    private UUID tenantId;
    private UUID companyId;
    private UUID productId;
    
    // Identificação
    private String sku;
    private String barcode;
    private String description;
    private String size;
    private String color;
    private UnitType unitType;
    
    // Imagem e observações
    private String imageUrl;
    private String notes;
    
    // Controle
    private Boolean isActive;
    
    // Auditoria
    private OffsetDateTime createdAt;
    private String createdBy;
    private OffsetDateTime updatedAt;
    private String updatedBy;
    private Integer version;
}
