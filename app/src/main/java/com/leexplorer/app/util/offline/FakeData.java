package com.leexplorer.app.util.offline;

import com.google.gson.Gson;
import com.leexplorer.app.models.Artwork;
import com.leexplorer.app.models.Gallery;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by hectormonserrate on 13/02/14.
 */
@SuppressWarnings("PMD")
public class FakeData {

  private static final String SCULPTURES = "Sculptures";

  private static final String[] ARTWORKS_JSON = {
      "{\"name\":\"crying woman\",\"mac\":\"1\",\"description\":\"lala\",\"image_url\":\"http://blogs.sundaymercury.net/lorne-jackson/Picasso%20-%20Weeping%20Woman.jpg\",\"author\":\"picasso\",\"likes_count\":3,\"published_at\":\"1822-10-10\"}",
      "{\"name\":\"guernica\",\"mac\":\"2\",\"description\":\"lala\",\"image_url\":\"http://viz.cwrl.utexas.edu/files/picasso_guernica1937.jpg\",\"author\":\"picasso\",\"likes_count\":333,\"published_at\":\"1822-10-10\"}",
      "{\"name\":\"paint4\",\"mac\":\"3\",\"description\":\"lala\",\"image_url\":\"http://www.glenwoodnyc.com/manhattan-living/wp-content/uploads/2011/06/gagosian-picasso-marie-therese-accoundee-painting2.jpeg\",\"author\":\"picasso\",\"likes_count\":6,\"published_at\":\"1822-10-10\"}",
      "{\"name\":\"paint3\",\"mac\":\"4\",\"description\":\"lala\",\"image_url\":\"http://www.glenwoodnyc.com/manhattan-living/wp-content/uploads/2011/06/gagosian-picasso-marie-therese-avec-une-guirlande.jpeg\",\"author\":\"picasso\",\"likes_count\":50,\"published_at\":\"1822-10-10\"}",
      "{\"name\":\"paint4\",\"mac\":\"5\",\"description\":\"lala\",\"image_url\":\"http://www.glenwoodnyc.com/manhattan-living/wp-content/uploads/2011/06/gagosian-picasso-femme-nue-couchee-painting.jpeg\",\"author\":\"picasso\",\"likes_count\":30,\"published_at\":\"1822-10-10\"}"
  };
  private static final Random RANDOM = new Random();
  private static List<Gallery> galleries = new ArrayList<>();

  static {
    galleries.add(new Gallery("1", "Picasso", "23 Octavia St\nSan Francisco CA ", "Art", 0,
        new ArrayList<String>(), "Mon-Fri: 9:30 AM - 5:00 PM\nSat, Sun: 10:00-3:00 PM",
        "Seniors: Free\nAdults: $10\nStudents: $5", new ArrayList<String>(),
        "One of the most-recognized figures in 20th century art, Pablo Picasso (1881-1973) was a Spanish painter, sculptor, printmaker, ceramicist and stage designer. His early success, through the Blue Period (1901-1904) and Rose Period (1904-1906) led to the establishment of Cubism (1909-1912) – one of his major contributions to the art world. Picasso's personal life was as controversial as his work – he was known for his love affairs, often with studio models that became his muses. In addition to his many affairs, he had two wives and four children."));
    galleries.add(
        new Gallery("2", "Aiguiere Gallery", "387 Gough St\nSan Francisco CA ", "Painting", 20,
            new ArrayList<String>(), "Mon-Fri: 9:40 AM - 5:00 PM\nSat, Sun: 10:00-3:00 PM",
            "Seniors: $20\nAdults: $25", new ArrayList<String>(),
            "One of the most-recognized figures in 20th century art, Pablo Picasso (1881-1973) was a Spanish painter, sculptor, printmaker, ceramicist and stage designer. His early success, through the Blue Period (1901-1904) and Rose Period (1904-1906) led to the establishment of Cubism (1909-1912) – one of his major contributions to the art world. Picasso's personal life was as controversial as his work – he was known for his love affairs, often with studio models that became his muses. In addition to his many affairs, he had two wives and four children.")
    );
    galleries.add(new Gallery("3", "My Gallery", "381 Gough St\nSan Francisco CA ", SCULPTURES, 10,
        new ArrayList<String>(), "Mon-Fri: 9:50 AM - 5:00 PM\nSat, Sun: 10:00-3:00 PM",
        "Adults: $25", new ArrayList<String>(),
        "One of the most-recognized figures in 20th century art, Pablo Picasso (1881-1973) was a Spanish painter, sculptor, printmaker, ceramicist and stage designer. His early success, through the Blue Period (1901-1904) and Rose Period (1904-1906) led to the establishment of Cubism (1909-1912) – one of his major contributions to the art world. Picasso's personal life was as controversial as his work – he was known for his love affairs, often with studio models that became his muses. In addition to his many affairs, he had two wives and four children."));
    galleries.add(new Gallery("4", "Gallery 10", "38 Gough St\nSan Francisco CA ", SCULPTURES, 5,
        new ArrayList<String>(), "Mon-Fri: 9:60 AM - 5:00 PM\nSat, Sun: 10:00-3:00 PM",
        "Seniors: $20\nAdults: $25", new ArrayList<String>(),
        "One of the most-recognized figures in 20th century art, Pablo Picasso (1881-1973) was a Spanish painter, sculptor, printmaker, ceramicist and stage designer. His early success, through the Blue Period (1901-1904) and Rose Period (1904-1906) led to the establishment of Cubism (1909-1912) – one of his major contributions to the art world. Picasso's personal life was as controversial as his work – he was known for his love affairs, often with studio models that became his muses. In addition to his many affairs, he had two wives and four children."));
    galleries.add(new Gallery("5", "SFO Gallery", "387 Gough St\nSan Francisco CA ", SCULPTURES, 0,
        new ArrayList<String>(), "Mon-Fri: 9:10 AM - 5:00 PM\nSat, Sun: 10:00-3:00 PM",
        "Seniors: $20\nAdults: $25", new ArrayList<String>(),
        "One of the most-recognized figures in 20th century art, Pablo Picasso (1881-1973) was a Spanish painter, sculptor, printmaker, ceramicist and stage designer. His early success, through the Blue Period (1901-1904) and Rose Period (1904-1906) led to the establishment of Cubism (1909-1912) – one of his major contributions to the art world. Picasso's personal life was as controversial as his work – he was known for his love affairs, often with studio models that became his muses. In addition to his many affairs, he had two wives and four children."));
    galleries.add(new Gallery("6", "Gallery 39", "389 Gough St\nSan Francisco CA ", SCULPTURES, 15,
        new ArrayList<String>(), "Mon-Fri: 9:24 AM - 5:00 PM\nSat, Sun: 10:00-3:00 PM",
        "Seniors: $20\nAdults: $25", new ArrayList<String>(),
        "One of the most-recognized figures in 20th century art, Pablo Picasso (1881-1973) was a Spanish painter, sculptor, printmaker, ceramicist and stage designer. His early success, through the Blue Period (1901-1904) and Rose Period (1904-1906) led to the establishment of Cubism (1909-1912) – one of his major contributions to the art world. Picasso's personal life was as controversial as his work – he was known for his love affairs, often with studio models that became his muses. In addition to his many affairs, he had two wives and four children."));
  }

  // Gets all fake artworks
  public static ArrayList<Artwork> getArtworks() {
    Gson gson = new Gson();
    ArrayList<Artwork> artworks = new ArrayList<>();
    for (String awj : ARTWORKS_JSON) {
      try {
        com.leexplorer.app.api.models.Artwork awm =
            gson.fromJson(awj, com.leexplorer.app.api.models.Artwork.class);
        Artwork aw = Artwork.fromJsonModel(awm);
        // Random distance
        aw.setDistance(new Double(RANDOM.nextInt(1)));
        aw.unlike();
        for (int i = 0; i < 100; i++) {
          aw.setDescription("Avium sodium rules all. " + aw.getDescription());
        }

        artworks.add(aw);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    Collections.sort(artworks, new Artwork.ArtworkComparable());
    return artworks;
  }

  public static List<Gallery> getGalleries() {
    return galleries;
  }
}
