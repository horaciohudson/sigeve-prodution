# Requirements Document - Gerenciamento de Itens de Composição

## Introduction

Este documento especifica os requisitos para implementar o gerenciamento de itens dentro das composições (BOM - Bill of Materials). O sistema deve permitir adicionar, editar, remover e visualizar itens (matérias-primas e serviços) que compõem um produto de produção.

## Glossary

- **Composition_System**: Sistema de gerenciamento de composições de produtos
- **Composition_Item**: Item individual dentro de uma composição (matéria-prima ou serviço)
- **Raw_Material**: Matéria-prima utilizada na produção
- **Service**: Serviço terceirizado utilizado na produção
- **BOM**: Bill of Materials (Lista de Materiais)
- **Sequence**: Ordem de utilização do item na produção
- **Loss_Percentage**: Percentual de perda esperado do material
- **Unit_Cost**: Custo unitário do item
- **Total_Cost**: Custo total calculado (quantidade × custo unitário × (1 + perda))

## Requirements

### Requirement 1

**User Story:** Como um usuário do sistema, eu quero visualizar os itens de uma composição, para que eu possa entender quais materiais e serviços são necessários para produzir um produto.

#### Acceptance Criteria

1. WHEN um usuário acessa uma composição THEN o Composition_System SHALL exibir uma lista de todos os itens da composição
2. WHEN os itens são exibidos THEN o Composition_System SHALL mostrar tipo, nome, código, quantidade, unidade, custo unitário e custo total de cada item
3. WHEN os itens são listados THEN o Composition_System SHALL ordenar os itens por sequência de produção
4. WHEN não há itens na composição THEN o Composition_System SHALL exibir uma mensagem informativa
5. WHEN o custo total da composição é calculado THEN o Composition_System SHALL somar todos os custos dos itens incluindo perdas

### Requirement 2

**User Story:** Como um usuário do sistema, eu quero adicionar itens a uma composição, para que eu possa definir quais materiais e serviços são necessários para a produção.

#### Acceptance Criteria

1. WHEN um usuário clica em adicionar item THEN o Composition_System SHALL abrir um formulário de criação de item
2. WHEN o formulário é exibido THEN o Composition_System SHALL permitir selecionar entre matéria-prima e serviço terceirizado
3. WHEN uma matéria-prima é selecionada THEN o Composition_System SHALL listar apenas matérias-primas ativas da empresa
4. WHEN um item é selecionado THEN o Composition_System SHALL preencher automaticamente a unidade de medida e custo unitário
5. WHEN os dados são válidos e salvos THEN o Composition_System SHALL adicionar o item à composição e atualizar a lista

### Requirement 3

**User Story:** Como um usuário do sistema, eu quero editar itens de uma composição, para que eu possa ajustar quantidades, custos e outras propriedades conforme necessário.

#### Acceptance Criteria

1. WHEN um usuário clica em editar um item THEN o Composition_System SHALL abrir o formulário preenchido com os dados atuais
2. WHEN os dados são modificados e válidos THEN o Composition_System SHALL atualizar o item na composição
3. WHEN a quantidade ou custo é alterado THEN o Composition_System SHALL recalcular automaticamente o custo total do item
4. WHEN o percentual de perda é alterado THEN o Composition_System SHALL recalcular o custo considerando a nova perda
5. WHEN a sequência é alterada THEN o Composition_System SHALL reordenar os itens na lista

### Requirement 4

**User Story:** Como um usuário do sistema, eu quero remover itens de uma composição, para que eu possa manter apenas os materiais e serviços realmente necessários.

#### Acceptance Criteria

1. WHEN um usuário clica em remover um item THEN o Composition_System SHALL solicitar confirmação da ação
2. WHEN a remoção é confirmada THEN o Composition_System SHALL excluir o item da composição
3. WHEN um item é removido THEN o Composition_System SHALL atualizar automaticamente o custo total da composição
4. WHEN um item é removido THEN o Composition_System SHALL reordenar as sequências dos itens restantes
5. WHEN não há mais itens THEN o Composition_System SHALL exibir a mensagem de composição vazia

### Requirement 5

**User Story:** Como um usuário do sistema, eu quero calcular automaticamente os custos dos itens, para que eu possa ter uma visão precisa do custo de produção.

#### Acceptance Criteria

1. WHEN um item é adicionado ou editado THEN o Composition_System SHALL calcular automaticamente o custo total do item
2. WHEN o cálculo é realizado THEN o Composition_System SHALL aplicar a fórmula: quantidade × custo unitário × (1 + percentual de perda)
3. WHEN todos os itens têm custos THEN o Composition_System SHALL calcular e exibir o custo total da composição
4. WHEN um custo unitário não está disponível THEN o Composition_System SHALL permitir inserção manual
5. WHEN custos são atualizados THEN o Composition_System SHALL persistir os valores calculados imediatamente

### Requirement 6

**User Story:** Como um usuário do sistema, eu quero validar os dados dos itens, para que eu possa garantir a integridade das informações da composição.

#### Acceptance Criteria

1. WHEN um item é criado ou editado THEN o Composition_System SHALL validar que todos os campos obrigatórios estão preenchidos
2. WHEN a quantidade é informada THEN o Composition_System SHALL validar que é um número positivo maior que zero
3. WHEN o percentual de perda é informado THEN o Composition_System SHALL validar que está entre 0% e 100%
4. WHEN um item duplicado é adicionado THEN o Composition_System SHALL impedir a duplicação e exibir mensagem de erro
5. WHEN dados inválidos são submetidos THEN o Composition_System SHALL exibir mensagens de erro específicas para cada campo

### Requirement 7

**User Story:** Como um usuário do sistema, eu quero filtrar e pesquisar itens disponíveis, para que eu possa encontrar rapidamente os materiais e serviços necessários.

#### Acceptance Criteria

1. WHEN o usuário está selecionando um item THEN o Composition_System SHALL permitir pesquisa por nome ou código
2. WHEN uma pesquisa é realizada THEN o Composition_System SHALL filtrar os resultados em tempo real
3. WHEN matérias-primas são listadas THEN o Composition_System SHALL exibir apenas itens ativos da empresa selecionada
4. WHEN nenhum resultado é encontrado THEN o Composition_System SHALL exibir mensagem informativa
5. WHEN um item é selecionado da lista THEN o Composition_System SHALL preencher automaticamente os campos relacionados

### Requirement 8

**User Story:** Como um usuário do sistema, eu quero gerenciar a sequência dos itens, para que eu possa definir a ordem correta de utilização na produção.

#### Acceptance Criteria

1. WHEN itens são exibidos THEN o Composition_System SHALL permitir reordenação através de drag-and-drop ou botões
2. WHEN a sequência é alterada THEN o Composition_System SHALL atualizar automaticamente os números de sequência
3. WHEN um novo item é adicionado THEN o Composition_System SHALL atribuir automaticamente a próxima sequência disponível
4. WHEN um item é removido THEN o Composition_System SHALL reorganizar as sequências para manter continuidade
5. WHEN a sequência é salva THEN o Composition_System SHALL persistir a nova ordem imediatamente