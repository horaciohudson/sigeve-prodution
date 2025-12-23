package br.com.sigeve.sigeve_prodution.dto;

import br.com.sigeve.sigeve_prodution.enums.ProductionCostType;
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
public class UpdateProductionCostDTO {
    
    private ProductionCostType costType;
    private UUID referenceId;
    
    private LocalDate costDate;
    
    private BigDecimal quantity;
    private BigDecimal unitCost;
    private BigDecimal totalCost;
    
    private String notes;
}
