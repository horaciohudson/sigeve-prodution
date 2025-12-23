package br.com.sigeve.sigeve_prodution.dto;

import br.com.sigeve.sigeve_prodution.enums.UnitType;
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
public class UpdateProductionProductDTO {
    
    private UUID productId;
    
    @Size(max = 50, message = "SKU deve ter no máximo 50 caracteres")
    private String sku;
    
    @Size(max = 50, message = "Código de barras deve ter no máximo 50 caracteres")
    private String barcode;
    
    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    private String description;
    
    @Size(max = 50, message = "Tamanho deve ter no máximo 50 caracteres")
    private String size;
    
    @Size(max = 50, message = "Cor deve ter no máximo 50 caracteres")
    private String color;
    
    private UnitType unitType;
    
    @Size(max = 500, message = "URL da imagem deve ter no máximo 500 caracteres")
    private String imageUrl;
    
    private String notes;
    
    private Boolean isActive;
}
