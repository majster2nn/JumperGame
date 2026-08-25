package majster2nn.dev.ecs.components;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class CollisionComponent extends AbstractComponent<Rectangle> {
    /***
     * Constructor specyfing the bounding box of object
     * @param value
     */
    public CollisionComponent(Rectangle value) {
        super(value);
    }
}
