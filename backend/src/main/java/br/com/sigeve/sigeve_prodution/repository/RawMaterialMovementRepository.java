package br.com.sigeve.sigeve_prodution.repository;

import br.com.sigeve.sigeve_prodution.model.RawMaterialMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RawMaterialMovementRepository extends JpaRepository<RawMaterialMovement, UUID> {

    /**
     * Busca movimentos de uma empresa ordenados por data
     */
    List<RawMaterialMovement> findByCompanyIdOrderByMovementDateDesc(UUID companyId);

    /**
     * Busca movimentos de uma matéria-prima
     */
    List<RawMaterialMovement> findByRawMaterialIdOrderByMovementDateDesc(UUID rawMaterialId);

    /**
     * Busca movimentos por origem
     */
    List<RawMaterialMovement> findByOriginId(UUID originId);
}
