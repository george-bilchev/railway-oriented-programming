package com.example.demo;

import com.example.demo.railway.Person;
import com.example.demo.railway.Result;
import com.example.demo.railway.Validator;
import com.example.demo.railway.WebfluxExceptionHandler;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ValidatorTests {

  private Validator validator = new Validator();

  @BeforeAll
  static void setUpBeforeClass() throws Exception {}

  @AfterAll
  static void tearDownAfterClass() throws Exception {}

  @BeforeEach
  void setUp() throws Exception {}

  @AfterEach
  void tearDown() throws Exception {}

  @Test
  @Order(1)
  void successfulValidationTest() {
    Mono<Result<Person, String>> mono =
        Mono.just(
                Person.builder()
                    .hasWristBand(true)
                    .heightInCm(180)
                    .weightInKg(80)
                    .isPrgenant(false)
                    .hasHeartCondition(false)
                    .jumpedQueue(false)
                    .build())
            .flatMap(validator::validate)
            .<Result<Person, String>>map(Result::success)
            .onErrorResume(
                t -> Mono.just(Result.failure(WebfluxExceptionHandler.getSurpressedExceptions(t))));

    StepVerifier.create(mono)
        .expectNextMatches(result -> result.getValue().isPresent())
        .expectComplete()
        .verify();
  }

  @Test
  @Order(1)
  void multipleValidationErrorTest() {

    Mono<Result<Person, String>> mono =
        Mono.just(
                Person.builder()
                    .hasWristBand(false)
                    .heightInCm(180)
                    .weightInKg(80)
                    .isPrgenant(false)
                    .hasHeartCondition(false)
                    .jumpedQueue(true)
                    .build())
            .flatMap(validator::validate)
            .<Result<Person, String>>map(Result::success)
            .onErrorResume(
                t -> Mono.just(Result.failure(WebfluxExceptionHandler.getSurpressedExceptions(t))));

    StepVerifier.create(mono)
        .expectNextMatches(
            result ->
                result.getFailure().isPresent()
                    && result
                        .getFailure()
                        .get()
                        .equals("Person has no wrist band!, Person skipped the queue!"))
        .expectComplete()
        .verify();
  }
}
