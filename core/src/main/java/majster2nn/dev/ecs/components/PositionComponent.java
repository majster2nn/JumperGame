package majster2nn.dev.ecs.components;

import com.badlogic.gdx.math.Vector2;
import lombok.Getter;
import lombok.Setter;

public class PositionComponent extends AbstractComponent<Vector2> {

    public PositionComponent(Vector2 value) {
        super(value);
    }
}
