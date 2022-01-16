package com.example.demo.railway;

import com.example.demo.railway.Person;
import com.example.demo.railway.ReactorDemoApplication;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = {ReactorDemoApplication.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PesonControllerTests {

  @Autowired private WebTestClient webTestClient;

  @BeforeAll
  static void setUpBeforeClass() throws Exception {}

  @AfterAll
  static void tearDownAfterClass() throws Exception {}

  @BeforeEach
  void setUp() throws Exception {}

  @AfterEach
  void tearDown() throws Exception {}

  @Test
  void successfulValidationApiTest() {
    Person person =
        Person.builder()
            .hasWristBand(true)
            .heightInCm(180)
            .weightInKg(80)
            .isPrgenant(false)
            .hasHeartCondition(false)
            .jumpedQueue(false)
            .build();

    webTestClient
        .post()
        .uri("/person/validate")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .body(Mono.just(person), Person.class)
        .exchange()
        .expectStatus()
        .isOk()
        .expectHeader()
        .contentType(MediaType.APPLICATION_JSON)
        .expectBody()
        .jsonPath("$.hasWristBand")
        .isEqualTo(person.hasWristBand())
        .jsonPath("$.heightInCm")
        .isEqualTo(person.getHeightInCm())
        .jsonPath("$.weightInKg")
        .isEqualTo(person.getWeightInKg())
        .jsonPath("$.isPrgenant")
        .isEqualTo(person.isPrgenant())
        .jsonPath("$.hasHeartCondition")
        .isEqualTo(person.hasHeartCondition())
        .jsonPath("$.jumpedQueue")
        .isEqualTo(person.jumpedQueue());
  }

  @Test
  void multipleValidationFailuresApiTest() {
    Person person =
        Person.builder()
            .hasWristBand(false)
            .heightInCm(180)
            .weightInKg(80)
            .isPrgenant(false)
            .hasHeartCondition(false)
            .jumpedQueue(true)
            .build();

    webTestClient
        .post()
        .uri("/person/validate")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .body(Mono.just(person), Person.class)
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectHeader()
        .contentType(MediaType.APPLICATION_JSON)
        .expectBody()
        .jsonPath("$.message")
        .isEqualTo("Person has no wrist band!, Person skipped the queue!")
        .jsonPath("$.error")
        .isEqualTo("Input Validation Error");
  }
}
