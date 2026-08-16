package com.nisha.bookmyshow;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CatalogApiTest extends AbstractApiTest {

    @Test
    void userCannotCreateMovie() throws Exception {
        String token = register("User", "u-" + UUID.randomUUID() + "@test.local", "Secret1");
        mockMvc.perform(post("/api/admin/movies")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"X","status":"NOW_SHOWING"}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminMovieTheaterShowCrud() throws Exception {
        String admin = adminToken();

        MvcResult movieResult = mockMvc.perform(post("/api/admin/movies")
                        .header("Authorization", "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Avengers","description":"Heroes","language":"English","genre":"Action",
                                 "duration":180,"releaseDate":"2012-04-27","posterUrl":"http://x","rating":8.4,"status":"NOW_SHOWING"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.title").value("Avengers"))
                .andReturn();
        long movieId = data(movieResult).path("id").asLong();

        mockMvc.perform(get("/api/movies/" + movieId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Avengers"));

        mockMvc.perform(get("/api/movies").param("q", "Aveng"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].title").value("Avengers"));

        mockMvc.perform(put("/api/admin/movies/" + movieId)
                        .header("Authorization", "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Avengers Endgame","description":"Heroes","language":"English","genre":"Action",
                                 "duration":181,"releaseDate":"2019-04-26","posterUrl":"http://x","rating":8.4,"status":"NOW_SHOWING"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Avengers Endgame"));

        MvcResult theaterResult = mockMvc.perform(post("/api/admin/theaters")
                        .header("Authorization", "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"PVR","location":"Mall","city":"Bengaluru","address":"Whitefield"}
                                """))
                .andExpect(status().isCreated())
                .andReturn();
        long theaterId = data(theaterResult).path("id").asLong();

        MvcResult screenResult = mockMvc.perform(post("/api/admin/screens")
                        .header("Authorization", "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Screen 2","theaterId":%d}
                                """.formatted(theaterId)))
                .andExpect(status().isCreated())
                .andReturn();
        long screenId = data(screenResult).path("id").asLong();

        mockMvc.perform(post("/api/admin/screens/" + screenId + "/seats")
                        .header("Authorization", "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"seatNumber":"A1","rowNumber":"A","seatType":"REGULAR","price":250}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/admin/shows")
                        .header("Authorization", "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"movieId":%d,"theaterId":%d,"screenId":%d,"showDate":"2026-08-20","startTime":"19:30:00","endTime":"22:30:00"}
                                """.formatted(movieId, theaterId, screenId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.movieTitle").value("Avengers Endgame"))
                .andExpect(jsonPath("$.data.theaterName").value("PVR"));

        mockMvc.perform(get("/api/shows").param("movieId", String.valueOf(movieId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].screenName").value("Screen 2"));

        mockMvc.perform(delete("/api/admin/movies/" + movieId)
                        .header("Authorization", "Bearer " + admin))
                .andExpect(status().isConflict());
    }

    @Test
    void missingMovieIsNotFound() throws Exception {
        mockMvc.perform(get("/api/movies/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
