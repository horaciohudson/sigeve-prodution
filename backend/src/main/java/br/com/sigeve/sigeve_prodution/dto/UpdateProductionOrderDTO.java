package br.com.sigeve.sigeve_prodution.dto;

import br.com.sigeve.sigeve_prodution.enums.PriorityLevel;
import br.com.sigeve.sigeve_prodution.enums.ProductionOrderStatus;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductionOrderDTO {
    
    @Size(max = 50, message = "Código deve ter no máximo 50 caracteres")
    private String code;
    
    private UUID productId;
    
    @Positive(message = "Quantidade planejada deve ser positiva")
    private BigDecimal quantityPlanned;
    
    @Positive(message = "Quantidade produzida deve ser positiva ou zero")
    private BigDecimal quantityProduced;
    
    private ProductionOrderStatus status;
    private PriorityLevel priority;
    
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate deadline;
    
    private UUID customerId;
    private UUID orderId;
    
    private String notes;
    private String canceledReason;
}
