package com.spix.sample.objetoanimado3d;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.utils.UBJsonReader;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.Color;

public class ObjetoAnimado3D extends ApplicationAdapter {
    private OrthographicCamera camera;
    // private javafx.scene.PerspectiveCamera camera;
    private ModelBatch modelBatch;
    private Model model, model2;
    private ModelInstance modelInstance, modelInstance2;
    private Environment environment;
    private AnimationController controller, controller2;
    public SpriteBatch batch;
    public BitmapFont font;
    private GlyphLayout glyphLayout;
    private float rotationSpeed = 1f;
    String Jogging = "Armature|Jogging";
    String Macarena = "Armature|Macarena";
    String currentAnimation = Jogging;

    @Override
    public void create() {
        // Font
        batch = new SpriteBatch();
        font = new BitmapFont(); // use libGDX's default Arial font
        glyphLayout = new GlyphLayout();

        // camera = new PerspectiveCamera(75,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // camera.position.set(0f,100f,100f);
        camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
        camera.lookAt(0f,100f,0f);

        camera.near = 0.1f;
        camera.far = 1000.0f;

        modelBatch = new ModelBatch();

        UBJsonReader jsonReader = new UBJsonReader();

        G3dModelLoader modelLoader = new G3dModelLoader(jsonReader);
        
        model = modelLoader.loadModel(Gdx.files.getFileHandle("Walking/model.g3db", Files.FileType.Internal));
        modelInstance = new ModelInstance(model);
        
        model2 = modelLoader.loadModel(Gdx.files.getFileHandle("Vodoo/twoAnimations/twoAnim.g3db", Files.FileType.Internal));
        modelInstance2 = new ModelInstance(model2);

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight,0.8f,0.8f,0.8f,1.0f));

        controller = new AnimationController(modelInstance);
        controller.setAnimation("mixamo.com", -1, new AnimationController.AnimationListener() {
            @Override
            public void onEnd(AnimationController.AnimationDesc animation) {
            }

            @Override
            public void onLoop(AnimationController.AnimationDesc animation) {
            }
        });

        controller2 = new AnimationController(modelInstance2);
        controller2.setAnimation(currentAnimation, 1, new AnimationController.AnimationListener() {
            @Override
            public void onEnd(AnimationController.AnimationDesc animation) {
                if(currentAnimation.equals(Jogging)){
                    currentAnimation = Macarena;
                    Gdx.app.log("INFO","Jogging");
                } else if(currentAnimation.equals(Macarena)){
                    Gdx.app.log("INFO","Macarena");
                    currentAnimation = Jogging;
                }
                controller2.setAnimation(currentAnimation, 1, this);
            }

            @Override
            public void onLoop(AnimationController.AnimationDesc animation) {
            }
        });

        modelInstance2.transform.setToTranslation(20f, 20f, -230f);
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        model.dispose();
        model2.dispose();
    }

    @Override
    public void render() {
        handleInput(); // Pegar as teclas
        Gdx.gl.glViewport(0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        Gdx.gl.glClearColor(0,0.5f,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT|GL20.GL_DEPTH_BUFFER_BIT);

        camera.update();
        controller.update(Gdx.graphics.getDeltaTime());
        controller2.update(Gdx.graphics.getDeltaTime());

        modelBatch.begin(camera);
        modelBatch.render(modelInstance);
        modelBatch.render(modelInstance2);
        modelBatch.end();
        writeInfo("Meliante", 300f, 100f, 1, 1, Color.ORANGE);
        writeInfo("Alef", 550f, 100f, 1, 1, Color.ORANGE);
        writeInfo("Alef de Brinquedo", Gdx.graphics.getWidth()/2, 450f, 3, 3, null);

    }

    private void handleInput() {
		if (Gdx.input.isKeyPressed(Input.Keys.P)) {
            controller2.setAnimation(Macarena, 1);
        }
		if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			camera.zoom += 0.02;
			//If the A Key is pressed, add 0.02 to the Camera's Zoom
		}
		if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
			camera.zoom -= 0.02;
			//If the Q Key is pressed, subtract 0.02 from the Camera's Zoom
		}
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			camera.translate(0, 0, -3);
			//If the LEFT Key is pressed, translate the camera -3 units in the X-Axis
		}
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			camera.translate(0, 0, 3);
			//If the RIGHT Key is pressed, translate the camera 3 units in the X-Axis
		}
		if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
			camera.translate(0, -3, 0);
			//If the DOWN Key is pressed, translate the camera -3 units in the Y-Axis
		}
		if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
			camera.translate(0, 3, 0);
			//If the UP Key is pressed, translate the camera 3 units in the Y-Axis
		}
		if (Gdx.input.isKeyPressed(Input.Keys.W)) {
			camera.rotate(-rotationSpeed, 1, 0, 0);
			//If the W Key is pressed, rotate the camera by -rotationSpeed around the Z-Axis
		}
		if (Gdx.input.isKeyPressed(Input.Keys.E)) {
			camera.rotate(rotationSpeed, 1, 0, 0);
			//If the E Key is pressed, rotate the camera by rotationSpeed around the Z-Axis
		}

		//camera.zoom = MathUtils.clamp(camera.zoom, 0.1f, 100/camera.viewportWidth);

		//float effectiveViewportWidth = camera.viewportWidth * camera.zoom;
		//float effectiveViewportHeight = camera.viewportHeight * camera.zoom;

		//camera.position.x = MathUtils.clamp(camera.position.x, effectiveViewportWidth / 2f, 100 - effectiveViewportWidth / 2f);
		//camera.position.y = MathUtils.clamp(camera.position.y, effectiveViewportHeight / 2f, 100 - effectiveViewportHeight / 2f);
	}

    public void writeInfo(String text, float midX, float midY, float scaleX, float scaleY, Color color){
        font.setColor(color != null ? color : Color.BLACK);
        font.getData().setScale(scaleX != 0f ? scaleX : 1f, scaleY != 0f ? scaleY : 1f);
        glyphLayout.setText(font, text);
        float textX = midX - (glyphLayout.width) / 2; // Centralizando horizontalmente
        float textY = midY + (glyphLayout.height) / 2; // Centralizando verticalmente
        batch.begin();
        font.draw(batch, glyphLayout, textX, textY);
		batch.end();
    }
}