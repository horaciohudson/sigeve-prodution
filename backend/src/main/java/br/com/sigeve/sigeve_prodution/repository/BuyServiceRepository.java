package br.com.sigeve.sigeve_prodution.repository;

import br.com.sigeve.sigeve_prodution.enums.BuyServiceStatus;
import br.com.sigeve.sigeve_prodution.model.BuyService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BuyServiceRepository extends JpaRepository<BuyService, UUID> {

    /**
     * Busca todas as compras de serviço de uma empresa (não deletadas)
     */
    List<BuyService> findByCompanyIdAndDeletedAtIsNull(UUID companyId);

    /**
     * Busca por status
     */
    List<BuyService> findByCompanyIdAndStatusAndDeletedAtIsNull(UUID companyId, BuyServiceStatus status);

    /**
     * Busca por código
     */
    Optional<BuyService> findByCompanyIdAndCodeAndDeletedAtIsNull(UUID companyId, String code);

    /**
     * Busca por ID (não deletada)
     */
    Optional<BuyService> findByIdAndDeletedAtIsNull(UUID id);

    /**
     * Busca por fornecedor
     */
    List<BuyService> findBySupplierIdAndDeletedAtIsNull(UUID supplierId);
}
