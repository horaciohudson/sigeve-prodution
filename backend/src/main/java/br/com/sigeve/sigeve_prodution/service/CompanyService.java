package br.com.sigeve.sigeve_prodution.service;

import br.com.sigeve.sigeve_prodution.dto.CompanyDTO;
import br.com.sigeve.sigeve_prodution.mapper.CompanyMapper;
import br.com.sigeve.sigeve_prodution.model.Company;
import br.com.sigeve.sigeve_prodution.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;

    @Transactional(readOnly = true)
    public Page<CompanyDTO> findByTenant(UUID tenantId, Pageable pageable) {
        log.debug("Buscando empresas para tenant: {}", tenantId);
        Page<Company> companies = companyRepository.findByTenantIdAndIsActiveTrue(tenantId, pageable);
        return companies.map(companyMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public Optional<CompanyDTO> findById(UUID id, UUID tenantId) {
        log.debug("Buscando empresa por ID: {} para tenant: {}", id, tenantId);
        
        Optional<Company> company = companyRepository.findByIdAndTenantIdAndIsActiveTrue(id, tenantId);
        return company.map(companyMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public Optional<CompanyDTO> findByCnpj(String cnpj, UUID tenantId) {
        log.debug("Buscando empresa por CNPJ: {} para tenant: {}", cnpj, tenantId);
        
        Optional<Company> company = companyRepository.findByCnpjAndTenantIdAndIsActiveTrue(cnpj, tenantId);
        return company.map(companyMapper::toDTO);
    }

    public CompanyDTO create(CompanyDTO companyDTO, UUID tenantId, String createdBy) {
        log.debug("Criando nova empresa para tenant: {}", tenantId);
        
        validateCompanyData(companyDTO, tenantId, null);
        
        Company company = companyMapper.toEntity(companyDTO);
        company.setTenantId(tenantId);
        company.setCreatedBy(createdBy);
        
        Company savedCompany = companyRepository.save(company);
        
        log.info("Empresa criada com sucesso: {} para tenant: {}", savedCompany.getId(), tenantId);
        return companyMapper.toDTO(savedCompany);
    }

    public CompanyDTO update(UUID id, CompanyDTO companyDTO, UUID tenantId, String updatedBy) {
        log.debug("Atualizando empresa: {} para tenant: {}", id, tenantId);
        
        Company existingCompany = companyRepository.findByIdAndTenantIdAndIsActiveTrue(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));
        
        validateCompanyData(companyDTO, tenantId, id);
        
        companyMapper.updateEntityFromDTO(companyDTO, existingCompany);
        existingCompany.setUpdatedBy(updatedBy);
        
        Company updatedCompany = companyRepository.save(existingCompany);
        
        log.info("Empresa atualizada com sucesso: {} para tenant: {}", id, tenantId);
        return companyMapper.toDTO(updatedCompany);
    }

    public void delete(UUID id, UUID tenantId, String deletedBy) {
        log.debug("Removendo empresa: {} para tenant: {}", id, tenantId);
        
        Company company = companyRepository.findByIdAndTenantIdAndIsActiveTrue(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));
        
        company.setIsActive(false);
        company.setUpdatedBy(deletedBy);
        companyRepository.save(company);
        
        log.info("Empresa removida com sucesso: {} para tenant: {}", id, tenantId);
    }

    @Transactional(readOnly = true)
    public Page<CompanyDTO> searchByText(String text, UUID tenantId, Pageable pageable) {
        log.debug("Buscando empresas por texto: '{}' para tenant: {}", text, tenantId);
        
        String searchText = "%" + text + "%";
        Page<Company> companies = companyRepository.findByTextSearch(tenantId, searchText, pageable);
        return companies.map(companyMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<CompanyDTO> findSuppliers(UUID tenantId, Pageable pageable) {
        Page<Company> suppliers = companyRepository.findBySupplierFlagTrueAndTenantIdAndIsActiveTrue(tenantId, pageable);
        return suppliers.map(companyMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<CompanyDTO> findCustomers(UUID tenantId, Pageable pageable) {
        Page<Company> customers = companyRepository.findByCustomerFlagTrueAndTenantIdAndIsActiveTrue(tenantId, pageable);
        return customers.map(companyMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<CompanyDTO> findTransporters(UUID tenantId, Pageable pageable) {
        Page<Company> transporters = companyRepository.findByTransporterFlagTrueAndTenantIdAndIsActiveTrue(tenantId, pageable);
        return transporters.map(companyMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public List<CompanyDTO> findSuppliersSimple(UUID tenantId) {
        List<Company> suppliers = companyRepository.findSuppliersSimple(tenantId);
        return suppliers.stream().map(companyMapper::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<CompanyDTO> findCustomersSimple(UUID tenantId) {
        List<Company> customers = companyRepository.findCustomersSimple(tenantId);
        return customers.stream().map(companyMapper::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<CompanyDTO> findTransportersSimple(UUID tenantId) {
        List<Company> transporters = companyRepository.findTransportersSimple(tenantId);
        return transporters.stream().map(companyMapper::toDTO).toList();
    }

    private void validateCompanyData(CompanyDTO companyDTO, UUID tenantId, UUID excludeId) {
        if (companyDTO.getCnpj() != null && !companyDTO.getCnpj().trim().isEmpty()) {
            Optional<Company> existingByCnpj = companyRepository.findByCnpjAndTenantIdAndIsActiveTrue(
                    companyDTO.getCnpj(), tenantId);
            
            if (existingByCnpj.isPresent()) {
                if (excludeId == null || !existingByCnpj.get().getId().equals(excludeId)) {
                    throw new RuntimeException("Já existe uma empresa com este CNPJ neste sistema");
                }
            }
        }
        
        if (companyDTO.getCorporateName() != null && !companyDTO.getCorporateName().trim().isEmpty()) {
            boolean existsByCorporateName = companyRepository.existsByCorporateNameAndTenantIdAndIsActiveTrue(
                    companyDTO.getCorporateName(), tenantId);
            
            if (existsByCorporateName && excludeId == null) {
                throw new RuntimeException("Já existe uma empresa com esta razão social neste sistema");
            }
        }
    }
}
