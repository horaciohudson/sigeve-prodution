package br.com.sigeve.sigeve_prodution.service;

import br.com.sigeve.sigeve_prodution.dto.CreateProductionClosureDTO;
import br.com.sigeve.sigeve_prodution.dto.ProductionClosureDTO;
import br.com.sigeve.sigeve_prodution.model.ProductionClosure;
import br.com.sigeve.sigeve_prodution.repository.ProductionClosureRepository;
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
public class ProductionClosureService {

    private final ProductionClosureRepository productionClosureRepository;

    @Transactional(readOnly = true)
    public List<ProductionClosureDTO> findByCompany(UUID companyId) {
        log.debug("Buscando fechamentos da empresa: {}", companyId);
        return productionClosureRepository.findByCompanyId(companyId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ProductionClosureDTO> findByProductionOrder(UUID productionOrderId) {
        log.debug("Buscando fechamento da ordem: {}", productionOrderId);
        return productionClosureRepository.findByProductionOrderId(productionOrderId)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public List<ProductionClosureDTO> findByExportStatus(UUID companyId, Boolean exported) {
        log.debug("Buscando fechamentos por status de exportação: {} na empresa: {}", exported, companyId);
        return productionClosureRepository.findByCompanyIdAndExportedToFinancial(companyId, exported).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ProductionClosureDTO create(CreateProductionClosureDTO request, String createdBy) {
        log.debug("Criando novo fechamento de produção");

        Optional<ProductionClosure> existing = productionClosureRepository
                .findByProductionOrderId(request.getProductionOrderId());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Já existe um fechamento para esta ordem de produção");
        }

        ProductionClosure closure = new ProductionClosure();
        closure.setTenantId(request.getTenantId());
        closure.setCompanyId(request.getCompanyId());
        closure.setProductionOrderId(request.getProductionOrderId());
        closure.setTotalMaterial(request.getTotalMaterial());
        closure.setTotalService(request.getTotalService());
        closure.setTotalLabor(request.getTotalLabor());
        closure.setTotalIndirect(request.getTotalIndirect());
        closure.setTotalCost(request.getTotalCost());
        closure.setClosureDate(request.getClosureDate());
        closure.setClosedAt(LocalDateTime.now());
        closure.setClosedBy(createdBy);
        closure.setExportedToFinancial(false);
        closure.setNotes(request.getNotes());
        closure.setCreatedBy(createdBy);

        ProductionClosure saved = productionClosureRepository.save(closure);
        log.info("Fechamento de produção criado com sucesso: {}", saved.getId());

        return convertToDTO(saved);
    }

    public ProductionClosureDTO exportToFinancial(UUID id, UUID financialDocumentId, String exportedBy) {
        log.debug("Exportando fechamento para financeiro: {}", id);

        ProductionClosure closure = productionClosureRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Fechamento não encontrado: " + id));

        if (closure.getExportedToFinancial()) {
            throw new IllegalStateException("Fechamento já foi exportado para o financeiro");
        }

        closure.setExportedToFinancial(true);
        closure.setFinancialExportDate(LocalDateTime.now());
        closure.setFinancialDocumentId(financialDocumentId);

        ProductionClosure saved = productionClosureRepository.save(closure);
        log.info("Fechamento exportado para financeiro: {}", id);

        return convertToDTO(saved);
    }

    private ProductionClosureDTO convertToDTO(ProductionClosure closure) {
        ProductionClosureDTO dto = new ProductionClosureDTO();
        dto.setId(closure.getId());
        dto.setTenantId(closure.getTenantId());
        dto.setCompanyId(closure.getCompanyId());
        dto.setProductionOrderId(closure.getProductionOrderId());
        dto.setTotalMaterial(closure.getTotalMaterial());
        dto.setTotalService(closure.getTotalService());
        dto.setTotalLabor(closure.getTotalLabor());
        dto.setTotalIndirect(closure.getTotalIndirect());
        dto.setTotalCost(closure.getTotalCost());
        dto.setClosureDate(closure.getClosureDate());
        dto.setClosedAt(closure.getClosedAt() != null ? closure.getClosedAt().atOffset(OffsetDateTime.now().getOffset()) : null);
        dto.setClosedBy(closure.getClosedBy());
        dto.setExportedToFinancial(closure.getExportedToFinancial());
        dto.setFinancialExportDate(closure.getFinancialExportDate() != null ? closure.getFinancialExportDate().atOffset(OffsetDateTime.now().getOffset()) : null);
        dto.setFinancialDocumentId(closure.getFinancialDocumentId());
        dto.setNotes(closure.getNotes());
        dto.setCreatedAt(closure.getCreatedAt() != null ? closure.getCreatedAt().atOffset(OffsetDateTime.now().getOffset()) : null);
        dto.setCreatedBy(closure.getCreatedBy());
        return dto;
    }
}
