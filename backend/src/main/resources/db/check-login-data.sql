-- ============================================
-- DIAGNÓSTICO: 403 Forbidden no Login
-- ============================================

-- Este script verifica se os dados necessários existem
-- Execute no banco: prodution_sigeve_db

-- 1. Verificar se o tenant existe
SELECT 
    CASE 
        WHEN COUNT(*) > 0 THEN '✅ Tenant SIGEVE existe'
        ELSE '❌ Tenant SIGEVE NÃO existe - Execute init-login-data.sql'
    END as status,
    COUNT(*) as quantidade
FROM tab_tenants 
WHERE code = 'SIGEVE';

-- 2. Verificar se o usuário existe
SELECT 
    CASE 
        WHEN COUNT(*) > 0 THEN '✅ Usuário admin existe'
        ELSE '❌ Usuário admin NÃO existe - Execute init-login-data.sql'
    END as status,
    COUNT(*) as quantidade
FROM tab_users u
JOIN tab_tenants t ON u.tenant_id = t.tenant_id
WHERE u.username = 'admin' AND t.code = 'SIGEVE';

-- 3. Verificar status do usuário
SELECT 
    u.username,
    u.status,
    u.failed_attempts,
    u.locked_until,
    CASE 
        WHEN u.status = 'ACTIVE' THEN '✅ Usuário ativo'
        ELSE '❌ Usuário inativo'
    END as status_check,
    CASE 
        WHEN u.locked_until IS NULL OR u.locked_until < NOW() THEN '✅ Conta desbloqueada'
        ELSE '❌ Conta bloqueada até ' || u.locked_until::text
    END as lock_check
FROM tab_users u
JOIN tab_tenants t ON u.tenant_id = t.tenant_id
WHERE u.username = 'admin' AND t.code = 'SIGEVE';

-- 4. Verificar roles
SELECT 
    CASE 
        WHEN COUNT(*) > 0 THEN '✅ Usuário tem roles'
        ELSE '⚠️  Usuário sem roles - Execute init-login-data.sql'
    END as status,
    COUNT(*) as quantidade_roles
FROM tab_users u
JOIN tab_tenants t ON u.tenant_id = t.tenant_id
JOIN tab_user_roles ur ON u.user_id = ur.user_id
WHERE u.username = 'admin' AND t.code = 'SIGEVE';

-- ============================================
-- RESULTADO ESPERADO:
-- ============================================
-- ✅ Tenant SIGEVE existe
-- ✅ Usuário admin existe
-- ✅ Usuário ativo
-- ✅ Conta desbloqueada
-- ✅ Usuário tem roles
-- ============================================
