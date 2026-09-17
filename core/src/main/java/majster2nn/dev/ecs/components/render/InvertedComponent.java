package majster2nn.dev.ecs.components.render;

import majster2nn.dev.ecs.components.AbstractComponent;

public class InvertedComponent extends AbstractComponent<Boolean> {
    public InvertedComponent(boolean inverted) {
        super(inverted);
    }
}
