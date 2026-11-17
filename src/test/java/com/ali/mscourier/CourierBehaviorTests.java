package com.ali.mscourier;

import com.ali.mscourier.dto.CreateCourierRequest;
import com.ali.mscourier.enums.*;
import com.ali.mscourier.exception.*;
import com.ali.mscourier.repository.*;
import com.ali.mscourier.service.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class CourierBehaviorTests {
    @Autowired CourierService service;
    @Autowired CourierEventService events;
    @Autowired CourierRepository couriers;
    @Autowired CourierOrderRepository orders;
    @Autowired WebApplicationContext context;

    @BeforeEach
    void clear() { orders.deleteAll(); couriers.deleteAll(); }

    private CreateCourierRequest request() {
        var request = new CreateCourierRequest();
        request.setName("Ali");
        request.setFinCode("ABC1234");
        request.setGender(Gender.MALE);
        request.setAddress("Baku");
        request.setDateOfBirth(java.time.LocalDate.of(2000, 1, 1));
        return request;
    }

    @Test
    void persistsProfileAndReturnsGender() {
        var result = service.createCourier(request());
        var entity = couriers.findById(result.getId()).orElseThrow();
        assertThat(entity.getFinCode()).isEqualTo("ABC1234");
        assertThat(entity.getBirthDate()).isEqualTo(request().getDateOfBirth());
        assertThat(entity.getAddress()).isEqualTo("Baku");
        assertThat(result.getGender()).isEqualTo(Gender.MALE);
        assertThat(result.getStatus()).isEqualTo(CourierStatus.FREE);
        assertThatThrownBy(() -> service.createCourier(request())).isInstanceOf(CourierConflictException.class);
    }

    @Test
    void duplicateAndLateEventsCannotOverrideCurrentAssignment() {
        Long id = service.createCourier(request()).getId();
        events.apply("first", id, false);
        events.apply("first", id, false);
        assertThat(service.getAvailableCourier()).isEmpty();
        assertThatThrownBy(() -> events.apply("second", id, false)).isInstanceOf(CourierConflictException.class);
        assertThat(orders.existsById("second")).isFalse();
        assertThatThrownBy(() -> service.deleteCourier(id)).isInstanceOf(CourierConflictException.class);
        events.apply("first", id, true);
        events.apply("second", id, false);
        events.apply("first", id, true);
        events.apply("first", id, false);
        assertThat(couriers.findById(id).orElseThrow().getActiveOrderId()).isEqualTo("second");
        events.apply("second", id, true);
        assertThat(service.getAvailableCourier()).isPresent();
    }

    @Test
    void deliveredBeforeAssignedRemainsFree() {
        Long id = service.createCourier(request()).getId();
        events.apply("first", id, true);
        events.apply("first", id, false);
        assertThat(service.getCourier(id).getStatus()).isEqualTo(CourierStatus.FREE);
    }

    @Test
    void usesCustomExceptions() {
        assertThatThrownBy(() -> service.getCourier(999L)).isInstanceOf(CourierNotFoundException.class);
        assertThatThrownBy(() -> events.apply("order", 999L, false)).isInstanceOf(CourierNotFoundException.class);
        assertThatThrownBy(() -> events.apply("", 1L, false)).isInstanceOf(InvalidOrderEventException.class);
    }

    @Test
    void httpValidatesInputAndMapsDomainExceptions() throws Exception {
        var mvc = MockMvcBuilders.webAppContextSetup(context).build();
        mvc.perform(get("/api/v1/couriers/999")).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Courier not found with id: 999"));
        mvc.perform(post("/api/v1/couriers").contentType("application/json").content("{}"))
                .andExpect(status().isBadRequest());
        Long id = service.createCourier(request()).getId();
        mvc.perform(get("/api/v1/couriers/available")).andExpect(status().isOk());
        mvc.perform(get("/orders/couriers/available")).andExpect(status().isOk());
        events.apply("order", id, false);
        mvc.perform(delete("/api/v1/couriers/" + id)).andExpect(status().isConflict());
        mvc.perform(get("/api/v1/couriers/available")).andExpect(status().isNotFound());
    }
}
