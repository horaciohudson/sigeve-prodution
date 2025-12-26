package br.com.sigeve.sigeve_prodution.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompositionCostSummaryDTO {
    private Integer totalItems;
    private BigDecimal totalCost;
    private List<CompositionItemCostDTO> itemsCost;
}
