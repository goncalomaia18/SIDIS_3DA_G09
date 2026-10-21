# SIDIS 2026/2027 --- Assignment 1

## AISafe Flight Management System

### Decisões de Arquitetura, DDD e Plano de Implementação

## 1. Contexto

O projeto parte da aplicação **AISafe Flight Management System**,
desenvolvida anteriormente no âmbito de Projeto de Software (PSOFT). A
aplicação disponibiliza APIs REST para gestão de:

-   Aircraft models e aircraft instances;[SIDIS_DDD_Decisoes_Arquitetura.md](SIDIS_DDD_Decisoes_Arquitetura.md)
-   Airports;
-   Flight routes;
-   Maintenance records e maintenance templates;
-   Scheduled flight operations.

A implementação original apresenta uma arquitetura monolítica. No âmbito
de **Sistemas Distribuídos (SIDIS)**, pretende-se evoluir esta solução
para uma arquitetura distribuída, com especial atenção à
descentralização, interoperabilidade, escalabilidade, disponibilidade,
tolerância a falhas e resiliência.

------------------------------------------------------------------------

## 2. Estratégia geral adotada

A estratégia proposta consiste em **reaproveitar a lógica de negócio
existente no projeto de PSOFT**, evitando uma reimplementação
desnecessária, mas reorganizando a aplicação segundo fronteiras de
domínio.

A aplicação será decomposta em três serviços independentes:

1.  **Aircraft & Maintenance Service**
2.  **Airports & Routes Service**
3.  **Flight Operations Service**

Cada serviço deverá:

-   possuir responsabilidades de negócio bem definidas;
-   controlar os dados pertencentes ao seu contexto;
-   poder ser executado e disponibilizado independentemente;
-   comunicar com outros serviços através da rede;
-   possuir múltiplas instâncias/réplicas;
-   participar nos mecanismos de tolerância a falhas e distribuição
    pedidos no trabalho.

Esta divisão segue diretamente a proposta de *domain-driven segregation*
apresentada no enunciado.

------------------------------------------------------------------------

## 3. Domain-Driven Design (DDD)

### 3.1. O que é DDD?

**Domain-Driven Design (DDD)** é uma abordagem de desenho de software
que procura estruturar o sistema em torno do domínio de negócio e das
respetivas responsabilidades.

Neste projeto, o objetivo não é separar componentes apenas porque
pertencem a diferentes packages ou camadas técnicas. Pretende-se
identificar áreas do negócio que possuem responsabilidades próprias e
estabelecer fronteiras claras entre elas.

Uma noção particularmente importante é o **Bounded Context**.

Um Bounded Context estabelece uma fronteira dentro da qual determinado
modelo, regras e conceitos possuem significado próprio e são geridos de
forma coerente.

------------------------------------------------------------------------

## 4. Bounded Contexts escolhidos

Foram identificados três Bounded Contexts principais, alinhados com a
decomposição sugerida no enunciado.

### 4.1. Aircraft & Maintenance

Este contexto será responsável pela gestão das aeronaves e respetiva
manutenção.

Inclui, entre outros:

-   modelos de aeronaves;
-   instâncias de aeronaves;
-   características das aeronaves;
-   estado operacional;
-   templates de manutenção;
-   registos de manutenção.

**Justificação:** aeronaves e manutenção possuem uma relação funcional
forte. O estado e histórico de manutenção de uma aeronave influenciam
diretamente a sua disponibilidade operacional. Mantê-los no mesmo
contexto evita separar conceitos altamente relacionados e permite
concentrar as regras relativas à condição operacional da frota.

------------------------------------------------------------------------

### 4.2. Airports & Routes

Este contexto será responsável pelos aeroportos e pela definição das
rotas.

Inclui, entre outros:

-   aeroportos;
-   pistas;
-   certificações e informação associada aos aeroportos;
-   rotas;
-   histórico das rotas.

**Justificação:** uma rota depende diretamente dos aeroportos que liga e
das suas características. A definição e validação de rotas utiliza
informação pertencente ao domínio aeroportuário, pelo que estes
elementos apresentam elevada coesão funcional.

------------------------------------------------------------------------

### 4.3. Flight Operations

Este contexto será responsável pelas operações de voo.

Inclui, entre outros:

-   scheduled flights;
-   atribuição de aeronaves;
-   associação dos voos às rotas;
-   estado das operações;
-   verificações de disponibilidade;
-   consultas operacionais.

**Justificação:** este contexto representa a utilização operacional da
informação fornecida pelos restantes contextos. Um voo necessita de uma
rota válida e de uma aeronave adequada e disponível, mas o contexto
Flight Operations não deve passar a ser responsável pela gestão interna
desses recursos.

------------------------------------------------------------------------

## 5. Core Domain e Supporting Subdomains

Como decisão de desenho da equipa, propõe-se considerar:

-   **Core Domain:** Flight Operations
-   **Supporting Subdomain:** Aircraft & Maintenance
-   **Supporting Subdomain:** Airports & Routes

### Justificação

O **Flight Operations** pode ser considerado o Core Domain porque
representa a atividade operacional central do AISafe: planear e gerir
operações de voo através da combinação de aeronaves, rotas e condições
de disponibilidade.

Aircraft & Maintenance e Airports & Routes são essenciais ao
funcionamento do sistema, mas fornecem capacidades e informação
necessárias para suportar as operações de voo.

> Esta classificação é uma decisão de modelação proposta para o projeto.
> O enunciado determina a separação possível dos três serviços, mas não
> classifica explicitamente qual deles é o Core Domain.

------------------------------------------------------------------------

## 6. Relação entre DDD e os serviços distribuídos

A estratégia será fazer corresponder cada Bounded Context a um serviço
independente:

``` text
AISafe Flight Management System
│
├── Aircraft & Maintenance
│   └── Aircraft & Maintenance Service
│
├── Airports & Routes
│   └── Airports & Routes Service
│
└── Flight Operations
    └── Flight Operations Service
```

Esta escolha permite:

-   maior coesão dentro de cada serviço;
-   menor acoplamento entre domínios;
-   evolução independente;
-   deployment independente;
-   replicação independente;
-   isolamento de falhas;
-   fronteiras claras de responsabilidade.

------------------------------------------------------------------------

## 7. Propriedade dos dados

Cada serviço deverá controlar os dados correspondentes ao seu próprio
Bounded Context.

Por exemplo, o **Flight Operations Service não deverá aceder diretamente
ao `AircraftRepository` ou ao `RouteRepository`**.

No monólito original podem existir dependências deste género:

``` text
ScheduledFlightService
│
├── ScheduledFlightRepository
├── AircraftRepository
└── RouteRepository
```

Na arquitetura distribuída pretende-se evoluir para:

``` text
ScheduledFlightService
│
├── ScheduledFlightRepository
├── Aircraft Service Client ──REST──> Aircraft & Maintenance Service
└── Route Service Client ─────REST──> Airports & Routes Service
```

### Justificação

Permitir acesso direto aos repositories ou à base de dados de outro
serviço criaria novamente forte acoplamento entre os contextos.

Ao obrigar a comunicação através de interfaces de serviço bem definidas:

-   cada serviço mantém controlo sobre os seus dados;
-   alterações internas têm menor impacto nos restantes serviços;
-   os serviços podem ser executados separadamente;
-   a comunicação passa efetivamente pela rede, como é esperado num
    sistema distribuído.

------------------------------------------------------------------------

## 8. Comunicação entre serviços

A comunicação entre serviços será efetuada através de protocolos leves,
sendo **HTTP/REST** a escolha principal.

### Justificação

O enunciado identifica HTTP/REST como a solução esperada. Além disso, o
projeto original já utiliza uma arquitetura REST, permitindo
reaproveitar controllers, DTOs e parte significativa da lógica
existente.

Exemplo:

``` text
Flight Operations Service
        │
        ├──── HTTP/REST ────> Aircraft & Maintenance Service
        │
        └──── HTTP/REST ────> Airports & Routes Service
```

O Flight Operations poderá, por exemplo, consultar o Aircraft &
Maintenance Service para verificar a existência e disponibilidade de uma
aeronave e o Airports & Routes Service para obter ou validar informação
de uma rota.

------------------------------------------------------------------------

## 9. Replicação dos serviços

O enunciado exige a existência de **múltiplas réplicas de cada
serviço**.

Uma representação simplificada é:

``` text
Aircraft & Maintenance
├── Replica 1
└── Replica 2

Airports & Routes
├── Replica 1
└── Replica 2

Flight Operations
├── Replica 1
└── Replica 2
```

### Objetivos da replicação

A replicação pretende contribuir para:

-   maior disponibilidade;
-   distribuição de carga;
-   redundância;
-   tolerância à falha de uma instância;
-   melhoria da resiliência do sistema.

O número concreto de réplicas e a configuração final serão definidos
durante a implementação e demonstração.

------------------------------------------------------------------------

## 10. Comunicação Peer-to-Peer entre réplicas

Um requisito importante do trabalho é o comportamento perante um pedido
`GET` dirigido a uma réplica que não contém os dados procurados devido à
distribuição/particionamento dos dados.

Nesse cenário, a réplica deverá consultar os seus peers.

Exemplo:

``` text
CLIENT
  │
  │ GET /resource/X
  ▼
Replica A
  │
  ├── procura localmente
  │
  ├── não encontrou X
  │
  ├── consulta Replica B
  └── consulta Replica C
             │
             ▼
      agrega respostas
             │
             ▼
           CLIENT
```

### Justificação

Esta estratégia permite que o cliente não necessite de saber qual
réplica contém determinado recurso. A própria rede de réplicas participa
na localização da informação e na construção de uma resposta coerente.

A implementação deverá evitar ciclos de propagação e pedidos repetidos.
O mecanismo concreto para identificação e controlo dos pedidos P2P será
definido durante a implementação.

------------------------------------------------------------------------

## 11. Consistência e CAP

O enunciado indica que o desenho deve favorecer:

-   **eventual consistency**;
-   tolerância a falhas baseada em redundância;
-   resiliência perante falhas parciais da rede;
-   prioridades de **Availability** e **Partition Tolerance** do teorema
    CAP.

Assim, perante uma partição de rede, o objetivo será favorecer a
disponibilidade do sistema, aceitando que diferentes réplicas possam,
temporariamente, não possuir exatamente o mesmo estado.

### Eventual Consistency

A consistência eventual significa que as réplicas podem apresentar
diferenças temporárias, mas, na ausência de novas alterações e após a
propagação necessária, deverão convergir para um estado consistente.

O mecanismo concreto de sincronização/propagação de alterações entre
réplicas deverá ser definido e justificado durante o desenvolvimento.

------------------------------------------------------------------------

## 12. Tolerância a falhas

Uma das motivações para abandonar o monólito é reduzir os *single points
of failure*.

A solução deverá ser preparada para demonstrar situações como:

``` text
Replica 1     Replica 2
   X              ✓
 FALHOU          ATIVA
```

A falha de uma réplica não deverá, sempre que existam alternativas
disponíveis, implicar a indisponibilidade total do serviço.

Durante a demonstração poderão ser realizados testes desligando
deliberadamente uma instância e verificando o comportamento das
restantes.

------------------------------------------------------------------------

## 13. Segurança

O enunciado identifica três áreas concretas que têm de ser tratadas:

1.  **Inter-service authentication**
2.  **Encryption of data in transit**
3.  **Access control and audit logging**

### 13.1. Autenticação entre serviços

Os serviços não deverão confiar automaticamente em qualquer pedido
recebido através da rede. Deverá existir um mecanismo que permita
autenticar comunicações entre componentes autorizados.

O projeto original já contém mecanismos de autenticação e autorização
que poderão ser analisados e adaptados, evitando reimplementação
desnecessária.

### 13.2. Encriptação em trânsito

A comunicação de informação operacional sensível deverá ser protegida
durante a transmissão. A solução final deverá demonstrar ou documentar a
utilização de canais protegidos, de acordo com a implementação
escolhida.

### 13.3. Role-Based Access Control

O acesso às operações deverá respeitar as permissões associadas aos
utilizadores/roles. A lógica existente de roles e segurança no projeto
PSOFT será reutilizada sempre que seja adequada à nova arquitetura.

### 13.4. Audit Logging

Operações relevantes deverão produzir informação de auditoria que
permita identificar acontecimentos importantes, acessos ou alterações
realizadas no sistema.

Os detalhes do modelo de auditoria serão definidos durante a
implementação.

------------------------------------------------------------------------

## 14. Estratégia de reaproveitamento do projeto PSOFT

Não se pretende reescrever todo o AISafe.

A estratégia será:

``` text
PSOFT
  │
  │ reaproveitar
  ▼
Entidades + regras + services + controllers + DTOs + testes
  │
  │ reorganizar
  ▼
3 serviços independentes
  │
  │ acrescentar
  ▼
Comunicação REST
Replicação
P2P
Consistência
Tolerância a falhas
Segurança distribuída
```

Desta forma, o esforço é concentrado nos objetivos de Sistemas
Distribuídos e não na repetição de funcionalidades já implementadas.

------------------------------------------------------------------------

## 15. Plano de implementação

### Fase 1 --- Preservar a implementação original

Criar uma cópia ou branch do projeto PSOFT funcional para permitir
comparação e evitar a perda da implementação original.

### Fase 2 --- Separar os três serviços

Criar três aplicações executáveis:

``` text
aircraft-maintenance-service
airports-routes-service
flight-operations-service
```

Inicialmente, o objetivo será conseguir iniciar os três serviços
independentemente.

### Fase 3 --- Separar responsabilidades e dados

Mover para cada aplicação apenas as entidades, repositories, services,
controllers e DTOs pertencentes ao respetivo Bounded Context.

Eliminar dependências diretas entre repositories pertencentes a
contextos diferentes.

### Fase 4 --- Implementar comunicação REST

Substituir dependências locais entre contextos por clientes HTTP/REST.

Exemplo:

``` text
Flight Operations
      │
      ├──REST──> Aircraft & Maintenance
      └──REST──> Airports & Routes
```

### Fase 5 --- Implementar réplicas

Executar múltiplas instâncias dos serviços e configurar a identificação
dos peers.

### Fase 6 --- Implementar consultas P2P

Quando uma réplica não possuir os dados pedidos, consultar as restantes
réplicas, recolher as respostas e produzir uma resposta agregada.

### Fase 7 --- Implementar estratégia de consistência

Definir e implementar a estratégia necessária para que os dados
distribuídos pelas réplicas possam convergir, documentando as
implicações da consistência eventual.

### Fase 8 --- Segurança

Implementar e demonstrar:

-   autenticação entre serviços;
-   proteção da comunicação em trânsito;
-   controlo de acesso baseado em roles;
-   audit logging.

### Fase 9 --- Testes de falha

Testar cenários como:

-   uma réplica indisponível;
-   dados existentes apenas noutra réplica;
-   comunicação entre diferentes serviços;
-   pesquisa P2P;
-   agregação das respostas;
-   pedidos sem autorização;
-   recuperação/manutenção da disponibilidade perante falhas parciais.

### Fase 10 --- Documentação e deployment

Documentar:

-   arquitetura;
-   decisões DDD;
-   Bounded Contexts;
-   comunicação entre serviços;
-   estratégia de replicação;
-   modelo de consistência;
-   relação com CAP;
-   mecanismos de tolerância a falhas;
-   mecanismos de segurança;
-   instruções para executar e demonstrar o sistema.

------------------------------------------------------------------------

## 16. Arquitetura alvo

``` text
                         CLIENT / POSTMAN
                                │
              ┌─────────────────┼─────────────────┐
              │                 │                 │
              ▼                 ▼                 ▼
      Aircraft &          Airports &          Flight
      Maintenance           Routes           Operations
        Service             Service            Service
           │                   │                  │
      ┌────┴────┐         ┌────┴────┐        ┌────┴────┐
      │         │         │         │        │         │
   Replica   Replica   Replica   Replica   Replica   Replica
      A1        A2        R1        R2        F1        F2
      ↕ P2P     ↕         ↕ P2P     ↕         ↕ P2P     ↕
```

Os serviços comunicam entre si através da rede, enquanto as réplicas de
um mesmo serviço colaboram na localização/agregação de informação e na
manutenção da disponibilidade.

------------------------------------------------------------------------

## 17. Resumo das principais decisões

  -----------------------------------------------------------------------
  Decisão                 Escolha                 Justificação
  ----------------------- ----------------------- -----------------------
  Abordagem de desenho    DDD                     Separar segundo
                                                  responsabilidades de
                                                  negócio

  Bounded Context 1       Aircraft & Maintenance  Elevada relação entre
                                                  aeronave, estado e
                                                  manutenção

  Bounded Context 2       Airports & Routes       Rotas dependem
                                                  diretamente da
                                                  informação dos
                                                  aeroportos

  Bounded Context 3       Flight Operations       Concentra as operações
                                                  e agendamento de voos

  Core Domain proposto    Flight Operations       Representa a atividade
                                                  operacional central do
                                                  AISafe

  Comunicação             HTTP/REST               Esperada pelo enunciado
                                                  e compatível com a base
                                                  PSOFT

  Dados                   Controlados pelo        Reduz acoplamento e
                          respetivo serviço       preserva as fronteiras
                                                  dos contextos

  Disponibilidade         Múltiplas réplicas      Redundância e
                                                  tolerância a falhas

  Pesquisa distribuída    P2P entre réplicas      Permite localizar dados
                                                  que não estão na
                                                  réplica contactada

  Consistência            Eventual consistency    Alinhada com as
                                                  prioridades AP
                                                  indicadas no enunciado

  Segurança               Auth + encryption +     Requisitos explícitos
                          RBAC + audit            para dados operacionais
  -----------------------------------------------------------------------

------------------------------------------------------------------------

## 18. Conclusão

A evolução proposta transforma o AISafe de uma aplicação monolítica numa
solução distribuída organizada segundo fronteiras de domínio.

A utilização de DDD permite definir responsabilidades claras através de
três Bounded Contexts. A separação desses contextos em serviços
independentes reduz o acoplamento e cria a base necessária para
introduzir replicação, comunicação peer-to-peer, consistência eventual e
tolerância a falhas.

A implementação será realizada de forma incremental, reaproveitando a
lógica funcional do projeto PSOFT e concentrando o desenvolvimento
adicional nos requisitos específicos de Sistemas Distribuídos.

As decisões ainda não impostas explicitamente pelo enunciado ---
nomeadamente detalhes do mecanismo de sincronização, quantidade final de
réplicas, prevenção de ciclos P2P e implementação concreta de alguns
mecanismos de segurança --- deverão ser validadas e justificadas durante
o desenvolvimento.
