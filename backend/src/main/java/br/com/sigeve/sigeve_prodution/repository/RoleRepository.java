package br.com.sigeve.sigeve_prodution.repository;

import br.com.sigeve.sigeve_prodution.model.Role;
import br.com.sigeve.sigeve_prodution.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Busca role por tipo
     */
    Optional<Role> findByRole(RoleType roleType);

    /**
     * Busca roles por IDs
     */
    List<Role> findByIdIn(Set<Long> ids);

    /**
     * Verifica se existe role por tipo
     */
    boolean existsByRole(RoleType roleType);
}