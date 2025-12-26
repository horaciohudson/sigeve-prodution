package br.com.sigeve.sigeve_prodution.repository;

import br.com.sigeve.sigeve_prodution.model.RawMaterialStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RawMaterialStockRepository extends JpaRepository<RawMaterialStock, UUID> {

    /**
     * Busca estoque por matéria-prima
     */
    Optional<RawMaterialStock> findByRawMaterialId(UUID rawMaterialId);

    /**
     * Busca estoque por matéria-prima e warehouse
     */
    Optional<RawMaterialStock> findByRawMaterialIdAndWarehouseId(UUID rawMaterialId, UUID warehouseId);

    /**
     * Busca estoque por empresa, matéria-prima e warehouse
     */
    Optional<RawMaterialStock> findByCompanyIdAndRawMaterialIdAndWarehouseId(UUID companyId, UUID rawMaterialId, UUID warehouseId);

    /**
     * Busca todos os estoques de uma empresa
     */
    List<RawMaterialStock> findByCompanyId(UUID companyId);

    /**
     * Busca estoques com quantidade disponível baixa
     */
    List<RawMaterialStock> findByCompanyIdAndAvailableQuantityLessThan(UUID companyId, java.math.BigDecimal quantity);
}
