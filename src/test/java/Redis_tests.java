
import main.Redis;

public class Redis_tests {

    public static void main(String[] args) throws InterruptedException {
        Redis redis = new Redis("5dche ss");

        redis.add("teststreamer", 5);
        Thread.sleep(1000);
        System.out.println("teststreamer: " + redis.get("teststreamer"));

        redis.add("teststreamer4", 5);
        redis.add("teststreamer2", 5);
        redis.add("teststreamer3", 5);
        System.out.println("5dchess: " + redis.getremaining().toString());
        Thread.sleep(4000);
        System.out.println("5dchess: " + redis.getremaining().toString());
        redis.add("teststreamer4", 3);
        Thread.sleep(1000);
        System.out.println("5dchess: " + redis.getremaining().toString());
        Thread.sleep(2000);
        System.out.println("5dchess: " + redis.getremaining().toString());

    }
}
