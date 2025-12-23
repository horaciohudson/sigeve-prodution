package br.com.sigeve.sigeve_prodution.dto;

import br.com.sigeve.sigeve_prodution.enums.MovementOrigin;
import br.com.sigeve.sigeve_prodution.enums.StockMovementType;
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
public class RawMaterialMovementDTO {
    
    private UUID id;
    private UUID tenantId;
    private UUID companyId;
    private UUID rawMaterialId;
    
    private StockMovementType movementType;
    private MovementOrigin movementOrigin;
    private UUID originId;
    
    private String documentNumber;
    private OffsetDateTime movementDate;
    
    private BigDecimal quantity;
    private BigDecimal unitCost;
    private BigDecimal totalCost;
    
    private UUID userId;
    private String notes;
    
    private OffsetDateTime createdAt;
    private String createdBy;
}
