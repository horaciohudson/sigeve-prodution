package br.com.sigeve.sigeve_prodution.dto;

import br.com.sigeve.sigeve_prodution.enums.UnitType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CreateRawMaterialDTO {
    
    @NotNull(message = "Tenant ID é obrigatório")
    private UUID tenantId;
    
    @NotNull(message = "Company ID é obrigatório")
    private UUID companyId;
    
    @NotBlank(message = "Código é obrigatório")
    @Size(max = 50, message = "Código deve ter no máximo 50 caracteres")
    private String code;
    
    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 200, message = "Nome deve ter no máximo 200 caracteres")
    private String name;
    
    @NotNull(message = "Tipo de unidade é obrigatório")
    private UnitType unitType;
    
    private UUID supplierId;
    
    private BigDecimal averageCost;
    private BigDecimal lastPurchasePrice;
    
    private Boolean stockControl = true;
    private BigDecimal minStock;
    private BigDecimal maxStock;
    private BigDecimal reorderPoint;
    private Integer leadTimeDays;
    
    private UUID categoryId;
    
    private Boolean isActive = true;
}
