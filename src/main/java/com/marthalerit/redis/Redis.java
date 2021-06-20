package com.marthalerit.redis;

import com.marthalerit.skeleton.Config;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;

public class Redis {
  private static HostAndPort hostAndPort = null;

  public static Jedis connect() {
    if (hostAndPort == null) {
      hostAndPort = new HostAndPort(Config.get("redis.host"), Config.getAsInteger("redis.port"));
    }
    return new Jedis(hostAndPort);
  }
}
