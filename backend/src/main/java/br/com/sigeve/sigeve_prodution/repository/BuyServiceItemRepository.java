package br.com.sigeve.sigeve_prodution.repository;

import br.com.sigeve.sigeve_prodution.model.BuyServiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BuyServiceItemRepository extends JpaRepository<BuyServiceItem, UUID> {

    /**
     * Busca itens de uma compra de serviço (não deletados)
     */
    List<BuyServiceItem> findByBuyServiceIdAndDeletedAtIsNull(UUID buyServiceId);

    /**
     * Busca itens ordenados por sequência
     */
    List<BuyServiceItem> findByBuyServiceIdAndDeletedAtIsNullOrderBySequence(UUID buyServiceId);
}
