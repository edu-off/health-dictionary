build:
	@echo "executando build do projeto"
	@mvn clean install

unit-test:
	@echo "executando testes unitarios"
	@mvn test

integration-test:
	@echo "executando testes integrados"
	@mvn test -P integration-test

all-tests: unit-test integration-test

docker-start:
	@echo "subindo container da aplicacao"
	@docker-compose -f docker-compose.yml up -d

upload-tests-report:
	@echo "exibindo relatorio de testes"
	@allure serve target/allure-results

generate-tests-report: build upload-tests-report
