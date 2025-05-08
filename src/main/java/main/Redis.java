package main;

import java.util.Set;

import redis.clients.jedis.Jedis;

public class Redis {

    private final Jedis redis;
    private final String category;

    public Redis(String category) {
        this.category = "discordtwitch:" + category + ":";
        this.redis = new Jedis();
    }

    public String get(String key) {
        return redis.get(this.category + key);
    }

    public void add(String streamer, int i) {
        redis.setex(this.category + streamer, i, "exists");
    }

    public Set<String> getremaining() {
        return redis.keys(category + "*");
    }

}
