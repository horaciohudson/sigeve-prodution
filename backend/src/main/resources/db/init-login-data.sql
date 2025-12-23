-- ============================================
-- Script de Inicialização para Login
-- ============================================

-- 1. Inserir Tenant
INSERT INTO tab_tenants (tenant_id, code, name, status, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    'SIGEVE',
    'SIGEVE Sistema',
    'ACTIVE',
    NOW(),
    NOW()
)
ON CONFLICT (code) DO NOTHING;

-- 2. Inserir Usuário Admin
-- Senha: 123456 (hash BCrypt)
-- IMPORTANTE: Este hash é para a senha "123456"
INSERT INTO tab_users (
    user_id,
    tenant_id,
    username,
    email,
    password_hash,
    full_name,
    status,
    failed_attempts,
    language,
    timezone,
    is_system_admin,
    created_at,
    updated_at
)
SELECT 
    gen_random_uuid(),
    t.tenant_id,
    'admin',
    'admin@sigeve.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', -- Senha: 123456
    'Administrador',
    'ACTIVE',
    0,
    'pt',
    'America/Sao_Paulo',
    true,
    NOW(),
    NOW()
FROM tab_tenants t
WHERE t.code = 'SIGEVE'
ON CONFLICT (username, tenant_id) DO NOTHING;

-- 3. Criar Role Admin (se não existir)
INSERT INTO tab_roles (role_id, role, description, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    'ADMIN',
    'Administrador do Sistema',
    NOW(),
    NOW()
)
ON CONFLICT (role) DO NOTHING;

-- 4. Associar Usuário à Role
INSERT INTO tab_user_roles (user_id, role_id)
SELECT 
    u.user_id,
    r.role_id
FROM tab_users u
CROSS JOIN tab_roles r
WHERE u.username = 'admin'
  AND r.role = 'ADMIN'
  AND u.tenant_id = (SELECT tenant_id FROM tab_tenants WHERE code = 'SIGEVE')
ON CONFLICT (user_id, role_id) DO NOTHING;

-- ============================================
-- Verificação dos Dados Inseridos
-- ============================================

-- Verificar Tenant
SELECT tenant_id, code, name, status 
FROM tab_tenants 
WHERE code = 'SIGEVE';

-- Verificar Usuário
SELECT 
    u.user_id,
    u.username,
    u.email,
    u.full_name,
    u.status,
    t.code as tenant_code
FROM tab_users u
JOIN tab_tenants t ON u.tenant_id = t.tenant_id
WHERE u.username = 'admin'
  AND t.code = 'SIGEVE';

-- Verificar Roles do Usuário
SELECT 
    u.username,
    r.role,
    r.description
FROM tab_users u
JOIN tab_user_roles ur ON u.user_id = ur.user_id
JOIN tab_roles r ON ur.role_id = r.role_id
WHERE u.username = 'admin'
  AND u.tenant_id = (SELECT tenant_id FROM tab_tenants WHERE code = 'SIGEVE');

-- ============================================
-- IMPORTANTE: Hash de Senhas
-- ============================================
-- O hash acima é para a senha "123456"
-- Para gerar um novo hash BCrypt, você pode usar:
-- 
-- 1. Online: https://bcrypt-generator.com/
-- 2. Java:
--    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
--    String hash = encoder.encode("sua_senha");
-- 
-- 3. Node.js:
--    const bcrypt = require('bcrypt');
--    const hash = await bcrypt.hash('sua_senha', 10);
-- ============================================
