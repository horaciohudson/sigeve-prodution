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
public class ProductionClosureDTO {
    
    private UUID id;
    private UUID tenantId;
    private UUID companyId;
    private UUID productionOrderId;
    
    private BigDecimal totalMaterial;
    private BigDecimal totalService;
    private BigDecimal totalLabor;
    private BigDecimal totalIndirect;
    private BigDecimal totalCost;
    
    private LocalDate closureDate;
    private OffsetDateTime closedAt;
    private String closedBy;
    
    private Boolean exportedToFinancial;
    private OffsetDateTime financialExportDate;
    private UUID financialDocumentId;
    
    private String notes;
    
    private OffsetDateTime createdAt;
    private String createdBy;
}
