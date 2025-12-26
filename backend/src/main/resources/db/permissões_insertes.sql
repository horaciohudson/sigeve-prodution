-- Copie e execute no seu banco de dados PostgreSQL
INSERT INTO tab_permissions (permission_key, description, level, created_at, created_by)
VALUES
    ('PRODUCTION.VIEW', 'Visualizar módulo de produção', 1, NOW(), 'system'),
    ('PRODUCTION.CREATE', 'Criar itens de produção', 2, NOW(), 'system'),
    ('PRODUCTION.EDIT', 'Editar itens de produção', 2, NOW(), 'system'),
    ('PRODUCTION.DELETE', 'Excluir itens de produção', 3, NOW(), 'system'),
    ('ADMIN.VIEW', 'Visualizar módulo administrativo', 1, NOW(), 'system'),
    ('ADMIN.USERS', 'Gerenciar usuários', 3, NOW(), 'system'),
    ('ADMIN.PERMISSIONS', 'Gerenciar permissões', 3, NOW(), 'system')
    ON CONFLICT (permission_key) DO NOTHING;