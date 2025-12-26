# Implementation Plan - Gerenciamento de Itens de Composição

- [-] 1. Implementar serviço de itens de composição



  - Criar compositionItemService.ts com métodos CRUD
  - Implementar endpoints para buscar, criar, atualizar e excluir itens
  - Adicionar método para reordenação de itens
  - Implementar busca de matérias-primas e serviços para seleção
  - _Requirements: 1.1, 2.5, 3.2, 4.2_

- [ ]* 1.1 Escrever teste de propriedade para adição de itens
  - **Property 1: Item Addition Consistency**
  - **Validates: Requirements 2.5**

- [ ]* 1.2 Escrever teste de propriedade para cálculo de custos
  - **Property 2: Cost Calculation Accuracy**
  - **Validates: Requirements 5.2**

- [ ] 2. Criar componente ItemSelector

  - Implementar dropdown com pesquisa para matérias-primas e serviços
  - Adicionar filtro por tipo de item (matéria-prima/serviço)
  - Implementar pesquisa em tempo real por nome e código
  - Adicionar preenchimento automático de unidade e custo
  - _Requirements: 2.3, 2.4, 7.2, 7.3, 7.5_

- [ ]* 2.1 Escrever teste de propriedade para filtros de pesquisa
  - **Property 7: Search and Filter Accuracy**
  - **Validates: Requirements 7.2**

- [ ] 3. Implementar CompositionItemForm
  - Criar formulário com validação usando Formik e Yup
  - Adicionar campos para tipo, item, quantidade, custo, perda, etc.
  - Implementar validação de campos obrigatórios e ranges
  - Adicionar cálculo automático de custo total
  - Integrar com ItemSelector para seleção de itens
  - _Requirements: 2.1, 2.2, 3.3, 3.4, 5.1, 5.2, 6.1, 6.2, 6.3_

- [ ]* 3.1 Escrever teste de propriedade para validação de dados
  - **Property 4: Item Validation Rules**
  - **Validates: Requirements 6.1, 6.2, 6.3**

- [ ]* 3.2 Escrever teste de propriedade para prevenção de duplicatas
  - **Property 5: Duplicate Item Prevention**
  - **Validates: Requirements 6.4**

- [ ] 4. Criar CompositionItemFormModal
  - Implementar modal seguindo padrão estabelecido
  - Integrar com CompositionItemForm
  - Adicionar controle de estado de loading
  - Implementar tratamento de erros
  - _Requirements: 2.1, 3.1_

- [ ] 5. Implementar CompositionItemsGrid
  - Criar tabela responsiva para exibir itens da composição
  - Adicionar colunas para todos os dados relevantes
  - Implementar ordenação por sequência
  - Adicionar ações de editar e excluir por linha
  - Implementar drag-and-drop para reordenação
  - Adicionar cálculo e exibição do custo total
  - _Requirements: 1.1, 1.2, 1.3, 1.5, 8.1, 8.2_

- [ ]* 5.1 Escrever teste de propriedade para ordenação de sequência
  - **Property 3: Sequence Ordering Integrity**
  - **Validates: Requirements 8.2, 8.4**

- [ ]* 5.2 Escrever teste de propriedade para remoção de itens
  - **Property 6: Item Removal Consistency**
  - **Validates: Requirements 4.3, 4.4**

- [ ] 6. Integrar gerenciamento de itens na página de composições
  - Adicionar seção de itens na visualização de composição
  - Implementar navegação entre composição e seus itens
  - Adicionar botões para gerenciar itens
  - Integrar com os componentes criados
  - _Requirements: 1.1, 1.4, 4.5_

- [ ]* 6.1 Escrever teste de propriedade para recálculo de custos
  - **Property 8: Cost Recalculation on Changes**
  - **Validates: Requirements 3.3, 3.4, 5.5**

- [ ] 7. Implementar funcionalidades de reordenação
  - Adicionar drag-and-drop na tabela de itens
  - Implementar botões de mover para cima/baixo
  - Adicionar atribuição automática de sequência para novos itens
  - Implementar reorganização após remoção de itens
  - _Requirements: 8.1, 8.2, 8.3, 8.4, 8.5_

- [ ] 8. Adicionar tratamento de casos especiais
  - Implementar mensagem para composição sem itens
  - Adicionar mensagem para pesquisa sem resultados
  - Implementar confirmação para remoção de itens
  - Adicionar tratamento para custos não disponíveis
  - _Requirements: 1.4, 4.1, 5.4, 7.4_

- [ ] 9. Implementar validações e cálculos avançados
  - Adicionar validação de duplicação de itens
  - Implementar cálculo de custo com percentual de perda
  - Adicionar validação de ranges para quantidade e perda
  - Implementar persistência automática de custos calculados
  - _Requirements: 5.2, 5.5, 6.2, 6.3, 6.4, 6.5_

- [ ]* 9.1 Escrever testes unitários para cálculos
  - Criar testes para fórmula de custo com perda
  - Testar cálculo de custo total da composição
  - Validar cálculos com diferentes cenários
  - _Requirements: 5.1, 5.2, 5.3_

- [ ] 10. Otimizações e melhorias de UX
  - Implementar lazy loading para seleção de itens
  - Adicionar debounce na pesquisa
  - Implementar memoização de cálculos
  - Adicionar feedback visual para operações
  - Otimizar re-renders com React.memo
  - _Requirements: 7.2_

- [ ] 11. Checkpoint - Garantir que todos os testes passem
  - Ensure all tests pass, ask the user if questions arise.

- [ ]* 11.1 Escrever testes de integração
  - Testar fluxo completo de CRUD de itens
  - Validar integração com APIs de matérias-primas
  - Testar cálculo de custos end-to-end
  - Validar reordenação com persistência
  - _Requirements: 1.1, 2.5, 3.2, 4.2, 8.5_

- [ ] 12. Implementar melhorias de acessibilidade
  - Adicionar labels apropriados para screen readers
  - Implementar navegação por teclado
  - Garantir contraste adequado
  - Associar mensagens de erro aos campos
  - _Requirements: 6.5_

- [ ] 13. Testes finais e ajustes
  - Testar responsividade em diferentes dispositivos
  - Validar performance com grandes quantidades de itens
  - Testar todos os fluxos de erro
  - Ajustar estilos e animações
  - _Requirements: All_

- [ ] 14. Checkpoint final - Validação completa
  - Ensure all tests pass, ask the user if questions arise.