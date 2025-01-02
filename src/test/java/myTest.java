import java.util.Arrays;

public class myTest {

    public static void main(String[] args) {
        String[] oldstreamer = new String[]{"a", "b", "c", "d", "e"};
        String[] newstreamer = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i"};
        String[] x = filternewstreamer(oldstreamer, newstreamer);
        for (String s : x) {
            System.out.println(s);
        }
        oldstreamer = newstreamer;
        newstreamer = new String[]{"a", "b", "c", "f", "g", "km"};
        x = filternewstreamer(oldstreamer, newstreamer);
        if (x.length == 0) {
            System.out.println("no new streamer");
        }
        for (String s : x) {
            System.out.println(":" + s);
        }
    }
    public static boolean compareArrays(String[] array1, String[] array2) {
        if (array1.length != array2.length) {
            return false;
        }

        Arrays.sort(array1);
        Arrays.sort(array2);

        return Arrays.equals(array1, array2);
    }

    public static String[] filternewstreamer(String[] oldstreamer, String[] newstreamer){
        //only copy the new streamer into a new array
        int y = oldstreamer.length + newstreamer.length;
        String[] x = new String[y];
        for (int i = 0; i < newstreamer.length; i++) {
            if (!Arrays.asList(oldstreamer).contains(newstreamer[i])) {
                x[i] = newstreamer[i];
            }
        }
        y = 0;
        int[] z = new int[x.length];
        for (int i = 0; i < x.length; i++) {
            if (x[i] != null) {
                z[y] = i;
                y++;
            }
        }
        String[] q = new String[y];
        for (int i = 0; i < y; i++) {
            q[i] = x[z[i]];
        }
        return q;
    }

}
