package br.com.sigeve.sigeve_prodution.repository;

import br.com.sigeve.sigeve_prodution.model.ProductionProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductionProductRepository extends JpaRepository<ProductionProduct, UUID> {

    /**
     * Busca todos os produtos de produção de uma empresa (não deletados)
     */
    List<ProductionProduct> findByCompanyIdAndDeletedAtIsNull(UUID companyId);

    /**
     * Busca produto por SKU em uma empresa
     */
    Optional<ProductionProduct> findByCompanyIdAndSkuAndDeletedAtIsNull(UUID companyId, String sku);

    /**
     * Busca produtos ativos de uma empresa
     */
    List<ProductionProduct> findByCompanyIdAndIsActiveTrueAndDeletedAtIsNull(UUID companyId);

    /**
     * Busca produto por ID (não deletado)
     */
    Optional<ProductionProduct> findByIdAndDeletedAtIsNull(UUID id);

    /**
     * Busca produtos por tenant
     */
    List<ProductionProduct> findByTenantIdAndDeletedAtIsNull(UUID tenantId);
}
