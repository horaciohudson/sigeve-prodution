package br.com.sigeve.sigeve_prodution.dto;

import br.com.sigeve.sigeve_prodution.enums.QualityStatus;
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
public class ProductionExecutionDTO {
    
    private UUID id;
    private UUID tenantId;
    private UUID companyId;
    
    private UUID productionOrderId;
    private UUID stepId;
    
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    
    private BigDecimal quantityDone;
    private BigDecimal lossQuantity;
    
    private UUID employeeId;
    private UUID machineId;
    
    private QualityStatus qualityStatus;
    private String rejectionReason;
    
    private String notes;
    
    // Auditoria
    private OffsetDateTime createdAt;
    private String createdBy;
    private OffsetDateTime updatedAt;
    private String updatedBy;
}
