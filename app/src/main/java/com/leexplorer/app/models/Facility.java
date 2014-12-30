package com.leexplorer.app.models;

public class Facility {

  private String facilityName;
  private int facilityBitMapId;

  public Facility() {

  }

  public Facility(String facilityName, int facilityBitMapId) {
    this.facilityName = facilityName;
    this.facilityBitMapId = facilityBitMapId;
  }

  public String getFacilityName() {
    return facilityName;
  }

  public void setFacilityName(String facilityName) {
    this.facilityName = facilityName;
  }

  public int getFacilityBitMapId() {
    return facilityBitMapId;
  }

  public void setFacilityBitMapId(int facilityBitMapId) {
    this.facilityBitMapId = facilityBitMapId;
  }
}
