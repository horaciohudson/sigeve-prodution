package br.com.sigeve.sigeve_prodution.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO para transferência de dados de Company
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyDTO {

    private UUID id;
    
    @NotNull(message = "Tenant ID é obrigatório")
    private UUID tenantId;

    @NotBlank(message = "Razão social é obrigatória")
    @Size(max = 200, message = "Razão social deve ter no máximo 200 caracteres")
    private String corporateName;

    @Size(max = 200, message = "Nome fantasia deve ter no máximo 200 caracteres")
    private String tradeName;

    @Size(max = 20, message = "CNPJ deve ter no máximo 20 caracteres")
    private String cnpj;

    @Size(max = 50, message = "Inscrição estadual deve ter no máximo 50 caracteres")
    private String stateRegistration;

    @Size(max = 50, message = "Inscrição municipal deve ter no máximo 50 caracteres")
    private String municipalRegistration;

    @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres")
    private String phone;

    @Size(max = 20, message = "Celular deve ter no máximo 20 caracteres")
    private String mobile;

    @Email(message = "Email deve ser válido")
    @Size(max = 100, message = "Email deve ter no máximo 100 caracteres")
    private String email;

    @Size(max = 20, message = "WhatsApp deve ter no máximo 20 caracteres")
    private String whatsapp;

    @DecimalMin(value = "0.0", message = "Taxa ISS deve ser maior ou igual a zero")
    @DecimalMax(value = "100.0", message = "Taxa ISS deve ser menor ou igual a 100")
    private Float issRate;

    @DecimalMin(value = "0.0", message = "Taxa Funrural deve ser maior ou igual a zero")
    @DecimalMax(value = "100.0", message = "Taxa Funrural deve ser menor ou igual a 100")
    private Float funruralRate;

    @Size(max = 100, message = "Gerente deve ter no máximo 100 caracteres")
    private String manager;

    private Boolean factory;
    private Boolean supplierFlag;
    private Boolean customerFlag;
    private Boolean transporterFlag;
    private Boolean isActive;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    public String getDisplayName() {
        return this.tradeName != null && !this.tradeName.trim().isEmpty() 
               ? this.tradeName 
               : this.corporateName;
    }

    public boolean isSupplier() {
        return this.supplierFlag != null && this.supplierFlag;
    }

    public boolean isCustomer() {
        return this.customerFlag != null && this.customerFlag;
    }

    public boolean isTransporter() {
        return this.transporterFlag != null && this.transporterFlag;
    }

    public boolean isFactory() {
        return this.factory != null && this.factory;
    }

    public boolean isActive() {
        return this.isActive != null && this.isActive;
    }
}
