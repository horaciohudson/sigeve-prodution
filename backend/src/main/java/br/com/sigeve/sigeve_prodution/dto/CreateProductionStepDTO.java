package br.com.sigeve.sigeve_prodution.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductionStepDTO {
    
    @NotNull(message = "Tenant ID é obrigatório")
    private UUID tenantId;
    
    @NotNull(message = "Company ID é obrigatório")
    private UUID companyId;
    
    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 200, message = "Nome deve ter no máximo 200 caracteres")
    private String name;
    
    private String description;
    
    private Integer sequence = 1;
    private Integer estimatedTime = 0;
    
    private UUID costCenterId;
    
    private Boolean isOutsourced = false;
    private Boolean requiresApproval = false;
    private Boolean isActive = true;
}
