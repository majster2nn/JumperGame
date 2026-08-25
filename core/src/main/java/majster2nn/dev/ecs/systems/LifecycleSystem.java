package majster2nn.dev.ecs.systems;

import majster2nn.dev.ecs.Entity;
import majster2nn.dev.ecs.components.IdComponent;
import majster2nn.dev.ecs.components.ManagerComponent;

import java.util.ArrayList;
import java.util.List;

public class LifecycleSystem extends AbstractSystem {
    @Override
    public void updateEntityAssignment(Entity entity) {
        managedEntities.putIfAbsent(entity.getComponent(IdComponent.class).getValue(), entity);
    }

    @Override
    public void update(float delta) {
        List<Entity> toRemove = new ArrayList<>();

        for (Entity entity : managedEntities.values()) {
            if (entity.isToBeRemoved()) {
                toRemove.add(entity); // just collect, don't remove yet
            }
        }

        for (Entity entity : toRemove) {
            entity.getComponent(ManagerComponent.class).getValue()
                .removeEntity(entity.getComponent(IdComponent.class).getValue());
        }
    }
}
