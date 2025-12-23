package br.com.sigeve.sigeve_prodution.service;

import br.com.sigeve.sigeve_prodution.dto.BuyServiceDTO;
import br.com.sigeve.sigeve_prodution.dto.CreateBuyServiceDTO;
import br.com.sigeve.sigeve_prodution.dto.UpdateBuyServiceDTO;
import br.com.sigeve.sigeve_prodution.enums.BuyServiceStatus;
import br.com.sigeve.sigeve_prodution.model.BuyService;
import br.com.sigeve.sigeve_prodution.repository.BuyServiceRepository;
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
public class BuyServiceService {

    private final BuyServiceRepository buyServiceRepository;

    @Transactional(readOnly = true)
    public List<BuyServiceDTO> findAllByCompany(UUID companyId) {
        log.debug("Buscando todas as compras de serviço da empresa: {}", companyId);
        return buyServiceRepository.findByCompanyIdAndDeletedAtIsNull(companyId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BuyServiceDTO> findByStatus(UUID companyId, BuyServiceStatus status) {
        log.debug("Buscando compras por status: {} na empresa: {}", status, companyId);
        return buyServiceRepository.findByCompanyIdAndStatusAndDeletedAtIsNull(companyId, status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<BuyServiceDTO> findById(UUID id) {
        log.debug("Buscando compra de serviço por ID: {}", id);
        return buyServiceRepository.findByIdAndDeletedAtIsNull(id)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public Optional<BuyServiceDTO> findByCode(UUID companyId, String code) {
        log.debug("Buscando compra por código: {} na empresa: {}", code, companyId);
        return buyServiceRepository.findByCompanyIdAndCodeAndDeletedAtIsNull(companyId, code)
                .map(this::convertToDTO);
    }

    public BuyServiceDTO create(CreateBuyServiceDTO request, String createdBy) {
        log.debug("Criando nova compra de serviço: {}", request.getCode());

        Optional<BuyService> existing = buyServiceRepository
                .findByCompanyIdAndCodeAndDeletedAtIsNull(request.getCompanyId(), request.getCode());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Já existe uma compra com o código: " + request.getCode());
        }

        BuyService service = new BuyService();
        service.setTenantId(request.getTenantId());
        service.setCompanyId(request.getCompanyId());
        service.setSupplierId(request.getSupplierId());
        service.setCode(request.getCode());
        service.setServiceName(request.getServiceName());
        service.setReference(request.getReference());
        service.setOrderDate(request.getOrderDate());
        service.setDeliveryDate(request.getDeliveryDate());
        service.setBaseValue(request.getBaseValue());
        service.setTotalValue(BigDecimal.ZERO);
        service.setPaymentTerms(request.getPaymentTerms());
        service.setStatus(BuyServiceStatus.OPEN);
        service.setNotes(request.getNotes());
        service.setCreatedBy(createdBy);

        BuyService saved = buyServiceRepository.save(service);
        log.info("Compra de serviço criada com sucesso: {}", saved.getId());

        return convertToDTO(saved);
    }

    public BuyServiceDTO update(UUID id, UpdateBuyServiceDTO request, String updatedBy) {
        log.debug("Atualizando compra de serviço: {}", id);

        BuyService service = buyServiceRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Compra não encontrada: " + id));

        if (request.getCode() != null && !request.getCode().equals(service.getCode())) {
            Optional<BuyService> existing = buyServiceRepository
                    .findByCompanyIdAndCodeAndDeletedAtIsNull(service.getCompanyId(), request.getCode());
            if (existing.isPresent() && !existing.get().getId().equals(id)) {
                throw new IllegalArgumentException("Já existe uma compra com o código: " + request.getCode());
            }
        }

        if (request.getSupplierId() != null) service.setSupplierId(request.getSupplierId());
        if (request.getCode() != null) service.setCode(request.getCode());
        if (request.getServiceName() != null) service.setServiceName(request.getServiceName());
        if (request.getReference() != null) service.setReference(request.getReference());
        if (request.getOrderDate() != null) service.setOrderDate(request.getOrderDate());
        if (request.getDeliveryDate() != null) service.setDeliveryDate(request.getDeliveryDate());
        if (request.getBaseValue() != null) service.setBaseValue(request.getBaseValue());
        if (request.getPaymentTerms() != null) service.setPaymentTerms(request.getPaymentTerms());
        if (request.getStatus() != null) service.setStatus(request.getStatus());
        if (request.getNotes() != null) service.setNotes(request.getNotes());

        service.setUpdatedBy(updatedBy);
        service.setUpdatedAt(LocalDateTime.now());

        BuyService saved = buyServiceRepository.save(service);
        log.info("Compra de serviço atualizada com sucesso: {}", saved.getId());

        return convertToDTO(saved);
    }

    public void delete(UUID id, String deletedBy) {
        log.debug("Deletando compra de serviço: {}", id);

        BuyService service = buyServiceRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Compra não encontrada: " + id));

        service.setDeletedAt(LocalDateTime.now());
        service.setDeletedBy(deletedBy);

        buyServiceRepository.save(service);
        log.info("Compra de serviço deletada com sucesso: {}", id);
    }

    public BuyServiceDTO approve(UUID id, String approvedBy) {
        log.debug("Aprovando compra de serviço: {}", id);

        BuyService service = buyServiceRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Compra não encontrada: " + id));

        if (service.getStatus() != BuyServiceStatus.OPEN) {
            throw new IllegalStateException("Apenas compras abertas podem ser aprovadas");
        }

        service.setApprovedBy(approvedBy);
        service.setApprovedAt(LocalDateTime.now());
        service.setStatus(BuyServiceStatus.APPROVED);

        BuyService saved = buyServiceRepository.save(service);
        log.info("Compra de serviço aprovada: {}", id);

        return convertToDTO(saved);
    }

    public BuyServiceDTO close(UUID id, String closedBy) {
        log.debug("Fechando compra de serviço: {}", id);

        BuyService service = buyServiceRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Compra não encontrada: " + id));

        if (service.getStatus() == BuyServiceStatus.CLOSED) {
            throw new IllegalStateException("Compra já está fechada");
        }

        service.setStatus(BuyServiceStatus.CLOSED);
        service.setClosedBy(closedBy);
        service.setClosedAt(LocalDateTime.now());
        service.setUpdatedBy(closedBy);
        service.setUpdatedAt(LocalDateTime.now());

        BuyService saved = buyServiceRepository.save(service);
        log.info("Compra de serviço fechada: {}", id);

        return convertToDTO(saved);
    }

    private BuyServiceDTO convertToDTO(BuyService service) {
        BuyServiceDTO dto = new BuyServiceDTO();
        dto.setId(service.getId());
        dto.setTenantId(service.getTenantId());
        dto.setCompanyId(service.getCompanyId());
        dto.setSupplierId(service.getSupplierId());
        dto.setCode(service.getCode());
        dto.setServiceName(service.getServiceName());
        dto.setReference(service.getReference());
        dto.setOrderDate(service.getOrderDate());
        dto.setDeliveryDate(service.getDeliveryDate());
        dto.setBaseValue(service.getBaseValue());
        dto.setTotalValue(service.getTotalValue());
        dto.setPaymentTerms(service.getPaymentTerms());
        dto.setStatus(service.getStatus());
        dto.setNotes(service.getNotes());
        dto.setCreatedAt(service.getCreatedAt() != null ? service.getCreatedAt().atOffset(OffsetDateTime.now().getOffset()) : null);
        dto.setCreatedBy(service.getCreatedBy());
        dto.setUpdatedAt(service.getUpdatedAt() != null ? service.getUpdatedAt().atOffset(OffsetDateTime.now().getOffset()) : null);
        dto.setUpdatedBy(service.getUpdatedBy());
        dto.setApprovedBy(service.getApprovedBy());
        dto.setApprovedAt(service.getApprovedAt() != null ? service.getApprovedAt().atOffset(OffsetDateTime.now().getOffset()) : null);
        dto.setClosedBy(service.getClosedBy());
        dto.setClosedAt(service.getClosedAt() != null ? service.getClosedAt().atOffset(OffsetDateTime.now().getOffset()) : null);
        return dto;
    }
}
