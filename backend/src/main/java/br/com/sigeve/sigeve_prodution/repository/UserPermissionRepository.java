package br.com.sigeve.sigeve_prodution.repository;


import br.com.sigeve.sigeve_prodution.model.UserPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserPermissionRepository extends JpaRepository<UserPermission, Long> {

    /**
     * Busca permissões de um usuário em um tenant
     */
    @Query("SELECT up FROM UserPermission up " +
           "JOIN FETCH up.permission p " +
           "WHERE up.userId = :userId AND up.tenantId = :tenantId AND up.deletedAt IS NULL " +
           "ORDER BY p.permissionKey")
    List<UserPermission> findByUserIdAndTenantId(@Param("userId") UUID userId, @Param("tenantId") UUID tenantId);

    /**
     * Busca permissões concedidas de um usuário em um tenant
     */
    @Query("SELECT up FROM UserPermission up " +
           "JOIN FETCH up.permission p " +
           "WHERE up.userId = :userId AND up.tenantId = :tenantId AND up.granted = true AND up.deletedAt IS NULL " +
           "ORDER BY p.permissionKey")
    List<UserPermission> findGrantedByUserIdAndTenantId(@Param("userId") UUID userId, @Param("tenantId") UUID tenantId);

    /**
     * Busca permissões negadas de um usuário em um tenant
     */
    @Query("SELECT up FROM UserPermission up " +
           "JOIN FETCH up.permission p " +
           "WHERE up.userId = :userId AND up.tenantId = :tenantId AND up.granted = false AND up.deletedAt IS NULL " +
           "ORDER BY p.permissionKey")
    List<UserPermission> findDeniedByUserIdAndTenantId(@Param("userId") UUID userId, @Param("tenantId") UUID tenantId);

    /**
     * Busca uma permissão específica de um usuário
     */
    @Query("SELECT up FROM UserPermission up " +
           "WHERE up.userId = :userId AND up.permissionId = :permissionId AND up.tenantId = :tenantId AND up.deletedAt IS NULL")
    Optional<UserPermission> findByUserIdAndPermissionIdAndTenantId(
            @Param("userId") UUID userId, 
            @Param("permissionId") Long permissionId, 
            @Param("tenantId") UUID tenantId);

    /**
     * Verifica se usuário tem permissão específica concedida
     */
    @Query("SELECT COUNT(up) > 0 FROM UserPermission up " +
           "WHERE up.userId = :userId AND up.permissionId = :permissionId AND up.tenantId = :tenantId " +
           "AND up.granted = true AND up.deletedAt IS NULL")
    boolean hasGrantedPermission(@Param("userId") UUID userId, @Param("permissionId") Long permissionId, @Param("tenantId") UUID tenantId);

    /**
     * Verifica se usuário tem permissão específica negada
     */
    @Query("SELECT COUNT(up) > 0 FROM UserPermission up " +
           "WHERE up.userId = :userId AND up.permissionId = :permissionId AND up.tenantId = :tenantId " +
           "AND up.granted = false AND up.deletedAt IS NULL")
    boolean hasDeniedPermission(@Param("userId") UUID userId, @Param("permissionId") Long permissionId, @Param("tenantId") UUID tenantId);

    /**
     * Lista todos os usuários que têm uma permissão específica em um tenant
     */
    @Query("SELECT up FROM UserPermission up " +
           "JOIN FETCH up.user u " +
           "WHERE up.permissionId = :permissionId AND up.tenantId = :tenantId AND up.granted = true AND up.deletedAt IS NULL " +
           "ORDER BY u.fullName")
    List<UserPermission> findUsersWithPermission(@Param("permissionId") Long permissionId, @Param("tenantId") UUID tenantId);

    /**
     * Remove todas as permissões de um usuário em um tenant (soft delete)
     */
    @Modifying
    @Query("UPDATE UserPermission up SET up.deletedAt = CURRENT_TIMESTAMP, up.deletedBy = :deletedBy " +
           "WHERE up.userId = :userId AND up.tenantId = :tenantId AND up.deletedAt IS NULL")
    void softDeleteAllByUserIdAndTenantId(@Param("userId") UUID userId, @Param("tenantId") UUID tenantId, @Param("deletedBy") String deletedBy);

    /**
     * Remove uma permissão específica de um usuário (soft delete)
     */
    @Modifying
    @Query("UPDATE UserPermission up SET up.deletedAt = CURRENT_TIMESTAMP, up.deletedBy = :deletedBy " +
           "WHERE up.userId = :userId AND up.permissionId = :permissionId AND up.tenantId = :tenantId AND up.deletedAt IS NULL")
    void softDeleteByUserIdAndPermissionIdAndTenantId(
            @Param("userId") UUID userId, 
            @Param("permissionId") Long permissionId, 
            @Param("tenantId") UUID tenantId, 
            @Param("deletedBy") String deletedBy);

    /**
     * Lista permissões por chave de permissão
     */
    @Query("SELECT up FROM UserPermission up " +
           "JOIN FETCH up.permission p " +
           "JOIN FETCH up.user u " +
           "WHERE p.permissionKey = :permissionKey AND up.tenantId = :tenantId AND up.deletedAt IS NULL " +
           "ORDER BY u.fullName")
    List<UserPermission> findByPermissionKeyAndTenantId(@Param("permissionKey") String permissionKey, @Param("tenantId") UUID tenantId);
}