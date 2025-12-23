package br.com.sigeve.sigeve_prodution.dto;

import br.com.sigeve.sigeve_prodution.enums.BuyServiceStatus;
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
public class BuyServiceDTO {
    
    private UUID id;
    private UUID tenantId;
    private UUID companyId;
    private UUID supplierId;
    
    private String code;
    private String serviceName;
    private String reference;
    
    private LocalDate orderDate;
    private LocalDate deliveryDate;
    
    private BigDecimal baseValue;
    private BigDecimal totalValue;
    
    private String paymentTerms;
    private BuyServiceStatus status;
    private String notes;
    
    // Auditoria
    private OffsetDateTime createdAt;
    private String createdBy;
    private OffsetDateTime updatedAt;
    private String updatedBy;
    
    // Aprovação
    private String approvedBy;
    private OffsetDateTime approvedAt;
    
    // Fechamento
    private String closedBy;
    private OffsetDateTime closedAt;
}
