# Design Document - Gerenciamento de Itens de Composição

## Overview

Este documento detalha o design para implementar o gerenciamento de itens dentro das composições (BOM). O sistema permitirá adicionar, editar, remover e visualizar itens (matérias-primas e serviços) que compõem um produto de produção, com cálculo automático de custos e validação de dados.

## Architecture

O sistema seguirá a arquitetura já estabelecida no projeto:

- **Frontend**: React com TypeScript, seguindo o padrão modal já implementado
- **Componentes**: Reutilização dos padrões de Grid, Modal e Form
- **Estado**: Gerenciamento local com React hooks
- **API**: Integração com backend via serviços HTTP
- **Validação**: Formik + Yup para validação de formulários

## Components and Interfaces

### 1. CompositionItemsGrid
Componente responsável por exibir a lista de itens da composição em formato tabular.

**Props:**
- `compositionId: string` - ID da composição
- `items: CompositionItemWithDetails[]` - Lista de itens com detalhes
- `loading: boolean` - Estado de carregamento
- `onEdit: (item: CompositionItemDTO) => void` - Callback para edição
- `onDelete: (itemId: string) => void` - Callback para exclusão
- `onReorder: (items: CompositionItemDTO[]) => void` - Callback para reordenação

**Funcionalidades:**
- Exibição em tabela com colunas: Seq., Tipo, Item, Código, Qtd., Unidade, Custo Unit., % Perda, Custo Total
- Ordenação por sequência
- Ações de editar e excluir por linha
- Drag-and-drop para reordenação
- Cálculo e exibição do custo total da composição

### 2. CompositionItemFormModal
Modal para criação e edição de itens da composição.

**Props:**
- `isOpen: boolean` - Controle de visibilidade
- `item?: CompositionItemDTO` - Item para edição (undefined para criação)
- `compositionId: string` - ID da composição
- `companyId: string` - ID da empresa
- `onSubmit: (data: CreateCompositionItemDTO | UpdateCompositionItemDTO) => Promise<void>`
- `onCancel: () => void`
- `loading?: boolean`

### 3. CompositionItemForm
Formulário para dados do item da composição.

**Campos:**
- Tipo de Item (Radio: Matéria-Prima / Serviço)
- Seleção de Item (Dropdown com pesquisa)
- Sequência (Número)
- Quantidade (Número, obrigatório)
- Unidade de Medida (Readonly, preenchido automaticamente)
- Custo Unitário (Número, preenchido automaticamente, editável)
- Percentual de Perda (Número, 0-100%)
- Item Opcional (Checkbox)
- Observações (Textarea)

### 4. ItemSelector
Componente para seleção de matérias-primas ou serviços com pesquisa.

**Props:**
- `itemType: CompositionItemType` - Tipo de item a ser selecionado
- `companyId: string` - ID da empresa
- `value?: string` - Valor selecionado
- `onChange: (itemId: string, itemData: any) => void`
- `placeholder?: string`

## Data Models

### CompositionItemService
Serviço para gerenciar itens de composição via API.

**Métodos:**
- `findByComposition(compositionId: string): Promise<CompositionItemWithDetails[]>`
- `create(data: CreateCompositionItemDTO): Promise<CompositionItemDTO>`
- `update(id: string, data: UpdateCompositionItemDTO): Promise<CompositionItemDTO>`
- `delete(id: string): Promise<void>`
- `reorder(compositionId: string, itemIds: string[]): Promise<void>`

### Interfaces Auxiliares
```typescript
interface ItemOption {
    id: string;
    name: string;
    code?: string;
    unitType: UnitType;
    unitCost?: number;
}

interface CompositionCostSummary {
    totalItems: number;
    totalCost: number;
    itemsCost: CompositionItemCost[];
}

interface CompositionItemCost {
    itemId: string;
    quantity: number;
    unitCost: number;
    lossPercentage: number;
    totalCost: number;
}
```

## Correctness Properties

*A property is a characteristic or behavior that should hold true across all valid executions of a system-essentially, a formal statement about what the system should do. Properties serve as the bridge between human-readable specifications and machine-verifiable correctness guarantees.*

### Property 1: Item Addition Consistency
*For any* composition and valid item data, adding an item should increase the items count by one and update the total cost accordingly
**Validates: Requirements 2.5**

### Property 2: Cost Calculation Accuracy
*For any* composition item with quantity, unit cost, and loss percentage, the calculated total cost should equal quantity × unitCost × (1 + lossPercentage/100)
**Validates: Requirements 5.2**

### Property 3: Sequence Ordering Integrity
*For any* composition with multiple items, the sequence numbers should be unique, consecutive, and start from 1
**Validates: Requirements 8.2, 8.4**

### Property 4: Item Validation Rules
*For any* item creation or update, quantity must be positive, loss percentage must be between 0-100%, and required fields must be present
**Validates: Requirements 6.1, 6.2, 6.3**

### Property 5: Duplicate Item Prevention
*For any* composition, attempting to add an item that already exists (same type and reference) should be rejected
**Validates: Requirements 6.4**

### Property 6: Item Removal Consistency
*For any* composition item removal, the item count should decrease by one and sequences should be reorganized to maintain continuity
**Validates: Requirements 4.3, 4.4**

### Property 7: Search and Filter Accuracy
*For any* search term, the filtered results should contain only items whose name or code contains the search term (case-insensitive)
**Validates: Requirements 7.2**

### Property 8: Cost Recalculation on Changes
*For any* item modification that affects quantity, unit cost, or loss percentage, the total cost should be automatically recalculated
**Validates: Requirements 3.3, 3.4, 5.5**

## Error Handling

### Validation Errors
- **Campo obrigatório**: Exibir mensagem específica para cada campo
- **Valor inválido**: Validar ranges e formatos
- **Item duplicado**: Impedir adição e mostrar mensagem clara
- **Referência inválida**: Validar se matéria-prima/serviço existe

### API Errors
- **Erro de rede**: Exibir mensagem de conectividade
- **Erro de autorização**: Redirecionar para login
- **Erro de validação do backend**: Exibir mensagens específicas
- **Erro interno**: Mensagem genérica com opção de tentar novamente

### Estado de Loading
- **Carregamento de itens**: Skeleton na tabela
- **Salvamento**: Desabilitar botões e mostrar spinner
- **Exclusão**: Confirmação com loading
- **Reordenação**: Feedback visual durante drag-and-drop

## Testing Strategy

### Unit Tests
- Validação de formulários com dados válidos e inválidos
- Cálculos de custo com diferentes cenários
- Funções de ordenação e reordenação
- Filtros de pesquisa com diferentes termos

### Property-Based Tests
- Testes de propriedades universais usando fast-check (JavaScript)
- Mínimo de 100 iterações por propriedade
- Geração de dados aleatórios para composições e itens
- Validação de invariantes após operações

### Integration Tests
- Fluxo completo de CRUD de itens
- Integração com APIs de matérias-primas
- Cálculo de custos end-to-end
- Reordenação com persistência

### Test Data Generators
```typescript
// Gerador de itens de composição válidos
const generateCompositionItem = (): CreateCompositionItemDTO => ({
    tenantId: fc.uuid(),
    companyId: fc.uuid(),
    compositionId: fc.uuid(),
    itemType: fc.constantFrom('RAW_MATERIAL', 'SERVICE'),
    referenceId: fc.uuid(),
    sequence: fc.integer({ min: 1, max: 100 }),
    unitType: fc.constantFrom(...Object.values(UnitType)),
    quantity: fc.float({ min: 0.01, max: 1000 }),
    lossPercentage: fc.float({ min: 0, max: 100 }),
    unitCost: fc.float({ min: 0.01, max: 10000 }),
    isOptional: fc.boolean()
});
```

## Implementation Notes

### Performance Considerations
- Lazy loading de matérias-primas na seleção
- Debounce na pesquisa de itens
- Memoização de cálculos de custo
- Otimização de re-renders com React.memo

### Accessibility
- Labels apropriados para screen readers
- Navegação por teclado em todos os componentes
- Contraste adequado para elementos visuais
- Mensagens de erro associadas aos campos

### Responsive Design
- Tabela responsiva com scroll horizontal em mobile
- Modal adaptável para diferentes tamanhos de tela
- Botões e campos com tamanho adequado para touch

### State Management
- Estado local para formulários
- Cache de matérias-primas carregadas
- Sincronização automática após operações
- Rollback em caso de erro nas operações