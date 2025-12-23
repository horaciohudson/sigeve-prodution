package br.com.sigeve.sigeve_prodution.dto;

import br.com.sigeve.sigeve_prodution.enums.CompositionItemType;
import br.com.sigeve.sigeve_prodution.enums.UnitType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCompositionItemDTO {
    
    @NotNull(message = "Tenant ID é obrigatório")
    private UUID tenantId;
    
    @NotNull(message = "Company ID é obrigatório")
    private UUID companyId;
    
    @NotNull(message = "Composição é obrigatória")
    private UUID compositionId;
    
    @NotNull(message = "Tipo de item é obrigatório")
    private CompositionItemType itemType;
    
    @NotNull(message = "Referência é obrigatória")
    private UUID referenceId;
    
    private Integer sequence = 1;
    
    @NotNull(message = "Tipo de unidade é obrigatório")
    private UnitType unitType;
    
    @NotNull(message = "Quantidade é obrigatória")
    @Positive(message = "Quantidade deve ser positiva")
    private BigDecimal quantity;
    
    private BigDecimal lossPercentage = BigDecimal.ZERO;
    
    private BigDecimal unitCost;
    
    private Boolean isOptional = false;
    private String notes;
}
