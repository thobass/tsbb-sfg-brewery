package rocks.basset.brewery.web.controllers;

import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import rocks.basset.brewery.services.BeerOrderService;
import rocks.basset.brewery.web.model.BeerOrderDto;
import rocks.basset.brewery.web.model.BeerOrderPagedList;
import rocks.basset.brewery.web.model.CustomerDto;
import rocks.basset.brewery.web.model.OrderStatusEnum;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.reset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BeerOrderController.class)
class BeerOrderControllerTest {

    @MockBean
    BeerOrderService beerOrderService;

    @Autowired
    MockMvc mockMvc;

    BeerOrderDto validOrder;

    CustomerDto validCustomer;
    @BeforeEach
    void setUp() {
        validCustomer = CustomerDto.builder()
                .id(UUID.randomUUID())
                .version(1)
                .name("Thomas Basset")
                .build();

        validOrder = BeerOrderDto.builder()
                .id(UUID.randomUUID())
                .orderStatus(OrderStatusEnum.NEW)
                .version(1)
                .createdDate(OffsetDateTime.now())
                .lastModifiedDate(OffsetDateTime.now())
                .customerId(validCustomer.getId())
                .customerRef("123123")
                .orderStatusCallbackUrl("callbackurl")
                .build();
    }



    @Test
    void getOrder() throws Exception {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");

        given(beerOrderService.getOrderById(any(UUID.class), any(UUID.class))).willReturn(validOrder);

        mockMvc.perform(get("/api/v1/customers/{customerId}/orders/{orderId}", validCustomer.getId(), validOrder.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(validOrder.getId().toString())))
                .andExpect(jsonPath("$.customerId", is(validCustomer.getId().toString())))
                .andExpect(jsonPath("$.createdDate", is(dateTimeFormatter.format(validOrder.getCreatedDate()))));
    }

    @DisplayName("List Orders - ")
    @Nested
    public class TestListOrders {

        @Captor
        ArgumentCaptor<UUID> customerIdCaptor;

        @Captor
        ArgumentCaptor<PageRequest> pargeRequestCaptor;

        BeerOrderPagedList beerOrderPagedList;

        @BeforeEach
        void setUp() {
            List<BeerOrderDto> orders = new ArrayList<>();
            orders.add(validOrder);

            orders.add(BeerOrderDto.builder()
                    .id(UUID.randomUUID())
                    .customerId(validCustomer.getId())
                    .build());

            beerOrderPagedList = new BeerOrderPagedList(orders, PageRequest.of(1,1), 2L);

            given(beerOrderService.listOrders(customerIdCaptor.capture(), pargeRequestCaptor.capture()))
                    .willReturn(beerOrderPagedList);
        }

        @Test
        @DisplayName("Test list orders - with no parameters")
        void listOrders_whenNoParameters() throws Exception {
            mockMvc.perform(get("/api/v1/customers/{customerId}/orders", validCustomer.getId()))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content", hasSize(2)))
                    .andExpect(jsonPath("$.content[0].id", is(validOrder.getId().toString())));
        }
    }

    @AfterEach
    void tearDown() {
        reset(beerOrderService);
    }
}
