package br.com.sigeve.sigeve_prodution.service;

import br.com.sigeve.sigeve_prodution.dto.CompositionDTO;
import br.com.sigeve.sigeve_prodution.dto.CreateCompositionDTO;
import br.com.sigeve.sigeve_prodution.dto.UpdateCompositionDTO;
import br.com.sigeve.sigeve_prodution.model.Composition;
import br.com.sigeve.sigeve_prodution.repository.CompositionRepository;
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
public class CompositionService {

    private final CompositionRepository compositionRepository;
    private final CompositionItemService compositionItemService;
    private final br.com.sigeve.sigeve_prodution.repository.ProductionProductRepository productionProductRepository;

    @Transactional(readOnly = true)
    public List<CompositionDTO> findAllByCompany(UUID companyId) {
        log.debug("Buscando todas as composições da empresa: {}", companyId);
        return compositionRepository.findByCompanyIdAndDeletedAtIsNull(companyId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CompositionDTO> findActiveByCompany(UUID companyId) {
        log.debug("Buscando composições ativas da empresa: {}", companyId);
        return compositionRepository.findByCompanyIdAndIsActiveTrueAndDeletedAtIsNull(companyId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CompositionDTO> findByProduct(UUID productionProductId) {
        log.debug("Buscando composições do produto: {}", productionProductId);
        return compositionRepository.findByProductionProductIdAndDeletedAtIsNull(productionProductId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<CompositionDTO> findById(UUID id) {
        log.debug("Buscando composição por ID: {}", id);
        return compositionRepository.findByIdAndDeletedAtIsNull(id)
                .map(this::convertToDTO);
    }

    public CompositionDTO create(CreateCompositionDTO request, String createdBy) {
        log.debug("Criando nova composição: {}", request.getName());

        Composition composition = new Composition();
        composition.setTenantId(request.getTenantId());
        composition.setCompanyId(request.getCompanyId());
        composition.setProductionProductId(request.getProductionProductId());
        composition.setName(request.getName());
        composition.setVersion(request.getVersion());
        composition.setEffectiveDate(request.getEffectiveDate());
        composition.setExpirationDate(request.getExpirationDate());
        composition.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        composition.setNotes(request.getNotes());
        composition.setCreatedBy(createdBy);

        Composition saved = compositionRepository.save(composition);
        log.info("Composição criada com sucesso: {}", saved.getId());

        return convertToDTO(saved);
    }

    public CompositionDTO update(UUID id, UpdateCompositionDTO request, String updatedBy) {
        log.debug("Atualizando composição: {}", id);

        Composition composition = compositionRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Composição não encontrada: " + id));

        if (request.getProductionProductId() != null) composition.setProductionProductId(request.getProductionProductId());
        if (request.getName() != null) composition.setName(request.getName());
        if (request.getVersion() != null) composition.setVersion(request.getVersion());
        if (request.getEffectiveDate() != null) composition.setEffectiveDate(request.getEffectiveDate());
        if (request.getExpirationDate() != null) composition.setExpirationDate(request.getExpirationDate());
        if (request.getIsActive() != null) composition.setIsActive(request.getIsActive());
        if (request.getNotes() != null) composition.setNotes(request.getNotes());

        composition.setUpdatedBy(updatedBy);
        composition.setUpdatedAt(LocalDateTime.now());

        Composition saved = compositionRepository.save(composition);
        log.info("Composição atualizada com sucesso: {}", saved.getId());

        return convertToDTO(saved);
    }

    public void delete(UUID id, String deletedBy) {
        log.debug("Deletando composição: {}", id);

        Composition composition = compositionRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Composição não encontrada: " + id));

        composition.setDeletedAt(LocalDateTime.now());
        composition.setDeletedBy(deletedBy);

        compositionRepository.save(composition);
        log.info("Composição deletada com sucesso: {}", id);
    }

    public CompositionDTO approve(UUID id, String approvedBy) {
        Composition composition = compositionRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Composição não encontrada: " + id));

        composition.setApprovedBy(approvedBy);
        composition.setApprovedAt(LocalDateTime.now());

        Composition saved = compositionRepository.save(composition);

        return convertToDTO(saved);
    }

    private CompositionDTO convertToDTO(Composition composition) {
        CompositionDTO dto = new CompositionDTO();
        dto.setId(composition.getId());
        dto.setTenantId(composition.getTenantId());
        dto.setCompanyId(composition.getCompanyId());
        dto.setProductionProductId(composition.getProductionProductId());
        dto.setName(composition.getName());
        dto.setVersion(composition.getVersion());
        dto.setEffectiveDate(composition.getEffectiveDate());
        dto.setExpirationDate(composition.getExpirationDate());
        dto.setIsActive(composition.getIsActive());
        dto.setNotes(composition.getNotes());
        dto.setCreatedAt(composition.getCreatedAt() != null ? composition.getCreatedAt().atOffset(OffsetDateTime.now().getOffset()) : null);
        dto.setCreatedBy(composition.getCreatedBy());
        dto.setUpdatedAt(composition.getUpdatedAt() != null ? composition.getUpdatedAt().atOffset(OffsetDateTime.now().getOffset()) : null);
        dto.setUpdatedBy(composition.getUpdatedBy());
        dto.setApprovedBy(composition.getApprovedBy());
        dto.setApprovedAt(composition.getApprovedAt() != null ? composition.getApprovedAt().atOffset(OffsetDateTime.now().getOffset()) : null);
        
        log.info("🔍 [BACKEND] Convertendo Composition {} para DTO. ApprovedBy: {}, isApprovedEntity: {}", 
                composition.getId(), composition.getApprovedBy(), composition.isApproved());
        
        // Buscar nome do produto
        productionProductRepository.findById(composition.getProductionProductId())
                .ifPresent(product -> dto.setProductName(product.getDescription()));
        
        // Usar totalCost armazenado na entity
        dto.setTotalCost(composition.getTotalCost());
        
        // Calcular contagem de itens
        try {
            var costSummary = compositionItemService.calculateCompositionCosts(composition.getId());
            dto.setItemsCount(costSummary.getTotalItems());
        } catch (Exception e) {
            log.warn("Erro ao contar itens da composição {}: {}", composition.getId(), e.getMessage());
            dto.setItemsCount(0);
        }
        
        return dto;
    }

    // Método para recalcular e atualizar o custo total da composição
    public void recalculateTotalCost(UUID compositionId) {
        log.debug("Recalculando custo total da composição: {}", compositionId);
        
        Composition composition = compositionRepository.findByIdAndDeletedAtIsNull(compositionId)
                .orElseThrow(() -> new IllegalArgumentException("Composição não encontrada: " + compositionId));
        
        try {
            var costSummary = compositionItemService.calculateCompositionCosts(compositionId);
            composition.setTotalCost(costSummary.getTotalCost());
            compositionRepository.save(composition);
            
            log.info("Custo total da composição {} atualizado para: {}", compositionId, costSummary.getTotalCost());
        } catch (Exception e) {
            log.error("Erro ao recalcular custo total da composição {}: {}", compositionId, e.getMessage());
            throw new RuntimeException("Erro ao recalcular custo total", e);
        }
    }
}
