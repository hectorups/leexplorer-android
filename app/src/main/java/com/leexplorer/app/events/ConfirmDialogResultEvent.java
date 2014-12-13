package com.leexplorer.app.events;

public class ConfirmDialogResultEvent {
  private String caller;
  private boolean result;

  public ConfirmDialogResultEvent(String caller, boolean result) {
    this.caller = caller;
    this.result = result;
  }

  public String getCaller() {
    return caller;
  }

  public boolean getResult() {
    return result;
  }
}
