package br.com.sigeve.sigeve_prodution.repository;

import br.com.sigeve.sigeve_prodution.model.RawMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RawMaterialRepository extends JpaRepository<RawMaterial, UUID> {

    /**
     * Busca todas as matérias-primas de uma empresa (não deletadas)
     */
    List<RawMaterial> findByCompanyIdAndDeletedAtIsNull(UUID companyId);

    /**
     * Busca matéria-prima por código em uma empresa
     */
    Optional<RawMaterial> findByCompanyIdAndCodeAndDeletedAtIsNull(UUID companyId, String code);

    /**
     * Busca matérias-primas ativas de uma empresa
     */
    List<RawMaterial> findByCompanyIdAndIsActiveTrueAndDeletedAtIsNull(UUID companyId);

    /**
     * Busca matéria-prima por ID (não deletada)
     */
    Optional<RawMaterial> findByIdAndDeletedAtIsNull(UUID id);

    /**
     * Busca matérias-primas por categoria
     */
    List<RawMaterial> findByCategoryIdAndDeletedAtIsNull(UUID categoryId);

    /**
     * Busca matérias-primas por fornecedor
     */
    List<RawMaterial> findBySupplierIdAndDeletedAtIsNull(UUID supplierId);

    /**
     * Busca matérias-primas por tenant
     */
    List<RawMaterial> findByTenantIdAndDeletedAtIsNull(UUID tenantId);
}
