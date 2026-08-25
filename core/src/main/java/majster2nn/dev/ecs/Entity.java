package majster2nn.dev.ecs;

import lombok.Getter;
import lombok.Setter;
import majster2nn.dev.ecs.components.AbstractComponent;
import majster2nn.dev.ecs.components.ManagerComponent;
import majster2nn.dev.ecs.systems.EntityManager;

import java.util.HashMap;

public class Entity {
    private final HashMap<Class<?>, AbstractComponent<?>> components = new HashMap<>();

    @Getter @Setter
    private boolean toBeRemoved = false;

    public Entity(EntityManager manager, String name) {
        components.put(ManagerComponent.class, new ManagerComponent(manager));
        manager.assignNewEntity(this, name);
    }

    public Entity addComponent(AbstractComponent<?> component) {
        components.put(component.getClass(), component);
        ManagerComponent managerComponent = getComponent(ManagerComponent.class);
        if (managerComponent != null) {
            managerComponent.getValue().reasignEntityToManagers(this);
        }
        return this;
    }

    public Entity removeComponent(Class<? extends AbstractComponent<?>> componentType) {
        components.remove(componentType);
        ManagerComponent managerComponent = getComponent(ManagerComponent.class);

        if(managerComponent != null) {
            managerComponent.getValue().reasignEntityToManagers(this);
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T, C extends AbstractComponent<T>> C getComponent(Class<C> componentType) {
        return (C) components.get(componentType);
    }

    public boolean has(Class<? extends AbstractComponent<?>> componentType) {
        return components.containsKey(componentType);
    }
}
