package majster2nn.dev.ecs.systems;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import majster2nn.dev.Constants;
import majster2nn.dev.GameScreen;
import majster2nn.dev.ecs.Entity;
import majster2nn.dev.ecs.components.*;

import java.util.Iterator;
import java.util.Map;

import static majster2nn.dev.Constants.DIRT_LAYERS;

public class MovementSystem extends AbstractSystem {
    Vector2 placeholder = new Vector2();

    @Override
    public void updateEntityAssignment(Entity entity) {
        if(!(entity.has(PositionComponent.class) && entity.has(VelocityComponent.class))){
            managedEntities.remove(entity.getComponent(IdComponent.class).getValue());
        }else{
            managedEntities.putIfAbsent(entity.getComponent(IdComponent.class).getValue(), entity);
        }
    }

    @Override
    public void update(float delta){
        for (Entity entity : managedEntities.values()) {
            Vector3 position = entity.getComponent(PositionComponent.class).getValue();
            Vector2 velocity = entity.getComponent(VelocityComponent.class).getValue();
            if (entity.has(GravityComponent.class)) {
                velocity.y -= Constants.GRAVITY * delta;
            }

            position.x += velocity.x * delta;
            position.y += velocity.y * delta;

            if (position.y <= DIRT_LAYERS) {
                velocity.y = 0;
                position.y = DIRT_LAYERS;
                if (entity.has(GroundedComponent.class)) {
                    entity.getComponent(GroundedComponent.class).setValue(true);
                }
            }

            if(entity.has(CollisionComponent.class)) {
                placeholder.x = position.x;
                placeholder.y = position.y;
                entity.getComponent(CollisionComponent.class).getValue().setPosition(placeholder);
            }

            if (position.x < -3) {
                entity.setToBeRemoved(true);
            }
        }
    }
}
