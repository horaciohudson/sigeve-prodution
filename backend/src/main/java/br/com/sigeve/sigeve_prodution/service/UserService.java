package br.com.sigeve.sigeve_prodution.service;

import br.com.sigeve.sigeve_prodution.enums.UserStatus;
import br.com.sigeve.sigeve_prodution.model.User;
import br.com.sigeve.sigeve_prodution.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Lista todos os usuários ativos
     */
    public List<User> findAll() {
        log.info("Listando todos os usuários");
        return userRepository.findAll();
    }

    /**
     * Lista usuários com paginação
     */
    public Page<User> findAll(Pageable pageable) {
        log.info("Listando usuários com paginação");
        return userRepository.findAll(pageable);
    }

    /**
     * Busca usuário por ID
     */
    public Optional<User> findById(UUID id) {
        log.info("Buscando usuário por ID: {}", id);
        return userRepository.findById(id);
    }

    /**
     * Busca usuário por username
     */
    public Optional<User> findByUsername(String username) {
        log.info("Buscando usuário por username: {}", username);
        return userRepository.findByUsername(username);
    }

    /**
     * Busca usuário por username e tenant
     */
    public Optional<User> findByUsernameAndTenant(String username, UUID tenantId) {
        log.info("Buscando usuário por username: {} e tenant: {}", username, tenantId);
        return userRepository.findByUsernameAndTenantId(username, tenantId);
    }

    /**
     * Cria um novo usuário
     */
    public User create(User user) {
        log.info("Criando novo usuário: {}", user.getUsername());
        
        // Validações
        validateUser(user);
        
        // Verificar se username já existe
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username já existe: " + user.getUsername());
        }
        
        // Criptografar senha
        if (user.getPasswordHash() != null && !user.getPasswordHash().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        }
        
        // Definir valores padrão
        if (user.getStatus() == null) {
            user.setStatus(UserStatus.ACTIVE);
        }
        
        if (user.getFailedAttempts() == null) {
            user.setFailedAttempts(0);
        }
        
        if (user.getLanguage() == null || user.getLanguage().isEmpty()) {
            user.setLanguage("pt");
        }
        
        if (user.getTimezone() == null || user.getTimezone().isEmpty()) {
            user.setTimezone("America/Sao_Paulo");
        }
        
        User savedUser = userRepository.save(user);
        log.info("Usuário criado com sucesso: {}", savedUser.getId());
        return savedUser;
    }

    /**
     * Atualiza um usuário existente
     */
    public User update(UUID id, User userDetails) {
        log.info("Atualizando usuário: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + id));
        
        // Preservar tenantId existente se não fornecido nos detalhes
        if (userDetails.getTenantId() == null) {
            userDetails.setTenantId(user.getTenantId());
        }
        
        // Validações para atualização (preserva tenantId existente)
        validateUserForUpdate(userDetails);
        
        // Verificar se username já existe (exceto para o próprio usuário)
        Optional<User> existingUser = userRepository.findByUsername(userDetails.getUsername());
        if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
            throw new IllegalArgumentException("Username já existe: " + userDetails.getUsername());
        }
        
        // Atualizar campos
        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        user.setFullName(userDetails.getFullName());
        user.setStatus(userDetails.getStatus());
        user.setLanguage(userDetails.getLanguage());
        user.setTimezone(userDetails.getTimezone());
        user.setSystemAdmin(userDetails.isSystemAdmin());
        
        // Atualizar senha apenas se fornecida
        if (userDetails.getPasswordHash() != null && !userDetails.getPasswordHash().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(userDetails.getPasswordHash()));
        }
        
        // Atualizar roles apenas se fornecidas explicitamente
        // Se não foram fornecidas, preservar as roles existentes
        if (userDetails.getRoles() != null && !userDetails.getRoles().isEmpty()) {
            user.setRoles(userDetails.getRoles());
        }
        // Se userDetails.getRoles() for null ou vazio, mantém as roles existentes
        
        User updatedUser = userRepository.save(user);
        log.info("Usuário atualizado com sucesso: {}", updatedUser.getId());
        return updatedUser;
    }

    /**
     * Remove um usuário (soft delete)
     */
    public void delete(UUID id) {
        log.info("Removendo usuário: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + id));
        
        // Soft delete - apenas desativa o usuário
        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);
        
        log.info("Usuário removido com sucesso: {}", id);
    }

    /**
     * Bloqueia um usuário
     */
    public void blockUser(UUID id) {
        log.info("Bloqueando usuário: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + id));
        
        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);
        
        log.info("Usuário bloqueado com sucesso: {}", id);
    }

    /**
     * Desbloqueia um usuário
     */
    public void unblockUser(UUID id) {
        log.info("Desbloqueando usuário: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + id));
        
        user.setStatus(UserStatus.ACTIVE);
        user.setFailedAttempts(0);
        user.setLockedUntil(null);
        userRepository.save(user);
        
        log.info("Usuário desbloqueado com sucesso: {}", id);
    }

    /**
     * Valida os dados do usuário
     */
    private void validateUser(User user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username é obrigatório");
        }
        
        if (user.getUsername().length() > 50) {
            throw new IllegalArgumentException("Username deve ter no máximo 50 caracteres");
        }
        
        if (user.getFullName() == null || user.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome completo é obrigatório");
        }
        
        if (user.getFullName().length() > 100) {
            throw new IllegalArgumentException("Nome completo deve ter no máximo 100 caracteres");
        }
        
        if (user.getEmail() != null && user.getEmail().length() > 120) {
            throw new IllegalArgumentException("E-mail deve ter no máximo 120 caracteres");
        }
        
        if (user.getTenantId() == null) {
            throw new IllegalArgumentException("Sistema Cliente é obrigatório");
        }
    }

    /**
     * Valida os dados do usuário para atualização (não exige tenantId pois é preservado)
     */
    private void validateUserForUpdate(User user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username é obrigatório");
        }
        
        if (user.getUsername().length() > 50) {
            throw new IllegalArgumentException("Username deve ter no máximo 50 caracteres");
        }
        
        if (user.getFullName() == null || user.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome completo é obrigatório");
        }
        
        if (user.getFullName().length() > 100) {
            throw new IllegalArgumentException("Nome completo deve ter no máximo 100 caracteres");
        }
        
        if (user.getEmail() != null && user.getEmail().length() > 120) {
            throw new IllegalArgumentException("E-mail deve ter no máximo 120 caracteres");
        }
        
        // Não validar tenantId aqui pois é preservado do usuário existente
    }
}