package com.nisha.bookmyshow;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BookingApiTest extends AbstractApiTest {

    @Test
    void bookingFlowDuplicateSeatCancelAndHistory() throws Exception {
        ShowSetup setup = createShowWithSeat("A1");
        String userA = register("Alice", "a-" + UUID.randomUUID() + "@test.local", "Secret1");
        String userB = register("Bob", "b-" + UUID.randomUUID() + "@test.local", "Secret1");

        mockMvc.perform(get("/api/shows/" + setup.showId + "/seats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].status").value("AVAILABLE"));

        MvcResult booked = mockMvc.perform(post("/api/bookings")
                        .header("Authorization", "Bearer " + userA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"showId":%d,"showSeatIds":[%d]}
                                """.formatted(setup.showId, setup.showSeatId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.bookingStatus").value("CONFIRMED"))
                .andExpect(jsonPath("$.data.paymentStatus").value("SUCCESS"))
                .andExpect(jsonPath("$.data.selectedSeats[0]").value("A1"))
                .andExpect(jsonPath("$.data.bookingReference").isNotEmpty())
                .andReturn();
        long bookingId = data(booked).path("bookingId").asLong();

        mockMvc.perform(post("/api/bookings")
                        .header("Authorization", "Bearer " + userB)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"showId":%d,"showSeatIds":[%d]}
                                """.formatted(setup.showId, setup.showSeatId)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value(containsString("A1")))
                .andExpect(jsonPath("$.message").value(containsString("no longer available")));

        mockMvc.perform(get("/api/bookings")
                        .header("Authorization", "Bearer " + userA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].bookingId").value(bookingId));

        mockMvc.perform(get("/api/bookings/upcoming")
                        .header("Authorization", "Bearer " + userA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].bookingId").value(bookingId));

        mockMvc.perform(get("/api/bookings/" + bookingId)
                        .header("Authorization", "Bearer " + userB))
                .andExpect(status().isForbidden());

        mockMvc.perform(delete("/api/bookings/" + bookingId)
                        .header("Authorization", "Bearer " + userA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.bookingStatus").value("CANCELLED"));

        mockMvc.perform(get("/api/bookings/past")
                        .header("Authorization", "Bearer " + userA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].bookingStatus").value("CANCELLED"));

        mockMvc.perform(post("/api/bookings")
                        .header("Authorization", "Bearer " + userB)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"showId":%d,"showSeatIds":[%d]}
                                """.formatted(setup.showId, setup.showSeatId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.bookingStatus").value("CONFIRMED"));

        mockMvc.perform(get("/api/admin/bookings/stats")
                        .header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalBookings").isNumber());
    }

    @Test
    void bookingRequiresAuthentication() throws Exception {
        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"showId":1,"showSeatIds":[1]}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void concurrentDuplicateSeatOnlyOneSucceeds() throws Exception {
        ShowSetup setup = createShowWithSeat("A2");
        String userA = register("Cara", "c-" + UUID.randomUUID() + "@test.local", "Secret1");
        String userB = register("Drew", "d-" + UUID.randomUUID() + "@test.local", "Secret1");
        String body = """
                {"showId":%d,"showSeatIds":[%d]}
                """.formatted(setup.showId, setup.showSeatId);

        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(2);
        AtomicInteger created = new AtomicInteger();
        AtomicInteger conflict = new AtomicInteger();

        Thread t1 = new Thread(() -> bookSeat(userA, body, start, done, created, conflict));
        Thread t2 = new Thread(() -> bookSeat(userB, body, start, done, created, conflict));
        t1.start();
        t2.start();
        start.countDown();
        done.await();
        t1.join();
        t2.join();

        org.junit.jupiter.api.Assertions.assertEquals(1, created.get(), "exactly one booking should succeed");
        org.junit.jupiter.api.Assertions.assertEquals(1, conflict.get(), "the other booking should conflict");
    }

    private void bookSeat(String token, String body, CountDownLatch start, CountDownLatch done,
                          AtomicInteger created, AtomicInteger conflict) {
        try {
            start.await();
            int status = mockMvc.perform(post("/api/bookings")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andReturn()
                    .getResponse()
                    .getStatus();
            if (status == 201) {
                created.incrementAndGet();
            } else if (status == 409) {
                conflict.incrementAndGet();
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            done.countDown();
        }
    }

    private ShowSetup createShowWithSeat(String seatNumber) throws Exception {
        String admin = adminToken();
        long movieId = data(mockMvc.perform(post("/api/admin/movies")
                        .header("Authorization", "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Avengers %s","language":"English","genre":"Action","duration":180,"status":"NOW_SHOWING"}
                                """.formatted(UUID.randomUUID())))
                .andExpect(status().isCreated()).andReturn()).path("id").asLong();

        long theaterId = data(mockMvc.perform(post("/api/admin/theaters")
                        .header("Authorization", "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"PVR","city":"Bengaluru","address":"Mall"}
                                """))
                .andExpect(status().isCreated()).andReturn()).path("id").asLong();

        long screenId = data(mockMvc.perform(post("/api/admin/screens")
                        .header("Authorization", "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Screen 2","theaterId":%d}
                                """.formatted(theaterId)))
                .andExpect(status().isCreated()).andReturn()).path("id").asLong();

        mockMvc.perform(post("/api/admin/screens/" + screenId + "/seats")
                        .header("Authorization", "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"seatNumber":"%s","rowNumber":"A","seatType":"REGULAR","price":250}
                                """.formatted(seatNumber)))
                .andExpect(status().isCreated());

        long showId = data(mockMvc.perform(post("/api/admin/shows")
                        .header("Authorization", "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"movieId":%d,"theaterId":%d,"screenId":%d,"showDate":"2026-08-20","startTime":"19:30:00","endTime":"22:30:00"}
                                """.formatted(movieId, theaterId, screenId)))
                .andExpect(status().isCreated()).andReturn()).path("id").asLong();

        JsonNode seats = data(mockMvc.perform(get("/api/shows/" + showId + "/seats"))
                .andExpect(status().isOk()).andReturn());
        long showSeatId = seats.get(0).path("showSeatId").asLong();
        return new ShowSetup(showId, showSeatId);
    }

    private record ShowSetup(long showId, long showSeatId) {
    }
}
