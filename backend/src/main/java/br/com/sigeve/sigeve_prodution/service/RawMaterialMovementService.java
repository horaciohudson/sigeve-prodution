package br.com.sigeve.sigeve_prodution.service;

import br.com.sigeve.sigeve_prodution.dto.CreateRawMaterialMovementDTO;
import br.com.sigeve.sigeve_prodution.dto.RawMaterialMovementDTO;
import br.com.sigeve.sigeve_prodution.model.RawMaterialMovement;
import br.com.sigeve.sigeve_prodution.repository.RawMaterialMovementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RawMaterialMovementService {

    private final RawMaterialMovementRepository rawMaterialMovementRepository;

    @Transactional(readOnly = true)
    public List<RawMaterialMovementDTO> findByCompany(UUID companyId) {
        log.debug("Buscando movimentos da empresa: {}", companyId);
        return rawMaterialMovementRepository.findByCompanyIdOrderByMovementDateDesc(companyId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RawMaterialMovementDTO> findByRawMaterial(UUID rawMaterialId) {
        log.debug("Buscando movimentos da matéria-prima: {}", rawMaterialId);
        return rawMaterialMovementRepository.findByRawMaterialIdOrderByMovementDateDesc(rawMaterialId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public RawMaterialMovementDTO create(CreateRawMaterialMovementDTO request, String createdBy) {
        log.debug("Criando novo movimento de estoque");

        RawMaterialMovement movement = new RawMaterialMovement();
        movement.setTenantId(request.getTenantId());
        movement.setCompanyId(request.getCompanyId());
        movement.setRawMaterialId(request.getRawMaterialId());
        movement.setMovementType(request.getMovementType());
        movement.setMovementOrigin(request.getMovementOrigin());
        movement.setOriginId(request.getOriginId());
        movement.setDocumentNumber(request.getDocumentNumber());
        movement.setMovementDate(request.getMovementDate() != null ? request.getMovementDate().toLocalDateTime() : LocalDateTime.now());
        movement.setQuantity(request.getQuantity());
        movement.setUnitCost(request.getUnitCost());
        movement.setTotalCost(request.getTotalCost());
        movement.setUserId(request.getUserId());
        movement.setNotes(request.getNotes());
        movement.setCreatedBy(createdBy);

        RawMaterialMovement saved = rawMaterialMovementRepository.save(movement);
        log.info("Movimento de estoque criado com sucesso: {}", saved.getId());

        return convertToDTO(saved);
    }

    private RawMaterialMovementDTO convertToDTO(RawMaterialMovement movement) {
        RawMaterialMovementDTO dto = new RawMaterialMovementDTO();
        dto.setId(movement.getId());
        dto.setTenantId(movement.getTenantId());
        dto.setCompanyId(movement.getCompanyId());
        dto.setRawMaterialId(movement.getRawMaterialId());
        dto.setMovementType(movement.getMovementType());
        dto.setMovementOrigin(movement.getMovementOrigin());
        dto.setOriginId(movement.getOriginId());
        dto.setDocumentNumber(movement.getDocumentNumber());
        dto.setMovementDate(movement.getMovementDate() != null ? movement.getMovementDate().atOffset(OffsetDateTime.now().getOffset()) : null);
        dto.setQuantity(movement.getQuantity());
        dto.setUnitCost(movement.getUnitCost());
        dto.setTotalCost(movement.getTotalCost());
        dto.setUserId(movement.getUserId());
        dto.setNotes(movement.getNotes());
        dto.setCreatedAt(movement.getCreatedAt() != null ? movement.getCreatedAt().atOffset(OffsetDateTime.now().getOffset()) : null);
        dto.setCreatedBy(movement.getCreatedBy());
        return dto;
    }
}
