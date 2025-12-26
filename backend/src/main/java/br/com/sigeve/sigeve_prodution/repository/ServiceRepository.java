package br.com.sigeve.sigeve_prodution.repository;

import br.com.sigeve.sigeve_prodution.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ServiceRepository extends JpaRepository<Service, UUID> {
    
    /**
     * Busca todos os serviços de uma empresa que não foram deletados
     */
    List<Service> findByCompanyIdAndDeletedAtIsNullOrderByCode(UUID companyId);
    
    /**
     * Busca serviços ativos de uma empresa
     */
    List<Service> findByCompanyIdAndIsActiveAndDeletedAtIsNullOrderByCode(UUID companyId, Boolean isActive);
    
    /**
     * Busca serviço por código e empresa
     */
    Optional<Service> findByCompanyIdAndCodeAndDeletedAtIsNull(UUID companyId, String code);
}
