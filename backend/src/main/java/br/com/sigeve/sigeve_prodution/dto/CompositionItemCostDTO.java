package br.com.sigeve.sigeve_prodution.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompositionItemCostDTO {
    private UUID itemId;
    private BigDecimal quantity;
    private BigDecimal unitCost;
    private BigDecimal lossPercentage;
    private BigDecimal totalCost;
}
