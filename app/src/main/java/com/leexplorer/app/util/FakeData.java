package com.leexplorer.app.util;

import com.leexplorer.app.models.Artwork;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by hectormonserrate on 13/02/14.
 */
public class FakeData {

    private static final String[] ARTWORKS= {
        "{\"name\":\"crying woman\",\"mac\":\"1\",\"description\":\"lala\",\"image_url\":\"http://blogs.sundaymercury.net/lorne-jackson/Picasso%20-%20Weeping%20Woman.jpg\",\"author\":\"picasso\",\"likes_count\":3,\"published_at\":\"1822-10-10\"}",
        "{\"name\":\"guernica\",\"mac\":\"1\",\"description\":\"lala\",\"image_url\":\"http://www.caribousmom.com/wordpress/wp-content/uploads/guernicamural-300x133.jpg\",\"author\":\"picasso\",\"likes_count\":333,\"published_at\":\"1822-10-10\"}"
    };


    public static ArrayList<Artwork> getArtworks(){
        ArrayList<Artwork> artworks = new ArrayList<>();
        for(String aw: ARTWORKS){
           try{
            artworks.add(Artwork.fromJson(new JSONObject(aw)));
           } catch(Exception e){
            e.printStackTrace();
           }
        }

        return artworks;
    }
}
