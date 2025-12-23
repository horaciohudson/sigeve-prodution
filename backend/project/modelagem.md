1️⃣ Núcleo da Produção
Ordem de Produção (OP)

tab_production_orders

id (UUID)

code (string – OP-000123)

product_id

quantity_planned

quantity_produced

status (PLANNED, IN_PROGRESS, FINISHED, CANCELED)

start_date

end_date

cost_total (calculado)

tenant_id

created_at / updated_at

👉 É o centro de tudo

2️⃣ Produto de Produção (pode diferir do comercial)

tab_production_products

id

product_id (ref ao Gestor)

description

size

color

active

3️⃣ Composição (BOM – Bill of Materials)
Composição

tab_compositions

id

production_product_id

version

active

notes

Itens da Composição

tab_composition_items

id

composition_id

item_type (RAW_MATERIAL | SERVICE)

reference_id (material_id ou buy_service_id)

unit_type

quantity

loss_percentage

unit_cost (snapshot)

total_cost

👉 Daqui sai o custo padrão

4️⃣ Matéria-Prima (produção)

tab_raw_materials

id

name

unit_type

average_cost

stock_control (boolean)

Estoque de Produção

tab_raw_material_stocks

id

raw_material_id

quantity

Movimento

tab_raw_material_movements

id

raw_material_id

movement_type (IN | OUT)

origin (PURCHASE | PRODUCTION)

origin_id

quantity

unit_cost

5️⃣ Compra de Serviços (costura, lavagem, bordado)
Compra de Serviço

tab_buy_services

id

supplier_id

service_name

reference

base_value

total_value

status (OPEN, APPROVED, CLOSED)

Itens

tab_buy_service_items

id

buy_service_id

description

unit_type

unit_price

quantity

discount

total_price

👉 Usado pela composição e pela OP

6️⃣ Execução da Produção
Etapas / Operações

tab_production_steps

id

name (Corte, Costura, Acabamento)

sequence

Execução

tab_production_executions

id

production_order_id

step_id

start_time

end_time

quantity_done

loss_quantity

7️⃣ Apontamento de Custos Reais

tab_production_costs

id

production_order_id

cost_type (MATERIAL | SERVICE | LABOR | INDIRECT)

reference_id

quantity

unit_cost

total_cost

👉 Custo real ≠ custo padrão

8️⃣ Fechamento da Produção (integração)

tab_production_closures

id

production_order_id

total_material

total_service

total_labor

total_indirect

total_cost

closed_at

exported_to_financial (boolean)