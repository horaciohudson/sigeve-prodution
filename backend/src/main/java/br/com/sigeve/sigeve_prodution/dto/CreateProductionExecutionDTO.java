package br.com.sigeve.sigeve_prodution.dto;

import br.com.sigeve.sigeve_prodution.enums.QualityStatus;
import jakarta.validation.constraints.NotNull;
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
public class CreateProductionExecutionDTO {
    
    @NotNull(message = "Tenant ID é obrigatório")
    private UUID tenantId;
    
    @NotNull(message = "Company ID é obrigatório")
    private UUID companyId;
    
    @NotNull(message = "Ordem de produção é obrigatória")
    private UUID productionOrderId;
    
    @NotNull(message = "Etapa é obrigatória")
    private UUID stepId;
    
    @NotNull(message = "Hora de início é obrigatória")
    private OffsetDateTime startTime;
    
    private OffsetDateTime endTime;
    
    @NotNull(message = "Quantidade realizada é obrigatória")
    private BigDecimal quantityDone;
    
    private BigDecimal lossQuantity = BigDecimal.ZERO;
    
    private UUID employeeId;
    private UUID machineId;
    
    private QualityStatus qualityStatus;
    private String rejectionReason;
    
    private String notes;
}
