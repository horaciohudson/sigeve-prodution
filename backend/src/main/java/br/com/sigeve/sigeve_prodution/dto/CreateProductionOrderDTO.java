package br.com.sigeve.sigeve_prodution.dto;

import br.com.sigeve.sigeve_prodution.enums.PriorityLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductionOrderDTO {
    
    @NotNull(message = "Tenant ID é obrigatório")
    private UUID tenantId;
    
    @NotNull(message = "Company ID é obrigatório")
    private UUID companyId;
    
    @NotBlank(message = "Código é obrigatório")
    @Size(max = 50, message = "Código deve ter no máximo 50 caracteres")
    private String code;
    
    @NotNull(message = "Produto é obrigatório")
    private UUID productId;
    
    @NotNull(message = "Quantidade planejada é obrigatória")
    @Positive(message = "Quantidade planejada deve ser positiva")
    private BigDecimal quantityPlanned;
    
    private PriorityLevel priority = PriorityLevel.MEDIUM;
    
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate deadline;
    
    private UUID customerId;
    private UUID orderId;
    
    private String notes;
}
