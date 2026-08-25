package majster2nn.dev.ecs.components;

import majster2nn.dev.ecs.systems.EntityManager;

public class ManagerComponent extends AbstractComponent<EntityManager> {
    public ManagerComponent(EntityManager value) {
        super(value);
    }
}
