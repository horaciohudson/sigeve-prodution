package br.com.sigeve.sigeve_prodution.service;

import br.com.sigeve.sigeve_prodution.dto.CreateProductionExecutionDTO;
import br.com.sigeve.sigeve_prodution.dto.ProductionExecutionDTO;
import br.com.sigeve.sigeve_prodution.dto.UpdateProductionExecutionDTO;
import br.com.sigeve.sigeve_prodution.model.ProductionExecution;
import br.com.sigeve.sigeve_prodution.repository.ProductionExecutionRepository;
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
public class ProductionExecutionService {

    private final ProductionExecutionRepository productionExecutionRepository;

    @Transactional(readOnly = true)
    public List<ProductionExecutionDTO> findByProductionOrder(UUID productionOrderId) {
        log.debug("Buscando execuções da ordem: {}", productionOrderId);
        return productionExecutionRepository.findByProductionOrderIdAndDeletedAtIsNull(productionOrderId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductionExecutionDTO> findByStep(UUID stepId) {
        log.debug("Buscando execuções da etapa: {}", stepId);
        return productionExecutionRepository.findByStepIdAndDeletedAtIsNull(stepId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ProductionExecutionDTO> findById(UUID id) {
        log.debug("Buscando execução por ID: {}", id);
        return productionExecutionRepository.findById(id)
                .map(this::convertToDTO);
    }

    public ProductionExecutionDTO create(CreateProductionExecutionDTO request, String createdBy) {
        log.debug("Criando nova execução de produção");

        ProductionExecution execution = new ProductionExecution();
        execution.setTenantId(request.getTenantId());
        execution.setCompanyId(request.getCompanyId());
        execution.setProductionOrderId(request.getProductionOrderId());
        execution.setStepId(request.getStepId());
        execution.setStartTime(request.getStartTime() != null ? request.getStartTime().toLocalDateTime() : null);
        execution.setEndTime(request.getEndTime() != null ? request.getEndTime().toLocalDateTime() : null);
        execution.setQuantityDone(request.getQuantityDone());
        execution.setLossQuantity(request.getLossQuantity());
        execution.setEmployeeId(request.getEmployeeId());
        execution.setMachineId(request.getMachineId());
        execution.setQualityStatus(request.getQualityStatus());
        execution.setRejectionReason(request.getRejectionReason());
        execution.setNotes(request.getNotes());
        execution.setCreatedBy(createdBy);

        ProductionExecution saved = productionExecutionRepository.save(execution);
        log.info("Execução de produção criada com sucesso: {}", saved.getId());

        return convertToDTO(saved);
    }

    public ProductionExecutionDTO update(UUID id, UpdateProductionExecutionDTO request, String updatedBy) {
        log.debug("Atualizando execução de produção: {}", id);

        ProductionExecution execution = productionExecutionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Execução não encontrada: " + id));

        if (request.getStepId() != null) execution.setStepId(request.getStepId());
        if (request.getStartTime() != null) execution.setStartTime(request.getStartTime().toLocalDateTime());
        if (request.getEndTime() != null) execution.setEndTime(request.getEndTime().toLocalDateTime());
        if (request.getQuantityDone() != null) execution.setQuantityDone(request.getQuantityDone());
        if (request.getLossQuantity() != null) execution.setLossQuantity(request.getLossQuantity());
        if (request.getEmployeeId() != null) execution.setEmployeeId(request.getEmployeeId());
        if (request.getMachineId() != null) execution.setMachineId(request.getMachineId());
        if (request.getQualityStatus() != null) execution.setQualityStatus(request.getQualityStatus());
        if (request.getRejectionReason() != null) execution.setRejectionReason(request.getRejectionReason());
        if (request.getNotes() != null) execution.setNotes(request.getNotes());

        execution.setUpdatedBy(updatedBy);
        execution.setUpdatedAt(LocalDateTime.now());

        ProductionExecution saved = productionExecutionRepository.save(execution);
        log.info("Execução de produção atualizada com sucesso: {}", saved.getId());

        return convertToDTO(saved);
    }

    public void delete(UUID id, String deletedBy) {
        log.debug("Deletando execução de produção: {}", id);

        ProductionExecution execution = productionExecutionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Execução não encontrada: " + id));

        execution.setDeletedAt(LocalDateTime.now());
        execution.setDeletedBy(deletedBy);

        productionExecutionRepository.save(execution);
        log.info("Execução de produção deletada com sucesso: {}", id);
    }

    private ProductionExecutionDTO convertToDTO(ProductionExecution execution) {
        ProductionExecutionDTO dto = new ProductionExecutionDTO();
        dto.setId(execution.getId());
        dto.setTenantId(execution.getTenantId());
        dto.setCompanyId(execution.getCompanyId());
        dto.setProductionOrderId(execution.getProductionOrderId());
        dto.setStepId(execution.getStepId());
        dto.setStartTime(execution.getStartTime() != null ? execution.getStartTime().atOffset(OffsetDateTime.now().getOffset()) : null);
        dto.setEndTime(execution.getEndTime() != null ? execution.getEndTime().atOffset(OffsetDateTime.now().getOffset()) : null);
        dto.setQuantityDone(execution.getQuantityDone());
        dto.setLossQuantity(execution.getLossQuantity());
        dto.setEmployeeId(execution.getEmployeeId());
        dto.setMachineId(execution.getMachineId());
        dto.setQualityStatus(execution.getQualityStatus());
        dto.setRejectionReason(execution.getRejectionReason());
        dto.setNotes(execution.getNotes());
        dto.setCreatedAt(execution.getCreatedAt() != null ? execution.getCreatedAt().atOffset(OffsetDateTime.now().getOffset()) : null);
        dto.setCreatedBy(execution.getCreatedBy());
        dto.setUpdatedAt(execution.getUpdatedAt() != null ? execution.getUpdatedAt().atOffset(OffsetDateTime.now().getOffset()) : null);
        dto.setUpdatedBy(execution.getUpdatedBy());
        return dto;
    }
}
