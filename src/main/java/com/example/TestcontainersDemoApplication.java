package com.example;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.sql.DataSourceDefinition;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;

@SpringBootApplication
@EnableTransactionManagement
public class TestcontainersDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestcontainersDemoApplication.class, args);
    }

}

@Service
@RequiredArgsConstructor
class CustomerService {

    private final CustomerRepository customerRepository;

    @Transactional
    void saveAll(String... names) {
        Arrays.stream(names)
                .map(name -> new Customer(null, name))
                .flatMap(customer -> Stream.of(customerRepository.save(customer)));
    }

}

interface CustomerRepository extends CrudRepository<Customer, String> {
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("customers")
class Customer {

    private String id;
    private String name;
}