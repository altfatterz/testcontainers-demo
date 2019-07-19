package com.example;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.ReactiveMongoTransactionManager;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

@SpringBootApplication
@EnableTransactionManagement
public class TestcontainersDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestcontainersDemoApplication.class, args);
    }

    @Bean
    ReactiveTransactionManager reactiveTransactionManager(ReactiveMongoDatabaseFactory rmdf) {
        return new ReactiveMongoTransactionManager(rmdf);
    }
}

@Service
@RequiredArgsConstructor
class CustomerService {

    private final CustomerRepository customerRepository;


    @Transactional
    public Flux<Customer> saveAll(String... names) {
        Flux<Customer> customers = Flux.just(names)
                .map(name -> new Customer(null, name))
                .flatMap(customerRepository::save)
                .doOnNext(customer -> Assert.isTrue(!StringUtils.isEmpty(customer.getName()), "'names' query parameter must not be empty"));

        return customers;
    }

}

interface CustomerRepository extends ReactiveCrudRepository<Customer, String> {
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("customers")
class Customer {

    private String id;

    private String name;
}