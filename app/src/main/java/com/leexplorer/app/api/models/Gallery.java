package com.leexplorer.app.api.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Gallery {

  public String id;

  public String name;

  public String address;

  public float latitude;

  public float longitude;

  public String type;

  public List<String> languages;

  public String hours;

  @SerializedName("price_description")
  public String priceDescription;

  @SerializedName("price_reference")
  public int priceReference;

  public String description;

  public List<String> facilities;

  public List<String> artworks;
}
