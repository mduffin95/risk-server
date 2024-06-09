build-RiskFunction:
	mvn clean package
	mkdir -p $(ARTIFACTS_DIR)/lib
	cp risk-lambda/target/risk-lambda*.jar $(ARTIFACTS_DIR)/lib/
