package br.com.sigeve.sigeve_prodution.dto;

import br.com.sigeve.sigeve_prodution.enums.BuyServiceStatus;
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
public class UpdateBuyServiceDTO {
    
    private UUID supplierId;
    
    @Size(max = 50, message = "Código deve ter no máximo 50 caracteres")
    private String code;
    
    @Size(max = 200, message = "Nome do serviço deve ter no máximo 200 caracteres")
    private String serviceName;
    
    @Size(max = 100, message = "Referência deve ter no máximo 100 caracteres")
    private String reference;
    
    private LocalDate orderDate;
    private LocalDate deliveryDate;
    
    private BigDecimal baseValue;
    
    @Size(max = 200, message = "Condições de pagamento devem ter no máximo 200 caracteres")
    private String paymentTerms;
    
    private BuyServiceStatus status;
    private String notes;
}
