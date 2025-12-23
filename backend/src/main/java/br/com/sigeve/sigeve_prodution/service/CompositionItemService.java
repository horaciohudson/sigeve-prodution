package br.com.sigeve.sigeve_prodution.service;

import br.com.sigeve.sigeve_prodution.dto.CompositionItemDTO;
import br.com.sigeve.sigeve_prodution.dto.CreateCompositionItemDTO;
import br.com.sigeve.sigeve_prodution.dto.UpdateCompositionItemDTO;
import br.com.sigeve.sigeve_prodution.model.CompositionItem;
import br.com.sigeve.sigeve_prodution.repository.CompositionItemRepository;
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
public class CompositionItemService {

    private final CompositionItemRepository compositionItemRepository;

    @Transactional(readOnly = true)
    public List<CompositionItemDTO> findByComposition(UUID compositionId) {
        log.debug("Buscando itens da composição: {}", compositionId);
        return compositionItemRepository.findByCompositionIdAndDeletedAtIsNullOrderBySequence(compositionId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<CompositionItemDTO> findById(UUID id) {
        log.debug("Buscando item de composição por ID: {}", id);
        return compositionItemRepository.findById(id)
                .map(this::convertToDTO);
    }

    public CompositionItemDTO create(CreateCompositionItemDTO request, String createdBy) {
        log.debug("Criando novo item de composição");

        CompositionItem item = new CompositionItem();
        item.setTenantId(request.getTenantId());
        item.setCompanyId(request.getCompanyId());
        item.setCompositionId(request.getCompositionId());
        item.setItemType(request.getItemType());
        item.setReferenceId(request.getReferenceId());
        item.setSequence(request.getSequence());
        item.setUnitType(request.getUnitType());
        item.setQuantity(request.getQuantity());
        item.setLossPercentage(request.getLossPercentage());
        item.setUnitCost(request.getUnitCost());
        item.setIsOptional(request.getIsOptional() != null ? request.getIsOptional() : false);
        item.setNotes(request.getNotes());
        item.setCreatedBy(createdBy);

        CompositionItem saved = compositionItemRepository.save(item);
        log.info("Item de composição criado com sucesso: {}", saved.getId());

        return convertToDTO(saved);
    }

    public CompositionItemDTO update(UUID id, UpdateCompositionItemDTO request, String updatedBy) {
        log.debug("Atualizando item de composição: {}", id);

        CompositionItem item = compositionItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item não encontrado: " + id));

        if (request.getItemType() != null) item.setItemType(request.getItemType());
        if (request.getReferenceId() != null) item.setReferenceId(request.getReferenceId());
        if (request.getSequence() != null) item.setSequence(request.getSequence());
        if (request.getUnitType() != null) item.setUnitType(request.getUnitType());
        if (request.getQuantity() != null) item.setQuantity(request.getQuantity());
        if (request.getLossPercentage() != null) item.setLossPercentage(request.getLossPercentage());
        if (request.getUnitCost() != null) item.setUnitCost(request.getUnitCost());
        if (request.getIsOptional() != null) item.setIsOptional(request.getIsOptional());
        if (request.getNotes() != null) item.setNotes(request.getNotes());

        item.setUpdatedBy(updatedBy);
        item.setUpdatedAt(LocalDateTime.now());

        CompositionItem saved = compositionItemRepository.save(item);
        log.info("Item de composição atualizado com sucesso: {}", saved.getId());

        return convertToDTO(saved);
    }

    public void delete(UUID id, String deletedBy) {
        log.debug("Deletando item de composição: {}", id);

        CompositionItem item = compositionItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item não encontrado: " + id));

        item.setDeletedAt(LocalDateTime.now());
        item.setDeletedBy(deletedBy);

        compositionItemRepository.save(item);
        log.info("Item de composição deletado com sucesso: {}", id);
    }

    private CompositionItemDTO convertToDTO(CompositionItem item) {
        CompositionItemDTO dto = new CompositionItemDTO();
        dto.setId(item.getId());
        dto.setTenantId(item.getTenantId());
        dto.setCompanyId(item.getCompanyId());
        dto.setCompositionId(item.getCompositionId());
        dto.setItemType(item.getItemType());
        dto.setReferenceId(item.getReferenceId());
        dto.setSequence(item.getSequence());
        dto.setUnitType(item.getUnitType());
        dto.setQuantity(item.getQuantity());
        dto.setLossPercentage(item.getLossPercentage());
        dto.setUnitCost(item.getUnitCost());
        dto.setTotalCost(item.getTotalCost());
        dto.setIsOptional(item.getIsOptional());
        dto.setNotes(item.getNotes());
        dto.setCreatedAt(item.getCreatedAt() != null ? item.getCreatedAt().atOffset(OffsetDateTime.now().getOffset()) : null);
        dto.setCreatedBy(item.getCreatedBy());
        dto.setUpdatedAt(item.getUpdatedAt() != null ? item.getUpdatedAt().atOffset(OffsetDateTime.now().getOffset()) : null);
        dto.setUpdatedBy(item.getUpdatedBy());
        return dto;
    }
}
