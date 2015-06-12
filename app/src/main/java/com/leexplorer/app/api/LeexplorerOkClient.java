package com.leexplorer.app.api;

import android.util.Log;
import com.leexplorer.app.core.AppConstants;
import com.leexplorer.app.util.EncodingUtils;
import com.squareup.okhttp.OkHttpClient;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import retrofit.client.Header;
import retrofit.client.OkClient;
import retrofit.client.Request;
import retrofit.client.Response;

public class LeexplorerOkClient extends OkClient {
  private static final String TAG = "LeexplorerOkClient";
  private final String key;

  public LeexplorerOkClient(OkHttpClient client, String key) {
    super(client);
    this.key = key;
  }

  @Override public Response execute(Request request) throws IOException {
    Request newRequest = sign(request);
    return super.execute(newRequest);
  }

  private Request sign(Request request) throws MalformedURLException, UnsupportedEncodingException {

    byte[] urlToSignBytes = getUrlToSign(request.getUrl());

    byte[] signedBytes = EncodingUtils.hmacSha1(urlToSignBytes, key);
    String signedString = EncodingUtils.base16Encode(signedBytes);

    ArrayList<Header> headers = new ArrayList<>(request.getHeaders());
    headers.add(new Header(AppConstants.CLIENT_HMAC_KEY, signedString));
    Log.d(TAG, signedString);
    return new Request(request.getMethod(), request.getUrl(), headers, request.getBody());
  }

  private byte[] getUrlToSign(String url)
      throws MalformedURLException, UnsupportedEncodingException {
    String path = new URL(url).getPath();
    int pathPosition = url.indexOf(path);

    return url.substring(pathPosition).getBytes("UTF-8");
  }
}
