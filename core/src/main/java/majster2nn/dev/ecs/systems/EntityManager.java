package majster2nn.dev.ecs.systems;

import majster2nn.dev.ecs.Entity;
import majster2nn.dev.ecs.components.IdComponent;
import majster2nn.dev.ecs.components.NameComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityManager {
    private final Map<Integer, Entity> entities = new HashMap<>();
    private final Map<Class<? extends AbstractSystem>, AbstractSystem> systems = new HashMap<>();

    private final List<Integer> availableIds = new ArrayList<>();
    private int maxId = 0;

    public void assignNewEntity(Entity entity, String name) {
        int entityId = !availableIds.isEmpty() ? availableIds.removeFirst() : ++maxId;
        entity.addComponent(new IdComponent(entityId));
        entity.addComponent(new NameComponent(name + "_" + entityId));

        entities.put(entityId, entity);
    }

    public void removeEntity(int id){
        entities.remove(id);
        availableIds.add(id);
        systems.values().forEach(manager -> manager.removeEntity(id));
    }

    public void registerNewManager(AbstractSystem manager) {
        systems.put(manager.getClass(), manager);
    }

    @SuppressWarnings("unchecked")
    public <C extends AbstractSystem> C getSystem(Class<C> managerClass) {
        return (C) systems.get(managerClass);
    }

    public void reasignEntityToManagers(Entity entity) {
        systems.values().forEach(manager -> {manager.updateEntityAssignment(entity);});
    }

    public void updateManagers(float delta) {
        systems.values().forEach(system -> system.update(delta));
    }
}
