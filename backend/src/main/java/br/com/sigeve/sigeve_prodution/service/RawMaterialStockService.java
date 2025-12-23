package br.com.sigeve.sigeve_prodution.service;

import br.com.sigeve.sigeve_prodution.dto.RawMaterialStockDTO;
import br.com.sigeve.sigeve_prodution.model.RawMaterialStock;
import br.com.sigeve.sigeve_prodution.repository.RawMaterialStockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RawMaterialStockService {

    private final RawMaterialStockRepository rawMaterialStockRepository;

    public Optional<RawMaterialStockDTO> findByRawMaterial(UUID rawMaterialId) {
        log.debug("Buscando estoque da matéria-prima: {}", rawMaterialId);
        return rawMaterialStockRepository.findByRawMaterialId(rawMaterialId)
                .map(this::convertToDTO);
    }

    public List<RawMaterialStockDTO> findByCompany(UUID companyId) {
        log.debug("Buscando estoques da empresa: {}", companyId);
        return rawMaterialStockRepository.findByCompanyId(companyId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<RawMaterialStockDTO> findLowStock(UUID companyId, BigDecimal threshold) {
        log.debug("Buscando estoques baixos da empresa: {} com limite: {}", companyId, threshold);
        return rawMaterialStockRepository.findByCompanyIdAndAvailableQuantityLessThan(companyId, threshold).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
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
