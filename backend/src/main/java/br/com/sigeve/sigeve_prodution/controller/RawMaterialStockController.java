package br.com.sigeve.sigeve_prodution.controller;

import br.com.sigeve.sigeve_prodution.dto.RawMaterialStockDTO;
import br.com.sigeve.sigeve_prodution.service.RawMaterialStockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/raw-material-stocks")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class RawMaterialStockController {

    private final RawMaterialStockService rawMaterialStockService;

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<RawMaterialStockDTO>> getByCompany(@PathVariable UUID companyId) {
        try {
            log.info("Listando estoques da empresa: {}", companyId);

            List<RawMaterialStockDTO> stocks = rawMaterialStockService.findByCompany(companyId);
            return ResponseEntity.ok(stocks);
        } catch (Exception e) {
            log.error("Erro ao listar estoques", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/raw-material/{rawMaterialId}")
    public ResponseEntity<RawMaterialStockDTO> getByRawMaterial(@PathVariable UUID rawMaterialId) {
        try {
            log.info("Buscando estoque da matéria-prima: {}", rawMaterialId);

            Optional<RawMaterialStockDTO> stock = rawMaterialStockService.findByRawMaterial(rawMaterialId);
            return stock.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Erro ao buscar estoque: {}", rawMaterialId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<RawMaterialStockDTO>> getLowStock(
            @RequestParam UUID companyId,
            @RequestParam(defaultValue = "10") BigDecimal threshold) {
        try {
            log.info("Buscando estoques baixos da empresa: {} com limite: {}", companyId, threshold);

            List<RawMaterialStockDTO> stocks = rawMaterialStockService.findLowStock(companyId, threshold);
            return ResponseEntity.ok(stocks);
        } catch (Exception e) {
            log.error("Erro ao buscar estoques baixos", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
