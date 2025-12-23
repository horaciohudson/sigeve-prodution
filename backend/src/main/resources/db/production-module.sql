-- =====================================================
-- MÓDULO DE PRODUÇÃO - SIGEVE
-- =====================================================
-- Sistema completo de gestão de produção
-- Inclui: Ordens de Produção, Composições (BOM), Matéria-Prima,
--         Serviços Terceirizados, Execução e Custos
-- =====================================================
-- Versão: 1.0
-- Data: 2025-12-14
-- =====================================================

-- =====================================================
-- EXTENSÕES NECESSÁRIAS
-- =====================================================
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
--CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- =====================================================
-- ENUMS
-- =====================================================

-- Status da Ordem de Produção
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname='production_order_status') THEN
    CREATE TYPE production_order_status AS ENUM (
      'PLANNED',        -- Planejada
      'IN_PROGRESS',    -- Em andamento
      'FINISHED',       -- Finalizada
      'CANCELED'        -- Cancelada
    );
  END IF;
END$$;

-- Prioridade
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname='priority_level') THEN
    CREATE TYPE priority_level AS ENUM (
      'LOW',            -- Baixa
      'MEDIUM',         -- Média
      'HIGH',           -- Alta
      'URGENT'          -- Urgente
    );
  END IF;
END$$;

-- Tipo de Item na Composição
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname='composition_item_type') THEN
    CREATE TYPE composition_item_type AS ENUM (
      'RAW_MATERIAL',   -- Matéria-prima
      'SERVICE'         -- Serviço terceirizado
    );
  END IF;
END$$;

-- Tipo de Unidade
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname='unit_type') THEN
    CREATE TYPE unit_type AS ENUM (
      'UN',             -- Unidade
      'KG',             -- Quilograma
      'G',              -- Grama
      'M',              -- Metro
      'M2',             -- Metro quadrado
      'M3',             -- Metro cúbico
      'L',              -- Litro
      'ML',             -- Mililitro
      'PC',             -- Peça
      'CX',             -- Caixa
      'FD',             -- Fardo
      'RL',             -- Rolo
      'HR'              -- Hora (para serviços)
    );
  END IF;
END$$;

-- Tipo de Movimento de Estoque
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname='stock_movement_type') THEN
    CREATE TYPE stock_movement_type AS ENUM (
      'IN',             -- Entrada
      'OUT'             -- Saída
    );
  END IF;
END$$;

-- Origem do Movimento
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname='movement_origin') THEN
    CREATE TYPE movement_origin AS ENUM (
      'PURCHASE',       -- Compra
      'PRODUCTION',     -- Produção
      'ADJUSTMENT',     -- Ajuste
      'RETURN',         -- Devolução
      'TRANSFER'        -- Transferência
    );
  END IF;
END$$;

-- Status de Compra de Serviço
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname='buy_service_status') THEN
    CREATE TYPE buy_service_status AS ENUM (
      'OPEN',           -- Aberta
      'APPROVED',       -- Aprovada
      'CLOSED'          -- Fechada
    );
  END IF;
END$$;

-- Status de Qualidade
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname='quality_status') THEN
    CREATE TYPE quality_status AS ENUM (
      'APPROVED',       -- Aprovado
      'REJECTED',       -- Rejeitado
      'REWORK'          -- Retrabalho
    );
  END IF;
END$$;

-- Tipo de Custo de Produção
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname='production_cost_type') THEN
    CREATE TYPE production_cost_type AS ENUM (
      'MATERIAL',       -- Material
      'SERVICE',        -- Serviço
      'LABOR',          -- Mão de obra
      'INDIRECT'        -- Custo indireto
    );
  END IF;
END$$;

-- =====================================================
-- TABELA: PRODUTOS DE PRODUÇÃO
-- =====================================================
CREATE TABLE IF NOT EXISTS tab_production_products (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id           UUID NOT NULL REFERENCES tab_tenants(tenant_id) ON DELETE CASCADE,
    company_id          UUID NOT NULL REFERENCES tab_companies(company_id) ON DELETE RESTRICT,
    
    -- Referência ao produto comercial (Gestor)
    product_id          UUID REFERENCES tab_products(product_id),
    
    -- Identificação
    sku                 VARCHAR(50),
    barcode             VARCHAR(50),
    description         VARCHAR(500) NOT NULL,
    size                VARCHAR(50),
    color               VARCHAR(50),
    unit_type           unit_type NOT NULL DEFAULT 'UN',
    
    -- Imagem e observações
    image_url           VARCHAR(500),
    notes               TEXT,
    
    -- Controle
    is_active           BOOLEAN NOT NULL DEFAULT TRUE,
    
    -- Auditoria completa
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by          VARCHAR(100),
    updated_at          TIMESTAMPTZ,
    updated_by          VARCHAR(100),
    deleted_at          TIMESTAMPTZ,
    deleted_by          VARCHAR(100),
    version             INTEGER NOT NULL DEFAULT 1,
    
    -- Constraints
    CONSTRAINT uq_production_products_company_sku UNIQUE (company_id, sku)
);

-- =====================================================
-- TABELA: COMPOSIÇÕES (BOM - Bill of Materials)
-- =====================================================
CREATE TABLE IF NOT EXISTS tab_compositions (
    id                      UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id               UUID NOT NULL REFERENCES tab_tenants(tenant_id) ON DELETE CASCADE,
    company_id              UUID NOT NULL REFERENCES tab_companies(company_id) ON DELETE RESTRICT,
    
    -- Produto
    production_product_id   UUID NOT NULL REFERENCES tab_production_products(id) ON DELETE RESTRICT,
    
    -- Identificação
    name                    VARCHAR(200) NOT NULL,
    version                 INTEGER NOT NULL DEFAULT 1,
    
    -- Vigência
    effective_date          DATE,
    expiration_date         DATE,
    
    -- Controle
    is_active               BOOLEAN NOT NULL DEFAULT TRUE,
    notes                   TEXT,
    
    -- Auditoria completa
    created_at              TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by              VARCHAR(100),
    updated_at              TIMESTAMPTZ,
    updated_by              VARCHAR(100),
    deleted_at              TIMESTAMPTZ,
    deleted_by              VARCHAR(100),
    
    -- Aprovação
    approved_by             VARCHAR(100),
    approved_at             TIMESTAMPTZ,
    
    -- Constraints
    CONSTRAINT chk_composition_dates CHECK (
        expiration_date IS NULL OR effective_date IS NULL OR expiration_date > effective_date
    ),
    CONSTRAINT uq_composition_product_version UNIQUE (company_id, production_product_id, version)
);

-- =====================================================
-- TABELA: ITENS DA COMPOSIÇÃO
-- =====================================================
CREATE TABLE IF NOT EXISTS tab_composition_items (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id           UUID NOT NULL REFERENCES tab_tenants(tenant_id) ON DELETE CASCADE,
    company_id          UUID NOT NULL REFERENCES tab_companies(company_id) ON DELETE RESTRICT,
    
    -- Composição
    composition_id      UUID NOT NULL REFERENCES tab_compositions(id) ON DELETE CASCADE,
    
    -- Tipo e referência
    item_type           composition_item_type NOT NULL,
    reference_id        UUID NOT NULL,  -- ID da matéria-prima ou serviço
    
    -- Quantidade
    sequence            INTEGER NOT NULL DEFAULT 1,
    unit_type           unit_type NOT NULL,
    quantity            NUMERIC(15,4) NOT NULL CHECK (quantity > 0),
    loss_percentage     NUMERIC(5,2) DEFAULT 0 CHECK (loss_percentage >= 0 AND loss_percentage <= 100),
    
    -- Custos (snapshot)
    unit_cost           NUMERIC(15,4),
    total_cost          NUMERIC(15,4),
    
    -- Controle
    is_optional         BOOLEAN NOT NULL DEFAULT FALSE,
    notes               TEXT,
    
    -- Auditoria
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by          VARCHAR(100),
    updated_at          TIMESTAMPTZ,
    updated_by          VARCHAR(100),
    deleted_at          TIMESTAMPTZ,
    deleted_by          VARCHAR(100)
);

-- =====================================================
-- TABELA: MATÉRIAS-PRIMAS
-- =====================================================
CREATE TABLE IF NOT EXISTS tab_raw_materials (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id           UUID NOT NULL REFERENCES tab_tenants(tenant_id) ON DELETE CASCADE,
    company_id          UUID NOT NULL REFERENCES tab_companies(company_id) ON DELETE RESTRICT,
    
    -- Identificação
    code                VARCHAR(50) NOT NULL,
    name                VARCHAR(200) NOT NULL,
    unit_type           unit_type NOT NULL,
    
    -- Fornecedor
    supplier_id         UUID REFERENCES tab_suppliers(supplier_id),
    
    -- Custos
    average_cost        NUMERIC(15,4) DEFAULT 0,
    last_purchase_price NUMERIC(15,4),
    last_purchase_date  DATE,
    
    -- Controle de estoque
    stock_control       BOOLEAN NOT NULL DEFAULT TRUE,
    min_stock           NUMERIC(15,4) DEFAULT 0,
    max_stock           NUMERIC(15,4),
    reorder_point       NUMERIC(15,4),
    lead_time_days      INTEGER DEFAULT 0,
    
    -- Categoria
    category_id         UUID REFERENCES tab_material_groups(group_id),
    
    -- Controle
    is_active           BOOLEAN NOT NULL DEFAULT TRUE,
    
    -- Auditoria completa
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by          VARCHAR(100),
    updated_at          TIMESTAMPTZ,
    updated_by          VARCHAR(100),
    deleted_at          TIMESTAMPTZ,
    deleted_by          VARCHAR(100),
    version             INTEGER NOT NULL DEFAULT 1,
    
    -- Constraints
    CONSTRAINT uq_raw_materials_company_code UNIQUE (company_id, code)
);

-- =====================================================
-- TABELA: ESTOQUE DE MATÉRIAS-PRIMAS
-- =====================================================
CREATE TABLE IF NOT EXISTS tab_raw_material_stocks (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id           UUID NOT NULL REFERENCES tab_tenants(tenant_id) ON DELETE CASCADE,
    company_id          UUID NOT NULL REFERENCES tab_companies(company_id) ON DELETE RESTRICT,
    
    -- Material
    raw_material_id     UUID NOT NULL REFERENCES tab_raw_materials(id) ON DELETE RESTRICT,
    
    -- Localização (opcional)
    warehouse_id        UUID,
    
    -- Quantidades
    quantity            NUMERIC(15,4) NOT NULL DEFAULT 0,
    reserved_quantity   NUMERIC(15,4) NOT NULL DEFAULT 0,
    available_quantity  NUMERIC(15,4) GENERATED ALWAYS AS (quantity - reserved_quantity) STORED,
    
    -- Controle
    last_movement_date  TIMESTAMPTZ,
    
    -- Auditoria
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ,
    
    -- Constraints
    CONSTRAINT chk_stock_quantities CHECK (reserved_quantity >= 0 AND reserved_quantity <= quantity),
    CONSTRAINT uq_stock_material_warehouse UNIQUE (company_id, raw_material_id, warehouse_id)
);

-- =====================================================
-- TABELA: MOVIMENTOS DE ESTOQUE
-- =====================================================
CREATE TABLE IF NOT EXISTS tab_raw_material_movements (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id           UUID NOT NULL REFERENCES tab_tenants(tenant_id) ON DELETE CASCADE,
    company_id          UUID NOT NULL REFERENCES tab_companies(company_id) ON DELETE RESTRICT,
    
    -- Material
    raw_material_id     UUID NOT NULL REFERENCES tab_raw_materials(id) ON DELETE RESTRICT,
    
    -- Tipo de movimento
    movement_type       stock_movement_type NOT NULL,
    movement_origin     movement_origin NOT NULL,
    origin_id           UUID,  -- ID da compra, OP, etc.
    
    -- Documento
    document_number     VARCHAR(100),
    movement_date       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    
    -- Quantidade e custo
    quantity            NUMERIC(15,4) NOT NULL CHECK (quantity > 0),
    unit_cost           NUMERIC(15,4),
    total_cost          NUMERIC(15,4),
    
    -- Responsável
    user_id             UUID REFERENCES tab_users(user_id),
    
    -- Observações
    notes               TEXT,
    
    -- Auditoria
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by          VARCHAR(100)
);

-- =====================================================
-- TABELA: COMPRA DE SERVIÇOS
-- =====================================================
CREATE TABLE IF NOT EXISTS tab_buy_services (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id           UUID NOT NULL REFERENCES tab_tenants(tenant_id) ON DELETE CASCADE,
    company_id          UUID NOT NULL REFERENCES tab_companies(company_id) ON DELETE RESTRICT,
    
    -- Fornecedor
    supplier_id         UUID NOT NULL REFERENCES tab_suppliers(supplier_id),
    
    -- Identificação
    code                VARCHAR(50) NOT NULL,
    service_name        VARCHAR(200) NOT NULL,
    reference           VARCHAR(100),
    
    -- Datas
    order_date          DATE NOT NULL DEFAULT CURRENT_DATE,
    delivery_date       DATE,
    
    -- Valores
    base_value          NUMERIC(15,2) NOT NULL DEFAULT 0,
    total_value         NUMERIC(15,2) NOT NULL DEFAULT 0,
    
    -- Condições
    payment_terms       VARCHAR(200),
    
    -- Status
    status              buy_service_status NOT NULL DEFAULT 'OPEN',
    
    -- Observações
    notes               TEXT,
    
    -- Auditoria completa
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by          VARCHAR(100),
    updated_at          TIMESTAMPTZ,
    updated_by          VARCHAR(100),
    deleted_at          TIMESTAMPTZ,
    deleted_by          VARCHAR(100),
    
    -- Aprovação
    approved_by         VARCHAR(100),
    approved_at         TIMESTAMPTZ,
    
    -- Fechamento
    closed_by           VARCHAR(100),
    closed_at           TIMESTAMPTZ,
    
    -- Constraints
    CONSTRAINT uq_buy_services_company_code UNIQUE (company_id, code)
);

-- =====================================================
-- TABELA: ITENS DE COMPRA DE SERVIÇO
-- =====================================================
CREATE TABLE IF NOT EXISTS tab_buy_service_items (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id           UUID NOT NULL REFERENCES tab_tenants(tenant_id) ON DELETE CASCADE,
    company_id          UUID NOT NULL REFERENCES tab_companies(company_id) ON DELETE RESTRICT,
    
    -- Compra de serviço
    buy_service_id      UUID NOT NULL REFERENCES tab_buy_services(id) ON DELETE CASCADE,
    
    -- Descrição
    sequence            INTEGER NOT NULL DEFAULT 1,
    description         VARCHAR(500) NOT NULL,
    
    -- Quantidade e valores
    unit_type           unit_type NOT NULL,
    quantity            NUMERIC(15,4) NOT NULL CHECK (quantity > 0),
    unit_price          NUMERIC(15,4) NOT NULL CHECK (unit_price >= 0),
    discount            NUMERIC(15,2) DEFAULT 0,
    total_price         NUMERIC(15,2) NOT NULL,
    
    -- Entrega
    delivery_date       DATE,
    quantity_received   NUMERIC(15,4) DEFAULT 0,
    
    -- Observações
    notes               TEXT,
    
    -- Auditoria
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by          VARCHAR(100),
    updated_at          TIMESTAMPTZ,
    updated_by          VARCHAR(100),
    deleted_at          TIMESTAMPTZ,
    deleted_by          VARCHAR(100)
);

-- =====================================================
-- TABELA: ETAPAS DE PRODUÇÃO
-- =====================================================
CREATE TABLE IF NOT EXISTS tab_production_steps (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id           UUID NOT NULL REFERENCES tab_tenants(tenant_id) ON DELETE CASCADE,
    company_id          UUID NOT NULL REFERENCES tab_companies(company_id) ON DELETE RESTRICT,
    
    -- Identificação
    name                VARCHAR(200) NOT NULL,
    description         TEXT,
    sequence            INTEGER NOT NULL DEFAULT 1,
    
    -- Tempo estimado (em minutos)
    estimated_time      INTEGER DEFAULT 0,
    
    -- Centro de custo
    cost_center_id      UUID REFERENCES tab_cost_centers(cost_center_id),
    
    -- Controle
    is_outsourced       BOOLEAN NOT NULL DEFAULT FALSE,
    requires_approval   BOOLEAN NOT NULL DEFAULT FALSE,
    is_active           BOOLEAN NOT NULL DEFAULT TRUE,
    
    -- Auditoria
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by          VARCHAR(100),
    updated_at          TIMESTAMPTZ,
    updated_by          VARCHAR(100),
    deleted_at          TIMESTAMPTZ,
    deleted_by          VARCHAR(100)
);

-- =====================================================
-- TABELA: ORDENS DE PRODUÇÃO
-- =====================================================
CREATE TABLE IF NOT EXISTS tab_production_orders (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id           UUID NOT NULL REFERENCES tab_tenants(tenant_id) ON DELETE CASCADE,
    company_id          UUID NOT NULL REFERENCES tab_companies(company_id) ON DELETE RESTRICT,
    
    -- Identificação
    code                VARCHAR(50) NOT NULL,
    
    -- Produto
    product_id          UUID NOT NULL REFERENCES tab_production_products(id),
    
    -- Quantidades
    quantity_planned    NUMERIC(15,4) NOT NULL CHECK (quantity_planned > 0),
    quantity_produced   NUMERIC(15,4) DEFAULT 0 CHECK (quantity_produced >= 0),
    
    -- Status e prioridade
    status              production_order_status NOT NULL DEFAULT 'PLANNED',
    priority            priority_level NOT NULL DEFAULT 'MEDIUM',
    
    -- Datas
    start_date          DATE,
    end_date            DATE,
    deadline            DATE,
    
    -- Relacionamentos
    customer_id         UUID REFERENCES tab_customers(customer_id),
    order_id            UUID REFERENCES tab_orders(order_id),
    
    -- Custos
    cost_total          NUMERIC(15,2) DEFAULT 0,
    
    -- Observações
    notes               TEXT,
    
    -- Auditoria completa
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by          VARCHAR(100),
    updated_at          TIMESTAMPTZ,
    updated_by          VARCHAR(100),
    deleted_at          TIMESTAMPTZ,
    deleted_by          VARCHAR(100),
    version             INTEGER NOT NULL DEFAULT 1,
    
    -- Aprovação
    approved_by         VARCHAR(100),
    approved_at         TIMESTAMPTZ,
    
    -- Finalização
    finished_by         VARCHAR(100),
    finished_at         TIMESTAMPTZ,
    
    -- Cancelamento
    canceled_reason     TEXT,
    
    -- Constraints
    CONSTRAINT chk_production_order_dates CHECK (end_date IS NULL OR start_date IS NULL OR end_date >= start_date),
    CONSTRAINT uq_production_orders_company_code UNIQUE (company_id, code)
);

-- =====================================================
-- TABELA: EXECUÇÕES DE PRODUÇÃO
-- =====================================================
CREATE TABLE IF NOT EXISTS tab_production_executions (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id           UUID NOT NULL REFERENCES tab_tenants(tenant_id) ON DELETE CASCADE,
    company_id          UUID NOT NULL REFERENCES tab_companies(company_id) ON DELETE RESTRICT,
    
    -- Ordem e etapa
    production_order_id UUID NOT NULL REFERENCES tab_production_orders(id) ON DELETE CASCADE,
    step_id             UUID NOT NULL REFERENCES tab_production_steps(id),
    
    -- Tempo
    start_time          TIMESTAMPTZ NOT NULL,
    end_time            TIMESTAMPTZ,
    
    -- Quantidades
    quantity_done       NUMERIC(15,4) NOT NULL CHECK (quantity_done >= 0),
    loss_quantity       NUMERIC(15,4) DEFAULT 0 CHECK (loss_quantity >= 0),
    
    -- Responsável
    employee_id         UUID REFERENCES tab_employees(employee_id),
    
    -- Máquina/Equipamento
    machine_id          UUID,
    
    -- Qualidade
    quality_status      quality_status,
    rejection_reason    TEXT,
    
    -- Observações
    notes               TEXT,
    
    -- Auditoria
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by          VARCHAR(100),
    updated_at          TIMESTAMPTZ,
    updated_by          VARCHAR(100),
    deleted_at          TIMESTAMPTZ,
    deleted_by          VARCHAR(100),
    
    -- Constraints
    CONSTRAINT chk_execution_times CHECK (end_time IS NULL OR end_time >= start_time)
);

-- =====================================================
-- TABELA: CUSTOS DE PRODUÇÃO (Apontamento Real)
-- =====================================================
CREATE TABLE IF NOT EXISTS tab_production_costs (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id           UUID NOT NULL REFERENCES tab_tenants(tenant_id) ON DELETE CASCADE,
    company_id          UUID NOT NULL REFERENCES tab_companies(company_id) ON DELETE RESTRICT,
    
    -- Ordem de produção
    production_order_id UUID NOT NULL REFERENCES tab_production_orders(id) ON DELETE CASCADE,
    
    -- Tipo de custo
    cost_type           production_cost_type NOT NULL,
    reference_id        UUID,  -- ID do material, serviço, funcionário, etc.
    
    -- Data
    cost_date           DATE NOT NULL DEFAULT CURRENT_DATE,
    
    -- Quantidade e valores
    quantity            NUMERIC(15,4),
    unit_cost           NUMERIC(15,4),
    total_cost          NUMERIC(15,2) NOT NULL CHECK (total_cost >= 0),
    
    -- Observações
    notes               TEXT,
    
    -- Auditoria
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by          VARCHAR(100),
    updated_at          TIMESTAMPTZ,
    updated_by          VARCHAR(100),
    deleted_at          TIMESTAMPTZ,
    deleted_by          VARCHAR(100),
    
    -- Aprovação
    approved_by         VARCHAR(100),
    approved_at         TIMESTAMPTZ
);

-- =====================================================
-- TABELA: FECHAMENTO DE PRODUÇÃO
-- =====================================================
CREATE TABLE IF NOT EXISTS tab_production_closures (
    id                      UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id               UUID NOT NULL REFERENCES tab_tenants(tenant_id) ON DELETE CASCADE,
    company_id              UUID NOT NULL REFERENCES tab_companies(company_id) ON DELETE RESTRICT,
    
    -- Ordem de produção
    production_order_id     UUID NOT NULL UNIQUE REFERENCES tab_production_orders(id) ON DELETE RESTRICT,
    
    -- Totais por tipo
    total_material          NUMERIC(15,2) DEFAULT 0,
    total_service           NUMERIC(15,2) DEFAULT 0,
    total_labor             NUMERIC(15,2) DEFAULT 0,
    total_indirect          NUMERIC(15,2) DEFAULT 0,
    total_cost              NUMERIC(15,2) NOT NULL,
    
    -- Datas
    closure_date            DATE NOT NULL DEFAULT CURRENT_DATE,
    closed_at               TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    closed_by               VARCHAR(100) NOT NULL,
    
    -- Integração financeira
    exported_to_financial   BOOLEAN NOT NULL DEFAULT FALSE,
    financial_export_date   TIMESTAMPTZ,
    financial_document_id   UUID,
    
    -- Observações
    notes                   TEXT,
    
    -- Auditoria
    created_at              TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by              VARCHAR(100)
);

-- =====================================================
-- ÍNDICES PARA PERFORMANCE
-- =====================================================

-- Production Products
CREATE INDEX idx_production_products_company ON tab_production_products(company_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_production_products_sku ON tab_production_products(company_id, sku) WHERE deleted_at IS NULL;
CREATE INDEX idx_production_products_product ON tab_production_products(product_id) WHERE deleted_at IS NULL;

-- Compositions
CREATE INDEX idx_compositions_company_product ON tab_compositions(company_id, production_product_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_compositions_active ON tab_compositions(company_id, is_active) WHERE deleted_at IS NULL;
CREATE INDEX idx_composition_items_composition ON tab_composition_items(composition_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_composition_items_type_ref ON tab_composition_items(company_id, item_type, reference_id) WHERE deleted_at IS NULL;

-- Raw Materials
CREATE INDEX idx_raw_materials_company_code ON tab_raw_materials(company_id, code) WHERE deleted_at IS NULL;
CREATE INDEX idx_raw_materials_supplier ON tab_raw_materials(supplier_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_raw_materials_category ON tab_raw_materials(category_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_raw_material_stocks_material ON tab_raw_material_stocks(raw_material_id);
CREATE INDEX idx_raw_material_movements_material_date ON tab_raw_material_movements(raw_material_id, movement_date DESC);
CREATE INDEX idx_raw_material_movements_company_date ON tab_raw_material_movements(company_id, movement_date DESC);

-- Buy Services
CREATE INDEX idx_buy_services_company_status ON tab_buy_services(company_id, status) WHERE deleted_at IS NULL;
CREATE INDEX idx_buy_services_supplier ON tab_buy_services(supplier_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_buy_service_items_service ON tab_buy_service_items(buy_service_id) WHERE deleted_at IS NULL;

-- Production Steps
CREATE INDEX idx_production_steps_company ON tab_production_steps(company_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_production_steps_sequence ON tab_production_steps(company_id, sequence) WHERE deleted_at IS NULL;

-- Production Orders
CREATE INDEX idx_production_orders_company_status ON tab_production_orders(company_id, status) WHERE deleted_at IS NULL;
CREATE INDEX idx_production_orders_tenant_company ON tab_production_orders(tenant_id, company_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_production_orders_product ON tab_production_orders(product_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_production_orders_dates ON tab_production_orders(start_date, end_date) WHERE deleted_at IS NULL;
CREATE INDEX idx_production_orders_priority ON tab_production_orders(company_id, priority, status) WHERE deleted_at IS NULL;

-- Production Executions
CREATE INDEX idx_production_executions_order ON tab_production_executions(production_order_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_production_executions_company_order ON tab_production_executions(company_id, production_order_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_production_executions_step ON tab_production_executions(step_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_production_executions_employee ON tab_production_executions(employee_id) WHERE deleted_at IS NULL;

-- Production Costs
CREATE INDEX idx_production_costs_order ON tab_production_costs(production_order_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_production_costs_company_type ON tab_production_costs(company_id, cost_type) WHERE deleted_at IS NULL;
CREATE INDEX idx_production_costs_date ON tab_production_costs(cost_date DESC) WHERE deleted_at IS NULL;

-- Production Closures
CREATE INDEX idx_production_closures_company ON tab_production_closures(company_id);
CREATE INDEX idx_production_closures_exported ON tab_production_closures(exported_to_financial, company_id);

-- =====================================================
-- TRIGGERS E FUNCTIONS
-- =====================================================

-- Função para atualizar custo total da composição
CREATE OR REPLACE FUNCTION update_composition_item_total_cost()
RETURNS TRIGGER AS $$
BEGIN
    NEW.total_cost := NEW.quantity * COALESCE(NEW.unit_cost, 0) * (1 + COALESCE(NEW.loss_percentage, 0) / 100);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_composition_item_total_cost
    BEFORE INSERT OR UPDATE ON tab_composition_items
    FOR EACH ROW
    EXECUTE FUNCTION update_composition_item_total_cost();

-- Função para atualizar custo total do item de serviço
CREATE OR REPLACE FUNCTION update_buy_service_item_total()
RETURNS TRIGGER AS $$
BEGIN
    NEW.total_price := (NEW.quantity * NEW.unit_price) - COALESCE(NEW.discount, 0);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_buy_service_item_total
    BEFORE INSERT OR UPDATE ON tab_buy_service_items
    FOR EACH ROW
    EXECUTE FUNCTION update_buy_service_item_total();

-- Função para atualizar valor total da compra de serviço
CREATE OR REPLACE FUNCTION update_buy_service_total()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE tab_buy_services
    SET total_value = (
        SELECT COALESCE(SUM(total_price), 0)
        FROM tab_buy_service_items
        WHERE buy_service_id = NEW.buy_service_id
          AND deleted_at IS NULL
    )
    WHERE id = NEW.buy_service_id;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_buy_service_total
    AFTER INSERT OR UPDATE OR DELETE ON tab_buy_service_items
    FOR EACH ROW
    EXECUTE FUNCTION update_buy_service_total();

-- Função para atualizar estoque após movimento
CREATE OR REPLACE FUNCTION update_stock_after_movement()
RETURNS TRIGGER AS $$
DECLARE
    v_stock_id UUID;
    v_quantity_change NUMERIC(15,4);
BEGIN
    -- Determinar a mudança na quantidade
    IF NEW.movement_type = 'IN' THEN
        v_quantity_change := NEW.quantity;
    ELSE
        v_quantity_change := -NEW.quantity;
    END IF;
    
    -- Buscar ou criar registro de estoque
    SELECT id INTO v_stock_id
    FROM tab_raw_material_stocks
    WHERE raw_material_id = NEW.raw_material_id
      AND company_id = NEW.company_id
      AND warehouse_id IS NULL;  -- Estoque padrão
    
    IF v_stock_id IS NULL THEN
        INSERT INTO tab_raw_material_stocks (
            tenant_id, company_id, raw_material_id, quantity, last_movement_date
        ) VALUES (
            NEW.tenant_id, NEW.company_id, NEW.raw_material_id, v_quantity_change, NEW.movement_date
        );
    ELSE
        UPDATE tab_raw_material_stocks
        SET quantity = quantity + v_quantity_change,
            last_movement_date = NEW.movement_date,
            updated_at = NOW()
        WHERE id = v_stock_id;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_stock_after_movement
    AFTER INSERT ON tab_raw_material_movements
    FOR EACH ROW
    EXECUTE FUNCTION update_stock_after_movement();

-- Função para calcular custo médio após entrada
CREATE OR REPLACE FUNCTION update_average_cost_after_entry()
RETURNS TRIGGER AS $$
DECLARE
    v_current_stock NUMERIC(15,4);
    v_current_avg_cost NUMERIC(15,4);
    v_new_avg_cost NUMERIC(15,4);
BEGIN
    IF NEW.movement_type = 'IN' AND NEW.unit_cost IS NOT NULL THEN
        -- Buscar estoque e custo médio atual
        SELECT 
            COALESCE(s.quantity, 0),
            COALESCE(m.average_cost, 0)
        INTO v_current_stock, v_current_avg_cost
        FROM tab_raw_materials m
        LEFT JOIN tab_raw_material_stocks s ON s.raw_material_id = m.id
        WHERE m.id = NEW.raw_material_id;
        
        -- Calcular novo custo médio ponderado
        IF v_current_stock > 0 THEN
            v_new_avg_cost := (
                (v_current_stock * v_current_avg_cost) + (NEW.quantity * NEW.unit_cost)
            ) / (v_current_stock + NEW.quantity);
        ELSE
            v_new_avg_cost := NEW.unit_cost;
        END IF;
        
        -- Atualizar custo médio
        UPDATE tab_raw_materials
        SET average_cost = v_new_avg_cost,
            last_purchase_price = NEW.unit_cost,
            last_purchase_date = NEW.movement_date::date,
            updated_at = NOW()
        WHERE id = NEW.raw_material_id;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_average_cost
    AFTER INSERT ON tab_raw_material_movements
    FOR EACH ROW
    EXECUTE FUNCTION update_average_cost_after_entry();

-- Função para atualizar custo total da ordem de produção
CREATE OR REPLACE FUNCTION update_production_order_cost()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE tab_production_orders
    SET cost_total = (
        SELECT COALESCE(SUM(total_cost), 0)
        FROM tab_production_costs
        WHERE production_order_id = NEW.production_order_id
          AND deleted_at IS NULL
    ),
    updated_at = NOW()
    WHERE id = NEW.production_order_id;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_production_order_cost
    AFTER INSERT OR UPDATE OR DELETE ON tab_production_costs
    FOR EACH ROW
    EXECUTE FUNCTION update_production_order_cost();

-- =====================================================
-- COMENTÁRIOS PARA DOCUMENTAÇÃO
-- =====================================================

COMMENT ON TABLE tab_production_products IS 'Produtos específicos para produção, podem diferir dos produtos comerciais';
COMMENT ON TABLE tab_compositions IS 'Composições (BOM) dos produtos - define materiais e serviços necessários';
COMMENT ON TABLE tab_composition_items IS 'Itens que compõem cada produto (matérias-primas e serviços)';
COMMENT ON TABLE tab_raw_materials IS 'Matérias-primas utilizadas na produção';
COMMENT ON TABLE tab_raw_material_stocks IS 'Estoque atual de matérias-primas por localização';
COMMENT ON TABLE tab_raw_material_movements IS 'Histórico de movimentações de estoque';
COMMENT ON TABLE tab_buy_services IS 'Compras de serviços terceirizados (costura, lavagem, bordado, etc.)';
COMMENT ON TABLE tab_buy_service_items IS 'Itens detalhados de cada compra de serviço';
COMMENT ON TABLE tab_production_steps IS 'Etapas do processo produtivo (corte, costura, acabamento, etc.)';
COMMENT ON TABLE tab_production_orders IS 'Ordens de produção - centro do módulo';
COMMENT ON TABLE tab_production_executions IS 'Registro de execução de cada etapa da produção';
COMMENT ON TABLE tab_production_costs IS 'Apontamento de custos reais da produção';
COMMENT ON TABLE tab_production_closures IS 'Fechamento e totalização de custos da ordem de produção';

-- =====================================================
-- FIM DO SCRIPT
-- =====================================================
