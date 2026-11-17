# AISafe - Documentacao de Entrega (Assignment 2)

## 1. Objetivo

Este documento consolida o estado de implementacao do trabalho face ao enunciado do professor (CQRS, AMQP, REST externo, Database-per-Service por instancia, multiplas instancias e testes de integracao), incluindo evidencias no codigo e lacunas a fechar.

## 2. Contexto e Arquitetura

O sistema encontra-se separado em tres microservicos:

- `aircraft-maintenance-service`
- `airports-routes-service`
- `flight-operations-service`

Comunicacao externa: HTTP/REST.

Comunicacao interna assicrona: RabbitMQ (exchange topica `sidis.events`).

Persistencia: H2 por servico (com isolamento por instancia via `server.port`).

## 3. Matriz de Conformidade com o Enunciado

### 3.1 CQRS nos recursos core

**Estado:** Parcial alto (quase completo)

**Evidencias**

- Aircraft & Maintenance
  - Read models: `C:\Users\Administrador\IdeaProjects\SIDIS_3DA_G09\aircraft-maintenance-service\src\main\java\pt\isep\sidis\aircraftmaintenance\readmodel\AircraftReadModel.java`
  - Read models: `C:\Users\Administrador\IdeaProjects\SIDIS_3DA_G09\aircraft-maintenance-service\src\main\java\pt\isep\sidis\aircraftmaintenance\readmodel\MaintenanceRecordReadModel.java`
  - Projections: `C:\Users\Administrador\IdeaProjects\SIDIS_3DA_G09\aircraft-maintenance-service\src\main\java\pt\isep\sidis\aircraftmaintenance\projections\AircraftProjectionHandler.java`
  - Projections: `C:\Users\Administrador\IdeaProjects\SIDIS_3DA_G09\aircraft-maintenance-service\src\main\java\pt\isep\sidis\aircraftmaintenance\projections\MaintenanceRecordProjectionHandler.java`

- Airports & Routes
  - Command side: `C:\Users\Administrador\IdeaProjects\SIDIS_3DA_G09\airports-routes-service\src\main\java\pt\isep\sidis\airportsroutes\application\commands\RouteCommandHandler.java`
  - Query side: `C:\Users\Administrador\IdeaProjects\SIDIS_3DA_G09\airports-routes-service\src\main\java\pt\isep\sidis\airportsroutes\application\queries\RouteQueryHandler.java`
  - Read model: `C:\Users\Administrador\IdeaProjects\SIDIS_3DA_G09\airports-routes-service\src\main\java\pt\isep\sidis\airportsroutes\readmodel\RouteReadModel.java`

- Flight Operations
  - Command side: `C:\Users\Administrador\IdeaProjects\SIDIS_3DA_G09\flight-operations-service\src\main\java\pt\isep\sidis\flightoperations\application\commands\ScheduledFlightCommandHandler.java`
  - Query side: `C:\Users\Administrador\IdeaProjects\SIDIS_3DA_G09\flight-operations-service\src\main\java\pt\isep\sidis\flightoperations\application\queries\ScheduledFlightQueryHandler.java`
  - Read model: `C:\Users\Administrador\IdeaProjects\SIDIS_3DA_G09\flight-operations-service\src\main\java\pt\isep\sidis\flightoperations\readmodel\ScheduledFlightReadModel.java`

**Observacao:** em `aircraft-maintenance-service` a separacao existe via read model/projecoes/repositorios, mas sem nomenclatura explicita `CommandHandler`/`QueryHandler`.

### 3.2 AMQP + Message Broker entre servicos

**Estado:** Cumpre (com pontos a reforcar na demonstracao)

**Evidencias**

- Config RabbitMQ - Aircraft Maintenance
  - `C:\Users\Administrador\IdeaProjects\SIDIS_3DA_G09\aircraft-maintenance-service\src\main\java\pt\isep\sidis\aircraftmaintenance\messaging\RabbitMQConfig.java`
  - Publisher: `C:\Users\Administrador\IdeaProjects\SIDIS_3DA_G09\aircraft-maintenance-service\src\main\java\pt\isep\sidis\aircraftmaintenance\messaging\AircraftEventPublisher.java`

- Config RabbitMQ - Airports Routes
  - `C:\Users\Administrador\IdeaProjects\SIDIS_3DA_G09\airports-routes-service\src\main\java\pt\isep\sidis\airportsroutes\messaging\RabbitMQConfig.java`
  - Publisher: `C:\Users\Administrador\IdeaProjects\SIDIS_3DA_G09\airports-routes-service\src\main\java\pt\isep\sidis\airportsroutes\messaging\RouteEventPublisher.java`
  - Consumer: `C:\Users\Administrador\IdeaProjects\SIDIS_3DA_G09\airports-routes-service\src\main\java\pt\isep\sidis\airportsroutes\messaging\AircraftEventConsumer.java`

- Config RabbitMQ - Flight Operations
  - `C:\Users\Administrador\IdeaProjects\SIDIS_3DA_G09\flight-operations-service\src\main\java\pt\isep\sidis\flightoperations\messaging\RabbitMQConfig.java`
  - Consumers: `C:\Users\Administrador\IdeaProjects\SIDIS_3DA_G09\flight-operations-service\src\main\java\pt\isep\sidis\flightoperations\messaging\AircraftEventConsumer.java`
  - Consumers: `C:\Users\Administrador\IdeaProjects\SIDIS_3DA_G09\flight-operations-service\src\main\java\pt\isep\sidis\flightoperations\messaging\RouteEventConsumer.java`

### 3.3 Endpoints externos em HTTP REST

**Estado:** Cumpre

**Evidencias**

- `C:\Users\Administrador\IdeaProjects\SIDIS_3DA_G09\aircraft-maintenance-service\src\main\java\pt\isep\sidis\aircraftmaintenance\api\AircraftController.java`
- `C:\Users\Administrador\IdeaProjects\SIDIS_3DA_G09\aircraft-maintenance-service\src\main\java\pt\isep\sidis\aircraftmaintenance\api\MaintenanceController.java`
- `C:\Users\Administrador\IdeaProjects\SIDIS_3DA_G09\airports-routes-service\src\main\java\pt\isep\sidis\airportsroutes\api\AirportController.java`
- `C:\Users\Administrador\IdeaProjects\SIDIS_3DA_G09\airports-routes-service\src\main\java\pt\isep\sidis\airportsroutes\api\RouteController.java`
- `C:\Users\Administrador\IdeaProjects\SIDIS_3DA_G09\flight-operations-service\src\main\java\pt\isep\sidis\flightoperations\api\ScheduledFlightController.java`

### 3.4 Database-per-Service (instancia dedicada por microservico)

**Estado:** Parcial alto

**Evidencias**

- Aircraft Maintenance (persistente por porta)
  - `C:\Users\Administrador\IdeaProjects\SIDIS_3DA_G09\aircraft-maintenance-service\src\main\resources\application.properties`
  - URL: `jdbc:h2:file:./data/aircraftdb_${server.port}`

- Flight Operations (persistente por porta)
  - `C:\Users\Administrador\IdeaProjects\SIDIS_3DA_G09\flight-operations-service\src\main\resources\application.properties`
  - URL: `jdbc:h2:file:./data/flightdb_${server.port}`

- Airports Routes (isolado por porta, em memoria)
  - `C:\Users\Administrador\IdeaProjects\SIDIS_3DA_G09\airports-routes-service\src\main\resources\application.properties`
  - URL: `jdbc:h2:mem:airports_db_${server.port}`

**Observacao:** o isolamento por instancia esta presente, mas `airports-routes-service` usa H2 in-memory (sem ficheiro persistente).

### 3.5 Pelo menos 2 instancias de cada microservico

**Estado:** Parcial

**Evidencias**

- Configuracoes por `server.port` e filas RabbitMQ por porta em todos os servicos.
- Em `flight-operations-service`, referencia a duas instancias externas:
  - `services.airports-routes.urls=http://localhost:8083,http://localhost:8084`
  - `services.aircraft-maintenance.urls=http://localhost:8081,http://localhost:8085`
- Ficheiros de BD ja existentes na raiz `data/`:
  - `aircraftdb_8081.mv.db`
  - `aircraftdb_8085.mv.db`
  - `flightdb_8080.mv.db`
  - `flightdb_8082.mv.db`

**O que falta para fechar criterio:** evidencias de execucao simultanea das 2 instancias para cada um dos 3 servicos (logs/print/roteiro de demo).

### 3.6 Testes de integracao (Postman ou equivalente)

**Estado:** Parcial

**Evidencias**

- Guia de testes Postman/curl:
  - `C:\Users\Administrador\IdeaProjects\SIDIS_3DA_G09\aircraft-maintenance-service\postman-tests.md`
- Colecao API:
  - `C:\Users\Administrador\IdeaProjects\SIDIS_3DA_G09\Documentacao\hoppscotch-team-collections-openapi.json`

**Lacuna:** falta colecao integrada orientada a cenarios end-to-end entre os 3 servicos (com validacao de efeitos assincros).

### 3.7 Documentacao de decisoes, consistencia, comunicacao e tolerancia a falhas

**Estado:** Parcial

**Evidencias atuais**

- README detalhado do servico de manutencao:
  - `C:\Users\Administrador\IdeaProjects\SIDIS_3DA_G09\aircraft-maintenance-service\README.md`
- Diagramas de arquitetura e sequencia:
  - `C:\Users\Administrador\IdeaProjects\SIDIS_3DA_G09\Documentacao\diagramas\`

**Lacuna:** falta um documento unificado com:
- modelo de consistencia eventual adotado,
- estrategia de retries/idempotencia,
- comportamento em falha de broker/consumidor,
- alternativas arquiteturais consideradas e trade-offs.

## 4. Estado dos testes no workspace

Resultados encontrados em `target/surefire-reports`:

- `aircraft-maintenance-service`: presente (1 teste de contexto, sem falhas)
- `airports-routes-service`: presente (1 teste de contexto, sem falhas)
- `flight-operations-service`: sem relatorio surefire encontrado no estado atual da pasta

## 5. Comandos de validacao rapida para demonstracao

Executar testes por servico:

```powershell
Push-Location C:\Users\Administrador\IdeaProjects\SIDIS_3DA_G09\aircraft-maintenance-service
.\mvnw.cmd test
Pop-Location

Push-Location C:\Users\Administrador\IdeaProjects\SIDIS_3DA_G09\airports-routes-service
.\mvnw.cmd test
Pop-Location

Push-Location C:\Users\Administrador\IdeaProjects\SIDIS_3DA_G09\flight-operations-service
.\mvnw.cmd test
Pop-Location
```

Exemplo de arranque multi-instancia (ajustar terminais separados):

```powershell
Push-Location C:\Users\Administrador\IdeaProjects\SIDIS_3DA_G09\aircraft-maintenance-service
$env:SERVER_PORT="8081"; .\mvnw.cmd spring-boot:run
```

```powershell
Push-Location C:\Users\Administrador\IdeaProjects\SIDIS_3DA_G09\aircraft-maintenance-service
$env:SERVER_PORT="8085"; .\mvnw.cmd spring-boot:run
```

```powershell
Push-Location C:\Users\Administrador\IdeaProjects\SIDIS_3DA_G09\airports-routes-service
$env:SERVER_PORT="8083"; .\mvnw.cmd spring-boot:run
```

```powershell
Push-Location C:\Users\Administrador\IdeaProjects\SIDIS_3DA_G09\airports-routes-service
$env:SERVER_PORT="8084"; .\mvnw.cmd spring-boot:run
```

```powershell
Push-Location C:\Users\Administrador\IdeaProjects\SIDIS_3DA_G09\flight-operations-service
$env:SERVER_PORT="8080"; .\mvnw.cmd spring-boot:run
```

```powershell
Push-Location C:\Users\Administrador\IdeaProjects\SIDIS_3DA_G09\flight-operations-service
$env:SERVER_PORT="8082"; .\mvnw.cmd spring-boot:run
```

## 6. Percentagem de conclusao estimada

**Estimativa atual:** **85%**

Justificacao:

- Arquitetura distribuida, CQRS e AMQP estao implementados com boa cobertura estrutural.
- Persistencia por instancia ja esta forte em 2 servicos e isolada por porta no terceiro.
- Faltam evidencias finais de demonstracao integrada (2 instancias por servico em simultaneo), testes end-to-end unificados e documentacao transversal de resiliencia/consistencia.

## 7. Checklist final para fechar 100%

1. Executar e registar evidencias de 2 instancias simultaneas para os 3 servicos.
2. Consolidar uma colecao de testes integrada (Postman/Hoppscotch) com cenarios cross-service.
3. Adicionar secao de consistencia eventual, retries, idempotencia e fault tolerance (broker indisponivel/consumidor off).
4. Opcional tecnico: migrar `airports-routes-service` de `h2:mem` para `h2:file` por porta, para alinhamento total com persistencia local.
5. Incluir um roteiro unico de demo para apresentacao ao professor.

