package br.com.sigeve.sigeve_prodution.repository;

import br.com.sigeve.sigeve_prodution.model.ProductionExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductionExecutionRepository extends JpaRepository<ProductionExecution, UUID> {

    /**
     * Busca execuções de uma ordem de produção (não deletadas)
     */
    List<ProductionExecution> findByProductionOrderIdAndDeletedAtIsNull(UUID productionOrderId);

    /**
     * Busca execuções de uma etapa
     */
    List<ProductionExecution> findByStepIdAndDeletedAtIsNull(UUID stepId);

    /**
     * Busca execuções de um funcionário
     */
    List<ProductionExecution> findByEmployeeIdAndDeletedAtIsNull(UUID employeeId);
}
