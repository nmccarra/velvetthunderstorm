package com.nmccarra.velvetthunderstorm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    private Logger logger = LoggerFactory.getLogger(BaseIntegrationTest.class);

    @Container
    protected static LocalStackContainer localStack = new LocalStackContainer(
            DockerImageName.parse("localstack/localstack:2.3.0")
    ).withServices(LocalStackContainer.Service.DYNAMODB);

    @Autowired
    protected DynamoDbClient dynamoDbClient;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("aws.dynamodb.endpoint",
                () -> localStack.getEndpointOverride(LocalStackContainer.Service.DYNAMODB).toString());
    }

    protected static final String TABLE_NAME = "velvet-thunderstorm-test";

    @BeforeEach
    void setUp() {
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        try {
            // Check if table exists
            dynamoDbClient.describeTable(DescribeTableRequest.builder()
                    .tableName(TABLE_NAME)
                    .build());
        } catch (ResourceNotFoundException e) {
            // Create table if it doesn't exist
            CreateTableRequest createTableRequest = CreateTableRequest.builder()
                    .tableName(TABLE_NAME)
                    .keySchema(
                            KeySchemaElement.builder()
                                    .attributeName("PK")
                                    .keyType(KeyType.HASH)
                                    .build(),
                            KeySchemaElement.builder()
                                    .attributeName("SK")
                                    .keyType(KeyType.RANGE)
                                    .build()
                    )
                    .attributeDefinitions(
                            AttributeDefinition.builder()
                                    .attributeName("PK")
                                    .attributeType(ScalarAttributeType.S)
                                    .build(),
                            AttributeDefinition.builder()
                                    .attributeName("SK")
                                    .attributeType(ScalarAttributeType.S)
                                    .build()
                    )
                    .billingMode(BillingMode.PAY_PER_REQUEST)
                    .build();

            dynamoDbClient.createTable(createTableRequest);

            // Wait for table to be active
            waitForTableToBeActive();
            logger.info(() -> "Created DynamoDB table: " + TABLE_NAME);
        }
    }

    private void waitForTableToBeActive() {
        int maxAttempts = 10;
        int attempt = 0;

        while (attempt < maxAttempts) {
            try {
                DescribeTableResponse response = dynamoDbClient.describeTable(
                        DescribeTableRequest.builder().tableName(TABLE_NAME).build()
                );

                if (TableStatus.ACTIVE.equals(response.table().tableStatus())) {
                    return;
                }

                Thread.sleep(1000);
                attempt++;
            } catch (Exception e) {
                attempt++;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        throw new RuntimeException("Table did not become active within timeout");
    }
}
