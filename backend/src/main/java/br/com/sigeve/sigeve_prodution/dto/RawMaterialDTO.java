package br.com.sigeve.sigeve_prodution.dto;

import br.com.sigeve.sigeve_prodution.enums.UnitType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RawMaterialDTO {
    
    private UUID id;
    private UUID tenantId;
    private UUID companyId;
    
    // Identificação
    private String code;
    private String name;
    private UnitType unitType;
    
    // Fornecedor
    private UUID supplierId;
    
    // Custos
    private BigDecimal averageCost;
    private BigDecimal lastPurchasePrice;
    private LocalDate lastPurchaseDate;
    
    // Controle de estoque
    private Boolean stockControl;
    private BigDecimal minStock;
    private BigDecimal maxStock;
    private BigDecimal reorderPoint;
    private Integer leadTimeDays;
    
    // Categoria
    private UUID categoryId;
    
    // Controle
    private Boolean isActive;
    
    // Auditoria
    private OffsetDateTime createdAt;
    private String createdBy;
    private OffsetDateTime updatedAt;
    private String updatedBy;
    private Integer version;
}
