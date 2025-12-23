package br.com.sigeve.sigeve_prodution.repository;

import br.com.sigeve.sigeve_prodution.model.ProductionStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductionStepRepository extends JpaRepository<ProductionStep, UUID> {

    /**
     * Busca todas as etapas de uma empresa (não deletadas) ordenadas por sequência
     */
    List<ProductionStep> findByCompanyIdAndDeletedAtIsNullOrderBySequence(UUID companyId);

    /**
     * Busca etapas ativas
     */
    List<ProductionStep> findByCompanyIdAndIsActiveTrueAndDeletedAtIsNullOrderBySequence(UUID companyId);

    /**
     * Busca etapa por ID (não deletada)
     */
    Optional<ProductionStep> findByIdAndDeletedAtIsNull(UUID id);
}
