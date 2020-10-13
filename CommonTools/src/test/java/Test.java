import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wpq on 2020/6/19.
 */
public class Test {
    public static void main(String[] args) {
        HashMap<String,String> map = new HashMap<String,String>(2);
        map.put("k1","aa");
        System.out.println(map.get("k1"));
        map.put(null,"abc");
        System.out.println(map.get(null));
        System.out.println("============================");
        ConcurrentHashMap<String, String> ccmap = new ConcurrentHashMap<>();
        ccmap.put("k1","aa");
        System.out.println(ccmap.get("k1"));
    }
}
