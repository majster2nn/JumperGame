package majster2nn.dev.ecs.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import majster2nn.dev.ecs.Entity;
import majster2nn.dev.ecs.components.*;

import static majster2nn.dev.Constants.DIRT_LAYERS;
import static majster2nn.dev.Constants.JUMP_STRENGTH;

public class ControlSystem extends AbstractSystem {
    @Override
    public void updateEntityAssignment(Entity entity) {
        if(!(entity.has(PlayerComponent.class) && entity.has(PositionComponent.class) && entity.has(VelocityComponent.class))){
            managedEntities.remove(entity.getComponent(IdComponent.class).getValue());
        }else{
            managedEntities.putIfAbsent(entity.getComponent(IdComponent.class).getValue(), entity);
        }
    }

    @Override
    public void update(float delta) {
        managedEntities.forEach((id, entity) -> {
            if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && entity.getComponent(GroundedComponent.class).getValue()){
                Vector2 velocity = entity.getComponent(VelocityComponent.class).getValue();
                velocity.y = JUMP_STRENGTH;

                entity.getComponent(VelocityComponent.class).setValue(velocity);
                entity.getComponent(GroundedComponent.class).setValue(false);
            }
        });
    }
}
