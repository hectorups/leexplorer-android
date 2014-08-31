package com.leexplorer.app.events;

public class ConfirmDialogResultEvent {
  private String caller;

  public ConfirmDialogResultEvent(String caller) {
    this.caller = caller;
  }

  public String getCaller() {
    return caller;
  }
}
