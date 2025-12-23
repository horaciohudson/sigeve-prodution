package br.com.sigeve.sigeve_prodution.dto;

import br.com.sigeve.sigeve_prodution.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequestDTO {
    
    @NotNull(message = "Tenant ID is required")
    private UUID tenantId;
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 120, message = "Email must not exceed 120 characters")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters")
    private String password;
    
    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name must not exceed 100 characters")
    private String fullName;
    
    private UserStatus status = UserStatus.ACTIVE;
    
    @Size(max = 2, message = "Language code must be 2 characters")
    private String language;
    
    @Size(max = 64, message = "Timezone must not exceed 64 characters")
    private String timezone;
    
    private boolean systemAdmin = false;
    
    private List<Long> roleIds;
}
