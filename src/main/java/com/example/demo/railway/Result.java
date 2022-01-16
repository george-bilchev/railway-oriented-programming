package com.example.demo.railway;

import java.util.Optional;
import java.util.function.Function;

public abstract class Result<S, F> {
  private Result() {}

  public abstract <R> R either(Function<S, R> onSuccess, Function<F, R> onFailure);

  public abstract Optional<S> getValue();

  public abstract Optional<F> getFailure();

  public static <S, F> Result<S, F> success(S value) {
    return new Success<>(value);
  }

  public static <S, F> Result<S, F> failure(F failure) {
    return new Failure<>(failure);
  }

  private static class Success<S, F> extends Result<S, F> {
    private final S value;

    public Success(S value) {
      this.value = value;
    }

    public <R> R either(Function<S, R> onSuccess, Function<F, R> onFailure) {
      return onSuccess.apply(value);
    }

    @Override
    public Optional<S> getValue() {
      return Optional.of(value);
    }

    @Override
    public Optional<F> getFailure() {
      return Optional.empty();
    }
  }

  private static class Failure<S, F> extends Result<S, F> {
    private final F err;

    public Failure(F failure) {
      this.err = failure;
    }

    public <R> R either(Function<S, R> onSuccess, Function<F, R> onFailure) {
      return onFailure.apply(err);
    }

    @Override
    public Optional<S> getValue() {
      return Optional.empty();
    }

    @Override
    public Optional<F> getFailure() {
      return Optional.of(err);
    }
  }
}
