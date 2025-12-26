package br.com.sigeve.sigeve_prodution.dto;

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
public class CreateServiceDTO {
    
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
    
    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    private String description;
    
    private BigDecimal unitPrice;
    private UUID costCenterId;
    
    private Boolean isActive = true;
    private String notes;
}
