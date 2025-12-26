package br.com.sigeve.sigeve_prodution.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceDTO {
    
    private UUID id;
    private UUID tenantId;
    private UUID companyId;
    
    private String code;
    private String name;
    private String description;
    
    private BigDecimal unitPrice;
    private UUID costCenterId;
    
    private Boolean isActive;
    private String notes;
    
    // Auditoria
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
}
