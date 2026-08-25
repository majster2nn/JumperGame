package majster2nn.dev.ecs.systems;

import majster2nn.dev.GameScreen;
import majster2nn.dev.ecs.Entity;
import majster2nn.dev.ecs.components.*;

public class CollectibleSystem extends AbstractSystem {
    @Override
    public void updateEntityAssignment(Entity entity) {
        if(!(entity.has(CollisionComponent.class) && entity.has(CollectibleComponent.class))){
            managedEntities.remove(entity.getComponent(IdComponent.class).getValue());
        }else{
            managedEntities.putIfAbsent(entity.getComponent(IdComponent.class).getValue(), entity);
        }
    }

    @Override
    public void update(float delta) {
        managedEntities.values().forEach(entity -> {
            if(entity.getComponent(CollisionComponent.class).getValue().overlaps(GameScreen.player.getComponent(CollisionComponent.class).getValue())) {
                entity.setToBeRemoved(true);

                GameScreen.pickedCoins++;
            }
        });
    }
}
