package com.leexplorer.app.core;

public class NonFatalException extends Exception {
  private String message;

  public NonFatalException(String message) {
    this.message = message;
  }

  @Override public String toString() {
    return this.message;
  }
}
