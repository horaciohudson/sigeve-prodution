package br.com.sigeve.sigeve_prodution.controller;

import br.com.sigeve.sigeve_prodution.dto.CreateUserRequestDTO;
import br.com.sigeve.sigeve_prodution.dto.UpdateUserRequestDTO;
import br.com.sigeve.sigeve_prodution.dto.UserDTO;
import br.com.sigeve.sigeve_prodution.mapper.UserMapper;
import br.com.sigeve.sigeve_prodution.model.User;
import br.com.sigeve.sigeve_prodution.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    /**
     * Lista todos os usuários com paginação
     */
    @GetMapping
    public ResponseEntity<Page<UserDTO>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "username") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        log.info("GET /api/users - Listando usuários - Página: {}, Tamanho: {}", page, size);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<User> users = userService.findAll(pageable);
        Page<UserDTO> userDTOs = users.map(userMapper::toDTO);
        
        return ResponseEntity.ok(userDTOs);
    }

    /**
     * Busca usuário por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable UUID id) {
        log.info("GET /api/users/{} - Buscando usuário por ID", id);
        
        return userService.findById(id)
                .map(userMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Busca usuário por username
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        log.info("GET /api/users/username/{} - Buscando usuário por username", username);
        
        return userService.findByUsername(username)
                .map(userMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Cria um novo usuário
     */
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody CreateUserRequestDTO createUserRequest) {
        log.info("POST /api/users - Criando novo usuário: {}", createUserRequest.getUsername());
        
        try {
            User user = userMapper.toEntity(createUserRequest);
            User savedUser = userService.create(user);
            UserDTO userDTO = userMapper.toDTO(savedUser);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);
        } catch (IllegalArgumentException e) {
            log.error("Erro ao criar usuário: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Atualiza um usuário existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRequestDTO updateUserRequest
    ) {
        log.info("PUT /api/users/{} - Atualizando usuário", id);
        
        try {
            User userDetails = userMapper.toEntity(updateUserRequest);
            User updatedUser = userService.update(id, userDetails);
            UserDTO userDTO = userMapper.toDTO(updatedUser);
            
            return ResponseEntity.ok(userDTO);
        } catch (IllegalArgumentException e) {
            log.error("Erro ao atualizar usuário: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Remove um usuário (soft delete)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        log.info("DELETE /api/users/{} - Removendo usuário", id);
        
        try {
            userService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("Erro ao remover usuário: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Bloqueia um usuário
     */
    @PatchMapping("/{id}/block")
    public ResponseEntity<Void> blockUser(@PathVariable UUID id) {
        log.info("PATCH /api/users/{}/block - Bloqueando usuário", id);
        
        try {
            userService.blockUser(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("Erro ao bloquear usuário: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Desbloqueia um usuário
     */
    @PatchMapping("/{id}/unblock")
    public ResponseEntity<Void> unblockUser(@PathVariable UUID id) {
        log.info("PATCH /api/users/{}/unblock - Desbloqueando usuário", id);
        
        try {
            userService.unblockUser(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("Erro ao desbloquear usuário: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
