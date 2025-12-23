package br.com.sigeve.sigeve_prodution.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateTenantRequestDTO {
    
    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    private String name;
    
    private String status;
    
    // Constructors
    public UpdateTenantRequestDTO() {}
    
    public UpdateTenantRequestDTO(String name, String status) {
        this.name = name;
        this.status = status;
    }
    
    // Getters and Setters
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