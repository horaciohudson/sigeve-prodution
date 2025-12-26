# Plano de Implementação - Padrão Grade + Formulário para Produtos

- [x] 1. Refatorar estrutura de componentes





  - Extrair lógica da grade de produtos em componente separado
  - Criar componente ProductGrid para renderizar tabela de produtos
  - Criar componente ProductFormModal para modal do formulário
  - Extrair FilterControls em componente separado
  - _Requirements: 1.1, 1.2, 2.1, 2.2_

- [ ]* 1.1 Escrever teste de propriedade para renderização da grade
  - **Property 1: Grade renderização completa**
  - **Validates: Requirements 1.1, 1.2, 4.1**

- [ ] 2. Implementar componente ProductGrid
  - Criar interface ProductGridProps
  - Implementar renderização em formato de tabela similar ao CompanyPage
  - Adicionar ações para cada produto (editar, ativar/desativar, excluir)
  - Implementar estado vazio quando não há produtos
  - _Requirements: 1.1, 1.2, 1.3, 4.1_

- [ ]* 2.1 Escrever teste de propriedade para filtros
  - **Property 2: Filtros funcionais**
  - **Validates: Requirements 1.4**

- [ ] 3. Implementar componente ProductFormModal
  - Criar interface ProductFormModalProps
  - Implementar modal com backdrop semi-transparente
  - Integrar com ProductionProductForm existente
  - Adicionar controle de visibilidade (isOpen)
  - Implementar fechamento por ESC e clique no backdrop
  - _Requirements: 2.1, 2.2_

- [ ]* 3.1 Escrever teste de propriedade para modal sobreposto
  - **Property 4: Modal sobreposto à grade**
  - **Validates: Requirements 2.1, 2.2**

- [ ] 4. Implementar FilterControls
  - Criar interface FilterControlsProps
  - Implementar busca por texto (descrição, SKU, código de barras)
  - Implementar filtros por status (todos, ativos, inativos)
  - Exibir contadores de produtos por categoria
  - _Requirements: 1.4_

- [ ] 5. Refatorar ProductionProductsPage para usar novos componentes
  - Substituir renderização atual pela nova estrutura de componentes
  - Manter estado centralizado na página principal
  - Implementar handlers para ações da grade
  - Integrar controles de filtro
  - _Requirements: 1.1, 1.2, 1.4, 1.5_

- [ ]* 5.1 Escrever teste de propriedade para seleção de empresa
  - **Property 3: Seleção de empresa atualiza produtos**
  - **Validates: Requirements 1.5**

- [ ] 6. Implementar funcionalidade de criação com modal
  - Modificar handleCreate para abrir modal em vez de trocar página
  - Implementar estado showForm para controlar visibilidade do modal
  - Garantir que a grade permaneça visível ao fundo
  - _Requirements: 2.1, 2.2, 2.3_

- [ ]* 6.1 Escrever teste de propriedade para criação round trip
  - **Property 5: Criação/edição round trip**
  - **Validates: Requirements 2.3, 3.3**

- [ ]* 6.2 Escrever teste de propriedade para feedback de sucesso
  - **Property 6: Feedback de sucesso**
  - **Validates: Requirements 2.4, 3.4**

- [ ] 7. Implementar funcionalidade de edição com modal
  - Modificar handleEdit para abrir modal preenchido
  - Implementar carregamento de dados do produto no formulário
  - Alterar título do modal para "Editar Produto" quando em modo de edição
  - _Requirements: 3.1, 3.2, 3.3_

- [ ]* 7.1 Escrever teste de propriedade para edição carrega dados
  - **Property 8: Edição carrega dados corretos**
  - **Validates: Requirements 3.1, 3.2**

- [ ] 8. Implementar funcionalidade de cancelamento
  - Implementar handleCancel para fechar modal sem alterações
  - Garantir que estado da grade não seja alterado
  - Limpar estado de edição ao cancelar
  - _Requirements: 2.5, 3.5_

- [ ]* 8.1 Escrever teste de propriedade para cancelamento
  - **Property 7: Cancelamento preserva estado**
  - **Validates: Requirements 2.5, 3.5**

- [ ] 9. Implementar ações da grade
  - Implementar toggle de status (ativar/desativar)
  - Implementar exclusão com confirmação
  - Atualizar grade após cada ação
  - _Requirements: 4.2, 4.3, 4.4_

- [ ]* 9.1 Escrever teste de propriedade para toggle de status
  - **Property 9: Toggle de status funcional**
  - **Validates: Requirements 4.2**

- [ ]* 9.2 Escrever teste de propriedade para exclusão com confirmação
  - **Property 10: Exclusão com confirmação**
  - **Validates: Requirements 4.3, 4.4**

- [ ] 10. Implementar tratamento de erros
  - Adicionar tratamento de erros para todas as operações
  - Exibir mensagens de erro user-friendly
  - Implementar retry para erros de rede
  - _Requirements: 4.5_

- [ ]* 10.1 Escrever teste de propriedade para tratamento de erros
  - **Property 11: Tratamento de erros**
  - **Validates: Requirements 4.5**

- [ ] 11. Implementar validação e estados de loading
  - Adicionar validação em tempo real no formulário
  - Implementar estados de loading durante operações
  - Desabilitar botões durante processamento
  - Exibir indicadores de carregamento
  - _Requirements: 5.2, 5.3, 5.4, 5.5_

- [ ]* 11.1 Escrever teste de propriedade para validação
  - **Property 12: Validação em tempo real**
  - **Validates: Requirements 5.2, 5.3**

- [ ]* 11.2 Escrever teste de propriedade para estados de loading
  - **Property 13: Estados de loading**
  - **Validates: Requirements 5.4, 5.5**

- [ ] 12. Implementar estilos CSS
  - Criar estilos para ProductGrid baseados no CompanyPage.css
  - Criar estilos para ProductFormModal
  - Implementar estilos responsivos
  - Adicionar animações de transição para o modal
  - _Requirements: 2.1, 2.2, 5.1_

- [ ] 13. Checkpoint - Garantir que todos os testes passem
  - Ensure all tests pass, ask the user if questions arise.

- [ ]* 14. Escrever testes unitários complementares
  - Criar testes unitários para casos específicos não cobertos pelas propriedades
  - Testar integração entre componentes
  - Testar casos extremos e condições de erro
  - Criar mocks para serviços externos

- [ ] 15. Otimizações e melhorias finais
  - Implementar debounce na busca por texto
  - Adicionar keyboard shortcuts (ESC para fechar modal)
  - Implementar lazy loading se necessário
  - Otimizar re-renderizações desnecessárias
  - _Requirements: 1.4, 2.1_

- [ ] 16. Checkpoint final - Garantir que todos os testes passem
  - Ensure all tests pass, ask the user if questions arise.