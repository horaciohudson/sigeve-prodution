package br.com.sigeve.sigeve_prodution.dto;

import br.com.sigeve.sigeve_prodution.enums.CompositionItemType;
import br.com.sigeve.sigeve_prodution.enums.UnitType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompositionItemDTO {
    
    private UUID id;
    private UUID tenantId;
    private UUID companyId;
    private UUID compositionId;
    
    private CompositionItemType itemType;
    private UUID referenceId;
    
    private Integer sequence;
    private UnitType unitType;
    private BigDecimal quantity;
    private BigDecimal lossPercentage;
    
    private BigDecimal unitCost;
    private BigDecimal totalCost;
    
    private Boolean isOptional;
    private String notes;
    
    // Auditoria
    private OffsetDateTime createdAt;
    private String createdBy;
    private OffsetDateTime updatedAt;
    private String updatedBy;
}
