package br.com.sigeve.sigeve_prodution.dto;

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
public class CompositionDTO {
    
    private UUID id;
    private UUID tenantId;
    private UUID companyId;
    private UUID productionProductId;
    
    private String name;
    private Integer version;
    
    private LocalDate effectiveDate;
    private LocalDate expirationDate;
    
    private Boolean isActive;
    private String notes;
    
    // Auditoria
    private OffsetDateTime createdAt;
    private String createdBy;
    private OffsetDateTime updatedAt;
    private String updatedBy;
    
    // Aprovação
    private String approvedBy;
    private OffsetDateTime approvedAt;
    
    // Detalhes adicionais
    private String productName;
    private Integer itemsCount;
    private BigDecimal totalCost;
}
