package rocks.basset.brewery.web.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import rocks.basset.brewery.domain.Customer;
import rocks.basset.brewery.repositories.BeerOrderRepository;
import rocks.basset.brewery.repositories.CustomerRepository;
import rocks.basset.brewery.web.model.BeerOrderPagedList;

import javax.swing.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BeerOrderControllerIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    CustomerRepository customerRepository;

    Customer customer;

    @BeforeEach
    void setUp() {
        customer = customerRepository.findAll().get(0);
    }

    @Test
    void listOrders() {
        BeerOrderPagedList pagedList = restTemplate.getForObject("/api/v1/customers/{customerId}/orders", BeerOrderPagedList.class, customer.getId());
        assertThat(pagedList.getContent()).hasSize(1);
    }
}
