package br.com.sigeve.sigeve_prodution.repository;

import br.com.sigeve.sigeve_prodution.model.Composition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompositionRepository extends JpaRepository<Composition, UUID> {

    /**
     * Busca todas as composições de uma empresa (não deletadas)
     */
    List<Composition> findByCompanyIdAndDeletedAtIsNull(UUID companyId);

    /**
     * Busca composições por produto de produção
     */
    List<Composition> findByProductionProductIdAndDeletedAtIsNull(UUID productionProductId);

    /**
     * Busca composições ativas
     */
    List<Composition> findByCompanyIdAndIsActiveTrueAndDeletedAtIsNull(UUID companyId);

    /**
     * Busca composição por ID (não deletada)
     */
    Optional<Composition> findByIdAndDeletedAtIsNull(UUID id);

    /**
     * Busca composição por produto e versão
     */
    Optional<Composition> findByCompanyIdAndProductionProductIdAndVersionAndDeletedAtIsNull(
            UUID companyId, UUID productionProductId, Integer version);
}
