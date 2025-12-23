package br.com.sigeve.sigeve_prodution.mapper;

import br.com.sigeve.sigeve_prodution.dto.CreateUserRequestDTO;
import br.com.sigeve.sigeve_prodution.dto.UpdateUserRequestDTO;
import br.com.sigeve.sigeve_prodution.dto.UserDTO;
import br.com.sigeve.sigeve_prodution.model.Role;
import br.com.sigeve.sigeve_prodution.model.User;
import br.com.sigeve.sigeve_prodution.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    @Autowired
    private RoleRepository roleRepository;

    /**
     * Converte CreateUserRequestDTO para User
     */
    public User toEntity(CreateUserRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        User user = new User();
        user.setTenantId(dto.getTenantId());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPasswordHash(dto.getPassword()); // Será criptografada no service
        user.setFullName(dto.getFullName());
        user.setStatus(dto.getStatus());
        user.setLanguage(dto.getLanguage());
        user.setTimezone(dto.getTimezone());
        user.setSystemAdmin(dto.isSystemAdmin());

        // Processar roles se fornecidas
        if (dto.getRoleIds() != null && !dto.getRoleIds().isEmpty()) {
            List<Role> roles = roleRepository.findByIdIn(new HashSet<>(dto.getRoleIds()));
            user.setRoles(new HashSet<>(roles));
        }

        return user;
    }

    /**
     * Converte UpdateUserRequestDTO para User
     */
    public User toEntity(UpdateUserRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPasswordHash(dto.getPassword()); // Será criptografada no service
        user.setFullName(dto.getFullName());
        user.setStatus(dto.getStatus());
        user.setLanguage(dto.getLanguage());
        user.setTimezone(dto.getTimezone());
        if (dto.getSystemAdmin() != null) {
            user.setSystemAdmin(dto.getSystemAdmin());
        }

        // Processar roles se fornecidas
        if (dto.getRoleIds() != null && !dto.getRoleIds().isEmpty()) {
            List<Role> roles = roleRepository.findByIdIn(new HashSet<>(dto.getRoleIds()));
            user.setRoles(new HashSet<>(roles));
        }

        return user;
    }

    /**
     * Converte User para UserDTO
     */
    public UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setTenantId(user.getTenantId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setStatus(user.getStatus());
        dto.setLanguage(user.getLanguage());
        dto.setTimezone(user.getTimezone());
        dto.setSystemAdmin(user.isSystemAdmin());
        dto.setFailedAttempts(user.getFailedAttempts());
        dto.setLastLoginAt(user.getLastLoginAt());
        dto.setLockedUntil(user.getLockedUntil());

        // Converter roles para strings
        if (user.getRoles() != null) {
            Set<String> roleNames = user.getRoles().stream()
                    .map(role -> role.getRole().name())
                    .collect(Collectors.toSet());
            dto.setRoles(roleNames);
        }

        return dto;
    }
}