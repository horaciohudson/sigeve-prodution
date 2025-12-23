package br.com.sigeve.sigeve_prodution.dto;

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
public class RawMaterialStockDTO {
    
    private UUID id;
    private UUID tenantId;
    private UUID companyId;
    private UUID rawMaterialId;
    private UUID warehouseId;
    
    private BigDecimal quantity;
    private BigDecimal reservedQuantity;
    private BigDecimal availableQuantity;
    
    private OffsetDateTime lastMovementDate;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
