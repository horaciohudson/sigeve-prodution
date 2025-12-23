package br.com.sigeve.sigeve_prodution.repository;

import br.com.sigeve.sigeve_prodution.enums.ProductionCostType;
import br.com.sigeve.sigeve_prodution.model.ProductionCost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductionCostRepository extends JpaRepository<ProductionCost, UUID> {

    /**
     * Busca custos de uma ordem de produção (não deletados)
     */
    List<ProductionCost> findByProductionOrderIdAndDeletedAtIsNull(UUID productionOrderId);

    /**
     * Busca custos por tipo
     */
    List<ProductionCost> findByCompanyIdAndCostTypeAndDeletedAtIsNull(UUID companyId, ProductionCostType costType);

    /**
     * Busca custos de uma ordem por tipo
     */
    List<ProductionCost> findByProductionOrderIdAndCostTypeAndDeletedAtIsNull(UUID productionOrderId, ProductionCostType costType);
}
