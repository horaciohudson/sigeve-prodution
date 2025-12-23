package br.com.sigeve.sigeve_prodution.service;

import br.com.sigeve.sigeve_prodution.dto.CreateProductionStepDTO;
import br.com.sigeve.sigeve_prodution.dto.ProductionStepDTO;
import br.com.sigeve.sigeve_prodution.dto.UpdateProductionStepDTO;
import br.com.sigeve.sigeve_prodution.model.ProductionStep;
import br.com.sigeve.sigeve_prodution.repository.ProductionStepRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductionStepService {

    private final ProductionStepRepository productionStepRepository;

    @Transactional(readOnly = true)
    public List<ProductionStepDTO> findAllByCompany(UUID companyId) {
        log.debug("Buscando todas as etapas da empresa: {}", companyId);
        return productionStepRepository.findByCompanyIdAndDeletedAtIsNullOrderBySequence(companyId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductionStepDTO> findActiveByCompany(UUID companyId) {
        log.debug("Buscando etapas ativas da empresa: {}", companyId);
        return productionStepRepository.findByCompanyIdAndIsActiveTrueAndDeletedAtIsNullOrderBySequence(companyId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ProductionStepDTO> findById(UUID id) {
        log.debug("Buscando etapa por ID: {}", id);
        return productionStepRepository.findByIdAndDeletedAtIsNull(id)
                .map(this::convertToDTO);
    }

    public ProductionStepDTO create(CreateProductionStepDTO request, String createdBy) {
        log.debug("Criando nova etapa de produção: {}", request.getName());

        ProductionStep step = new ProductionStep();
        step.setTenantId(request.getTenantId());
        step.setCompanyId(request.getCompanyId());
        step.setName(request.getName());
        step.setDescription(request.getDescription());
        step.setSequence(request.getSequence());
        step.setEstimatedTime(request.getEstimatedTime());
        step.setCostCenterId(request.getCostCenterId());
        step.setIsOutsourced(request.getIsOutsourced() != null ? request.getIsOutsourced() : false);
        step.setRequiresApproval(request.getRequiresApproval() != null ? request.getRequiresApproval() : false);
        step.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        step.setCreatedBy(createdBy);

        ProductionStep saved = productionStepRepository.save(step);
        log.info("Etapa de produção criada com sucesso: {}", saved.getId());

        return convertToDTO(saved);
    }

    public ProductionStepDTO update(UUID id, UpdateProductionStepDTO request, String updatedBy) {
        log.debug("Atualizando etapa de produção: {}", id);

        ProductionStep step = productionStepRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Etapa não encontrada: " + id));

        if (request.getName() != null) step.setName(request.getName());
        if (request.getDescription() != null) step.setDescription(request.getDescription());
        if (request.getSequence() != null) step.setSequence(request.getSequence());
        if (request.getEstimatedTime() != null) step.setEstimatedTime(request.getEstimatedTime());
        if (request.getCostCenterId() != null) step.setCostCenterId(request.getCostCenterId());
        if (request.getIsOutsourced() != null) step.setIsOutsourced(request.getIsOutsourced());
        if (request.getRequiresApproval() != null) step.setRequiresApproval(request.getRequiresApproval());
        if (request.getIsActive() != null) step.setIsActive(request.getIsActive());

        step.setUpdatedBy(updatedBy);
        step.setUpdatedAt(LocalDateTime.now());

        ProductionStep saved = productionStepRepository.save(step);
        log.info("Etapa de produção atualizada com sucesso: {}", saved.getId());

        return convertToDTO(saved);
    }

    public void delete(UUID id, String deletedBy) {
        log.debug("Deletando etapa de produção: {}", id);

        ProductionStep step = productionStepRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Etapa não encontrada: " + id));

        step.setDeletedAt(LocalDateTime.now());
        step.setDeletedBy(deletedBy);

        productionStepRepository.save(step);
        log.info("Etapa de produção deletada com sucesso: {}", id);
    }

    private ProductionStepDTO convertToDTO(ProductionStep step) {
        ProductionStepDTO dto = new ProductionStepDTO();
        dto.setId(step.getId());
        dto.setTenantId(step.getTenantId());
        dto.setCompanyId(step.getCompanyId());
        dto.setName(step.getName());
        dto.setDescription(step.getDescription());
        dto.setSequence(step.getSequence());
        dto.setEstimatedTime(step.getEstimatedTime());
        dto.setCostCenterId(step.getCostCenterId());
        dto.setIsOutsourced(step.getIsOutsourced());
        dto.setRequiresApproval(step.getRequiresApproval());
        dto.setIsActive(step.getIsActive());
        dto.setCreatedAt(step.getCreatedAt() != null ? step.getCreatedAt().atOffset(OffsetDateTime.now().getOffset()) : null);
        dto.setCreatedBy(step.getCreatedBy());
        dto.setUpdatedAt(step.getUpdatedAt() != null ? step.getUpdatedAt().atOffset(OffsetDateTime.now().getOffset()) : null);
        dto.setUpdatedBy(step.getUpdatedBy());
        return dto;
    }
}
