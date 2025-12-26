package br.com.sigeve.sigeve_prodution.mapper;

import br.com.sigeve.sigeve_prodution.dto.CompanyDTO;
import br.com.sigeve.sigeve_prodution.model.Company;
import org.springframework.stereotype.Component;

/**
 * Mapper para conversão entre Company e CompanyDTO
 */
@Component
public class CompanyMapper {

    public CompanyDTO toDTO(Company company) {
        if (company == null) {
            return null;
        }

        return CompanyDTO.builder()
                .id(company.getId())
                .tenantId(company.getTenantId())
                .corporateName(company.getCorporateName())
                .tradeName(company.getTradeName())
                .cnpj(company.getCnpj())
                .stateRegistration(company.getStateRegistration())
                .municipalRegistration(company.getMunicipalRegistration())
                .phone(company.getPhone())
                .mobile(company.getMobile())
                .email(company.getEmail())
                .whatsapp(company.getWhatsapp())
                .issRate(company.getIssRate())
                .funruralRate(company.getFunruralRate())
                .manager(company.getManager())
                .factory(company.getFactory())
                .supplierFlag(company.getSupplierFlag())
                .customerFlag(company.getCustomerFlag())
                .transporterFlag(company.getTransporterFlag())
                .isActive(company.getIsActive())
                .createdAt(company.getCreatedAt())
                .updatedAt(company.getUpdatedAt())
                .createdBy(company.getCreatedBy())
                .updatedBy(company.getUpdatedBy())
                .build();
    }

    public Company toEntity(CompanyDTO companyDTO) {
        if (companyDTO == null) {
            return null;
        }

        return Company.builder()
                .corporateName(companyDTO.getCorporateName())
                .tradeName(companyDTO.getTradeName())
                .cnpj(companyDTO.getCnpj())
                .stateRegistration(companyDTO.getStateRegistration())
                .municipalRegistration(companyDTO.getMunicipalRegistration())
                .phone(companyDTO.getPhone())
                .mobile(companyDTO.getMobile())
                .email(companyDTO.getEmail())
                .whatsapp(companyDTO.getWhatsapp())
                .issRate(companyDTO.getIssRate())
                .funruralRate(companyDTO.getFunruralRate())
                .manager(companyDTO.getManager())
                .factory(companyDTO.getFactory())
                .supplierFlag(companyDTO.getSupplierFlag())
                .customerFlag(companyDTO.getCustomerFlag())
                .transporterFlag(companyDTO.getTransporterFlag())
                .isActive(companyDTO.getIsActive())
                .build();
    }

    public void updateEntityFromDTO(CompanyDTO companyDTO, Company company) {
        if (companyDTO == null || company == null) {
            return;
        }

        if (companyDTO.getCorporateName() != null) {
            company.setCorporateName(companyDTO.getCorporateName());
        }
        if (companyDTO.getTradeName() != null) {
            company.setTradeName(companyDTO.getTradeName());
        }
        if (companyDTO.getCnpj() != null) {
            company.setCnpj(companyDTO.getCnpj());
        }
        if (companyDTO.getStateRegistration() != null) {
            company.setStateRegistration(companyDTO.getStateRegistration());
        }
        if (companyDTO.getMunicipalRegistration() != null) {
            company.setMunicipalRegistration(companyDTO.getMunicipalRegistration());
        }
        if (companyDTO.getPhone() != null) {
            company.setPhone(companyDTO.getPhone());
        }
        if (companyDTO.getMobile() != null) {
            company.setMobile(companyDTO.getMobile());
        }
        if (companyDTO.getEmail() != null) {
            company.setEmail(companyDTO.getEmail());
        }
        if (companyDTO.getWhatsapp() != null) {
            company.setWhatsapp(companyDTO.getWhatsapp());
        }
        if (companyDTO.getIssRate() != null) {
            company.setIssRate(companyDTO.getIssRate());
        }
        if (companyDTO.getFunruralRate() != null) {
            company.setFunruralRate(companyDTO.getFunruralRate());
        }
        if (companyDTO.getManager() != null) {
            company.setManager(companyDTO.getManager());
        }
        if (companyDTO.getFactory() != null) {
            company.setFactory(companyDTO.getFactory());
        }
        if (companyDTO.getSupplierFlag() != null) {
            company.setSupplierFlag(companyDTO.getSupplierFlag());
        }
        if (companyDTO.getCustomerFlag() != null) {
            company.setCustomerFlag(companyDTO.getCustomerFlag());
        }
        if (companyDTO.getTransporterFlag() != null) {
            company.setTransporterFlag(companyDTO.getTransporterFlag());
        }
        if (companyDTO.getIsActive() != null) {
            company.setIsActive(companyDTO.getIsActive());
        }
    }
}
