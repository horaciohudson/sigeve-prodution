package br.com.sigeve.sigeve_prodution.service;

import br.com.sigeve.sigeve_prodution.dto.CreateTenantRequestDTO;
import br.com.sigeve.sigeve_prodution.dto.TenantDTO;
import br.com.sigeve.sigeve_prodution.dto.UpdateTenantRequestDTO;
import br.com.sigeve.sigeve_prodution.enums.UserStatus;
import br.com.sigeve.sigeve_prodution.model.Tenant;
import br.com.sigeve.sigeve_prodution.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TenantService {

    private final TenantRepository tenantRepository;

    /**
     * Busca todos os tenants ativos
     */
    @Transactional(readOnly = true)
    public List<TenantDTO> findAllActive() {
        log.debug("Buscando todos os tenants ativos");
        List<Tenant> tenants = tenantRepository.findByStatusOrderByName(UserStatus.ACTIVE.name());
        return tenants.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca tenant por ID
     */
    @Transactional(readOnly = true)
    public Optional<TenantDTO> findById(UUID id) {
        log.debug("Buscando tenant por ID: {}", id);
        return tenantRepository.findById(id)
                .filter(tenant -> tenant.getStatus() == UserStatus.ACTIVE)
                .map(this::convertToDTO);
    }

    /**
     * Busca tenant por código
     */
    @Transactional(readOnly = true)
    public Optional<TenantDTO> findByCode(String code) {
        log.debug("Buscando tenant por código: {}", code);
        return tenantRepository.findByCodeAndStatus(code, UserStatus.ACTIVE.name())
                .map(this::convertToDTO);
    }

    /**
     * Cria um novo tenant
     */
    public TenantDTO createTenant(CreateTenantRequestDTO request, String createdBy) {
        log.debug("Criando novo tenant com código: {}", request.getCode());
        
        // Verificar se já existe tenant ativo com o mesmo código
        Optional<Tenant> existingTenant = tenantRepository.findByCode(request.getCode());
        if (existingTenant.isPresent()) {
            if (existingTenant.get().getStatus() == UserStatus.ACTIVE) {
                throw new IllegalArgumentException("Já existe um tenant ativo com o código: " + request.getCode());
            } else {
                // Se existe um tenant inativo, reativa ele
                Tenant tenant = existingTenant.get();
                tenant.setName(request.getName());
                tenant.setStatus(UserStatus.ACTIVE);
                tenant.setUpdatedBy(createdBy);
                
                Tenant savedTenant = tenantRepository.save(tenant);
                log.info("Tenant reativado com sucesso: {}", savedTenant.getId());
                return convertToDTO(savedTenant);
            }
        }

        // Criar novo tenant
        Tenant tenant = new Tenant();
        tenant.setCode(request.getCode());
        tenant.setName(request.getName());
        
        // Converter status de string para enum, default ACTIVE se não fornecido
        UserStatus status = UserStatus.ACTIVE;
        if (request.getStatus() != null && !request.getStatus().isEmpty()) {
            try {
                status = UserStatus.valueOf(request.getStatus().toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Status inválido fornecido: {}, usando ACTIVE como padrão", request.getStatus());
            }
        }
        tenant.setStatus(status);
        tenant.setCreatedBy(createdBy);

        Tenant savedTenant = tenantRepository.save(tenant);
        log.info("Tenant criado com sucesso: {}", savedTenant.getId());
        
        return convertToDTO(savedTenant);
    }

    /**
     * Atualiza um tenant existente
     */
    public TenantDTO updateTenant(UUID id, UpdateTenantRequestDTO request, String updatedBy) {
        log.debug("Atualizando tenant: {}", id);
        
        Tenant tenant = tenantRepository.findById(id)
                .filter(t -> t.getStatus() == UserStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("Tenant não encontrado: " + id));

        tenant.setName(request.getName());
        if (request.getStatus() != null) {
            try {
                UserStatus status = UserStatus.valueOf(request.getStatus().toUpperCase());
                tenant.setStatus(status);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Status inválido: " + request.getStatus());
            }
        }
        tenant.setUpdatedBy(updatedBy);

        Tenant savedTenant = tenantRepository.save(tenant);
        log.info("Tenant atualizado com sucesso: {}", savedTenant.getId());
        
        return convertToDTO(savedTenant);
    }

    /**
     * Desativa um tenant (bloqueio ao invés de exclusão)
     */
    public void deactivateTenant(UUID id, String updatedBy) {
        log.debug("Desativando tenant: {}", id);
        
        Tenant tenant = tenantRepository.findById(id)
                .filter(t -> t.getStatus() == UserStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("Tenant não encontrado: " + id));

        tenant.setStatus(UserStatus.INACTIVE);
        tenant.setUpdatedBy(updatedBy);

        tenantRepository.save(tenant);
        log.info("Tenant desativado com sucesso: {}", id);
    }

    /**
     * Converte entidade Tenant para DTO
     */
    private TenantDTO convertToDTO(Tenant tenant) {
        TenantDTO dto = new TenantDTO();
        dto.setId(tenant.getId());
        dto.setCode(tenant.getCode());
        dto.setName(tenant.getName());
        dto.setStatus(tenant.getStatus().name()); // Converter enum para string
        dto.setCreatedAt(tenant.getCreatedAt() != null ? tenant.getCreatedAt().atOffset(OffsetDateTime.now().getOffset()) : null);
        dto.setUpdatedAt(tenant.getUpdatedAt() != null ? tenant.getUpdatedAt().atOffset(OffsetDateTime.now().getOffset()) : null);
        dto.setCreatedBy(tenant.getCreatedBy());
        dto.setUpdatedBy(tenant.getUpdatedBy());
        return dto;
    }
}