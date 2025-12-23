package br.com.sigeve.sigeve_prodution.service;

import br.com.sigeve.sigeve_prodution.dto.CreateProductionOrderDTO;
import br.com.sigeve.sigeve_prodution.dto.ProductionOrderDTO;
import br.com.sigeve.sigeve_prodution.dto.UpdateProductionOrderDTO;
import br.com.sigeve.sigeve_prodution.enums.ProductionOrderStatus;
import br.com.sigeve.sigeve_prodution.model.ProductionOrder;
import br.com.sigeve.sigeve_prodution.repository.ProductionOrderRepository;
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
public class ProductionOrderService {

    private final ProductionOrderRepository productionOrderRepository;

    @Transactional(readOnly = true)
    public List<ProductionOrderDTO> findAllByCompany(UUID companyId) {
        log.debug("Buscando todas as ordens de produção da empresa: {}", companyId);
        return productionOrderRepository.findByCompanyIdAndDeletedAtIsNull(companyId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductionOrderDTO> findByStatus(UUID companyId, ProductionOrderStatus status) {
        log.debug("Buscando ordens por status: {} na empresa: {}", status, companyId);
        return productionOrderRepository.findByCompanyIdAndStatusAndDeletedAtIsNull(companyId, status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ProductionOrderDTO> findById(UUID id) {
        log.debug("Buscando ordem de produção por ID: {}", id);
        return productionOrderRepository.findByIdAndDeletedAtIsNull(id)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public Optional<ProductionOrderDTO> findByCode(UUID companyId, String code) {
        log.debug("Buscando ordem por código: {} na empresa: {}", code, companyId);
        return productionOrderRepository.findByCompanyIdAndCodeAndDeletedAtIsNull(companyId, code)
                .map(this::convertToDTO);
    }

    public ProductionOrderDTO create(CreateProductionOrderDTO request, String createdBy) {
        log.debug("Criando nova ordem de produção: {}", request.getCode());

        Optional<ProductionOrder> existing = productionOrderRepository
                .findByCompanyIdAndCodeAndDeletedAtIsNull(request.getCompanyId(), request.getCode());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Já existe uma ordem com o código: " + request.getCode());
        }

        ProductionOrder order = new ProductionOrder();
        order.setTenantId(request.getTenantId());
        order.setCompanyId(request.getCompanyId());
        order.setCode(request.getCode());
        order.setProductId(request.getProductId());
        order.setQuantityPlanned(request.getQuantityPlanned());
        order.setQuantityProduced(BigDecimal.ZERO);
        order.setStatus(ProductionOrderStatus.PLANNED);
        order.setPriority(request.getPriority());
        order.setStartDate(request.getStartDate());
        order.setEndDate(request.getEndDate());
        order.setDeadline(request.getDeadline());
        order.setCustomerId(request.getCustomerId());
        order.setOrderId(request.getOrderId());
        order.setNotes(request.getNotes());
        order.setCostTotal(BigDecimal.ZERO);
        order.setCreatedBy(createdBy);

        ProductionOrder saved = productionOrderRepository.save(order);
        log.info("Ordem de produção criada com sucesso: {}", saved.getId());

        return convertToDTO(saved);
    }

    public ProductionOrderDTO update(UUID id, UpdateProductionOrderDTO request, String updatedBy) {
        log.debug("Atualizando ordem de produção: {}", id);

        ProductionOrder order = productionOrderRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Ordem não encontrada: " + id));

        if (request.getCode() != null && !request.getCode().equals(order.getCode())) {
            Optional<ProductionOrder> existing = productionOrderRepository
                    .findByCompanyIdAndCodeAndDeletedAtIsNull(order.getCompanyId(), request.getCode());
            if (existing.isPresent() && !existing.get().getId().equals(id)) {
                throw new IllegalArgumentException("Já existe uma ordem com o código: " + request.getCode());
            }
        }

        if (request.getCode() != null) order.setCode(request.getCode());
        if (request.getProductId() != null) order.setProductId(request.getProductId());
        if (request.getQuantityPlanned() != null) order.setQuantityPlanned(request.getQuantityPlanned());
        if (request.getQuantityProduced() != null) order.setQuantityProduced(request.getQuantityProduced());
        if (request.getStatus() != null) order.setStatus(request.getStatus());
        if (request.getPriority() != null) order.setPriority(request.getPriority());
        if (request.getStartDate() != null) order.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) order.setEndDate(request.getEndDate());
        if (request.getDeadline() != null) order.setDeadline(request.getDeadline());
        if (request.getCustomerId() != null) order.setCustomerId(request.getCustomerId());
        if (request.getOrderId() != null) order.setOrderId(request.getOrderId());
        if (request.getNotes() != null) order.setNotes(request.getNotes());
        if (request.getCanceledReason() != null) order.setCanceledReason(request.getCanceledReason());

        order.setUpdatedBy(updatedBy);
        order.setUpdatedAt(LocalDateTime.now());

        ProductionOrder saved = productionOrderRepository.save(order);
        log.info("Ordem de produção atualizada com sucesso: {}", saved.getId());

        return convertToDTO(saved);
    }

    public void delete(UUID id, String deletedBy) {
        log.debug("Deletando ordem de produção: {}", id);

        ProductionOrder order = productionOrderRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Ordem não encontrada: " + id));

        order.setDeletedAt(LocalDateTime.now());
        order.setDeletedBy(deletedBy);

        productionOrderRepository.save(order);
        log.info("Ordem de produção deletada com sucesso: {}", id);
    }

    public ProductionOrderDTO approve(UUID id, String approvedBy) {
        log.debug("Aprovando ordem de produção: {}", id);

        ProductionOrder order = productionOrderRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Ordem não encontrada: " + id));

        if (order.getStatus() != ProductionOrderStatus.PLANNED) {
            throw new IllegalStateException("Apenas ordens planejadas podem ser aprovadas");
        }

        order.setApprovedBy(approvedBy);
        order.setApprovedAt(LocalDateTime.now());

        ProductionOrder saved = productionOrderRepository.save(order);
        log.info("Ordem de produção aprovada: {}", id);

        return convertToDTO(saved);
    }

    public ProductionOrderDTO start(UUID id, String updatedBy) {
        log.debug("Iniciando ordem de produção: {}", id);

        ProductionOrder order = productionOrderRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Ordem não encontrada: " + id));

        if (order.getStatus() != ProductionOrderStatus.PLANNED) {
            throw new IllegalStateException("Apenas ordens planejadas podem ser iniciadas");
        }

        order.setStatus(ProductionOrderStatus.IN_PROGRESS);
        order.setUpdatedBy(updatedBy);
        order.setUpdatedAt(LocalDateTime.now());

        ProductionOrder saved = productionOrderRepository.save(order);
        log.info("Ordem de produção iniciada: {}", id);

        return convertToDTO(saved);
    }

    public ProductionOrderDTO finish(UUID id, String finishedBy) {
        log.debug("Finalizando ordem de produção: {}", id);

        ProductionOrder order = productionOrderRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Ordem não encontrada: " + id));

        if (order.getStatus() != ProductionOrderStatus.IN_PROGRESS) {
            throw new IllegalStateException("Apenas ordens em andamento podem ser finalizadas");
        }

        order.setStatus(ProductionOrderStatus.FINISHED);
        order.setFinishedBy(finishedBy);
        order.setFinishedAt(LocalDateTime.now());
        order.setUpdatedBy(finishedBy);
        order.setUpdatedAt(LocalDateTime.now());

        ProductionOrder saved = productionOrderRepository.save(order);
        log.info("Ordem de produção finalizada: {}", id);

        return convertToDTO(saved);
    }

    public ProductionOrderDTO cancel(UUID id, String canceledReason, String updatedBy) {
        log.debug("Cancelando ordem de produção: {}", id);

        ProductionOrder order = productionOrderRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Ordem não encontrada: " + id));

        if (order.getStatus() == ProductionOrderStatus.FINISHED || order.getStatus() == ProductionOrderStatus.CANCELED) {
            throw new IllegalStateException("Ordens finalizadas ou já canceladas não podem ser canceladas");
        }

        order.setStatus(ProductionOrderStatus.CANCELED);
        order.setCanceledReason(canceledReason);
        order.setUpdatedBy(updatedBy);
        order.setUpdatedAt(LocalDateTime.now());

        ProductionOrder saved = productionOrderRepository.save(order);
        log.info("Ordem de produção cancelada: {}", id);

        return convertToDTO(saved);
    }

    private ProductionOrderDTO convertToDTO(ProductionOrder order) {
        ProductionOrderDTO dto = new ProductionOrderDTO();
        dto.setId(order.getId());
        dto.setTenantId(order.getTenantId());
        dto.setCompanyId(order.getCompanyId());
        dto.setCode(order.getCode());
        dto.setProductId(order.getProductId());
        dto.setQuantityPlanned(order.getQuantityPlanned());
        dto.setQuantityProduced(order.getQuantityProduced());
        dto.setStatus(order.getStatus());
        dto.setPriority(order.getPriority());
        dto.setStartDate(order.getStartDate());
        dto.setEndDate(order.getEndDate());
        dto.setDeadline(order.getDeadline());
        dto.setCustomerId(order.getCustomerId());
        dto.setOrderId(order.getOrderId());
        dto.setCostTotal(order.getCostTotal());
        dto.setNotes(order.getNotes());
        dto.setCreatedAt(order.getCreatedAt() != null ? order.getCreatedAt().atOffset(OffsetDateTime.now().getOffset()) : null);
        dto.setCreatedBy(order.getCreatedBy());
        dto.setUpdatedAt(order.getUpdatedAt() != null ? order.getUpdatedAt().atOffset(OffsetDateTime.now().getOffset()) : null);
        dto.setUpdatedBy(order.getUpdatedBy());
        dto.setVersion(order.getVersion());
        dto.setApprovedBy(order.getApprovedBy());
        dto.setApprovedAt(order.getApprovedAt() != null ? order.getApprovedAt().atOffset(OffsetDateTime.now().getOffset()) : null);
        dto.setFinishedBy(order.getFinishedBy());
        dto.setFinishedAt(order.getFinishedAt() != null ? order.getFinishedAt().atOffset(OffsetDateTime.now().getOffset()) : null);
        dto.setCanceledReason(order.getCanceledReason());
        return dto;
    }
}
