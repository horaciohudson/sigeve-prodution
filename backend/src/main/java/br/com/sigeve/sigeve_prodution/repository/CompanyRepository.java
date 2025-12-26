package br.com.sigeve.sigeve_prodution.repository;

import br.com.sigeve.sigeve_prodution.model.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompanyRepository extends JpaRepository<Company, UUID> {

    Page<Company> findByTenantIdAndIsActiveTrue(UUID tenantId, Pageable pageable);

    Optional<Company> findByIdAndTenantIdAndIsActiveTrue(UUID id, UUID tenantId);

    Optional<Company> findByCnpjAndTenantIdAndIsActiveTrue(String cnpj, UUID tenantId);

    boolean existsByCnpjAndTenantIdAndIsActiveTrue(String cnpj, UUID tenantId);

    boolean existsByCorporateNameAndTenantIdAndIsActiveTrue(String corporateName, UUID tenantId);

    @Query("SELECT c FROM Company c WHERE c.tenantId = :tenantId AND c.isActive = true AND " +
           "(LOWER(c.corporateName) LIKE LOWER(:text) OR " +
           "LOWER(c.tradeName) LIKE LOWER(:text) OR " +
           "c.cnpj LIKE :text)")
    Page<Company> findByTextSearch(@Param("tenantId") UUID tenantId, @Param("text") String text, Pageable pageable);

    Page<Company> findBySupplierFlagTrueAndTenantIdAndIsActiveTrue(UUID tenantId, Pageable pageable);

    Page<Company> findByCustomerFlagTrueAndTenantIdAndIsActiveTrue(UUID tenantId, Pageable pageable);

    Page<Company> findByTransporterFlagTrueAndTenantIdAndIsActiveTrue(UUID tenantId, Pageable pageable);

    @Query("SELECT c FROM Company c WHERE c.tenantId = :tenantId AND c.isActive = true AND c.supplierFlag = true ORDER BY c.corporateName")
    List<Company> findSuppliersSimple(@Param("tenantId") UUID tenantId);

    @Query("SELECT c FROM Company c WHERE c.tenantId = :tenantId AND c.isActive = true AND c.customerFlag = true ORDER BY c.corporateName")
    List<Company> findCustomersSimple(@Param("tenantId") UUID tenantId);

    @Query("SELECT c FROM Company c WHERE c.tenantId = :tenantId AND c.isActive = true AND c.transporterFlag = true ORDER BY c.corporateName")
    List<Company> findTransportersSimple(@Param("tenantId") UUID tenantId);

    @Query("SELECT c FROM Company c WHERE c.tenantId = :tenantId AND c.isActive = true ORDER BY c.corporateName")
    List<Company> findAllActiveOrderedByName(@Param("tenantId") UUID tenantId);
}
