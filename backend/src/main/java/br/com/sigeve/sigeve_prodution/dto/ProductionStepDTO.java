package br.com.sigeve.sigeve_prodution.dto;

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
public class ProductionStepDTO {
    
    private UUID id;
    private UUID tenantId;
    private UUID companyId;
    
    private String name;
    private String description;
    private Integer sequence;
    
    private Integer estimatedTime;
    private UUID costCenterId;
    
    private Boolean isOutsourced;
    private Boolean requiresApproval;
    private Boolean isActive;
    
    // Auditoria
    private OffsetDateTime createdAt;
    private String createdBy;
    private OffsetDateTime updatedAt;
    private String updatedBy;
}
