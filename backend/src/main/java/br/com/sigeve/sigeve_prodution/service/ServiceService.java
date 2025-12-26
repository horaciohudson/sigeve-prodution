package br.com.sigeve.sigeve_prodution.service;

import br.com.sigeve.sigeve_prodution.dto.CreateServiceDTO;
import br.com.sigeve.sigeve_prodution.dto.ServiceDTO;
import br.com.sigeve.sigeve_prodution.dto.UpdateServiceDTO;
import br.com.sigeve.sigeve_prodution.model.Service;
import br.com.sigeve.sigeve_prodution.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ServiceService {

    private final ServiceRepository serviceRepository;

    public List<ServiceDTO> findAllByCompany(UUID companyId) {
        log.info("Buscando todos os serviços da empresa: {}", companyId);
        return serviceRepository.findByCompanyIdAndDeletedAtIsNullOrderByCode(companyId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ServiceDTO> findByActiveStatus(UUID companyId, Boolean isActive) {
        log.info("Buscando serviços da empresa {} com status ativo: {}", companyId, isActive);
        return serviceRepository.findByCompanyIdAndIsActiveAndDeletedAtIsNullOrderByCode(companyId, isActive).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<ServiceDTO> findById(UUID id) {
        log.info("Buscando serviço por ID: {}", id);
        return serviceRepository.findById(id)
                .filter(service -> service.getDeletedAt() == null)
                .map(this::convertToDTO);
    }

    public Optional<ServiceDTO> findByCode(UUID companyId, String code) {
        log.info("Buscando serviço por código: {} na empresa: {}", code, companyId);
        return serviceRepository.findByCompanyIdAndCodeAndDeletedAtIsNull(companyId, code)
                .map(this::convertToDTO);
    }

    @Transactional
    public ServiceDTO create(CreateServiceDTO request, String createdBy) {
        log.info("Criando novo serviço: {} por usuário: {}", request.getName(), createdBy);

        // Verifica se já existe serviço com o mesmo código
        Optional<Service> existing = serviceRepository.findByCompanyIdAndCodeAndDeletedAtIsNull(
                request.getCompanyId(), request.getCode());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Já existe um serviço com o código: " + request.getCode());
        }

        Service service = new Service();
        service.setTenantId(request.getTenantId());
        service.setCompanyId(request.getCompanyId());
        service.setCode(request.getCode());
        service.setName(request.getName());
        service.setDescription(request.getDescription());
        service.setUnitPrice(request.getUnitPrice());
        service.setCostCenterId(request.getCostCenterId());
        service.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        service.setNotes(request.getNotes());
        service.setCreatedBy(createdBy);
        service.setCreatedAt(LocalDateTime.now());

        Service saved = serviceRepository.save(service);
        log.info("Serviço criado com sucesso: {}", saved.getId());

        return convertToDTO(saved);
    }

    @Transactional
    public ServiceDTO update(UUID id, UpdateServiceDTO request, String updatedBy) {
        log.info("Atualizando serviço: {} por usuário: {}", id, updatedBy);

        Service service = serviceRepository.findById(id)
                .filter(s -> s.getDeletedAt() == null)
                .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado: " + id));

        if (request.getCode() != null) {
            service.setCode(request.getCode());
        }
        if (request.getName() != null) {
            service.setName(request.getName());
        }
        if (request.getDescription() != null) {
            service.setDescription(request.getDescription());
        }
        if (request.getUnitPrice() != null) {
            service.setUnitPrice(request.getUnitPrice());
        }
        if (request.getCostCenterId() != null) {
            service.setCostCenterId(request.getCostCenterId());
        }
        if (request.getIsActive() != null) {
            service.setIsActive(request.getIsActive());
        }
        if (request.getNotes() != null) {
            service.setNotes(request.getNotes());
        }

        service.setUpdatedBy(updatedBy);
        service.setUpdatedAt(LocalDateTime.now());

        Service saved = serviceRepository.save(service);
        log.info("Serviço atualizado com sucesso: {}", saved.getId());

        return convertToDTO(saved);
    }

    @Transactional
    public void delete(UUID id, String deletedBy) {
        log.info("Deletando serviço: {} por usuário: {}", id, deletedBy);

        Service service = serviceRepository.findById(id)
                .filter(s -> s.getDeletedAt() == null)
                .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado: " + id));

        service.setDeletedBy(deletedBy);
        service.setDeletedAt(LocalDateTime.now());

        serviceRepository.save(service);
        log.info("Serviço deletado com sucesso: {}", id);
    }

    @Transactional
    public ServiceDTO activate(UUID id, String updatedBy) {
        log.info("Ativando serviço: {}", id);
        return updateActiveStatus(id, true, updatedBy);
    }

    @Transactional
    public ServiceDTO deactivate(UUID id, String updatedBy) {
        log.info("Desativando serviço: {}", id);
        return updateActiveStatus(id, false, updatedBy);
    }

    private ServiceDTO updateActiveStatus(UUID id, Boolean isActive, String updatedBy) {
        Service service = serviceRepository.findById(id)
                .filter(s -> s.getDeletedAt() == null)
                .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado: " + id));

        service.setIsActive(isActive);
        service.setUpdatedBy(updatedBy);
        service.setUpdatedAt(LocalDateTime.now());

        Service saved = serviceRepository.save(service);
        return convertToDTO(saved);
    }

    private ServiceDTO convertToDTO(Service service) {
        ServiceDTO dto = new ServiceDTO();
        dto.setId(service.getId());
        dto.setTenantId(service.getTenantId());
        dto.setCompanyId(service.getCompanyId());
        dto.setCode(service.getCode());
        dto.setName(service.getName());
        dto.setDescription(service.getDescription());
        dto.setUnitPrice(service.getUnitPrice());
        dto.setCostCenterId(service.getCostCenterId());
        dto.setIsActive(service.getIsActive());
        dto.setNotes(service.getNotes());
        dto.setCreatedAt(service.getCreatedAt());
        dto.setCreatedBy(service.getCreatedBy());
        dto.setUpdatedAt(service.getUpdatedAt());
        dto.setUpdatedBy(service.getUpdatedBy());
        return dto;
    }
}
