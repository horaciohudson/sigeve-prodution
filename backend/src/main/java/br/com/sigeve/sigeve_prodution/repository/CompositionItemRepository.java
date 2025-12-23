package br.com.sigeve.sigeve_prodution.repository;

import br.com.sigeve.sigeve_prodution.model.CompositionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CompositionItemRepository extends JpaRepository<CompositionItem, UUID> {

    /**
     * Busca itens de uma composição (não deletados)
     */
    List<CompositionItem> findByCompositionIdAndDeletedAtIsNull(UUID compositionId);

    /**
     * Busca itens ordenados por sequência
     */
    List<CompositionItem> findByCompositionIdAndDeletedAtIsNullOrderBySequence(UUID compositionId);
}
