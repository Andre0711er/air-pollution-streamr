package com.marthalerit.luftdaten;

import com.marthalerit.skeleton.Skeleton;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class Reader {
  private final static Logger logger = Skeleton.getLogger(Reader.class);

  private final String endpoint = "https://data.sensor.community/static/v2/data.json";

  /**
   * @see <a href="https://github.com/opendata-stuttgart/meta/wiki/APIs">https://github.com/opendata-stuttgart/meta/wiki/APIs</a>
   * https://data.sensor.community/static/v2/data.json
   */
  public void read() throws URISyntaxException, IOException, InterruptedException {
    // read data from HTTP Endpoint
    final HttpRequest request = HttpRequest.newBuilder().uri(new URI(endpoint)).timeout(Duration.ofSeconds(10)).GET().build();
    final HttpResponse<String> response = HttpClient.newBuilder().build().send(request, HttpResponse.BodyHandlers.ofString());

    // get data from request
    final String data = response.body();

    logger.debug(data);

    // compare data from existing last request

  }

  public static void main(String... args) throws URISyntaxException, IOException, InterruptedException {
    Reader reader = new Reader();
    reader.read();
  }
}
