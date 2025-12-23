package br.com.sigeve.sigeve_prodution.dto;

import br.com.sigeve.sigeve_prodution.enums.UnitType;
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
public class BuyServiceItemDTO {
    
    private UUID id;
    private UUID tenantId;
    private UUID companyId;
    private UUID buyServiceId;
    
    private Integer sequence;
    private String description;
    
    private UnitType unitType;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal discount;
    private BigDecimal totalPrice;
    
    private LocalDate deliveryDate;
    private BigDecimal quantityReceived;
    
    private String notes;
    
    // Auditoria
    private OffsetDateTime createdAt;
    private String createdBy;
    private OffsetDateTime updatedAt;
    private String updatedBy;
}
