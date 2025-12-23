package br.com.sigeve.sigeve_prodution.service;

import br.com.sigeve.sigeve_prodution.dto.CreateProductionCostDTO;
import br.com.sigeve.sigeve_prodution.dto.ProductionCostDTO;
import br.com.sigeve.sigeve_prodution.dto.UpdateProductionCostDTO;
import br.com.sigeve.sigeve_prodution.enums.ProductionCostType;
import br.com.sigeve.sigeve_prodution.model.ProductionCost;
import br.com.sigeve.sigeve_prodution.repository.ProductionCostRepository;
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
public class ProductionCostService {

    private final ProductionCostRepository productionCostRepository;

    @Transactional(readOnly = true)
    public List<ProductionCostDTO> findByProductionOrder(UUID productionOrderId) {
        log.debug("Buscando custos da ordem: {}", productionOrderId);
        return productionCostRepository.findByProductionOrderIdAndDeletedAtIsNull(productionOrderId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductionCostDTO> findByType(UUID companyId, ProductionCostType costType) {
        log.debug("Buscando custos por tipo: {} na empresa: {}", costType, companyId);
        return productionCostRepository.findByCompanyIdAndCostTypeAndDeletedAtIsNull(companyId, costType).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ProductionCostDTO> findById(UUID id) {
        log.debug("Buscando custo por ID: {}", id);
        return productionCostRepository.findById(id)
                .map(this::convertToDTO);
    }

    public ProductionCostDTO create(CreateProductionCostDTO request, String createdBy) {
        log.debug("Criando novo custo de produção");

        ProductionCost cost = new ProductionCost();
        cost.setTenantId(request.getTenantId());
        cost.setCompanyId(request.getCompanyId());
        cost.setProductionOrderId(request.getProductionOrderId());
        cost.setCostType(request.getCostType());
        cost.setReferenceId(request.getReferenceId());
        cost.setCostDate(request.getCostDate());
        cost.setQuantity(request.getQuantity());
        cost.setUnitCost(request.getUnitCost());
        cost.setTotalCost(request.getTotalCost());
        cost.setNotes(request.getNotes());
        cost.setCreatedBy(createdBy);

        ProductionCost saved = productionCostRepository.save(cost);
        log.info("Custo de produção criado com sucesso: {}", saved.getId());

        return convertToDTO(saved);
    }

    public ProductionCostDTO update(UUID id, UpdateProductionCostDTO request, String updatedBy) {
        log.debug("Atualizando custo de produção: {}", id);

        ProductionCost cost = productionCostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Custo não encontrado: " + id));

        if (request.getCostType() != null) cost.setCostType(request.getCostType());
        if (request.getReferenceId() != null) cost.setReferenceId(request.getReferenceId());
        if (request.getCostDate() != null) cost.setCostDate(request.getCostDate());
        if (request.getQuantity() != null) cost.setQuantity(request.getQuantity());
        if (request.getUnitCost() != null) cost.setUnitCost(request.getUnitCost());
        if (request.getTotalCost() != null) cost.setTotalCost(request.getTotalCost());
        if (request.getNotes() != null) cost.setNotes(request.getNotes());

        cost.setUpdatedBy(updatedBy);
        cost.setUpdatedAt(LocalDateTime.now());

        ProductionCost saved = productionCostRepository.save(cost);
        log.info("Custo de produção atualizado com sucesso: {}", saved.getId());

        return convertToDTO(saved);
    }

    public void delete(UUID id, String deletedBy) {
        log.debug("Deletando custo de produção: {}", id);

        ProductionCost cost = productionCostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Custo não encontrado: " + id));

        cost.setDeletedAt(LocalDateTime.now());
        cost.setDeletedBy(deletedBy);

        productionCostRepository.save(cost);
        log.info("Custo de produção deletado com sucesso: {}", id);
    }

    public ProductionCostDTO approve(UUID id, String approvedBy) {
        log.debug("Aprovando custo de produção: {}", id);

        ProductionCost cost = productionCostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Custo não encontrado: " + id));

        cost.setApprovedBy(approvedBy);
        cost.setApprovedAt(LocalDateTime.now());

        ProductionCost saved = productionCostRepository.save(cost);
        log.info("Custo de produção aprovado: {}", id);

        return convertToDTO(saved);
    }

    private ProductionCostDTO convertToDTO(ProductionCost cost) {
        ProductionCostDTO dto = new ProductionCostDTO();
        dto.setId(cost.getId());
        dto.setTenantId(cost.getTenantId());
        dto.setCompanyId(cost.getCompanyId());
        dto.setProductionOrderId(cost.getProductionOrderId());
        dto.setCostType(cost.getCostType());
        dto.setReferenceId(cost.getReferenceId());
        dto.setCostDate(cost.getCostDate());
        dto.setQuantity(cost.getQuantity());
        dto.setUnitCost(cost.getUnitCost());
        dto.setTotalCost(cost.getTotalCost());
        dto.setNotes(cost.getNotes());
        dto.setCreatedAt(cost.getCreatedAt() != null ? cost.getCreatedAt().atOffset(OffsetDateTime.now().getOffset()) : null);
        dto.setCreatedBy(cost.getCreatedBy());
        dto.setUpdatedAt(cost.getUpdatedAt() != null ? cost.getUpdatedAt().atOffset(OffsetDateTime.now().getOffset()) : null);
        dto.setUpdatedBy(cost.getUpdatedBy());
        dto.setApprovedBy(cost.getApprovedBy());
        dto.setApprovedAt(cost.getApprovedAt() != null ? cost.getApprovedAt().atOffset(OffsetDateTime.now().getOffset()) : null);
        return dto;
    }
}
