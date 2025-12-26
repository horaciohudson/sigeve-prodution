package br.com.sigeve.sigeve_prodution.controller;

import br.com.sigeve.sigeve_prodution.model.Permission;
import br.com.sigeve.sigeve_prodution.model.UserPermission;
import br.com.sigeve.sigeve_prodution.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/permissions")
@CrossOrigin(origins = "*")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    /**
     * Lista todas as permissões ativas
     */
    @GetMapping
    public ResponseEntity<List<Permission>> getAllPermissions() {
        log.info("GET /api/permissions - Listando todas as permissões");
        List<Permission> permissions = permissionService.getAllActivePermissions();
        return ResponseEntity.ok(permissions);
    }

    /**
     * Busca permissão por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Permission> getPermissionById(@PathVariable Long id) {
        log.info("GET /api/permissions/{} - Buscando permissão por ID", id);
        return permissionService.getPermissionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Busca permissões de um usuário em um tenant
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserPermission>> getUserPermissions(
            @PathVariable UUID userId,
            @RequestParam UUID tenantId) {
        log.info("GET /api/permissions/user/{} - Buscando permissões do usuário no tenant {}", userId, tenantId);
        List<UserPermission> permissions = permissionService.getUserPermissions(userId, tenantId);
        return ResponseEntity.ok(permissions);
    }

    /**
     * Concede uma permissão a um usuário
     */
    @PostMapping("/users/{userId}/permissions/{permissionId}/grant")
    public ResponseEntity<UserPermission> grantPermission(
            @PathVariable UUID userId,
            @PathVariable Long permissionId,
            @RequestParam UUID tenantId,
            @RequestParam(required = false) String notes) {
        log.info("POST /api/permissions/users/{}/permissions/{}/grant - Concedendo permissão", userId, permissionId);
        
        try {
            // TODO: Pegar o usuário atual do contexto de segurança
            String grantedBy = "system"; // Temporário
            
            UserPermission userPermission = permissionService.grantPermission(
                    userId, permissionId, tenantId, grantedBy, notes);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(userPermission);
        } catch (RuntimeException e) {
            log.error("Erro ao conceder permissão: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Revoga uma permissão de um usuário
     */
    @DeleteMapping("/user/{userId}/permission/{permissionId}")
    public ResponseEntity<Void> revokePermission(
            @PathVariable UUID userId,
            @PathVariable Long permissionId,
            @RequestParam UUID tenantId) {
        log.info("DELETE /api/permissions/user/{}/permission/{} - Revogando permissão", userId, permissionId);
        
        try {
            // TODO: Pegar o usuário atual do contexto de segurança
            String deletedBy = "system"; // Temporário
            
            permissionService.removeUserPermission(userId, permissionId, tenantId, deletedBy);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Erro ao revogar permissão: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Verifica se usuário tem uma permissão específica
     */
    @GetMapping("/users/{userId}/check-permission/{permissionKey}")
    public ResponseEntity<Boolean> checkPermission(
            @PathVariable UUID userId,
            @PathVariable String permissionKey,
            @RequestParam UUID tenantId) {
        log.info("GET /api/permissions/users/{}/check-permission/{} - Verificando permissão", userId, permissionKey);
        boolean hasPermission = permissionService.hasPermission(userId, permissionKey, tenantId);
        return ResponseEntity.ok(hasPermission);
    }

    /**
     * Pesquisa permissões por texto
     */
    @GetMapping("/search")
    public ResponseEntity<List<Permission>> searchPermissions(@RequestParam String query) {
        log.info("GET /api/permissions/search?query={} - Pesquisando permissões", query);
        List<Permission> permissions = permissionService.searchPermissions(query);
        return ResponseEntity.ok(permissions);
    }
}
