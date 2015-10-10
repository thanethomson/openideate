package com.dstvdm.labs.openideate.repos.exceptions;

public class DoesNotExist extends Exception {

  public DoesNotExist() {
  }

  public DoesNotExist(String message) {
    super(message);
  }

  public DoesNotExist(Throwable cause) {
    super(cause);
  }

}
