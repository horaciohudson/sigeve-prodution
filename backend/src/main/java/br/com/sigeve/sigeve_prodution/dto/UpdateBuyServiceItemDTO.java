package br.com.sigeve.sigeve_prodution.dto;

import br.com.sigeve.sigeve_prodution.enums.UnitType;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBuyServiceItemDTO {
    
    private Integer sequence;
    
    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    private String description;
    
    private UnitType unitType;
    
    @Positive(message = "Quantidade deve ser positiva")
    private BigDecimal quantity;
    
    private BigDecimal unitPrice;
    private BigDecimal discount;
    
    private LocalDate deliveryDate;
    private BigDecimal quantityReceived;
    
    private String notes;
}
