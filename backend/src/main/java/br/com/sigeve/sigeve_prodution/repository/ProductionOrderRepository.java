package br.com.sigeve.sigeve_prodution.repository;

import br.com.sigeve.sigeve_prodution.enums.ProductionOrderStatus;
import br.com.sigeve.sigeve_prodution.model.ProductionOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductionOrderRepository extends JpaRepository<ProductionOrder, UUID> {

    /**
     * Busca todas as ordens de produção de uma empresa (não deletadas)
     */
    List<ProductionOrder> findByCompanyIdAndDeletedAtIsNull(UUID companyId);

    /**
     * Busca ordens por status
     */
    List<ProductionOrder> findByCompanyIdAndStatusAndDeletedAtIsNull(UUID companyId, ProductionOrderStatus status);

    /**
     * Busca ordem por código
     */
    Optional<ProductionOrder> findByCompanyIdAndCodeAndDeletedAtIsNull(UUID companyId, String code);

    /**
     * Busca ordem por ID (não deletada)
     */
    Optional<ProductionOrder> findByIdAndDeletedAtIsNull(UUID id);

    /**
     * Busca ordens por produto
     */
    List<ProductionOrder> findByProductIdAndDeletedAtIsNull(UUID productId);

    /**
     * Busca ordens por cliente
     */
    List<ProductionOrder> findByCustomerIdAndDeletedAtIsNull(UUID customerId);

    /**
     * Busca ordens por pedido
     */
    List<ProductionOrder> findByOrderIdAndDeletedAtIsNull(UUID orderId);
}
