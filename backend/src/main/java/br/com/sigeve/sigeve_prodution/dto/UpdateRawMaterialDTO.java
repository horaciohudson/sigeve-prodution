package br.com.sigeve.sigeve_prodution.dto;

import br.com.sigeve.sigeve_prodution.enums.UnitType;
import jakarta.validation.constraints.Size;
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
public class UpdateRawMaterialDTO {
    
    @Size(max = 50, message = "Código deve ter no máximo 50 caracteres")
    private String code;
    
    @Size(max = 200, message = "Nome deve ter no máximo 200 caracteres")
    private String name;
    
    private UnitType unitType;
    
    private UUID supplierId;
    
    private BigDecimal averageCost;
    private BigDecimal lastPurchasePrice;
    
    private Boolean stockControl;
    private BigDecimal minStock;
    private BigDecimal maxStock;
    private BigDecimal reorderPoint;
    private Integer leadTimeDays;
    
    private UUID categoryId;
    
    private Boolean isActive;
}
