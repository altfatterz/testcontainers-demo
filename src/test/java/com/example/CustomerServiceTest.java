package com.example;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;

import java.io.IOException;

import static org.testcontainers.containers.Network.newNetwork;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(initializers = CustomerServiceTest.Initializer.class)
public class CustomerServiceTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerService customerService;

    // Use @ClassRule to set up something that can be reused by all the test methods, if you can achieve that in a static method.
    // Use @Rule to set up something that needs to be created a new, or reset, for each test method.

    @ClassRule
    public static GenericContainer mongo = new GenericContainer<>("mongo:4.0.9")
            .withExposedPorts(27017)
            .withCommand("--replSet demo --bind_ip localhost")
            ;

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            Integer mappedPort = mongo.getMappedPort(27017);
            TestPropertyValues.of(
                    "spring.data.mongodb.uri=mongodb://localhost:" + mappedPort + "/test?replicaSet=demo"
            ).applyTo(configurableApplicationContext.getEnvironment());
//            TestPropertyValues.of(
//                    "spring.data.mongodb.uri=mongodb://localhost:" + mappedPort + "/test"
//            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    @BeforeClass
    public static void setUpBeforeClass() throws IOException, InterruptedException {
        Container.ExecResult execResult = mongo.execInContainer("/bin/sh", "-c", "mongo --eval 'rs.initiate()'");
        System.out.println(execResult);
    }


    // somehow connection fails:
    // com.mongodb.MongoSocketReadException: Prematurely reached end of stream
    // 2019-06-22 20:29:04.883  INFO 30018 --- [localhost:32849] org.mongodb.driver.cluster               :
    // Exception in monitor thread while connecting to server localhost:32849

    @Before
    public void setUp() throws IOException, InterruptedException {
        customerRepository.deleteAll();
    }

    @Test
    public void saveAll() {
        customerService.saveAll("John Doe", "Jane Doe");
        customerRepository.findAll().forEach(System.out::println);
    }

}
