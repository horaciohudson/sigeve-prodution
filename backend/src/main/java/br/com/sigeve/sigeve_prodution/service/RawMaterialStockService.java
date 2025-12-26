package br.com.sigeve.sigeve_prodution.service;

import br.com.sigeve.sigeve_prodution.dto.RawMaterialStockDTO;
import br.com.sigeve.sigeve_prodution.enums.StockMovementType;
import br.com.sigeve.sigeve_prodution.model.RawMaterialMovement;
import br.com.sigeve.sigeve_prodution.model.RawMaterialStock;
import br.com.sigeve.sigeve_prodution.repository.RawMaterialStockRepository;
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
public class RawMaterialStockService {

    private final RawMaterialStockRepository rawMaterialStockRepository;

    @Transactional(readOnly = true)
    public List<RawMaterialStockDTO> findLowStock(UUID companyId, BigDecimal threshold) {
        log.debug("Buscando estoques baixos da empresa: {} com limite: {}", companyId, threshold);
        return rawMaterialStockRepository.findByCompanyIdAndAvailableQuantityLessThan(companyId, threshold).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateStock(RawMaterialMovement movement) {
        log.info("Atualizando estoque para material: {} na empresa: {}", 
                movement.getRawMaterialId(), movement.getCompanyId());

        RawMaterialStock stock = rawMaterialStockRepository
                .findByCompanyIdAndRawMaterialIdAndWarehouseId(
                        movement.getCompanyId(), 
                        movement.getRawMaterialId(), 
                        null // TODO: In the future, record warehouseId in movement
                )
                .orElseGet(() -> {
                    log.info("Criando novo registro de estoque para material: {}", movement.getRawMaterialId());
                    RawMaterialStock newStock = new RawMaterialStock();
                    newStock.setTenantId(movement.getTenantId());
                    newStock.setCompanyId(movement.getCompanyId());
                    newStock.setRawMaterialId(movement.getRawMaterialId());
                    newStock.setWarehouseId(null);
                    newStock.setQuantity(BigDecimal.ZERO);
                    newStock.setReservedQuantity(BigDecimal.ZERO);
                    return newStock;
                });

        if (StockMovementType.IN.equals(movement.getMovementType())) {
            stock.addQuantity(movement.getQuantity());
        } else {
            stock.removeQuantity(movement.getQuantity());
        }

        stock.setLastMovementDate(LocalDateTime.now());
        rawMaterialStockRepository.save(stock);
        log.info("Estoque atualizado com sucesso. Novo saldo: {}", stock.getQuantity());
    }

    @Transactional(readOnly = true)
    public List<RawMaterialStockDTO> findByCompany(UUID companyId) {
        log.debug("Buscando estoques da empresa: {}", companyId);
        return rawMaterialStockRepository.findByCompanyId(companyId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<RawMaterialStockDTO> findByRawMaterial(UUID rawMaterialId) {
        log.debug("Buscando estoque da matéria-prima: {}", rawMaterialId);
        return rawMaterialStockRepository.findByRawMaterialId(rawMaterialId)
                .map(this::convertToDTO);
    }

    private RawMaterialStockDTO convertToDTO(RawMaterialStock stock) {
        RawMaterialStockDTO dto = new RawMaterialStockDTO();
        dto.setId(stock.getId());
        dto.setTenantId(stock.getTenantId());
        dto.setCompanyId(stock.getCompanyId());
        dto.setRawMaterialId(stock.getRawMaterialId());
        dto.setWarehouseId(stock.getWarehouseId());
        dto.setQuantity(stock.getQuantity());
        dto.setReservedQuantity(stock.getReservedQuantity());
        dto.setAvailableQuantity(stock.getAvailableQuantity());
        dto.setLastMovementDate(stock.getLastMovementDate() != null ? stock.getLastMovementDate().atOffset(OffsetDateTime.now().getOffset()) : null);
        dto.setCreatedAt(stock.getCreatedAt() != null ? stock.getCreatedAt().atOffset(OffsetDateTime.now().getOffset()) : null);
        dto.setUpdatedAt(stock.getUpdatedAt() != null ? stock.getUpdatedAt().atOffset(OffsetDateTime.now().getOffset()) : null);
        return dto;
    }
}
