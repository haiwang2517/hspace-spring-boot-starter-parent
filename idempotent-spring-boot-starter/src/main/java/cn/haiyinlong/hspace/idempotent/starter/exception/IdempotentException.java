package cn.haiyinlong.hspace.idempotent.starter.exception;

public class IdempotentException extends Exception {
  public IdempotentException() {}

  public IdempotentException(String message) {
    super(message);
  }

  public IdempotentException(String message, Throwable cause) {
    super(message, cause);
  }

  public IdempotentException(Throwable cause) {
    super(cause);
  }

  public IdempotentException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
