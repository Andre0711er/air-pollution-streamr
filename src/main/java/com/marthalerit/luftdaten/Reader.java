package com.marthalerit.luftdaten;

import com.google.gson.JsonParser;
import com.marthalerit.helper.StringHelper;
import com.marthalerit.redis.Redis;
import com.marthalerit.skeleton.Skeleton;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Optional;

public class Reader implements Job {
  private final static Logger logger = Skeleton.getLogger(Reader.class);

  private final String endpoint = "https://data.sensor.community/static/v2/data.json";

  public void execute(JobExecutionContext jExeCtx) {
    try {
      logger.debug("Start");
      run();
      logger.debug("Done");
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }
  }

//  /**
//   * @see <a href="https://github.com/opendata-stuttgart/meta/wiki/APIs">https://github.com/opendata-stuttgart/meta/wiki/APIs</a>
//   * https://data.sensor.community/static/v2/data.json
//   */
//  public void run() throws URISyntaxException, IOException, InterruptedException {
//    try (Jedis redis = Redis.connect()) {
//      final HttpClient client = HttpClient.newHttpClient();
//
//      final HttpRequest headRequest = HttpRequest.newBuilder().uri(new URI(endpoint))
//          .timeout(Duration.ofSeconds(5))
//          .method("HEAD", HttpRequest.BodyPublishers.noBody())
//          .build();
//
//      final HttpHeaders headers = client.send(headRequest, HttpResponse.BodyHandlers.discarding()).headers();
//      final String lastModifiedKey = "last-modified";
//      Optional<String> lastModified = headers.firstValue(lastModifiedKey);
//      if (lastModified.isPresent()) {
//        final String lastModifiedStored = redis.get(lastModifiedKey);
//        final String lastModifiedDetected = lastModified.get();
//        if (!lastModifiedStored.equals(lastModifiedDetected)) {
//          logger.debug("Execute Request; get new data");
//          executeRequest();
//          redis.set(lastModifiedKey, lastModifiedDetected);
//        }
//      }
//    }
//  }

  public void run() throws URISyntaxException, IOException, InterruptedException {
    try (Jedis redis = Redis.connect()) {
      final String lastModifiedKey = "last-modified";

      final HttpClient client = HttpClient.newHttpClient();
      final HttpRequest.Builder request = HttpRequest.newBuilder()
          .uri(new URI(endpoint))
          .timeout(Duration.ofSeconds(10))
          .GET();
      final String lastModifiedStored = redis.get(lastModifiedKey);
      if (lastModifiedStored != null) {
        request.header("If-Modified-Since", lastModifiedStored);
      }

      final HttpResponse<String> response = client.send(request.build(), HttpResponse.BodyHandlers.ofString());

      Optional<String> lastModified = response.headers().firstValue(lastModifiedKey);
      if (lastModified.isPresent()) {

        final String lastModifiedDetected = lastModified.get();
        if (lastModifiedStored == null || !lastModifiedStored.equals(lastModifiedDetected)) {
          logger.debug("Execute Request; get new data");
          // get data from request
          JsonParser.parseString(response.body()).getAsJsonArray().forEach(data -> {
            try {
              // generate hash from element
              final String value = data.toString();
              // compare with stored data
              final String key = StringHelper.getHash(value);
              if (redis.get(key) == null) {
                // send into streamr queue
                // add to redis set
                final SetParams params = new SetParams().ex(600L);
                redis.set(key, value, params);
                logger.debug("Store key/value in store; key: {}", key);
              }
            } catch (NoSuchAlgorithmException e) {
              logger.error(e.getMessage());
            }
          });
          redis.set(lastModifiedKey, lastModifiedDetected);
        }
      }
    }
  }

  public static void main(String... args) throws URISyntaxException, IOException, InterruptedException {
    Reader reader = new Reader();
    reader.run();
  }
}
