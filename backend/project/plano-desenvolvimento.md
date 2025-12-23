# 📋 Plano de Desenvolvimento - Módulo de Produção SIGEVE

## 🎯 Visão Geral

Sistema de gestão de produção integrado ao SIGEVE, permitindo controle completo desde a composição de produtos até o fechamento e integração financeira.

---

## 📊 Análise da Modelagem Atual

### ✅ Pontos Fortes
- Estrutura bem organizada em 8 módulos principais
- Separação clara entre custo padrão (composição) e custo real (apontamento)
- Suporte a serviços terceirizados (costura, lavagem, bordado)
- Controle de estoque de matéria-prima
- Rastreabilidade de execução por etapas

### ⚠️ Campos Faltantes Identificados

#### 🔴 **Campos Críticos de Auditoria e Controle**
Todas as tabelas precisam de:
- `tenant_id` (UUID) - Multi-tenancy (isolamento por sistema/cliente)
- `company_id` (UUID) - Multi-empresa (isolamento por empresa dentro do tenant)
- `created_at` (TIMESTAMPTZ)
- `created_by` (VARCHAR)
- `updated_at` (TIMESTAMPTZ)
- `updated_by` (VARCHAR)
- `deleted_at` (TIMESTAMPTZ) - Soft delete
- `deleted_by` (VARCHAR)
- `is_active` (BOOLEAN)
- `version` (INTEGER) - Controle de concorrência otimista

> **⚠️ IMPORTANTE**: Assim como no financeiro-integrado, **TODAS** as tabelas devem ter `tenant_id` + `company_id` para garantir isolamento completo dos dados entre empresas.

#### 🟡 **Campos Importantes por Tabela**

**tab_production_orders:**
- `company_id` (UUID) - **Empresa** (isolamento multi-empresa)
- `priority` (ENUM: LOW, MEDIUM, HIGH, URGENT)
- `customer_id` (UUID) - Cliente final
- `order_id` (UUID) - Pedido de venda relacionado
- `deadline` (DATE) - Prazo de entrega
- `notes` (TEXT) - Observações
- `approved_by` (VARCHAR)
- `approved_at` (TIMESTAMPTZ)
- `finished_by` (VARCHAR)
- `finished_at` (TIMESTAMPTZ)
- `canceled_reason` (TEXT)

**tab_production_products:**
- `company_id` (UUID) - **Empresa**
- `sku` (VARCHAR) - Código SKU
- `barcode` (VARCHAR) - Código de barras
- `unit_type` (ENUM: UN, KG, M, M2, etc.)
- `image_url` (VARCHAR)
- `notes` (TEXT)

**tab_compositions:**
- `company_id` (UUID) - **Empresa**
- `name` (VARCHAR) - Nome da composição
- `effective_date` (DATE) - Data de vigência
- `expiration_date` (DATE) - Data de expiração
- `created_by`, `approved_by`

**tab_composition_items:**
- `company_id` (UUID) - **Empresa**
- `sequence` (INTEGER) - Ordem de montagem
- `is_optional` (BOOLEAN)
- `notes` (TEXT)

**tab_raw_materials:**
- `company_id` (UUID) - **Empresa**
- `code` (VARCHAR) - Código único
- `supplier_id` (UUID) - Fornecedor principal
- `min_stock` (DECIMAL) - Estoque mínimo
- `max_stock` (DECIMAL) - Estoque máximo
- `reorder_point` (DECIMAL) - Ponto de reposição
- `lead_time_days` (INTEGER) - Prazo de entrega
- `last_purchase_price` (DECIMAL)
- `last_purchase_date` (DATE)
- `category_id` (UUID) - Categoria/grupo

**tab_raw_material_stocks:**
- `company_id` (UUID) - **Empresa**
- `warehouse_id` (UUID) - Localização
- `reserved_quantity` (DECIMAL) - Quantidade reservada
- `available_quantity` (DECIMAL) - Quantidade disponível
- `last_movement_date` (TIMESTAMPTZ)

**tab_raw_material_movements:**
- `company_id` (UUID) - **Empresa**
- `document_number` (VARCHAR) - Número do documento
- `movement_date` (TIMESTAMPTZ)
- `user_id` (UUID) - Usuário responsável
- `notes` (TEXT)
- `total_cost` (DECIMAL) - Custo total do movimento

**tab_buy_services:**
- `company_id` (UUID) - **Empresa**
- `code` (VARCHAR) - Código único
- `order_date` (DATE)
- `delivery_date` (DATE)
- `payment_terms` (VARCHAR)
- `notes` (TEXT)
- `approved_by`, `approved_at`
- `closed_by`, `closed_at`

**tab_buy_service_items:**
- `company_id` (UUID) - **Empresa**
- `sequence` (INTEGER)
- `delivery_date` (DATE)
- `quantity_received` (DECIMAL)
- `notes` (TEXT)

**tab_production_steps:**
- `company_id` (UUID) - **Empresa**
- `description` (TEXT)
- `estimated_time` (INTEGER) - Tempo estimado em minutos
- `cost_center_id` (UUID) - Centro de custo
- `is_outsourced` (BOOLEAN) - Terceirizado?
- `requires_approval` (BOOLEAN)

**tab_production_executions:**
- `company_id` (UUID) - **Empresa**
- `employee_id` (UUID) - Funcionário/operador
- `machine_id` (UUID) - Máquina utilizada
- `notes` (TEXT)
- `quality_status` (ENUM: APPROVED, REJECTED, REWORK)
- `rejection_reason` (TEXT)

**tab_production_costs:**
- `company_id` (UUID) - **Empresa**
- `cost_date` (DATE)
- `notes` (TEXT)
- `approved_by`, `approved_at`

**tab_production_closures:**
- `company_id` (UUID) - **Empresa**
- `closure_date` (DATE)
- `closed_by` (VARCHAR)
- `financial_export_date` (TIMESTAMPTZ)
- `financial_document_id` (UUID)
- `notes` (TEXT)

---

## 🔗 Relacionamentos e Constraints

### Foreign Keys Principais

```sql
-- Production Orders
production_orders.product_id → production_products.id
production_orders.customer_id → tab_customers.id
production_orders.order_id → tab_orders.id

-- Compositions
compositions.production_product_id → production_products.id
composition_items.composition_id → compositions.id
composition_items.reference_id → raw_materials.id OR buy_services.id

-- Raw Materials
raw_material_stocks.raw_material_id → raw_materials.id
raw_material_movements.raw_material_id → raw_materials.id

-- Buy Services
buy_services.supplier_id → tab_suppliers.id
buy_service_items.buy_service_id → buy_services.id

-- Production Execution
production_executions.production_order_id → production_orders.id
production_executions.step_id → production_steps.id
production_executions.employee_id → tab_employees.id

-- Production Costs
production_costs.production_order_id → production_orders.id

-- Production Closures
production_closures.production_order_id → production_orders.id
```

### Índices Recomendados

```sql
-- Performance crítica - Multi-tenant + Multi-empresa
CREATE INDEX idx_production_orders_company_status ON tab_production_orders(company_id, status);
CREATE INDEX idx_production_orders_tenant_company ON tab_production_orders(tenant_id, company_id);
CREATE INDEX idx_production_orders_dates ON tab_production_orders(start_date, end_date);
CREATE INDEX idx_composition_items_company_type ON tab_composition_items(company_id, item_type, reference_id);
CREATE INDEX idx_raw_material_movements_company_date ON tab_raw_material_movements(company_id, movement_date DESC);
CREATE INDEX idx_production_executions_company_order ON tab_production_executions(company_id, production_order_id);
CREATE INDEX idx_raw_materials_company_code ON tab_raw_materials(company_id, code);
CREATE INDEX idx_buy_services_company_status ON tab_buy_services(company_id, status);

-- Garantir unicidade por empresa
CREATE UNIQUE INDEX uq_production_orders_company_code ON tab_production_orders(company_id, code) WHERE deleted_at IS NULL;
CREATE UNIQUE INDEX uq_raw_materials_company_code ON tab_raw_materials(company_id, code) WHERE deleted_at IS NULL;
CREATE UNIQUE INDEX uq_buy_services_company_code ON tab_buy_services(company_id, code) WHERE deleted_at IS NULL;
```

---

## 📐 Regras de Negócio

### 1. Ordem de Produção
- ✅ Só pode ser iniciada se status = PLANNED
- ✅ Só pode ser finalizada se todas as etapas estiverem concluídas
- ✅ Quantidade produzida não pode exceder quantidade planejada + tolerância (configurável)
- ✅ Cancelamento requer motivo obrigatório

### 2. Composição (BOM)
- ✅ Apenas uma composição ativa por produto
- ✅ Versionamento obrigatório ao alterar
- ✅ Não pode deletar composição em uso

### 3. Estoque de Matéria-Prima
- ✅ Movimentação OUT requer estoque disponível
- ✅ Custo médio recalculado a cada entrada
- ✅ Reserva automática ao criar OP

### 4. Execução de Produção
- ✅ Etapas devem seguir sequência definida
- ✅ Perda não pode exceder quantidade produzida
- ✅ Registro de tempo obrigatório

### 5. Fechamento
- ✅ Só pode fechar OP com status FINISHED
- ✅ Exportação financeira é irreversível
- ✅ Custos devem estar todos apontados

---

## 🚀 Ordem de Implementação Sugerida

### Fase 1: Fundação (Semana 1-2)
1. ✅ Criar ENUMs necessários
2. ✅ Implementar `tab_raw_materials`
3. ✅ Implementar `tab_raw_material_stocks`
4. ✅ Implementar `tab_raw_material_movements`
5. ✅ Criar serviços de controle de estoque

### Fase 2: Composição (Semana 3)
6. ✅ Implementar `tab_production_products`
7. ✅ Implementar `tab_compositions`
8. ✅ Implementar `tab_composition_items`
9. ✅ Criar cálculo de custo padrão

### Fase 3: Serviços Terceirizados (Semana 4)
10. ✅ Implementar `tab_buy_services`
11. ✅ Implementar `tab_buy_service_items`
12. ✅ Integrar com composição

### Fase 4: Produção (Semana 5-6)
13. ✅ Implementar `tab_production_orders`
14. ✅ Implementar `tab_production_steps`
15. ✅ Implementar `tab_production_executions`
16. ✅ Criar workflow de produção

### Fase 5: Custos e Fechamento (Semana 7)
17. ✅ Implementar `tab_production_costs`
18. ✅ Implementar `tab_production_closures`
19. ✅ Criar integração financeira

### Fase 6: Testes e Ajustes (Semana 8)
20. ✅ Testes de integração
21. ✅ Ajustes de performance
22. ✅ Documentação final

---

## 🎨 Melhorias Sugeridas

### 1. Adicionar Módulo de Qualidade
```
tab_quality_inspections
- id
- production_order_id
- step_id
- inspector_id
- inspection_date
- status (APPROVED, REJECTED, REWORK)
- defect_quantity
- defect_type
- notes
```

### 2. Adicionar Rastreabilidade de Lotes
```
tab_production_batches
- id
- production_order_id
- batch_number
- quantity
- manufacturing_date
- expiration_date
```

### 3. Adicionar Controle de Máquinas/Equipamentos
```
tab_machines
- id
- code
- name
- type
- status (AVAILABLE, IN_USE, MAINTENANCE, BROKEN)
- last_maintenance_date
```

### 4. Adicionar Planejamento de Capacidade
```
tab_production_capacity
- id
- date
- shift (MORNING, AFTERNOON, NIGHT)
- available_hours
- planned_hours
- utilized_hours
```

---

## 📝 Notas Técnicas

### Tecnologias Recomendadas
- **Backend**: Java 17+ com Spring Boot 3.x
- **Database**: PostgreSQL 14+
- **ORM**: JPA/Hibernate
- **Validação**: Bean Validation (JSR-380)
- **Auditoria**: Spring Data JPA Auditing
- **Segurança**: Spring Security com JWT

### Padrões de Código
- DTOs separados para Request/Response
- Mappers com MapStruct
- Services com transações declarativas
- Repositories com Spring Data JPA
- Controllers REST com versionamento

### Performance
- Lazy loading para relacionamentos
- Paginação obrigatória em listagens
- Cache para dados mestres (produtos, materiais)
- Índices em campos de busca frequente

---

## ✅ Checklist de Validação

Antes de considerar o módulo completo:

- [ ] Todas as tabelas têm campos de auditoria
- [ ] Todos os relacionamentos estão mapeados
- [ ] Índices criados para queries principais
- [ ] Constraints de integridade implementadas
- [ ] Regras de negócio validadas no backend
- [ ] Testes unitários > 80% cobertura
- [ ] Testes de integração para fluxos principais
- [ ] Documentação API (Swagger/OpenAPI)
- [ ] Logs estruturados implementados
- [ ] Tratamento de erros padronizado

---

**Documento criado em**: 2025-12-14  
**Versão**: 1.0  
**Autor**: Análise automatizada SIGEVE
