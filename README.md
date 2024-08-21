# Projeto de Cotação de Seguros (ACME)

Este projeto é uma aplicação de cotação de seguros desenvolvida em Java utilizando Spring Boot e Maven. Ele inclui serviços para consultar produtos e ofertas, calcular cotações e realizar testes unitários.

## Arquitetura

### Ferramentas
Essa solução utiliza: 
- **Linguagem:** Java 17
- **Gerenciamento de depêndencias e build:** Maven
- **Framework(s):** Spring
- **Mensageria:** Apache Kafka

### Arquitetura utilizada

A arquitetura utilizada neste projeto é a arquitetura de microsserviços. Cada serviço é responsável por uma funcionalidade específica e se comunica com outros serviços através de APIs REST.

### Justificativa

A escolha da arquitetura de microsserviços foi feita com base nos seguintes pontos:

- **Escalabilidade**: Permite escalar individualmente cada serviço conforme a demanda.
- **Manutenibilidade**: Facilita a manutenção e evolução do sistema, pois cada serviço pode ser desenvolvido, testado e implantado de forma independente.
- **Desempenho**: Melhora o desempenho ao permitir que serviços críticos sejam otimizados e escalados separadamente.
- **Flexibilidade**: Permite a utilização de diferentes tecnologias e frameworks para cada serviço, conforme a necessidade.


## Endpoints v1

### `POST /v1/solicitacoes-cotacao`
Realiza a cotação de seguro com base nos dados fornecidos.

- **Request Body**: `SolicitacaoCotacaoInput` (mais detalhe na seção **Modelos**)
- **Response**: `ResponseEntity` com o status da operação e detalhes da cotação.

### `GET /v1/solicitacoes-cotacao`
Consulta todas as cotações existentes no banco de dados.

- **Response**: `ResponseEntity<List<CotacaoSeguroEntity>>` com os detalhes de todas as cotações.

### `GET /v1/solicitacoes-cotacao/{id_cotacao}`
Consulta informações de uma cotação específica.

- **Path Variable**: `id_cotacao` (ID da cotação)
- **Response**: `ResponseEntity<CotacaoSeguroEntity>` com os detalhes da cotação.

## Estrutura do projeto

### Pacotes principais

#### `com.acme.seguro.cotacoes`
Pacote raiz do projeto que contém as classes principais e de configuração.

#### `com.acme.seguro.cotacoes.model`
Contém as classes de modelo que representam os dados utilizados na aplicação.

#### `com.acme.seguro.cotacoes.model.db`
Contém as classes de modelo que representam as entidades do banco de dados.

#### `com.acme.seguro.cotacoes.model.input`
Contém as classes de modelo que representam os dados de entrada para os serviços.

#### `com.acme.seguro.cotacoes.model.output.mock`
Contém as classes de modelo que representam as respostas simuladas das consultas de produtos e ofertas.

#### `com.acme.seguro.cotacoes.repository`
Contém as interfaces de repositório para operações de persistência de dados.

#### `com.acme.seguro.cotacoes.service`
Contém as classes de serviço que implementam a lógica de negócios da aplicação.

#### `com.acme.seguro.cotacoes.config`
Contém as classes de configuração da aplicação.

#### `com.acme.seguro.cotacoes.controller`
Contém as classes de controlador que expõem as APIs REST da aplicação.

#### `com.acme.seguro.cotacoes.util`
Contém classes utilitárias usadas em toda a aplicação.

#### `com.acme.seguro.cotacoes.test`
Contém as classes de testes unitários.

### Classes principais

#### `CotacaoService`
Classe responsável por calcular a cotação de seguros. Ela valida a solicitação de cotação, consulta produtos e ofertas, e persiste os dados no banco de dados.

#### `ConsultaCatalogoService`
Classe responsável por consultar o catálogo de produtos e ofertas. Ela se comunica com APIs externas para obter as informações necessárias.

#### `CotacaoSeguroRepository`
Interface de repositório que estende `JpaRepository` para operações de persistência de dados relacionadas a cotações de seguros.

### Modelos

#### `SolicitacaoCotacaoInput`
Classe que representa a entrada de dados para uma solicitação de cotação. Inclui informações como ID do produto, ID da oferta, valores de prêmio mensal e total de cobertura, coberturas e assistências.

#### `ConsultaProdutoOutput`
Classe que representa a resposta da consulta de um produto. Inclui informações sobre o status (ativo ou inativo) do produto.

#### `ConsultaOfertaOutput`
Classe que representa a resposta da consulta de uma oferta. Inclui informações sobre o status da oferta, valores de prêmio mensal, coberturas e assistências.

#### `MonthlyPremiumAmount`
Classe que representa os valores de prêmio mensal mínimo, máximo e recomendado.

### Testes

#### `CotacaoServiceTest`
Classe de testes unitários para `CotacaoService`. Inclui testes para validar diferentes cenários de cotação (sucesso, produto ou oferta inexistente, e validações de valores de cobertura e assistências).

#### `ConsultaCatalogoServiceIntegrationTest`
Classe de testes de integração para `ConsultaCatalogoService`. Utiliza WireMock para simular respostas de APIs externas e validar a integração dos serviços.

## Configuração e Execução dos Testes

### Pré-requisitos
- Java 11 ou superior
- Maven 3.6 ou superior

### Como executar

1. Clone o repositório:
```
git clone https://github.com/martinsacs/acme-seguradora.git
cd acme-seguradora
```

2. Abra o arquivo pom.xml como projeto no IDE de sua preferência (recomendamos IntelliJ)

3. Execute o comando abaixo para compilar o projeto:
```
maven clean install
```

4. Digite a instrução abaixo para executar a aplicação:
```
mvn exec:java -Dexec.mainClass="com.acme.seguro.cotacoes.CotacoesApplication"
```