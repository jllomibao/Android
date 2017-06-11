package x40241.lomibao.jeffrey.a1;

import java.util.HashMap;

/**
 * Created by jllom on 5/25/2017.
 */

public class ImageMapper {
    private static HashMap<String, Integer> map = new HashMap<>();
    public static void CreateMap() {
        map.put("Mercury", R.drawable.mercury);
        map.put("Venus", R.drawable.venus);
        map.put("Earth", R.drawable.earth);
        map.put("Mars", R.drawable.mars);
        map.put("Jupiter", R.drawable.jupiter);
        map.put("Saturn", R.drawable.saturn);
        map.put("Uranus", R.drawable.uranus);
        map.put("Neptune", R.drawable.neptune);
        map.put("Pluto", R.drawable.pluto);
    }

    public static int getResId(String key) {
        return map.get(key);
    }
}
