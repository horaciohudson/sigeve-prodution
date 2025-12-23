package br.com.sigeve.sigeve_prodution.dto;

import br.com.sigeve.sigeve_prodution.enums.ProductionCostType;
import jakarta.validation.constraints.NotNull;
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
public class CreateProductionCostDTO {
    
    @NotNull(message = "Tenant ID é obrigatório")
    private UUID tenantId;
    
    @NotNull(message = "Company ID é obrigatório")
    private UUID companyId;
    
    @NotNull(message = "Ordem de produção é obrigatória")
    private UUID productionOrderId;
    
    @NotNull(message = "Tipo de custo é obrigatório")
    private ProductionCostType costType;
    
    private UUID referenceId;
    
    private LocalDate costDate;
    
    private BigDecimal quantity;
    private BigDecimal unitCost;
    
    @NotNull(message = "Custo total é obrigatório")
    private BigDecimal totalCost;
    
    private String notes;
}
