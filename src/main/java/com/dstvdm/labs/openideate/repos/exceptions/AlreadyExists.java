package com.dstvdm.labs.openideate.repos.exceptions;

public class AlreadyExists extends Exception {

  public AlreadyExists() {
  }

  public AlreadyExists(String message) {
    super(message);
  }

  public AlreadyExists(Throwable cause) {
    super(cause);
  }

}
