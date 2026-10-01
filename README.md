# MTG Trade

Backend do ecossistema MTG Trade, uma plataforma para jogadores de *Magic:
The Gathering* catalogarem as suas cartas físicas e, numa próxima etapa,
encontrarem oportunidades de troca com outros jogadores.

O projeto resolve o problema de manter uma coleção física organizada,
identificando cada carta a partir de um catálogo centralizado e registrando os
atributos relevantes do exemplar do usuário, como quantidade, idioma,
acabamento, promoção e estado de conservação. A base atual disponibiliza
autenticação, gerenciamento de inventário e sincronização do catálogo com o
Scryfall; o matching entre usuários está previsto para a evolução do produto.

## Sumário

- [Visão geral](#visão-geral)
- [Arquitetura](#arquitetura)
- [Funcionalidades](#funcionalidades)
- [Tecnologias utilizadas](#tecnologias-utilizadas)
- [Destaques de engenharia](#destaques-de-engenharia)
- [Pré-requisitos](#pré-requisitos)
- [Configuração](#configuração)
- [Como executar](#como-executar)
- [API](#api)
- [Testes](#testes)
- [Roadmap do projeto](#roadmap-do-projeto)
  - [Épico 1: Catálogo](#épico-1-catálogo)
  - [Épico 2: Gateway e Autenticação](#épico-2-gateway-e-autenticação)
  - [Épico 3: Inventário](#épico-3-inventário)
  - [Épico 4: Ofertas](#épico-4-ofertas)
  - [Épico 5: Matchmaking](#épico-5-matchmaking)
- [Estrutura do projeto](#estrutura-do-projeto)
- [Licença](#licença)

## Visão geral

O MTG Trade é organizado como um pequeno ecossistema de serviços:

- **Gateway**: ponto de entrada HTTP na porta `9000`; diferencia rotas
  públicas e protegidas, valida tokens JWT e encaminha a identidade do usuário
  para a API.
- **MTG Trade API**: aplicação principal na porta `8080`; concentra as regras
  de identidade e acesso, catálogo e inventário.
- **PostgreSQL**: banco usado pela API para persistir usuários, cartas
  catalogadas e itens de inventário.

O caminho recomendado para consumir a aplicação é o gateway. As rotas de login
e registro são públicas; as demais exigem `Authorization: Bearer <token>`.

## Arquitetura

```text
Cliente
  |
  | HTTP :9000
  v
Gateway (Spring Cloud Gateway)
  |-- /api/v1/login e /api/v1/registro -> API :8080 (públicas)
  `-- demais rotas -> valida JWT, injeta X-User-Id -> API :8080
                                      |
                                      v
                              PostgreSQL :5433
                                      ^
                                      |
                             Scryfall Bulk Data
```

O catálogo externo é carregado por meio do endpoint de sincronização do
Scryfall. O processo lê o arquivo JSONL compactado em streaming, transforma as
linhas em entidades locais e persiste em lotes.

## Funcionalidades

- Registro de usuário com validação de entrada e proteção de senha usando
  PBKDF2.
- Login stateless com emissão de token JWT.
- Consulta paginada do catálogo de cartas por nome.
- Sincronização incremental do catálogo a partir do bulk data do Scryfall.
- Inclusão, consulta, atualização e remoção de itens do inventário do usuário.
- Upsert de exemplares com a mesma carta e os mesmos atributos físicos.
- Validação de acabamento, promoção, idioma e estado da carta.
- Respostas padronizadas para erros de domínio e erros de validação.


## Tecnologias utilizadas

| Categoria | Tecnologia |
| --- | --- |
| Linguagem | Java 21 |
| Framework da API | Spring Boot 4.1.x, Spring MVC |
| Gateway | Spring Cloud Gateway Server WebFlux |
| Persistência | Spring Data JPA, Hibernate e PostgreSQL 15 |
| Segurança | Spring Security, JWT (`java-jwt`) e PBKDF2 |
| Validação | Jakarta Bean Validation |
| Build | Maven Wrapper |
| Infraestrutura local | Docker Compose |
| Integração externa | Scryfall Bulk Data API |
| Testes | JUnit e Spring Boot Test |
| Produtividade | Lombok |

## Destaques de engenharia

### Separação por contexto

O código da API é separado em contextos de **IAM**, **catálogo** e
**inventário**, com camadas de domínio, aplicação, infraestrutura e web. O
catálogo expõe uma fachada própria para reduzir o acoplamento entre contextos.

### Segurança no gateway

O gateway mantém o fluxo de autenticação centralizado. Para rotas protegidas,
ele valida assinatura e `issuer` do JWT, rejeita tokens ausentes ou inválidos
com `401 Unauthorized` e substitui a informação `X-User-Id` pelo `subject` do
token antes de encaminhar a requisição.

### Inventário consistente

O banco possui uma restrição única por usuário, carta e atributos físicos
(`acabamento`, `tipo_promo`, `estado` e `idioma`). Isso impede duplicidades
semânticas e permite que a inclusão funcione como upsert.

### Sincronização incremental e eficiente

O processo de carga do Scryfall:

1. Descobre o bulk data mais recente.
2. Faz download do JSONL compactado em GZIP.
3. Processa o arquivo linha a linha, sem carregar todo o dataset em memória.
4. Ignora `print_id` já persistidos para realizar um delta load.
5. Persiste cartas em lotes de 1.000 registros.
6. Trata layouts e cartas sem imagem com exceções específicas.

### Contratos e tratamento de erros

DTOs imutáveis baseados em `record`, validação declarativa com Jakarta
Validation e um `GlobalExceptionHandler` tornam os contratos HTTP explícitos e
uniformes.

## Pré-requisitos

- Java 21 ou superior.
- Docker e Docker Compose.
- Acesso à internet para a sincronização com o Scryfall.
- Portas `5433`, `8080` e `9000` disponíveis.

O Maven não precisa ser instalado separadamente: cada serviço possui o seu
próprio Maven Wrapper.

## Configuração

Crie um arquivo `.env` na raiz do repositório. Ele é ignorado pelo Git e não
deve conter credenciais reais versionadas:

```dotenv
# Banco de dados
HOST_DB=localhost
PORT_DB=5433
POSTGRES_DB=catalogo_db
POSTGRES_USER=admin
POSTGRES_PASSWORD=admin_password

# Segurança: use um segredo forte em ambientes compartilhados
TOKEN_SECRET=troque-por-um-segredo-forte

# Aplicações
PORT_MTG_TRADE_API=8080
GATEWAY_PORT=9000
```

As mesmas variáveis são usadas pelo Docker Compose, pela API e pelo gateway.
Em produção, substitua os valores de desenvolvimento, especialmente
`POSTGRES_PASSWORD` e `TOKEN_SECRET`, por credenciais gerenciadas de forma
segura.

## Como executar

### 1. Subir o PostgreSQL

Na raiz do projeto:

```bash
docker compose up -d postgres-catalogo
```

Para acompanhar o estado dos serviços:

```bash
docker compose ps
```

### 2. Iniciar a API

Em um terminal, ainda na raiz:

```bash
./mtg-trade-api/mvnw -f mtg-trade-api/pom.xml spring-boot:run
```

A API ficará disponível em `http://localhost:8080`.

### 3. Iniciar o gateway

Em outro terminal:

```bash
./gateway/mvnw -f gateway/pom.xml spring-boot:run
```

O ponto de entrada recomendado ficará disponível em
`http://localhost:9000`.

### Encerrar a infraestrutura

```bash
docker compose down
```

Para remover também os dados persistidos do PostgreSQL, use
`docker compose down -v`. Essa operação é destrutiva para o volume local.

## API

Todos os exemplos abaixo usam o gateway (`http://localhost:9000`).

### Registrar usuário

`POST /api/v1/registro` — público.

O corpo exato é definido por `RegistroUsuarioRequestDTO`. Exemplo:

```bash
curl -i -X POST http://localhost:9000/api/v1/registro \
  -H 'Content-Type: application/json' \
  -d '{"nomeUsuario":"jogador1","email":"jogador1@example.com","senha":"senha-segura"}'
```

### Fazer login

`POST /api/v1/login` — público.

```bash
curl -s -X POST http://localhost:9000/api/v1/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"jogador1@example.com","senha":"senha-segura"}'
```

Use o token retornado como `TOKEN` nos exemplos seguintes:

```bash
TOKEN='cole-o-token-retornado-aqui'
```

### Consultar cartas

`GET /api/v1/cartas?nome=<termo>&page=0&size=20` — protegido e paginado.

```bash
curl -H "Authorization: Bearer ${TOKEN}" \
  'http://localhost:9000/api/v1/cartas?nome=Lightning&page=0&size=20'
```

### Sincronizar o catálogo

`POST /api/v1/cartas/sincronizar` — protegido. Executa manualmente a carga
incremental do bulk data mais recente do Scryfall.

```bash
curl -i -X POST http://localhost:9000/api/v1/cartas/sincronizar \
  -H "Authorization: Bearer ${TOKEN}"
```

### Gerenciar inventário

Todas as rotas de inventário são protegidas e associadas ao usuário
autenticado pelo gateway:

| Método | Rota | Finalidade |
| --- | --- | --- |
| `POST` | `/api/v1/inventario` | Adicionar ou incrementar um item |
| `GET` | `/api/v1/inventario` | Listar itens paginados do usuário |
| `GET` | `/api/v1/inventario/{itemId}` | Consultar um item |
| `PUT` | `/api/v1/inventario/{itemId}` | Atualizar atributos e quantidade |
| `DELETE` | `/api/v1/inventario/{itemId}` | Remover um item |

Exemplo de inclusão:

```bash
curl -i -X POST http://localhost:9000/api/v1/inventario \
  -H "Authorization: Bearer ${TOKEN}" \
  -H 'Content-Type: application/json' \
  -d '{
    "cartaCatalogoId": "uuid-da-carta",
    "acabamento": "NORMAL",
    "promo": "NONE",
    "quantidade": 1,
    "estado": "NM",
    "idioma": "PT"
  }'
```

As respostas de listagem seguem o formato paginado do Spring Data. Os valores
aceitos para atributos enumerados e as regras detalhadas de validação estão
implementados nos validadores do módulo de inventário.

## Testes

Execute os testes de cada serviço a partir da raiz:

```bash
./mtg-trade-api/mvnw -f mtg-trade-api/pom.xml test
./gateway/mvnw -f gateway/pom.xml test
```

Os testes de contexto precisam das configurações e dependências disponíveis no
ambiente de execução.

## Roadmap do projeto

O desenvolvimento do ecossistema segue uma abordagem de fatias verticais
(*Vertical Slicing*), garantindo entregas atômicas e testáveis. A evolução da
plataforma está organizada nos seguintes grandes épicos:

### Épico 1: Catálogo — Concluído

- Infraestrutura base, ETL de alta performance consumindo a API do Scryfall e
  indexação de consultas.

### Épico 2: Gateway e Autenticação — Concluído

- Provedor de identidade (IdP) artesanal isolado no monolito, malha de
  roteamento dinâmica (WebFlux) e segurança de borda validando JWT.

### Épico 3: Inventário — Em andamento

- **Sprint 1 (atual):** motor de regras físicas, prevenção de duplicidade
  semântica (Upsert/Merge) e CRUD básico.
- **Sprint 2:** inserção otimizada em lote para grandes volumes de cartas
  (estilo *Moxfield*).
- **Sprint 3:** sistema de agrupamento para que os usuários estruturem o seu
  inventário em Decks e *Binders*.

### Épico 4: Ofertas — Planejado

- Mecanismo para a criação, edição e cancelamento de propostas de troca entre
  jogadores.

### Épico 5: Matchmaking — Planejado

- Motor assíncrono orientado a eventos, utilizando RabbitMQ, para cruzar
  automaticamente os inventários de diferentes usuários e gerar notificações
  de *matches* perfeitos de troca.

## Estrutura do projeto

```text
.
├── docker-compose.yml
├── mtg-trade-api/
│   ├── pom.xml
│   └── src/
│       ├── main/java/.../iam/          # registro, login e JWT
│       ├── main/java/.../catalogo/     # catálogo local e integração Scryfall
│       ├── main/java/.../inventario/   # coleção física do usuário
│       └── main/java/.../shared/       # segurança e tratamento transversal
└── gateway/
    ├── pom.xml
    └── src/main/java/.../
        ├── filter/                    # filtro de autenticação JWT
        └── security/                  # validação do token
```

## Licença

Nenhum arquivo de licença foi definido no repositório até o momento.
