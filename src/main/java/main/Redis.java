package main;

import redis.clients.jedis.Jedis;

public class Redis {

    public static void main(String[] args) {

        Jedis jedis = new Jedis();
        jedis.setex("hallo", 5, "welt");
        String hallo = jedis.get("hallo");
        System.out.print(hallo);
        try {
            Thread.sleep(6000);
        } catch (Exception e) {
            System.err.println(e);
        }
        hallo = jedis.get("hallo");
        System.out.print(hallo);
    }
}
