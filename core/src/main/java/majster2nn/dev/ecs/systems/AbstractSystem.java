package majster2nn.dev.ecs.systems;

import majster2nn.dev.ecs.Entity;

import java.util.HashMap;

public abstract class AbstractSystem {
    protected final HashMap<Integer, Entity> managedEntities = new HashMap<>();

    public void removeEntity(int id){
        managedEntities.remove(id);
    }

    public abstract void updateEntityAssignment(Entity entity);

    public abstract void update(float delta);
}
