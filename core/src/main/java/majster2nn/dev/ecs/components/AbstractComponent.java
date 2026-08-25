package majster2nn.dev.ecs.components;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public abstract class AbstractComponent<T>{
    T value;

    public AbstractComponent(T value) {
        this.value = value;
    }
}
