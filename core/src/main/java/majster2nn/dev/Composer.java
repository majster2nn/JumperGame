package majster2nn.dev;

import com.badlogic.gdx.math.Vector3;
import majster2nn.dev.ecs.Entity;
import majster2nn.dev.ecs.components.PositionComponent;

import java.util.*;
import java.util.stream.Collectors;

public class Composer {
    private HashMap<Integer, List<Entity>> layers = new HashMap<>();

    public void addToLayer(int layer, Entity entity){
        layers.computeIfAbsent(layer, k -> new ArrayList<>()).add(entity);
    }

    public List<Entity> getOrderedBatch(){
        return layers.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .flatMap(entry -> entry.getValue().stream()
                .sorted(Comparator.comparingInt(entity -> {
                    Vector3 position = entity.getComponent(PositionComponent.class).getValue();
                    return position != null ? (int) position.z : 0;
                }))
            ).collect(Collectors.toList());
    }
}
