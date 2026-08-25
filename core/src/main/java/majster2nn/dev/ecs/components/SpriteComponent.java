package majster2nn.dev.ecs.components;

import com.badlogic.gdx.graphics.g2d.Sprite;
import lombok.Getter;

public class SpriteComponent extends AbstractComponent<Sprite> {
    public SpriteComponent(Sprite value) {
        super(value);
    }
}
