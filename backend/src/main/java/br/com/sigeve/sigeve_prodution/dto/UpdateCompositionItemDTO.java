package br.com.sigeve.sigeve_prodution.dto;

import br.com.sigeve.sigeve_prodution.enums.CompositionItemType;
import br.com.sigeve.sigeve_prodution.enums.UnitType;
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
public class UpdateCompositionItemDTO {
    
    private CompositionItemType itemType;
    private UUID referenceId;
    
    private Integer sequence;
    private UnitType unitType;
    
    @Positive(message = "Quantidade deve ser positiva")
    private BigDecimal quantity;
    
    private BigDecimal lossPercentage;
    private BigDecimal unitCost;
    
    private Boolean isOptional;
    private String notes;
}
