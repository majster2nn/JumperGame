package majster2nn.dev;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import majster2nn.dev.ecs.Entity;
import majster2nn.dev.ecs.components.*;
import majster2nn.dev.ecs.components.render.InvertedComponent;
import majster2nn.dev.ecs.components.render.RotationComponent;
import majster2nn.dev.ecs.components.render.ScaleComponent;
import majster2nn.dev.ecs.systems.*;

import java.util.Comparator;

import static majster2nn.dev.Constants.DIRT_LAYERS;

public class GameScreen extends ScreenAdapter {
    public AssetManager assetManager = new AssetManager();

    Sound sound = Gdx.audio.newSound(Gdx.files.internal("soundtrack.mp3"));

    TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("ui/uiskin.atlas"));
    BitmapFont font = new BitmapFont(Gdx.files.internal("ui/font.fnt"), atlas.findRegion("font"), false);
    GlyphLayout layout = new GlyphLayout();

    NinePatch patch = new NinePatch(new Texture(Gdx.files.internal("ui/frame.png")), 5, 5, 5, 5);

    Sprite runner = new Sprite(new Texture(Gdx.files.internal("game/runner.png")));
    Sprite dirt = new Sprite(new Texture(Gdx.files.internal("game/dirt.png")));
    Sprite grass = new Sprite(new Texture(Gdx.files.internal("game/grass.png")));
    Sprite sky = new Sprite(new Texture(Gdx.files.internal("game/background.png")));

    Sprite cloud = new Sprite(new Texture(Gdx.files.internal("game/cloud.png")));
    Sprite coin = new Sprite(new Texture(Gdx.files.internal("game/coin.png")));

    SpriteBatch batch;

    Camera gameCamera;
    Camera guiCamera;

    Viewport gameViewport;
    Viewport guiViewport;

    float positionX = 0;
    final float SCROLL_SPEED = 3f;
    final int REPEAT_PATTERN = 5;
    final int TILE_BUFFERED = 2;

    public static int pickedCoins = 0;
    float coinSpawnTimer = 0;
    float nextSpawnDelay = MathUtils.random(1f, 3f);

    float cloudSpawnTimer = 0;
    float nextCloudDelay = MathUtils.random(1f, 3f);

    EntityManager entityManager = new EntityManager();

    public static Entity player;

    @Override
    public void show() {
        gameCamera = new OrthographicCamera();
        guiCamera = new OrthographicCamera();

        gameViewport = new ExtendViewport(15, 10, gameCamera);
        guiViewport = new ScreenViewport(guiCamera);

        batch = new SpriteBatch();

        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        runner.setSize(1, 2);
        dirt.setSize(1, 1);
        grass.setSize(1, 1);

        cloud.setSize(1, 1);
        coin.setSize(1, 1);

        sound.play(1f);
        sound.loop();

        entityManager.registerNewManager(new LifecycleSystem());
        entityManager.registerNewManager(new RenderSystem());
        entityManager.registerNewManager(new MovementSystem());
        entityManager.registerNewManager(new ControlSystem());
        entityManager.registerNewManager(new CollectibleSystem());

        player = new Entity(entityManager, "player");
        player.addComponent(new PositionComponent(new Vector3(2, DIRT_LAYERS, 2)))
            .addComponent(new GravityComponent(true))
            .addComponent(new GroundedComponent(true))
            .addComponent(new SpriteComponent(runner))
            .addComponent(new VelocityComponent(new  Vector2(0f, 0f)))
            .addComponent(new PlayerComponent(true))
            .addComponent(new CollisionComponent(new Rectangle(0, 0, 1, 2)));

        guiViewport = new ScreenViewport(guiCamera);
        ((ScreenViewport) guiViewport).setUnitsPerPixel(1f / 3f);
    }

    @Override
    public void render(float delta) {
        entityManager.updateManagers(delta);

        gameViewport.apply();

        updateCloudSpawning(delta);
        updateCoinSpawning(delta);

        positionX = (positionX + SCROLL_SPEED * delta) % REPEAT_PATTERN;

        ScreenUtils.clear(0, 0, 0, 1);
        batch.begin();

        drawGame();
        drawUi();

        batch.end();
    }

    private void drawGame() {
        batch.setProjectionMatrix(gameCamera.combined);

        int firstTile = (int) Math.floor(positionX);
        int tilesNeeded = (int) gameViewport.getWorldWidth();

        for (int i = 0; i < tilesNeeded + TILE_BUFFERED; i++) {
            int tileX = firstTile + i;
            grass.setPosition(tileX - positionX, DIRT_LAYERS - 1);
            grass.draw(batch);
        }

        for (int i = 0; i < tilesNeeded + TILE_BUFFERED; i++) {
            for (int j = 0; j < DIRT_LAYERS - 1; j++) {
                int tileX = firstTile + i;
                dirt.setPosition(tileX - positionX, j);
                dirt.draw(batch);
            }
        }

        for (int i = 0; i < tilesNeeded + TILE_BUFFERED; i++) {
            for (int j = DIRT_LAYERS; j < gameViewport.getWorldHeight(); j++) {
                int tileX = firstTile + i;
                sky.setPosition(tileX - positionX, j);
                sky.draw(batch);
            }
        }

        entityManager.getSystem(RenderSystem.class).getEntities().stream().sorted(Comparator.comparing(
            en -> en.getComponent(PositionComponent.class).getValue().z
        )).forEach(entity -> {
            Sprite sprite = entity.getComponent(SpriteComponent.class).getValue();
            sprite.setOrigin(sprite.getWidth() / 2f, sprite.getHeight() / 2f);

            sprite.setFlip(false, false);

            Vector3 pos = entity.getComponent(PositionComponent.class).getValue();
            sprite.setPosition(pos.x, pos.y);

            if (entity.has(InvertedComponent.class) && entity.getComponent(InvertedComponent.class).getValue()){
               sprite.flip(true, false);
            }

            if (entity.has(ScaleComponent.class)) {
                float scale = entity.getComponent(ScaleComponent.class).getValue();
                sprite.setScale(scale);
            }

            if (entity.has(RotationComponent.class)) {
                float rotation = entity.getComponent(RotationComponent.class).getValue();
                sprite.setRotation(rotation);
            }

            sprite.draw(batch);
        });
    }

    private void drawUi() {
        guiViewport.apply();
        batch.setProjectionMatrix(guiCamera.combined);

        float squareW = 40 ;
        float squareH = 40;
        float squareX = guiViewport.getWorldWidth() - squareW;
        float squareY = guiViewport.getWorldHeight() - squareH;

        patch.draw(batch, squareX, squareY, squareW, squareH);

        layout.setText(font, "" + pickedCoins);

        float textX = squareX + (squareW - layout.width) / 2f;
        float textY = squareY + (squareH + layout.height) / 2f;

        font.setColor(Color.BLACK);
        font.draw(batch, layout, textX, textY);
    }

    private void updateCloudSpawning(float delta) {
        cloudSpawnTimer += delta;

        if(cloudSpawnTimer >= nextCloudDelay) {
            cloudSpawnTimer = 0;
            nextCloudDelay = MathUtils.random(1f, 3f);

            float spawnX = gameViewport.getWorldWidth() + 2;

            float spawnY = MathUtils.random((int)(DIRT_LAYERS + 1), (int)(gameViewport.getWorldHeight() - 1));

            float cloudScale = MathUtils.random(.7f, 1.3f);
            float cloudRotation = MathUtils.random(0f, 45f);
            boolean inverted = MathUtils.randomBoolean();

            Entity cloudEntity = new Entity(entityManager, "cloud");
            cloudEntity.addComponent(new PositionComponent(new Vector3(spawnX, spawnY, 1)))
                .addComponent(new SpriteComponent(cloud))
                .addComponent(new VelocityComponent(new  Vector2(-1f, 0f)))
                .addComponent(new RotationComponent(cloudRotation))
                .addComponent(new ScaleComponent(cloudScale))
                .addComponent(new InvertedComponent(inverted));
        }
    }

    private void updateCoinSpawning(float delta) {
        coinSpawnTimer += delta;
        if (coinSpawnTimer >= nextSpawnDelay) {
            coinSpawnTimer = 0;
            nextSpawnDelay = MathUtils.random(1f, 3f);

            float spawnX = gameViewport.getWorldWidth() + 2;
            float spawnY = DIRT_LAYERS + 4;

            Entity coinEntity = new Entity(entityManager, "coin");
            coinEntity.addComponent(new PositionComponent(new Vector3(spawnX, spawnY, 2)))
                .addComponent(new SpriteComponent(coin))
                .addComponent(new VelocityComponent(new  Vector2(-3f, 0f)))
                .addComponent(new CollisionComponent(new Rectangle(0, 0, 1, 1)))
                .addComponent(new CollectibleComponent(1));
        }
    }

    @Override
    public void resize(int width, int height) {
        gameViewport.update(width, height, true);
        guiViewport.update(width, height, true);
    }


    @Override
    public void dispose() {
        atlas.dispose();
        batch.dispose();
        assetManager.dispose();
    }
}
