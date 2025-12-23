package br.com.sigeve.sigeve_prodution.service;

import br.com.sigeve.sigeve_prodution.dto.CreateRawMaterialDTO;
import br.com.sigeve.sigeve_prodution.dto.RawMaterialDTO;
import br.com.sigeve.sigeve_prodution.dto.UpdateRawMaterialDTO;
import br.com.sigeve.sigeve_prodution.model.RawMaterial;
import br.com.sigeve.sigeve_prodution.repository.RawMaterialRepository;
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
public class RawMaterialService {

    private final RawMaterialRepository rawMaterialRepository;

    /**
     * Busca todas as matérias-primas de uma empresa
     */
    @Transactional(readOnly = true)
    public List<RawMaterialDTO> findAllByCompany(UUID companyId) {
        log.debug("Buscando todas as matérias-primas da empresa: {}", companyId);
        List<RawMaterial> materials = rawMaterialRepository.findByCompanyIdAndDeletedAtIsNull(companyId);
        return materials.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca matérias-primas ativas de uma empresa
     */
    @Transactional(readOnly = true)
    public List<RawMaterialDTO> findActiveByCompany(UUID companyId) {
        log.debug("Buscando matérias-primas ativas da empresa: {}", companyId);
        List<RawMaterial> materials = rawMaterialRepository.findByCompanyIdAndIsActiveTrueAndDeletedAtIsNull(companyId);
        return materials.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca matéria-prima por ID
     */
    @Transactional(readOnly = true)
    public Optional<RawMaterialDTO> findById(UUID id) {
        log.debug("Buscando matéria-prima por ID: {}", id);
        return rawMaterialRepository.findByIdAndDeletedAtIsNull(id)
                .map(this::convertToDTO);
    }

    /**
     * Busca matéria-prima por código
     */
    @Transactional(readOnly = true)
    public Optional<RawMaterialDTO> findByCode(UUID companyId, String code) {
        log.debug("Buscando matéria-prima por código: {} na empresa: {}", code, companyId);
        return rawMaterialRepository.findByCompanyIdAndCodeAndDeletedAtIsNull(companyId, code)
                .map(this::convertToDTO);
    }

    /**
     * Busca matérias-primas por categoria
     */
    @Transactional(readOnly = true)
    public List<RawMaterialDTO> findByCategory(UUID categoryId) {
        log.debug("Buscando matérias-primas da categoria: {}", categoryId);
        List<RawMaterial> materials = rawMaterialRepository.findByCategoryIdAndDeletedAtIsNull(categoryId);
        return materials.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca matérias-primas por fornecedor
     */
    @Transactional(readOnly = true)
    public List<RawMaterialDTO> findBySupplier(UUID supplierId) {
        log.debug("Buscando matérias-primas do fornecedor: {}", supplierId);
        List<RawMaterial> materials = rawMaterialRepository.findBySupplierIdAndDeletedAtIsNull(supplierId);
        return materials.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Cria uma nova matéria-prima
     */
    public RawMaterialDTO create(CreateRawMaterialDTO request, String createdBy) {
        log.debug("Criando nova matéria-prima: {}", request.getName());

        // Verificar se já existe matéria-prima com o mesmo código
        Optional<RawMaterial> existing = rawMaterialRepository
                .findByCompanyIdAndCodeAndDeletedAtIsNull(request.getCompanyId(), request.getCode());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Já existe uma matéria-prima com o código: " + request.getCode());
        }

        RawMaterial material = new RawMaterial();
        material.setTenantId(request.getTenantId());
        material.setCompanyId(request.getCompanyId());
        material.setCode(request.getCode());
        material.setName(request.getName());
        material.setUnitType(request.getUnitType());
        material.setSupplierId(request.getSupplierId());
        material.setAverageCost(request.getAverageCost());
        material.setLastPurchasePrice(request.getLastPurchasePrice());
        material.setStockControl(request.getStockControl() != null ? request.getStockControl() : true);
        material.setMinStock(request.getMinStock());
        material.setMaxStock(request.getMaxStock());
        material.setReorderPoint(request.getReorderPoint());
        material.setLeadTimeDays(request.getLeadTimeDays());
        material.setCategoryId(request.getCategoryId());
        material.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        material.setCreatedBy(createdBy);

        RawMaterial saved = rawMaterialRepository.save(material);
        log.info("Matéria-prima criada com sucesso: {}", saved.getId());

        return convertToDTO(saved);
    }

    /**
     * Atualiza uma matéria-prima existente
     */
    public RawMaterialDTO update(UUID id, UpdateRawMaterialDTO request, String updatedBy) {
        log.debug("Atualizando matéria-prima: {}", id);

        RawMaterial material = rawMaterialRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Matéria-prima não encontrada: " + id));

        // Verificar código duplicado
        if (request.getCode() != null && !request.getCode().equals(material.getCode())) {
            Optional<RawMaterial> existing = rawMaterialRepository
                    .findByCompanyIdAndCodeAndDeletedAtIsNull(material.getCompanyId(), request.getCode());
            if (existing.isPresent() && !existing.get().getId().equals(id)) {
                throw new IllegalArgumentException("Já existe uma matéria-prima com o código: " + request.getCode());
            }
        }

        if (request.getCode() != null) material.setCode(request.getCode());
        if (request.getName() != null) material.setName(request.getName());
        if (request.getUnitType() != null) material.setUnitType(request.getUnitType());
        if (request.getSupplierId() != null) material.setSupplierId(request.getSupplierId());
        if (request.getAverageCost() != null) material.setAverageCost(request.getAverageCost());
        if (request.getLastPurchasePrice() != null) material.setLastPurchasePrice(request.getLastPurchasePrice());
        if (request.getStockControl() != null) material.setStockControl(request.getStockControl());
        if (request.getMinStock() != null) material.setMinStock(request.getMinStock());
        if (request.getMaxStock() != null) material.setMaxStock(request.getMaxStock());
        if (request.getReorderPoint() != null) material.setReorderPoint(request.getReorderPoint());
        if (request.getLeadTimeDays() != null) material.setLeadTimeDays(request.getLeadTimeDays());
        if (request.getCategoryId() != null) material.setCategoryId(request.getCategoryId());
        if (request.getIsActive() != null) material.setIsActive(request.getIsActive());

        material.setUpdatedBy(updatedBy);
        material.setUpdatedAt(LocalDateTime.now());

        RawMaterial saved = rawMaterialRepository.save(material);
        log.info("Matéria-prima atualizada com sucesso: {}", saved.getId());

        return convertToDTO(saved);
    }

    /**
     * Deleta uma matéria-prima (soft delete)
     */
    public void delete(UUID id, String deletedBy) {
        log.debug("Deletando matéria-prima: {}", id);

        RawMaterial material = rawMaterialRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Matéria-prima não encontrada: " + id));

        material.setDeletedAt(LocalDateTime.now());
        material.setDeletedBy(deletedBy);

        rawMaterialRepository.save(material);
        log.info("Matéria-prima deletada com sucesso: {}", id);
    }

    /**
     * Converte entidade para DTO
     */
    private RawMaterialDTO convertToDTO(RawMaterial material) {
        RawMaterialDTO dto = new RawMaterialDTO();
        dto.setId(material.getId());
        dto.setTenantId(material.getTenantId());
        dto.setCompanyId(material.getCompanyId());
        dto.setCode(material.getCode());
        dto.setName(material.getName());
        dto.setUnitType(material.getUnitType());
        dto.setSupplierId(material.getSupplierId());
        dto.setAverageCost(material.getAverageCost());
        dto.setLastPurchasePrice(material.getLastPurchasePrice());
        dto.setLastPurchaseDate(material.getLastPurchaseDate());
        dto.setStockControl(material.getStockControl());
        dto.setMinStock(material.getMinStock());
        dto.setMaxStock(material.getMaxStock());
        dto.setReorderPoint(material.getReorderPoint());
        dto.setLeadTimeDays(material.getLeadTimeDays());
        dto.setCategoryId(material.getCategoryId());
        dto.setIsActive(material.getIsActive());
        dto.setCreatedAt(material.getCreatedAt() != null ? material.getCreatedAt().atOffset(OffsetDateTime.now().getOffset()) : null);
        dto.setCreatedBy(material.getCreatedBy());
        dto.setUpdatedAt(material.getUpdatedAt() != null ? material.getUpdatedAt().atOffset(OffsetDateTime.now().getOffset()) : null);
        dto.setUpdatedBy(material.getUpdatedBy());
        dto.setVersion(material.getVersion());
        return dto;
    }
}
