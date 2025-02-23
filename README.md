# Health Dictionary - Hackathon Pós Tech FIAP

Hackathon Pós Tech FIAP - Aplicação de gestão de prontuários de ocorrências médicas e base de conhecimento para morbidades - CID

## **Tecnologias**

* **Linguagem:** Java 17
* **Framework principal:** Spring boot 3.4.2
* **Banco de dados:** Mysql latest
* **Banco de dados em memória:** H2 Database 2.3.232
* **Framework de testes:** Junit 5
* **Relatório de testes:** Allure 2.29.0

## **Collections**
### Collections com payloads de teste.

* **Authorization:** [clique aqui.](https://github.com/edu-off/health-dictionary/blob/main/collections/authorization_collection.json)
* **CID:** [clique aqui.](https://github.com/edu-off/health-dictionary/blob/main/collections/cid_collection.json)
* **Ocorrencia:** [clique aqui.](https://github.com/edu-off/health-dictionary/blob/main/collections/ocorrencia_collection.json)
* **Paciente:** [clique aqui.](https://github.com/edu-off/health-dictionary/blob/main/collections/paciente_collection.json)
* **Tratamento:** [clique aqui.](https://github.com/edu-off/health-dictionary/blob/main/collections/tratamento_collection.json)
* **Usuario:** [clique aqui.](https://github.com/edu-off/health-dictionary/blob/main/collections/usuario_collection.json)

## **Comandos importantes via makefile**

* Para build da aplicação:
```shell
make build
```

<br><br/>

* Para execução de testes unitários:
```shell
make unit-test
```

<br><br/>

* Para execução de testes integrados:
```shell
make integration-test
```

<br><br/>

* Para execução de todos os testes:
```shell
make all-tests
```

<br><br/>

* Para exibir relatório de testes:
```shell
make upload-tests-report
```

<br><br/>

* Para efetuar build da aplicação e gerar relatório de testes:
```shell
make generate-tests-report
```
Obs.: Para todos comandos listados, o make deve estar instalado na máquina de quem o for executar. Já para somente os dois últimos comandos, o node js e o allure devem estar instalados.

<br><br/>

## **Comandos importantes via terminal**

* Para execução de teste unitários:
```shell
mvn test
```

<br><br/>

* Para execução de teste integrados:
```shell
mvn test integration-test
```

<br><br/>

* Para exibição do relatório de testes:
```shell
allure serve target/allure-results
```
Obs.: O node js e o allure devem estar instalados e o build da aplicação estar concluído.
