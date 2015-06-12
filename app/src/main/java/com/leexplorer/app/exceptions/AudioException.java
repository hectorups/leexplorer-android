package com.leexplorer.app.exceptions;

public class AudioException extends Exception {
  private String mp3;

  public AudioException(String mp3) {
    this.mp3 = mp3;
  }

  @Override public String toString() {
    return "Could not open " + mp3;
  }
}
