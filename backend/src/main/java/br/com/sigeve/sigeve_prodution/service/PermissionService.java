package br.com.sigeve.sigeve_prodution.service;

import br.com.sigeve.sigeve_prodution.model.Permission;
import br.com.sigeve.sigeve_prodution.model.User;
import br.com.sigeve.sigeve_prodution.model.UserPermission;
import br.com.sigeve.sigeve_prodution.repository.PermissionRepository;
import br.com.sigeve.sigeve_prodution.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private br.com.sigeve.sigeve_prodution.repository.UserPermissionRepository userPermissionRepository;

    @Autowired
    private UserRepository userRepository;

    // ===== MÉTODOS PARA PERMISSION =====

    /**
     * Lista todas as permissões ativas
     */
    @Transactional(readOnly = true)
    public List<Permission> getAllActivePermissions() {
        return permissionRepository.findAllActive();
    }

    /**
     * Busca permissão por ID
     */
    @Transactional(readOnly = true)
    public Optional<Permission> getPermissionById(Long id) {
        return permissionRepository.findById(id);
    }

    /**
     * Busca permissão por chave
     */
    @Transactional(readOnly = true)
    public Optional<Permission> getPermissionByKey(String permissionKey) {
        return permissionRepository.findByPermissionKey(permissionKey);
    }

    /**
     * Busca permissões por módulo
     */
    @Transactional(readOnly = true)
    public List<Permission> getPermissionsByModule(String module) {
        return permissionRepository.findByModule(module);
    }

    /**
     * Busca permissões por nível máximo
     */
    @Transactional(readOnly = true)
    public List<Permission> getPermissionsByMaxLevel(Integer maxLevel) {
        return permissionRepository.findByLevelLessThanEqual(maxLevel);
    }

    /**
     * Pesquisa permissões por texto
     */
    @Transactional(readOnly = true)
    public List<Permission> searchPermissions(String search) {
        return permissionRepository.findByKeyOrDescriptionContaining(search);
    }

    // ===== MÉTODOS PARA USER PERMISSION =====

    /**
     * Lista todas as permissões de um usuário em um tenant
     */
    @Transactional(readOnly = true)
    public List<UserPermission> getUserPermissions(UUID userId, UUID tenantId) {
        return userPermissionRepository.findByUserIdAndTenantId(userId, tenantId);
    }

    /**
     * Lista permissões concedidas de um usuário em um tenant
     */
    @Transactional(readOnly = true)
    public List<UserPermission> getGrantedUserPermissions(UUID userId, UUID tenantId) {
        return userPermissionRepository.findGrantedByUserIdAndTenantId(userId, tenantId);
    }

    /**
     * Lista permissões negadas de um usuário em um tenant
     */
    @Transactional(readOnly = true)
    public List<UserPermission> getDeniedUserPermissions(UUID userId, UUID tenantId) {
        return userPermissionRepository.findDeniedByUserIdAndTenantId(userId, tenantId);
    }

    /**
     * Verifica se usuário tem permissão específica
     */
    @Transactional(readOnly = true)
    public boolean hasPermission(UUID userId, String permissionKey, UUID tenantId) {
        Optional<Permission> permission = permissionRepository.findByPermissionKey(permissionKey);
        if (permission.isEmpty()) {
            return false;
        }
        return userPermissionRepository.hasGrantedPermission(userId, permission.get().getId(), tenantId);
    }

    /**
     * Verifica se usuário tem permissão específica negada
     */
    @Transactional(readOnly = true)
    public boolean hasPermissionDenied(UUID userId, String permissionKey, UUID tenantId) {
        Optional<Permission> permission = permissionRepository.findByPermissionKey(permissionKey);
        if (permission.isEmpty()) {
            return false;
        }
        return userPermissionRepository.hasDeniedPermission(userId, permission.get().getId(), tenantId);
    }

    /**
     * Concede uma permissão a um usuário
     */
    public UserPermission grantPermission(UUID userId, Long permissionId, UUID tenantId, String grantedBy, String notes) {
        // Verifica se usuário existe
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new RuntimeException("Usuário não encontrado");
        }

        // Verifica se permissão existe
        Optional<Permission> permission = permissionRepository.findById(permissionId);
        if (permission.isEmpty()) {
            throw new RuntimeException("Permissão não encontrada");
        }

        // Verifica se já existe uma configuração para esta permissão
        Optional<UserPermission> existingPermission = userPermissionRepository
                .findByUserIdAndPermissionIdAndTenantId(userId, permissionId, tenantId);

        UserPermission userPermission;
        if (existingPermission.isPresent()) {
            // Atualiza a permissão existente
            userPermission = existingPermission.get();
            userPermission.setGranted(true);
            userPermission.setNotes(notes);
            userPermission.setUpdatedAt(LocalDateTime.now());
            userPermission.setUpdatedBy(grantedBy);
        } else {
            // Cria nova permissão
            userPermission = new UserPermission();
            userPermission.setUserId(userId);
            userPermission.setPermissionId(permissionId);
            userPermission.setTenantId(tenantId);
            userPermission.setGranted(true);
            userPermission.setNotes(notes);
            userPermission.setCreatedBy(grantedBy);
        }

        return userPermissionRepository.save(userPermission);
    }

    /**
     * Nega uma permissão a um usuário
     */
    public UserPermission denyPermission(UUID userId, Long permissionId, UUID tenantId, String deniedBy, String notes) {
        // Check if permission already exists
        Optional<UserPermission> existing = userPermissionRepository
                .findByUserIdAndPermissionIdAndTenantId(userId, permissionId, tenantId);
        
        UserPermission userPermission;
        if (existing.isPresent()) {
            userPermission = existing.get();
            userPermission.setGranted(false);
            userPermission.setNotes(notes);
            userPermission.setUpdatedAt(LocalDateTime.now());
            userPermission.setUpdatedBy(deniedBy);
        } else {
            userPermission = new UserPermission();
            userPermission.setUserId(userId);
            userPermission.setPermissionId(permissionId);
            userPermission.setTenantId(tenantId);
            userPermission.setGranted(false);
            userPermission.setNotes(notes);
            userPermission.setCreatedBy(deniedBy);
        }
        
        return userPermissionRepository.save(userPermission);
    }

    /**
     * Remove uma permissão específica de um usuário
     */
    public void removeUserPermission(UUID userId, Long permissionId, UUID tenantId, String deletedBy) {
        userPermissionRepository.softDeleteByUserIdAndPermissionIdAndTenantId(userId, permissionId, tenantId, deletedBy);
    }

    /**
     * Remove todas as permissões de um usuário em um tenant
     */
    public void removeAllUserPermissions(UUID userId, UUID tenantId, String deletedBy) {
        userPermissionRepository.softDeleteAllByUserIdAndTenantId(userId, tenantId, deletedBy);
    }

    /**
     * Lista usuários que têm uma permissão específica
     */
    @Transactional(readOnly = true)
    public List<UserPermission> getUsersWithPermission(Long permissionId, UUID tenantId) {
        return userPermissionRepository.findUsersWithPermission(permissionId, tenantId);
    }

    /**
     * Lista usuários que têm uma permissão específica por chave
     */
    @Transactional(readOnly = true)
    public List<UserPermission> getUsersWithPermissionByKey(String permissionKey, UUID tenantId) {
        return userPermissionRepository.findByPermissionKeyAndTenantId(permissionKey, tenantId);
    }

    /**
     * Configura múltiplas permissões para um usuário
     */
    public void configureUserPermissions(UserPermissionConfig config, String configuredBy) {
        for (UserPermissionConfig.PermissionSetting setting : config.getPermissions()) {
            if (setting.isGranted()) {
                grantPermission(config.getUserId(), setting.getPermissionId(), 
                              config.getTenantId(), configuredBy, setting.getNotes());
            } else {
                denyPermission(config.getUserId(), setting.getPermissionId(), 
                             config.getTenantId(), configuredBy, setting.getNotes());
            }
        }
    }

    /**
     * Classe interna para configuração de múltiplas permissões
     */
    public static class UserPermissionConfig {
        private UUID userId;
        private UUID tenantId;
        private List<PermissionSetting> permissions;

        public UserPermissionConfig() {}

        public UserPermissionConfig(UUID userId, UUID tenantId, List<PermissionSetting> permissions) {
            this.userId = userId;
            this.tenantId = tenantId;
            this.permissions = permissions;
        }

        // Getters e Setters
        public UUID getUserId() { return userId; }
        public void setUserId(UUID userId) { this.userId = userId; }
        public UUID getTenantId() { return tenantId; }
        public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
        public List<PermissionSetting> getPermissions() { return permissions; }
        public void setPermissions(List<PermissionSetting> permissions) { this.permissions = permissions; }

        /**
         * Classe interna para configuração individual de permissão
         */
        public static class PermissionSetting {
            private Long permissionId;
            private boolean granted;
            private String notes;

            public PermissionSetting() {}

            public PermissionSetting(Long permissionId, boolean granted, String notes) {
                this.permissionId = permissionId;
                this.granted = granted;
                this.notes = notes;
            }

            // Getters e Setters
            public Long getPermissionId() { return permissionId; }
            public void setPermissionId(Long permissionId) { this.permissionId = permissionId; }
            public boolean isGranted() { return granted; }
            public void setGranted(boolean granted) { this.granted = granted; }
            public String getNotes() { return notes; }
            public void setNotes(String notes) { this.notes = notes; }
        }
    }
}