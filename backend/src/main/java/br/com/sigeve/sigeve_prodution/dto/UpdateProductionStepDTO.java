package br.com.sigeve.sigeve_prodution.dto;

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
public class UpdateProductionStepDTO {
    
    @Size(max = 200, message = "Nome deve ter no máximo 200 caracteres")
    private String name;
    
    private String description;
    private Integer sequence;
    private Integer estimatedTime;
    
    private UUID costCenterId;
    
    private Boolean isOutsourced;
    private Boolean requiresApproval;
    private Boolean isActive;
}
