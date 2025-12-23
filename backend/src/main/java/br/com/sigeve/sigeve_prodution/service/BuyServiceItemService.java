package br.com.sigeve.sigeve_prodution.service;

import br.com.sigeve.sigeve_prodution.dto.BuyServiceItemDTO;
import br.com.sigeve.sigeve_prodution.dto.CreateBuyServiceItemDTO;
import br.com.sigeve.sigeve_prodution.dto.UpdateBuyServiceItemDTO;
import br.com.sigeve.sigeve_prodution.model.BuyServiceItem;
import br.com.sigeve.sigeve_prodution.repository.BuyServiceItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
public class BuyServiceItemService {

    private final BuyServiceItemRepository buyServiceItemRepository;

    @Transactional(readOnly = true)
    public List<BuyServiceItemDTO> findByBuyService(UUID buyServiceId) {
        log.debug("Buscando itens da compra de serviço: {}", buyServiceId);
        return buyServiceItemRepository.findByBuyServiceIdAndDeletedAtIsNullOrderBySequence(buyServiceId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<BuyServiceItemDTO> findById(UUID id) {
        log.debug("Buscando item de compra por ID: {}", id);
        return buyServiceItemRepository.findById(id)
                .map(this::convertToDTO);
    }

    public BuyServiceItemDTO create(CreateBuyServiceItemDTO request, String createdBy) {
        log.debug("Criando novo item de compra de serviço");

        BuyServiceItem item = new BuyServiceItem();
        item.setTenantId(request.getTenantId());
        item.setCompanyId(request.getCompanyId());
        item.setBuyServiceId(request.getBuyServiceId());
        item.setSequence(request.getSequence());
        item.setDescription(request.getDescription());
        item.setUnitType(request.getUnitType());
        item.setQuantity(request.getQuantity());
        item.setUnitPrice(request.getUnitPrice());
        item.setDiscount(request.getDiscount());
        item.setDeliveryDate(request.getDeliveryDate());
        item.setQuantityReceived(BigDecimal.ZERO);
        item.setNotes(request.getNotes());
        item.setCreatedBy(createdBy);

        BuyServiceItem saved = buyServiceItemRepository.save(item);
        log.info("Item de compra criado com sucesso: {}", saved.getId());

        return convertToDTO(saved);
    }

    public BuyServiceItemDTO update(UUID id, UpdateBuyServiceItemDTO request, String updatedBy) {
        log.debug("Atualizando item de compra: {}", id);

        BuyServiceItem item = buyServiceItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item não encontrado: " + id));

        if (request.getSequence() != null) item.setSequence(request.getSequence());
        if (request.getDescription() != null) item.setDescription(request.getDescription());
        if (request.getUnitType() != null) item.setUnitType(request.getUnitType());
        if (request.getQuantity() != null) item.setQuantity(request.getQuantity());
        if (request.getUnitPrice() != null) item.setUnitPrice(request.getUnitPrice());
        if (request.getDiscount() != null) item.setDiscount(request.getDiscount());
        if (request.getDeliveryDate() != null) item.setDeliveryDate(request.getDeliveryDate());
        if (request.getQuantityReceived() != null) item.setQuantityReceived(request.getQuantityReceived());
        if (request.getNotes() != null) item.setNotes(request.getNotes());

        item.setUpdatedBy(updatedBy);
        item.setUpdatedAt(LocalDateTime.now());

        BuyServiceItem saved = buyServiceItemRepository.save(item);
        log.info("Item de compra atualizado com sucesso: {}", saved.getId());

        return convertToDTO(saved);
    }

    public void delete(UUID id, String deletedBy) {
        log.debug("Deletando item de compra: {}", id);

        BuyServiceItem item = buyServiceItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item não encontrado: " + id));

        item.setDeletedAt(LocalDateTime.now());
        item.setDeletedBy(deletedBy);

        buyServiceItemRepository.save(item);
        log.info("Item de compra deletado com sucesso: {}", id);
    }

    private BuyServiceItemDTO convertToDTO(BuyServiceItem item) {
        BuyServiceItemDTO dto = new BuyServiceItemDTO();
        dto.setId(item.getId());
        dto.setTenantId(item.getTenantId());
        dto.setCompanyId(item.getCompanyId());
        dto.setBuyServiceId(item.getBuyServiceId());
        dto.setSequence(item.getSequence());
        dto.setDescription(item.getDescription());
        dto.setUnitType(item.getUnitType());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setDiscount(item.getDiscount());
        dto.setTotalPrice(item.getTotalPrice());
        dto.setDeliveryDate(item.getDeliveryDate());
        dto.setQuantityReceived(item.getQuantityReceived());
        dto.setNotes(item.getNotes());
        dto.setCreatedAt(item.getCreatedAt() != null ? item.getCreatedAt().atOffset(OffsetDateTime.now().getOffset()) : null);
        dto.setCreatedBy(item.getCreatedBy());
        dto.setUpdatedAt(item.getUpdatedAt() != null ? item.getUpdatedAt().atOffset(OffsetDateTime.now().getOffset()) : null);
        dto.setUpdatedBy(item.getUpdatedBy());
        return dto;
    }
}
