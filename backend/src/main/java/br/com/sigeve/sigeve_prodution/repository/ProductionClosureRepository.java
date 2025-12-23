package br.com.sigeve.sigeve_prodution.repository;

import br.com.sigeve.sigeve_prodution.model.ProductionClosure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductionClosureRepository extends JpaRepository<ProductionClosure, UUID> {

    /**
     * Busca fechamento por ordem de produção
     */
    Optional<ProductionClosure> findByProductionOrderId(UUID productionOrderId);

    /**
     * Busca fechamentos de uma empresa
     */
    List<ProductionClosure> findByCompanyId(UUID companyId);

    /**
     * Busca fechamentos por status de exportação
     */
    List<ProductionClosure> findByCompanyIdAndExportedToFinancial(UUID companyId, Boolean exported);
}
