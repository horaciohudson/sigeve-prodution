package br.com.sigeve.sigeve_prodution.dto;

import br.com.sigeve.sigeve_prodution.enums.MovementOrigin;
import br.com.sigeve.sigeve_prodution.enums.StockMovementType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateRawMaterialMovementDTO {
    
    @NotNull(message = "Tenant ID é obrigatório")
    private UUID tenantId;
    
    @NotNull(message = "Company ID é obrigatório")
    private UUID companyId;
    
    @NotNull(message = "Matéria-prima é obrigatória")
    private UUID rawMaterialId;
    
    @NotNull(message = "Tipo de movimento é obrigatório")
    private StockMovementType movementType;
    
    @NotNull(message = "Origem do movimento é obrigatória")
    private MovementOrigin movementOrigin;
    
    private UUID originId;
    
    @Size(max = 100, message = "Número do documento deve ter no máximo 100 caracteres")
    private String documentNumber;
    
    private LocalDate movementDate;
    
    @NotNull(message = "Quantidade é obrigatória")
    @Positive(message = "Quantidade deve ser positiva")
    private BigDecimal quantity;
    
    private BigDecimal unitCost;
    private BigDecimal totalCost;
    
    private UUID userId;
    private String notes;
}
