package x40241.lomibao.jeffrey.a1;

import java.io.Serializable;

/**
 * Created by jllom on 5/23/2017.
 */

public final class ListItem implements Serializable {
    String name;
    String color;
    int imageId;
    int itemId;

    ListItem(int itemId, int imageId, String name, String color) {
        this.itemId = itemId;
        this.imageId = imageId;
        this.name = name;
        this.color = color;
    }
}

