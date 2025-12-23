package br.com.sigeve.sigeve_prodution.dto;

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
public class CreateProductionClosureDTO {
    
    @NotNull(message = "Tenant ID é obrigatório")
    private UUID tenantId;
    
    @NotNull(message = "Company ID é obrigatório")
    private UUID companyId;
    
    @NotNull(message = "Ordem de produção é obrigatória")
    private UUID productionOrderId;
    
    @NotNull(message = "Custo total é obrigatório")
    private BigDecimal totalCost;
    
    private BigDecimal totalMaterial = BigDecimal.ZERO;
    private BigDecimal totalService = BigDecimal.ZERO;
    private BigDecimal totalLabor = BigDecimal.ZERO;
    private BigDecimal totalIndirect = BigDecimal.ZERO;
    
    private LocalDate closureDate;
    private String notes;
}
