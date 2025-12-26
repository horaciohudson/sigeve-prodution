package br.com.sigeve.sigeve_prodution.repository;


import br.com.sigeve.sigeve_prodution.model.Tenant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, UUID> {

    Optional<Tenant> findByCode(String code);
    
    @Query(value = "SELECT * FROM tab_tenants WHERE code = :code AND status = :status", nativeQuery = true)
    Optional<Tenant> findByCodeAndStatus(@Param("code") String code, @Param("status") String status);
    
    @Query(value = "SELECT * FROM tab_tenants WHERE status = :status ORDER BY name", nativeQuery = true)
    List<Tenant> findByStatusOrderByName(@Param("status") String status);
    
    List<Tenant> findAllByOrderByName();
}