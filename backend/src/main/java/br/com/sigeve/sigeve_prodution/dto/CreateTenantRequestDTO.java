package br.com.sigeve.sigeve_prodution.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateTenantRequestDTO {
    
    @NotBlank(message = "Código é obrigatório")
    @Size(max = 20, message = "Código deve ter no máximo 20 caracteres")
    private String code;
    
    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    private String name;
    
    private String status;
    
    // Constructors
    public CreateTenantRequestDTO() {}
    
    public CreateTenantRequestDTO(String code, String name, String status) {
        this.code = code;
        this.name = name;
        this.status = status;
    }
    
    // Getters and Setters
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
}