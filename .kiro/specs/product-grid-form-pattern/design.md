# Design - Padrão Grade + Formulário para Produtos

## Overview

Este documento descreve o design técnico para implementar o padrão de interface grade + formulário para o gerenciamento de produtos de produção no Sigeve Produção. O design segue o padrão já estabelecido no cadastro de empresas do Sigeve Financeiro, proporcionando consistência na experiência do usuário.

O padrão consiste em uma grade de apresentação que exibe todos os produtos cadastrados, com um formulário modal que aparece sobreposto à grade quando o usuário deseja criar ou editar um produto. Isso mantém o contexto visual e permite uma navegação mais fluida.

## Architecture

### Estrutura de Componentes

```
ProductionProductsPage (Container)
├── ProductGrid (Apresentação)
│   ├── ProductCard (Item individual)
│   └── EmptyState (Estado vazio)
├── ProductFormModal (Formulário modal)
│   └── ProductionProductForm (Formulário existente)
└── FilterControls (Controles de filtro)
```

### Fluxo de Estados

1. **Estado Inicial**: Grade visível, formulário oculto
2. **Criação**: Formulário modal aparece sobre a grade
3. **Edição**: Formulário modal aparece preenchido com dados do produto
4. **Processamento**: Indicadores de loading durante operações
5. **Finalização**: Formulário fecha, grade atualiza, mensagem de feedback

## Components and Interfaces

### ProductGrid Component

```typescript
interface ProductGridProps {
  products: ProductionProductDTO[];
  loading: boolean;
  onEdit: (product: ProductionProductDTO) => void;
  onDelete: (productId: string) => void;
  onToggleActive: (product: ProductionProductDTO) => void;
  selectedCompanyId: string;
}
```

**Responsabilidades:**
- Renderizar a grade de produtos em formato de tabela
- Fornecer ações para cada produto (editar, excluir, ativar/desativar)
- Exibir estado vazio quando não há produtos
- Aplicar filtros de busca e status

### ProductFormModal Component

```typescript
interface ProductFormModalProps {
  isOpen: boolean;
  product?: ProductionProductDTO;
  companyId: string;
  onSubmit: (data: CreateProductionProductDTO | UpdateProductionProductDTO) => void;
  onCancel: () => void;
  loading: boolean;
}
```

**Responsabilidades:**
- Controlar visibilidade do modal
- Renderizar backdrop semi-transparente
- Gerenciar estado de loading durante operações
- Integrar com o ProductionProductForm existente

### FilterControls Component

```typescript
interface FilterControlsProps {
  searchTerm: string;
  onSearchChange: (term: string) => void;
  filterActive: 'all' | 'active' | 'inactive';
  onFilterChange: (filter: 'all' | 'active' | 'inactive') => void;
  productCounts: {
    total: number;
    active: number;
    inactive: number;
  };
}
```

## Data Models

### Estado do Componente Principal

```typescript
interface ProductPageState {
  products: ProductionProductDTO[];
  companies: Company[];
  selectedCompanyId: string;
  loading: boolean;
  error: string;
  
  // Modal state
  showForm: boolean;
  editingProduct?: ProductionProductDTO;
  
  // Filter state
  searchTerm: string;
  filterActive: 'all' | 'active' | 'inactive';
  
  // UI feedback
  message?: {
    type: 'success' | 'error';
    text: string;
  };
}
```

### Tipos de Ação

```typescript
type ProductAction = 
  | { type: 'LOAD_PRODUCTS_START' }
  | { type: 'LOAD_PRODUCTS_SUCCESS'; payload: ProductionProductDTO[] }
  | { type: 'LOAD_PRODUCTS_ERROR'; payload: string }
  | { type: 'OPEN_CREATE_FORM' }
  | { type: 'OPEN_EDIT_FORM'; payload: ProductionProductDTO }
  | { type: 'CLOSE_FORM' }
  | { type: 'SET_SEARCH_TERM'; payload: string }
  | { type: 'SET_FILTER'; payload: 'all' | 'active' | 'inactive' }
  | { type: 'SET_MESSAGE'; payload: { type: 'success' | 'error'; text: string } }
  | { type: 'CLEAR_MESSAGE' };
```

## Correctness Properties

*A property is a characteristic or behavior that should hold true across all valid executions of a system-essentially, a formal statement about what the system should do. Properties serve as the bridge between human-readable specifications and machine-verifiable correctness guarantees.*

### Property Reflection

Após análise das propriedades identificadas no prework, identifiquei algumas redundâncias que podem ser consolidadas:

- **Propriedades 2.4 e 3.4** (mensagens de sucesso após criação/edição) podem ser combinadas em uma propriedade sobre feedback de sucesso
- **Propriedades 2.5 e 3.5** (cancelamento de criação/edição) podem ser combinadas em uma propriedade sobre cancelamento de formulário
- **Propriedades sobre validação (5.2, 5.3)** podem ser combinadas em uma propriedade abrangente sobre validação

### Propriedades Consolidadas

**Property 1: Grade renderização completa**
*Para qualquer* lista de produtos, quando a grade é renderizada, todos os produtos devem aparecer com informações essenciais (descrição, SKU, código de barras, status e ações)
**Validates: Requirements 1.1, 1.2, 4.1**

**Property 2: Filtros funcionais**
*Para qualquer* conjunto de produtos e termo de busca/filtro de status, os produtos exibidos devem corresponder exatamente aos critérios aplicados
**Validates: Requirements 1.4**

**Property 3: Seleção de empresa atualiza produtos**
*Para qualquer* mudança de empresa selecionada, a grade deve ser atualizada para mostrar apenas os produtos da empresa selecionada
**Validates: Requirements 1.5**

**Property 4: Modal sobreposto à grade**
*Para qualquer* ação que abre o formulário (criar/editar), o formulário deve aparecer como modal mantendo a grade visível ao fundo
**Validates: Requirements 2.1, 2.2**

**Property 5: Criação/edição round trip**
*Para qualquer* produto criado ou editado através do formulário, os dados submetidos devem aparecer corretamente na grade após o sucesso da operação
**Validates: Requirements 2.3, 3.3**

**Property 6: Feedback de sucesso**
*Para qualquer* operação bem-sucedida (criar/editar), o sistema deve fechar o formulário e exibir mensagem de sucesso
**Validates: Requirements 2.4, 3.4**

**Property 7: Cancelamento preserva estado**
*Para qualquer* cancelamento de formulário, o estado da grade deve permanecer inalterado e o formulário deve fechar
**Validates: Requirements 2.5, 3.5**

**Property 8: Edição carrega dados corretos**
*Para qualquer* produto selecionado para edição, o formulário deve abrir preenchido com os dados exatos do produto e exibir título "Editar Produto"
**Validates: Requirements 3.1, 3.2**

**Property 9: Toggle de status funcional**
*Para qualquer* produto, clicar em ativar/desativar deve alterar o status e refletir a mudança na grade
**Validates: Requirements 4.2**

**Property 10: Exclusão com confirmação**
*Para qualquer* tentativa de exclusão, deve aparecer confirmação, e apenas após confirmação o produto deve ser removido da grade
**Validates: Requirements 4.3, 4.4**

**Property 11: Tratamento de erros**
*Para qualquer* operação que falhe, deve ser exibida mensagem de erro apropriada
**Validates: Requirements 4.5**

**Property 12: Validação em tempo real**
*Para qualquer* entrada inválida no formulário, deve haver validação imediata com destaque visual e mensagem explicativa
**Validates: Requirements 5.2, 5.3**

**Property 13: Estados de loading**
*Para qualquer* operação em processamento, o botão de envio deve ficar desabilitado e indicador de carregamento deve aparecer
**Validates: Requirements 5.4, 5.5**

## Error Handling

### Estratégias de Tratamento de Erro

1. **Erros de Rede**: Retry automático com backoff exponencial
2. **Erros de Validação**: Feedback imediato no formulário
3. **Erros de Servidor**: Mensagens user-friendly com opção de tentar novamente
4. **Erros de Estado**: Fallback para estado seguro conhecido

### Tipos de Erro

```typescript
interface ErrorState {
  type: 'network' | 'validation' | 'server' | 'unknown';
  message: string;
  field?: string; // Para erros de validação específicos
  retryable: boolean;
}
```

## Testing Strategy

### Abordagem Dual de Testes

O projeto utilizará tanto testes unitários quanto testes baseados em propriedades para garantir cobertura abrangente:

- **Testes Unitários**: Verificam exemplos específicos, casos extremos e condições de erro
- **Testes Baseados em Propriedades**: Verificam propriedades universais que devem ser válidas para todas as entradas

### Framework de Property-Based Testing

Utilizaremos **fast-check** como biblioteca de testes baseados em propriedades para TypeScript/JavaScript. Cada teste de propriedade será configurado para executar no mínimo 100 iterações.

### Estrutura de Testes

```typescript
// Exemplo de teste de propriedade
import fc from 'fast-check';

describe('Product Grid Properties', () => {
  it('should render all products with essential information', () => {
    fc.assert(fc.property(
      fc.array(productGenerator), // Gerador de produtos aleatórios
      (products) => {
        const rendered = renderProductGrid(products);
        // Verificar se todos os produtos aparecem com informações essenciais
        return products.every(product => 
          rendered.includes(product.description) &&
          rendered.includes(product.sku || '') &&
          rendered.includes(product.barcode || '')
        );
      }
    ), { numRuns: 100 });
  });
});
```

### Geradores de Dados de Teste

```typescript
const productGenerator = fc.record({
  id: fc.uuid(),
  description: fc.string({ minLength: 1, maxLength: 100 }),
  sku: fc.option(fc.string({ minLength: 1, maxLength: 50 })),
  barcode: fc.option(fc.string({ minLength: 1, maxLength: 50 })),
  isActive: fc.boolean(),
  // ... outros campos
});

const companyGenerator = fc.record({
  id: fc.uuid(),
  corporateName: fc.string({ minLength: 1, maxLength: 100 }),
  // ... outros campos
});
```

### Requisitos de Testes

- Cada propriedade de correção deve ser implementada por um ÚNICO teste baseado em propriedades
- Cada teste deve ser marcado com comentário referenciando a propriedade do documento de design
- Formato do comentário: `**Feature: product-grid-form-pattern, Property {number}: {property_text}**`
- Testes unitários complementam os testes de propriedades cobrindo casos específicos e integração entre componentes