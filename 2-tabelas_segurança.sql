-- =========================================
-- Extensions
-- =========================================
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- =========================================
-- Enums
-- =========================================
DO $BODY$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'user_status') THEN
CREATE TYPE user_status AS ENUM ('ACTIVE','INACTIVE','BLOCKED');
END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'role_type') THEN
CREATE TYPE role_type AS ENUM (
      'ROLE_ADMIN',
      'ROLE_CLIENT',
      'ROLE_CAIXA',
      'ROLE_SUPPORT',
      'ROLE_SALESPERSON',
      'ROLE_MANAGER'
    );
END IF;
END $BODY$;

-- =========================================
-- Tenants
-- =========================================
CREATE TABLE IF NOT EXISTS tab_tenants (
                                           tenant_id   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code        VARCHAR(50)  NOT NULL UNIQUE,         -- e.g. SIGEVE, ACME
    name        VARCHAR(120) NOT NULL,
    status      user_status  NOT NULL DEFAULT 'ACTIVE',
    cancellation_reason VARCHAR(255),
    cancelled_at  TIMESTAMPTZ,
    cancelled_by  VARCHAR(100),
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ,
    created_by  VARCHAR(100),
    updated_by  VARCHAR(100),
    deleted_at  TIMESTAMPTZ,
    deleted_by  VARCHAR(100)
    );

-- =========================================
-- Users
-- =========================================
CREATE TABLE IF NOT EXISTS tab_users (
                                         user_id                 UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id               UUID NOT NULL REFERENCES tab_tenants(tenant_id) ON DELETE CASCADE,
    company_id              UUID,

    username                VARCHAR(50)  NOT NULL,
    email                   VARCHAR(120),
    password_hash           VARCHAR(255) NOT NULL,
    full_name               VARCHAR(100) NOT NULL,

    status                  user_status   NOT NULL DEFAULT 'ACTIVE',
    failed_attempts         INTEGER       NOT NULL DEFAULT 0 CHECK (failed_attempts >= 0),
    last_login_at           TIMESTAMPTZ,
    password_changed_at     TIMESTAMPTZ,
    require_password_change BOOLEAN       NOT NULL DEFAULT FALSE,

    language                VARCHAR(2),           -- 'pt', 'en'
    timezone                VARCHAR(64),          -- 'America/Fortaleza'
    is_system_admin         BOOLEAN       NOT NULL DEFAULT FALSE,
    cancellation_reason     VARCHAR(255),

    created_at              TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ,

    cancelled_at            TIMESTAMPTZ,
    cancelled_by            VARCHAR(100),

    deleted_at              TIMESTAMPTZ,
    deleted_by              VARCHAR(100),

    created_by              VARCHAR(100)   NOT NULL,
    updated_by              VARCHAR(100),

    locked_until           TIMESTAMP WITH TIME ZONE
                                                                                );

-- Uniqueness per tenant
CREATE UNIQUE INDEX IF NOT EXISTS ux_users_tenant_username
    ON tab_users(tenant_id, username);

-- Email unique per tenant when present
CREATE UNIQUE INDEX IF NOT EXISTS ux_users_tenant_email
    ON tab_users(tenant_id, email)
    WHERE email IS NOT NULL;

CREATE INDEX IF NOT EXISTS ix_users_tenant
    ON tab_users(tenant_id);

CREATE INDEX idx_users_locked_until ON tab_users(locked_until);

-- =========================================
-- Roles (ENUM-backed, globais)
-- =========================================
CREATE TABLE IF NOT EXISTS tab_roles (
                                         role_id      SERIAL PRIMARY KEY,
                                         role         role_type NOT NULL UNIQUE,
                                         description  VARCHAR(250),

    created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by   VARCHAR(100)

    );

-- =========================================
-- Permissions (globais)
-- =========================================
CREATE TABLE IF NOT EXISTS tab_permissions (
                                               permission_id   SERIAL PRIMARY KEY,
                                               permission_key  VARCHAR(50)  NOT NULL UNIQUE,  -- ex: FINANCE_READ
    description     VARCHAR(120),
    level           INTEGER CHECK (level >= 1 AND level <= 5),
    cancellation_reason    VARCHAR(255),
    cancelled_at             TIMESTAMP WITH TIME ZONE,
                                           cancelled_by             VARCHAR(100),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ,
    created_by      VARCHAR(100),
    updated_by      VARCHAR(100),
    deleted_at      TIMESTAMPTZ,
    deleted_by      VARCHAR(100)
    );

-- =========================================
-- Joins
-- =========================================
CREATE TABLE IF NOT EXISTS tab_user_roles (
                                              user_id   UUID NOT NULL REFERENCES tab_users(user_id) ON DELETE CASCADE,
    role_id   INT  NOT NULL REFERENCES tab_roles(role_id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    PRIMARY KEY (user_id, role_id)
    );

CREATE TABLE IF NOT EXISTS tab_role_permissions (
                                                    role_id        INT NOT NULL REFERENCES tab_roles(role_id) ON DELETE CASCADE,
    permission_id  INT NOT NULL REFERENCES tab_permissions(permission_id) ON DELETE CASCADE,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by     VARCHAR(100),
    PRIMARY KEY (role_id, permission_id)
    );

-- =========================================
-- User Permissions (permissões específicas por usuário)
-- =========================================
CREATE TABLE IF NOT EXISTS tab_user_permissions (
                                                    user_permission_id SERIAL PRIMARY KEY,
                                                    user_id           UUID NOT NULL REFERENCES tab_users(user_id) ON DELETE CASCADE,
    permission_id     INT  NOT NULL REFERENCES tab_permissions(permission_id) ON DELETE CASCADE,
    tenant_id         UUID NOT NULL REFERENCES tab_tenants(tenant_id) ON DELETE CASCADE,
    granted           BOOLEAN NOT NULL DEFAULT TRUE,
    notes             VARCHAR(255),
    created_at        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMPTZ,
    created_by        VARCHAR(100),
    updated_by        VARCHAR(100),
    deleted_at        TIMESTAMPTZ,
    deleted_by        VARCHAR(100),
    cancellation_reason VARCHAR(255),
    canceled_at timestamp with time zone,
                                                                                                                      canceled_by varchar(50),
    UNIQUE(user_id, permission_id, tenant_id)
    );

CREATE INDEX IF NOT EXISTS ix_user_permissions_user ON tab_user_permissions(user_id);
CREATE INDEX IF NOT EXISTS ix_user_permissions_permission ON tab_user_permissions(permission_id);
CREATE INDEX IF NOT EXISTS ix_user_permissions_tenant ON tab_user_permissions(tenant_id);

-- =========================================
-- Security / Audit Log (somente append)
-- =========================================
CREATE TABLE IF NOT EXISTS tab_user_security_log (
                                                     user_security_log_id BIGSERIAL PRIMARY KEY,
                                                     tenant_id  UUID REFERENCES tab_tenants(tenant_id) ON DELETE SET NULL,
    user_id    UUID REFERENCES tab_users(user_id)     ON DELETE SET NULL,
    event_type VARCHAR(40) NOT NULL,     -- LOGIN_SUCCESS, LOGIN_FAIL, PASSWORD_CHANGE, LOCKED, UNLOCKED, etc.
    event_ip   INET,
    user_agent TEXT,
    details    JSONB,
    event_at   TIMESTAMPTZ NOT NULL DEFAULT NOW()
    );

CREATE INDEX IF NOT EXISTS ix_sec_log_user_time   ON tab_user_security_log(user_id, event_at DESC);
CREATE INDEX IF NOT EXISTS ix_sec_log_tenant_time ON tab_user_security_log(tenant_id, event_at DESC);

-- =========================================
-- Seeds
-- =========================================

-- 1) Default tenant
INSERT INTO tab_tenants (code, name, created_by)
VALUES ('SIGEVE', 'SIGEVE Default Tenant', 'system')
    ON CONFLICT (code) DO NOTHING;

-- 2) Roles
INSERT INTO tab_roles (role, description, created_by)
VALUES
    ('ROLE_ADMIN','System administrator','system'),
    ('ROLE_CLIENT','Client user','system'),
    ('ROLE_CAIXA','Cashier','system'),
    ('ROLE_SUPPORT','Support user','system'),
    ('ROLE_SALESPERSON','Salesperson','system'),
    ('ROLE_MANAGER','Manager','system')
    ON CONFLICT (role) DO NOTHING;

-- 3) Permissions (exemplos)
INSERT INTO tab_permissions (permission_key, description, level, created_by)
VALUES
    ('FINANCE_READ','Read finance data',1,'system'),
    ('FINANCE_WRITE','Write finance data',3,'system'),
    ('FINANCE_APPROVE','Approve payments/receivables',4,'system')
    ON CONFLICT (permission_key) DO NOTHING;

-- 4) Admin user (username login) no tenant SIGEVE
-- Substitua o hash abaixo por um bcrypt real da senha desejada
-- Exemplo: 123456 -> gerar no app e colar aqui
WITH t AS (
    SELECT tenant_id FROM tab_tenants WHERE code='SIGEVE' LIMIT 1
    )
INSERT INTO tab_users (
    tenant_id, username, email, password_hash, full_name,
    status, language, timezone, is_system_admin,
    created_by
)
SELECT
    t.tenant_id,
    'admin', 'admin@financeiro.com',
    '$2a$10$x1gEflHiUj9BpP92PEzAb.gou5PwZvZd0I8oeBrK/loMP6vGF8IOq',
    'Administrator',
    'ACTIVE', 'pt', 'America/Fortaleza', TRUE,
    'system'
FROM t
    ON CONFLICT DO NOTHING;

-- 5) Vincular admin à ROLE_ADMIN
WITH u AS (
    SELECT user_id, tenant_id FROM tab_users WHERE username='admin' LIMIT 1
    ), r AS (
SELECT role_id FROM tab_roles WHERE role='ROLE_ADMIN' LIMIT 1
    )
INSERT INTO tab_user_roles (user_id, role_id, created_by)
SELECT u.user_id, r.role_id, 'system' FROM u, r
    ON CONFLICT DO NOTHING;

-- 6) Dar permissões a ROLE_ADMIN e ROLE_MANAGER (exemplo)
WITH
    r_admin AS (SELECT role_id FROM tab_roles WHERE role='ROLE_ADMIN'),
    r_mgr   AS (SELECT role_id FROM tab_roles WHERE role='ROLE_MANAGER'),
    p_read  AS (SELECT permission_id FROM tab_permissions WHERE permission_key='FINANCE_READ'),
    p_write AS (SELECT permission_id FROM tab_permissions WHERE permission_key='FINANCE_WRITE'),
    p_appr  AS (SELECT permission_id FROM tab_permissions WHERE permission_key='FINANCE_APPROVE')
INSERT INTO tab_role_permissions (role_id, permission_id, created_by)
SELECT r_admin.role_id, p_read.permission_id,  'system' FROM r_admin, p_read
UNION ALL
SELECT r_admin.role_id, p_write.permission_id, 'system' FROM r_admin, p_write
UNION ALL
SELECT r_admin.role_id, p_appr.permission_id,  'system' FROM r_admin, p_appr
UNION ALL
SELECT r_mgr.role_id,   p_read.permission_id,  'system' FROM r_mgr,   p_read
UNION ALL
SELECT r_mgr.role_id,   p_write.permission_id, 'system' FROM r_mgr,   p_write
    ON CONFLICT DO NOTHING;