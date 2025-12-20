# Beer API – Testes Unitários e TDD (Spring Boot)

Este repositório é um **fork** do projeto Beer API, originalmente desenvolvido no contexto da Digital Innovation One (DIO). O objetivo deste fork é **compreender, analisar e evoluir a estratégia de testes unitários**, aplicando boas práticas amplamente utilizadas no mercado, como **JUnit 5**, **Mockito** e **TDD (Test Driven Development)**.

---

## Objetivo do Projeto

O foco principal deste projeto não é apenas a implementação de uma API REST, mas sim:

* Entender o conceito de **testes unitários**
* Compreender como aplicar testes em **regras de negócio**
* Utilizar **mocks** para isolar dependências
* Analisar testes existentes e compreender seus cenários
* Compreender o uso de **TDD na prática**

Este projeto simula um sistema simples de gerenciamento de cervejas, com funcionalidades básicas de CRUD.

---

## Tecnologias Utilizadas apresentadas na aula 2 e 3

* **Java 14**
* **Spring Boot**
* **JUnit**
* **Mockito**
* **Hamcrest**
* **Maven 3.6.3**
* **H2**
* **Swagger**

---

## Estrutura do Projeto

```
src/
 ├── main/
 │   └── java/
 │       └── one.digitalinnovation.beerstock/
 │           ├── config/
 │           ├── controller/
 │           ├── dto/
 │           ├── entity/
 │           └── enums/
 │           ├── exception/
 │           ├── mapper/
 │           ├── repository/
 │           └── service/
 │
 └── test/
     └── java/
         └── one.digitalinnovation.beerstock/
             ├── builder/
             ├── controller/
             ├── service/
             ├── utils/
```

Os testes unitários estão concentrados na **camada service**, pois é nela que estão as principais regras de negócio.

---

## Testes Unitários

Os testes foram desenvolvidos com foco em **isolamento**, ou seja, nenhuma dependência externa (como banco de dados) é utilizada.

Para isso, o **BeerRepository** é mockado utilizando Mockito.

### Funcionalidades testadas

* Criação de cerveja
* Listagem de cervejas
* Consulta de cerveja por nome
* Exclusão de cerveja
* Validação de regras de negócio por meio de exceções existentes

---

## TDD – Test Driven Development

O projeto apresenta exemplos de aplicação do TDD, permitindo compreender o ciclo:

1. **Red** – criação de um teste que falha
2. **Green** – implementação mínima para o teste passar
3. **Refactor** – melhoria do código sem alterar o comportamento

## Aprendizados Obtidos

Com este projeto foi possível:

* Entender a importância dos testes unitários no ciclo de desenvolvimento
* Aprender a isolar regras de negócio usando mocks
* Compreender como testar exceções e cenários negativos
* Compreender o conceito de TDD aplicado no projeto
* Melhorar a leitura e organização de testes

---

## Considerações Finais

Este fork tem como objetivo demonstrar **conhecimento prático em testes unitários**, algo essencial para o desenvolvimento de aplicações confiáveis e manuteníveis.

O projeto serve tanto como material de estudo quanto como **item de portfólio**, evidenciando boas práticas adotadas no mercado de desenvolvimento Java com Spring Boot.

---
