package majster2nn.dev.ecs.components;

import lombok.Getter;

public class NameComponent extends AbstractComponent<String> {

    public NameComponent(String value) {
        super(value);
    }
}
