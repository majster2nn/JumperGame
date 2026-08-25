package majster2nn.dev.ecs.systems;

import com.badlogic.gdx.graphics.g2d.Sprite;
import majster2nn.dev.ecs.Entity;
import majster2nn.dev.ecs.components.IdComponent;
import majster2nn.dev.ecs.components.NameComponent;
import majster2nn.dev.ecs.components.PositionComponent;
import majster2nn.dev.ecs.components.SpriteComponent;

import java.util.Collection;

public class RenderSystem extends AbstractSystem {
    @Override
    public void updateEntityAssignment(Entity entity) {
        if(!(entity.has(SpriteComponent.class))){
            managedEntities.remove(entity.getComponent(IdComponent.class).getValue());
        }else{
            managedEntities.putIfAbsent(entity.getComponent(IdComponent.class).getValue(), entity);
        }
    }

    @Override
    public void update(float delta) {
        managedEntities.values().forEach(entity -> {IO.println("Rendered: " + entity.getComponent(NameComponent.class).getValue());});
    }

    public Collection<Entity> getEntities() {
        return managedEntities.values();
    }
}
