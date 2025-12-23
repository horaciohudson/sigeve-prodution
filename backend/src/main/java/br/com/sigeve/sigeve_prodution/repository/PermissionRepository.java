package br.com.sigeve.sigeve_prodution.repository;


import br.com.sigeve.sigeve_prodution.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    /**
     * Busca permissão por chave
     */
    Optional<Permission> findByPermissionKey(String permissionKey);

    /**
     * Verifica se existe permissão com a chave
     */
    boolean existsByPermissionKey(String permissionKey);

    /**
     * Lista todas as permissões ativas (não deletadas)
     */
    @Query("SELECT p FROM Permission p WHERE p.deletedAt IS NULL ORDER BY p.permissionKey")
    List<Permission> findAllActive();

    /**
     * Busca permissões por nível
     */
    @Query("SELECT p FROM Permission p WHERE p.level = :level AND p.deletedAt IS NULL ORDER BY p.permissionKey")
    List<Permission> findByLevel(@Param("level") Integer level);

    /**
     * Busca permissões por nível máximo
     */
    @Query("SELECT p FROM Permission p WHERE p.level <= :maxLevel AND p.deletedAt IS NULL ORDER BY p.level, p.permissionKey")
    List<Permission> findByLevelLessThanEqual(@Param("maxLevel") Integer maxLevel);

    /**
     * Busca permissões que contêm texto na chave ou descrição
     */
    @Query("SELECT p FROM Permission p WHERE " +
           "(LOWER(p.permissionKey) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "p.deletedAt IS NULL ORDER BY p.permissionKey")
    List<Permission> findByKeyOrDescriptionContaining(@Param("search") String search);

    /**
     * Busca permissões por módulo (baseado no prefixo da chave)
     */
    @Query("SELECT p FROM Permission p WHERE " +
           "LOWER(p.permissionKey) LIKE LOWER(CONCAT(:module, '%')) AND " +
           "p.deletedAt IS NULL ORDER BY p.permissionKey")
    List<Permission> findByModule(@Param("module") String module);
}