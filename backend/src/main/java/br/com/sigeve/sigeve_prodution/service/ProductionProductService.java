package br.com.sigeve.sigeve_prodution.service;

import br.com.sigeve.sigeve_prodution.dto.CreateProductionProductDTO;
import br.com.sigeve.sigeve_prodution.dto.ProductionProductDTO;
import br.com.sigeve.sigeve_prodution.dto.UpdateProductionProductDTO;
import br.com.sigeve.sigeve_prodution.model.ProductionProduct;
import br.com.sigeve.sigeve_prodution.repository.ProductionProductRepository;
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
public class ProductionProductService {

    private final ProductionProductRepository productionProductRepository;

    /**
     * Busca todos os produtos de produção de uma empresa
     */
    @Transactional(readOnly = true)
    public List<ProductionProductDTO> findAllByCompany(UUID companyId) {
        log.debug("Buscando todos os produtos de produção da empresa: {}", companyId);
        List<ProductionProduct> products = productionProductRepository.findByCompanyIdAndDeletedAtIsNull(companyId);
        return products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca produtos ativos de uma empresa
     */
    @Transactional(readOnly = true)
    public List<ProductionProductDTO> findActiveByCompany(UUID companyId) {
        log.debug("Buscando produtos ativos da empresa: {}", companyId);
        List<ProductionProduct> products = productionProductRepository.findByCompanyIdAndIsActiveTrueAndDeletedAtIsNull(companyId);
        return products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca produto por ID
     */
    @Transactional(readOnly = true)
    public Optional<ProductionProductDTO> findById(UUID id) {
        log.debug("Buscando produto de produção por ID: {}", id);
        return productionProductRepository.findByIdAndDeletedAtIsNull(id)
                .map(this::convertToDTO);
    }

    /**
     * Busca produto por SKU
     */
    @Transactional(readOnly = true)
    public Optional<ProductionProductDTO> findBySku(UUID companyId, String sku) {
        log.debug("Buscando produto por SKU: {} na empresa: {}", sku, companyId);
        return productionProductRepository.findByCompanyIdAndSkuAndDeletedAtIsNull(companyId, sku)
                .map(this::convertToDTO);
    }

    /**
     * Cria um novo produto de produção
     */
    public ProductionProductDTO create(CreateProductionProductDTO request, String createdBy) {
        log.debug("Criando novo produto de produção: {}", request.getDescription());

        // Verificar se já existe produto com o mesmo SKU
        if (request.getSku() != null) {
            Optional<ProductionProduct> existing = productionProductRepository
                    .findByCompanyIdAndSkuAndDeletedAtIsNull(request.getCompanyId(), request.getSku());
            if (existing.isPresent()) {
                throw new IllegalArgumentException("Já existe um produto com o SKU: " + request.getSku());
            }
        }

        ProductionProduct product = new ProductionProduct();
        product.setTenantId(request.getTenantId());
        product.setCompanyId(request.getCompanyId());
        product.setProductId(request.getProductId());
        product.setSku(request.getSku());
        product.setBarcode(request.getBarcode());
        product.setDescription(request.getDescription());
        product.setSize(request.getSize());
        product.setColor(request.getColor());
        product.setUnitType(request.getUnitType());
        product.setImageUrl(request.getImageUrl());
        product.setNotes(request.getNotes());
        product.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        product.setCreatedBy(createdBy);

        ProductionProduct saved = productionProductRepository.save(product);
        log.info("Produto de produção criado com sucesso: {}", saved.getId());

        return convertToDTO(saved);
    }

    /**
     * Atualiza um produto de produção existente
     */
    public ProductionProductDTO update(UUID id, UpdateProductionProductDTO request, UUID companyId, String updatedBy) {
        log.debug("Atualizando produto de produção: {}", id);

        ProductionProduct product = productionProductRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + id));

        if (!product.getCompanyId().equals(companyId)) {
            throw new IllegalArgumentException("Produto não pertence à empresa informada");
        }

        // Verificar SKU duplicado
        if (request.getSku() != null && !request.getSku().equals(product.getSku())) {
            Optional<ProductionProduct> existing = productionProductRepository
                    .findByCompanyIdAndSkuAndDeletedAtIsNull(product.getCompanyId(), request.getSku());
            if (existing.isPresent() && !existing.get().getId().equals(id)) {
                throw new IllegalArgumentException("Já existe um produto com o SKU: " + request.getSku());
            }
        }

        if (request.getProductId() != null) product.setProductId(request.getProductId());
        if (request.getSku() != null) product.setSku(request.getSku());
        if (request.getBarcode() != null) product.setBarcode(request.getBarcode());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getSize() != null) product.setSize(request.getSize());
        if (request.getColor() != null) product.setColor(request.getColor());
        if (request.getUnitType() != null) product.setUnitType(request.getUnitType());
        if (request.getImageUrl() != null) product.setImageUrl(request.getImageUrl());
        if (request.getNotes() != null) product.setNotes(request.getNotes());
        if (request.getIsActive() != null) product.setIsActive(request.getIsActive());

        product.setUpdatedBy(updatedBy);
        product.setUpdatedAt(LocalDateTime.now());

        ProductionProduct saved = productionProductRepository.save(product);
        log.info("Produto de produção atualizado com sucesso: {}", saved.getId());

        return convertToDTO(saved);
    }

    /**
     * Deleta um produto de produção (soft delete)
     */
    public void delete(UUID id, UUID companyId, String deletedBy) {
        log.debug("Deletando produto de produção: {}", id);

        ProductionProduct product = productionProductRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + id));

        if (!product.getCompanyId().equals(companyId)) {
            throw new IllegalArgumentException("Produto não pertence à empresa informada");
        }

        product.setDeletedAt(LocalDateTime.now());
        product.setDeletedBy(deletedBy);

        productionProductRepository.save(product);
        log.info("Produto de produção deletado com sucesso: {}", id);
    }

    /**
     * Converte entidade para DTO
     */
    private ProductionProductDTO convertToDTO(ProductionProduct product) {
        ProductionProductDTO dto = new ProductionProductDTO();
        dto.setId(product.getId());
        dto.setTenantId(product.getTenantId());
        dto.setCompanyId(product.getCompanyId());
        dto.setProductId(product.getProductId());
        dto.setSku(product.getSku());
        dto.setBarcode(product.getBarcode());
        dto.setDescription(product.getDescription());
        dto.setSize(product.getSize());
        dto.setColor(product.getColor());
        dto.setUnitType(product.getUnitType());
        dto.setImageUrl(product.getImageUrl());
        dto.setNotes(product.getNotes());
        dto.setIsActive(product.getIsActive());
        dto.setCreatedAt(product.getCreatedAt() != null ? product.getCreatedAt().atOffset(OffsetDateTime.now().getOffset()) : null);
        dto.setCreatedBy(product.getCreatedBy());
        dto.setUpdatedAt(product.getUpdatedAt() != null ? product.getUpdatedAt().atOffset(OffsetDateTime.now().getOffset()) : null);
        dto.setUpdatedBy(product.getUpdatedBy());
        dto.setVersion(product.getVersion());
        return dto;
    }
}
