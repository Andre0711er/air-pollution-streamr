package com.marthalerit.luftdaten;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.marthalerit.helper.StringHelper;
import com.marthalerit.redis.Redis;
import com.marthalerit.skeleton.Config;
import com.marthalerit.skeleton.Skeleton;
import com.streamr.client.StreamrClient;
import com.streamr.client.authentication.EthereumAuthenticationMethod;
import com.streamr.client.rest.Stream;
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
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class Reader implements Job {
  private final static Logger logger = Skeleton.getLogger(Reader.class);

  public void execute(JobExecutionContext jExeCtx) {
    try {
      logger.debug("Start");
      run();
      logger.debug("Done");
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }
  }

  public void run() throws URISyntaxException, IOException, InterruptedException {
    try (Jedis redis = Redis.connect()) {
      final String lastModifiedKey = "last-modified";

      final HttpClient client = HttpClient.newHttpClient();
      final HttpRequest.Builder request = HttpRequest.newBuilder()
          .uri(new URI(Objects.requireNonNull(Config.get("sensors.endpoint"))))
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
          final AtomicReference<List<Map<String, Object>>> changes = new AtomicReference<>(new ArrayList<>());
          // get data from request
          JsonParser.parseString(response.body()).getAsJsonArray().forEach(data -> {
            try {
              // generate hash from element
              final String value = data.toString();
              // compare with stored data
              final String key = StringHelper.getHash(value);
              if (redis.get(key) == null) {
                // send into streamr queue
                changes.get().add(new Gson().fromJson(data, Map.class));
                if (changes.get().size() >= 500) {
                  streamr(lastModifiedDetected, changes.get());
                  changes.set(new ArrayList<>());
                }
                // add to redis set
                final SetParams params = new SetParams().ex(600L);
                redis.set(key, value, params);
                logger.debug("Store key/value in store; key: {}", key);
              }
            } catch (NoSuchAlgorithmException | IOException e) {
              logger.error(e.getMessage());
            }
          });

          if (changes.get().size() > 0) {
            streamr(lastModifiedDetected, changes.get());
            changes.set(new ArrayList<>());
          }
          redis.set(lastModifiedKey, lastModifiedDetected);
        }
      }
    }
  }

  private StreamrClient streamrClient = null;
  private Stream stream = null;

  private void initStreamr() throws IOException {
    if (streamrClient == null) {
      final String privateKey = Objects.requireNonNull(Config.get("streamr.key"));
      streamrClient = new StreamrClient(new EthereumAuthenticationMethod(privateKey));
      stream = streamrClient.getStream(Config.get("streamr.stream"));
    }
  }

  public void streamr(final String timestamp, final List<Map<String, Object>> messages) throws IOException {
    initStreamr();
    final Map<String, Object> map = new HashMap();
    map.put("date", timestamp);
    map.put("data", messages);
    streamrClient.publish(stream, map);
  }

  public static void main(String... args) throws URISyntaxException, IOException, InterruptedException {
    Reader reader = new Reader();
    reader.run();
  }
}
