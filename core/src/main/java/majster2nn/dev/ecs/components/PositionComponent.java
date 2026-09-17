package majster2nn.dev.ecs.components;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import lombok.Getter;
import lombok.Setter;

public class PositionComponent extends AbstractComponent<Vector3> {

    public PositionComponent(Vector3 value) {
        super(value);
    }
}
