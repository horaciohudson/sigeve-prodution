package br.com.sigeve.sigeve_prodution.repository;



import java.util.Optional;
import java.util.UUID;

import br.com.sigeve.sigeve_prodution.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    // Para login simples
    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameAndTenantId(String username, UUID tenantId);
}
