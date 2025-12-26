package br.com.sigeve.sigeve_prodution.controller;

import br.com.sigeve.sigeve_prodution.dto.CompositionItemDTO;
import br.com.sigeve.sigeve_prodution.dto.CompositionCostSummaryDTO;
import br.com.sigeve.sigeve_prodution.dto.CreateCompositionItemDTO;
import br.com.sigeve.sigeve_prodution.dto.UpdateCompositionItemDTO;
import br.com.sigeve.sigeve_prodution.service.CompositionItemService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/composition-items")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class CompositionItemController {

    private final CompositionItemService compositionItemService;
    private final br.com.sigeve.sigeve_prodution.service.CompositionService compositionService;

    @GetMapping("/composition/{compositionId}")
    public ResponseEntity<List<CompositionItemDTO>> getByComposition(@PathVariable UUID compositionId) {
        try {
            log.info("Listando itens da composição: {}", compositionId);

            List<CompositionItemDTO> items = compositionItemService.findByComposition(compositionId);
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            log.error("Erro ao listar itens da composição", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompositionItemDTO> getById(@PathVariable UUID id) {
        try {
            log.info("Buscando item de composição por ID: {}", id);

            Optional<CompositionItemDTO> item = compositionItemService.findById(id);
            return item.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Erro ao buscar item de composição por ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/composition/{compositionId}/costs")
    public ResponseEntity<CompositionCostSummaryDTO> calculateCosts(@PathVariable UUID compositionId) {
        try {
            log.info("Calculando custos da composição: {}", compositionId);

            CompositionCostSummaryDTO costs = compositionItemService.calculateCompositionCosts(compositionId);
            return ResponseEntity.ok(costs);
        } catch (Exception e) {
            log.error("Erro ao calcular custos da composição: {}", compositionId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<CompositionItemDTO> create(
            @Valid @RequestBody CreateCompositionItemDTO request,
            Principal principal) {
        try {
            String username = principal.getName();

            log.info("Criando novo item de composição por usuário: {}", username);

            CompositionItemDTO created = compositionItemService.create(request, username);
            
            // Recalcular custo total da composição
            compositionService.recalculateTotalCost(request.getCompositionId());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            log.error("Erro de validação ao criar item: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erro ao criar item de composição", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompositionItemDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCompositionItemDTO request,
            Principal principal) {
        try {
            String username = principal.getName();

            log.info("Atualizando item de composição: {} por usuário: {}", id, username);

            // Buscar compositionId do item antes de atualizar
            var existingItem = compositionItemService.findById(id);
            UUID compositionId = existingItem.map(i -> i.getCompositionId()).orElse(null);

            CompositionItemDTO updated = compositionItemService.update(id, request, username);
            
            // Recalcular custo total da composição
            if (compositionId != null) {
                compositionService.recalculateTotalCost(compositionId);
            }
            
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            log.error("Erro de validação ao atualizar item: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erro ao atualizar item de composição: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            Principal principal) {
        try {
            String username = principal.getName();

            log.info("Deletando item de composição: {} por usuário: {}", id, username);

            // Buscar compositionId antes de deletar
            var item = compositionItemService.findById(id);
            UUID compositionId = item.map(i -> i.getCompositionId()).orElse(null);
            
            compositionItemService.delete(id, username);
            
            // Recalcular custo total da composição
            if (compositionId != null) {
                compositionService.recalculateTotalCost(compositionId);
            }
            
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("Erro de validação ao deletar item: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erro ao deletar item de composição: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
