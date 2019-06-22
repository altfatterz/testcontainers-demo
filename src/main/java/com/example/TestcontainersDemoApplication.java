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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
        List<Customer> customers = Arrays.stream(names)
                .map(name -> new Customer(null, name))
                .collect(Collectors.toList());
        customerRepository.saveAll(customers);
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