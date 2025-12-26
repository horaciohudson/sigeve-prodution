package br.com.sigeve.sigeve_prodution.dto;

import br.com.sigeve.sigeve_prodution.enums.PriorityLevel;
import br.com.sigeve.sigeve_prodution.enums.ProductionOrderStatus;
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
public class ProductionOrderDTO {
    
    private UUID id;
    private UUID tenantId;
    private UUID companyId;
    
    private String code;
    private UUID productId;
    private String productName;
    
    private BigDecimal quantityPlanned;
    private BigDecimal quantityProduced;
    
    private ProductionOrderStatus status;
    private PriorityLevel priority;
    
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate deadline;
    
    private UUID customerId;
    private UUID orderId;
    
    private BigDecimal costTotal;
    private String notes;
    
    // Auditoria
    private OffsetDateTime createdAt;
    private String createdBy;
    private OffsetDateTime updatedAt;
    private String updatedBy;
    private Integer version;
    
    // Aprovação
    private String approvedBy;
    private OffsetDateTime approvedAt;
    
    // Finalização
    private String finishedBy;
    private OffsetDateTime finishedAt;
    
    private String canceledReason;
}
