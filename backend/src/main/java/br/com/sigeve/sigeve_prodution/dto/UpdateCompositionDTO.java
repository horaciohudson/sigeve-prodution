package br.com.sigeve.sigeve_prodution.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompositionDTO {
    
    private UUID productionProductId;
    
    @Size(max = 200, message = "Nome deve ter no máximo 200 caracteres")
    private String name;
    
    private Integer version;
    
    private LocalDate effectiveDate;
    private LocalDate expirationDate;
    
    private Boolean isActive;
    private String notes;
}
