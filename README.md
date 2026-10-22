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
- **MTG Trade API**: aplicação principal na porta `8080` dentro da rede Docker;
  concentra as regras de identidade e acesso, catálogo e inventário.
- **PostgreSQL**: banco usado pela API para persistir usuários, cartas
  catalogadas e itens de inventário. A porta `5433` é publicada apenas para
  acesso local ao banco.

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
                              PostgreSQL :5432 (rede Docker)
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

- Docker e Docker Compose.
- Acesso à internet para a sincronização com o Scryfall.
- Porta `9000` disponível para o gateway. A porta `5433` também deve estar
  disponível caso você queira acessar o PostgreSQL diretamente pelo host.

As imagens da API e do gateway são construídas pelos respectivos Dockerfiles,
usando Java 21 e o Maven Wrapper durante o build. Não é necessário instalar
Java ou Maven no host.

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
MONOLITO_URL=http://localhost:8080
PORT_MTG_TRADE_API=8080
GATEWAY_PORT=9000
```

O Compose usa `MONOLITO_URL=http://monolito-mtg-trade-api:8080` para que o
gateway encontre a API pelo nome do serviço na rede interna. Para a API, o
Compose também sobrescreve a conexão do banco para usar
`postgres-catalogo:5432`; portanto, `HOST_DB=localhost` e `PORT_DB=5433` são
valores voltados ao acesso a partir do host.

Em produção, substitua os valores de desenvolvimento, especialmente
`POSTGRES_PASSWORD` e `TOKEN_SECRET`, por credenciais gerenciadas de forma
segura.

## Como executar

### 1. Subir todo o ecossistema

Na raiz do projeto, com o arquivo `.env` configurado:

```bash
docker compose up -d --build
```

Esse comando:

- cria o volume persistente `catalogo_data`;
- inicia o PostgreSQL e aguarda o healthcheck;
- constrói e inicia a API;
- constrói e inicia o gateway, publicado em `http://localhost:9000`.

Para acompanhar o estado dos serviços e os logs:

```bash
docker compose ps
docker compose logs -f
```

O serviço `monolito-mtg-trade-api` não publica uma porta no host. Ele é
acessado pelo gateway através da rede interna do Compose.

### Reconstruir após alterações

```bash
docker compose up -d --build
```

Para reconstruir somente um serviço:

```bash
docker compose up -d --build monolito-mtg-trade-api
docker compose up -d --build gateway
```

### Parar o ecossistema

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
| `POST` | `/api/v1/inventario/lote` | Adicionar ou incrementar vários itens em uma única operação |
| `POST` | `/api/v1/inventario/lote/previa` | Validar uma lista no formato Moxfield e obter uma prévia da importação |
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

### Inserção de inventário em lote

`POST /api/v1/inventario/lote` — protegido.

Recebe uma lista de itens no mesmo formato da inclusão individual e executa um
bulk upsert. Linhas com a mesma carta e os mesmos atributos físicos são
consolidadas, somando as quantidades. Itens já existentes no inventário também
recebem a quantidade enviada.

```bash
curl -i -X POST http://localhost:9000/api/v1/inventario/lote \
  -H "Authorization: ******" \
  -H 'Content-Type: application/json' \
  -d '{
    "lista": [
      {
        "cartaCatalogoId": "uuid-da-carta",
        "acabamento": "NORMAL",
        "promo": "NONE",
        "quantidade": 2,
        "estado": "NM",
        "idioma": "PT"
      },
      {
        "cartaCatalogoId": "outro-uuid",
        "acabamento": "NORMAL",
        "promo": "NONE",
        "quantidade": 1,
        "estado": "LP",
        "idioma": "EN"
      }
    ]
  }'
```

A resposta `200 OK` contém um objeto com a propriedade `lista`, formada pelos
itens persistidos e seus detalhes de catálogo:

```json
{
  "lista": [
    {
      "id": "uuid-do-item",
      "acabamento": "NORMAL",
      "promo": "NONE",
      "quantidade": 2,
      "estado": "NM",
      "idioma": "Português",
      "cartaCatalogoId": "uuid-da-carta",
      "nome": "Nome da carta",
      "edicao": "Edição",
      "imagemFrenteUrl": "https://exemplo/imagem.jpg"
    }
  ]
}
```

### Prévia de importação no formato Moxfield

`POST /api/v1/inventario/lote/previa` — protegido.

Permite conferir uma lista antes de persistir o inventário. O campo `lista`
recebe texto com uma carta por linha, no formato:

```text
<quantidade> <nome da carta> (<sigla da edição>) <número de colecionador>
```

Exemplo:

```bash
curl -i -X POST http://localhost:9000/api/v1/inventario/lote/previa \
  -H "Authorization: ******" \
  -H 'Content-Type: application/json' \
  --data-binary '{
    "lista": "1 Emry, Lurker of the Loch (EOC) 71\n2 Sol Ring (CMM) 320"
  }'
```

A resposta retorna uma entrada por linha, indicando se a carta foi encontrada
no catálogo e, quando encontrada, seu identificador, detalhes da edição,
imagem, quantidade e opções de acabamento e promoção. Linhas vazias,
malformadas ou não encontradas são retornadas com
`encontradaNoCatalogo: false` e podem ser corrigidas antes da chamada ao
endpoint de lote.

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

### Épico 3: Inventário — Concluído

- **Sprint 1:** motor de regras físicas, prevenção de duplicidade
  semântica (Upsert/Merge) e CRUD básico.
- **Sprint 2:** inserção otimizada em lote para grandes volumes de cartas
  (estilo *Moxfield*), incluindo consolidação de duplicidades e soma de
  quantidades.
- **Sprint 3:** sistema de agrupamento para que os usuários estruturem o seu
  inventário em Decks e *Binders*.

Os três sprints do inventário foram finalizados, incluindo o CRUD individual,
o bulk upsert e o fluxo de prévia para importação de coleções.

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
