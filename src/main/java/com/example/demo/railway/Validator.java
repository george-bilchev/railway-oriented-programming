package com.example.demo.railway;

import java.util.function.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class Validator {

  /** Demo */
  public static void main(String[] args) {

    Validator validator = new Validator();

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
            t -> Mono.just(Result.failure(WebfluxExceptionHandler.getSurpressedExceptions(t))))
        .subscribe(Validator::maybePrint);
  }

  public static void maybePrint(Result<Person, String> person) {
    String msg = person.either(Person::toString, failure -> failure);
    log.info("Result: {}", msg);
  }

  /** End of Demo */
  public Mono<Person> validate(Person person) {
    return Mono.whenDelayError(
            validateHasWristBand(person),
            validateMinimumHeight(person),
            validateIsNotPregnant(person),
            validateIsHealthy(person),
            validateMaximumWeght(person),
            validateWaitedInLine(person))
        .thenReturn(person);
  }

  /** Private sugar */

  // Validators

  private Mono<Person> validateHasWristBand(Person person) {
    return Mono.just(person)
        .filter(hasWristBand())
        .switchIfEmpty(Mono.error(new ValidationException("Person has no wrist band!")));
  }

  private Mono<Person> validateMinimumHeight(Person person) {
    return Mono.just(person)
        .filter(hasMinimumAllowedHeight())
        .switchIfEmpty(
            Mono.error(
                new ValidationException("Person does not have minimum allowed height of 120 cm!")));
  }

  private Mono<Person> validateIsNotPregnant(Person person) {
    return Mono.just(person)
        .filter(isNotPregnant())
        .switchIfEmpty(Mono.error(new ValidationException("Person is pregnant!")));
  }

  private Mono<Person> validateIsHealthy(Person person) {
    return Mono.just(person)
        .filter(isHealthy())
        .switchIfEmpty(Mono.error(new ValidationException("Person has a medical issue!")));
  }

  private Mono<Person> validateMaximumWeght(Person person) {
    return Mono.just(person)
        .filter(isBelowMaximumAllowedWeight())
        .switchIfEmpty(
            Mono.error(
                new ValidationException(
                    "Person is above maximum weight, restraints will not fit!")));
  }

  private Mono<Person> validateWaitedInLine(Person person) {
    return Mono.just(person)
        .filter(waitedInLine())
        .switchIfEmpty(Mono.error(new ValidationException("Person skipped the queue!")));
  }

  // Predicates

  private Predicate<Person> heightBelowAllowed() {
    return p -> p.getHeightInCm() < 120;
  }

  private Predicate<Person> hasMinimumAllowedHeight() {
    return heightBelowAllowed().negate();
  }

  private Predicate<Person> hasWristBand() {
    return Person::hasWristBand;
  }

  private Predicate<Person> isPregnant() {
    return Person::isPrgenant;
  }

  private Predicate<Person> isNotPregnant() {
    return isPregnant().negate();
  }

  private Predicate<Person> hasHeartCondition() {
    return Person::hasHeartCondition;
  }

  private Predicate<Person> isHealthy() {
    return hasHeartCondition().negate();
  }

  private Predicate<Person> isBelowMaximumAllowedWeight() {
    return p -> p.getWeightInKg() < 200;
  }

  private Predicate<Person> jumpedQueue() {
    return Person::jumpedQueue;
  }

  private Predicate<Person> waitedInLine() {
    return jumpedQueue().negate();
  }
}
