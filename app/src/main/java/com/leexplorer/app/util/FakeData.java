package com.leexplorer.app.util;

import com.leexplorer.app.models.Artwork;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by hectormonserrate on 13/02/14.
 */
public class FakeData {

    private static final String[] ARTWORKS_JSON= {
        "{\"name\":\"crying woman\",\"mac\":\"1\",\"description\":\"lala\",\"image_url\":\"http://blogs.sundaymercury.net/lorne-jackson/Picasso%20-%20Weeping%20Woman.jpg\",\"author\":\"picasso\",\"likes_count\":3,\"published_at\":\"1822-10-10\"}",
        "{\"name\":\"guernica\",\"mac\":\"2\",\"description\":\"lala\",\"image_url\":\"http://viz.cwrl.utexas.edu/files/picasso_guernica1937.jpg\",\"author\":\"picasso\",\"likes_count\":333,\"published_at\":\"1822-10-10\"}",
        "{\"name\":\"paint4\",\"mac\":\"3\",\"description\":\"lala\",\"image_url\":\"http://www.glenwoodnyc.com/manhattan-living/wp-content/uploads/2011/06/gagosian-picasso-marie-therese-accoundee-painting2.jpeg\",\"author\":\"picasso\",\"likes_count\":6,\"published_at\":\"1822-10-10\"}",
        "{\"name\":\"paint3\",\"mac\":\"4\",\"description\":\"lala\",\"image_url\":\"http://www.glenwoodnyc.com/manhattan-living/wp-content/uploads/2011/06/gagosian-picasso-marie-therese-avec-une-guirlande.jpeg\",\"author\":\"picasso\",\"likes_count\":50,\"published_at\":\"1822-10-10\"}",
        "{\"name\":\"paint4\",\"mac\":\"5\",\"description\":\"lala\",\"image_url\":\"http://www.glenwoodnyc.com/manhattan-living/wp-content/uploads/2011/06/gagosian-picasso-femme-nue-couchee-painting.jpeg\",\"author\":\"picasso\",\"likes_count\":30,\"published_at\":\"1822-10-10\"}"
    };

    private static final List<Artwork.Distance> DISTANCE_VALUES =
            Collections.unmodifiableList(Arrays.asList(Artwork.Distance.values()));
    private static final Random RANDOM = new Random();

    // Gets all fake artworks
    public static ArrayList<Artwork> getArtworks(){
        ArrayList<Artwork> artworks = new ArrayList<>();
        for(String awj: ARTWORKS_JSON){
           try{
                Artwork aw = Artwork.fromJson(new JSONObject(awj));
                // Random distance
                aw.setDistance( DISTANCE_VALUES.get(RANDOM.nextInt(DISTANCE_VALUES.size())) );
                aw.unlike();
                for(int i = 0; i < 100; i++){ aw.setDescription("Avium sodium rules all. " + aw.getDescription()); }

                artworks.add(aw);
           } catch(Exception e){
            e.printStackTrace();
           }
        }

        Collections.sort(artworks);
        return artworks;
    }
}
