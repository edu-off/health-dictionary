build:
	@echo "executando build do projeto"
	@mvn clean install

unit-test:
	@echo "executando testes unitarios"
	@mvn test

integration-test:
	@echo "executando testes integrados"
	@mvn test -P integration-test

test: unit-test integration-test

docker-start:
	@echo "subindo container da aplicacao"
	@docker-compose -f docker-compose.yml up -d

subindo-relatorio-testes:
	@echo "exibindo relatorio de testes"
	@allure serve target/allure-results

gerando-relatorio-testes: build subindo-relatorio-testes
