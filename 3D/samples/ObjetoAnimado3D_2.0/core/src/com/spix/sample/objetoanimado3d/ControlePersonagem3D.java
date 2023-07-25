package com.spix.sample.objetoanimado3d;

import java.util.Vector;

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
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.graphics.PerspectiveCamera;

public class ControlePersonagem3D extends ApplicationAdapter {
    private PerspectiveCamera camera;
    private ModelBatch modelBatch;
    private Model model;
    private ModelInstance Vodoo, Vodoo2;
    private Environment environment;
    private AnimationController controller;
    public SpriteBatch batch;
    public BitmapFont font;
    private GlyphLayout glyphLayout;
    private float rotationSpeed = 1f;
    String Jogging = "Armature|Jogging";
    String Macarena = "Armature|Macarena";
    String currentAnimation = Jogging;
    private Vector3 positionVodoo = new Vector3();
    Vector3 translation = new Vector3();
    float moveSpeed = 5f;


    //
    Vector3 cameraDirection;
    float angle;

    @Override
    public void create() {
        // Font
        batch = new SpriteBatch();
        font = new BitmapFont(); // use libGDX's default Arial font
        glyphLayout = new GlyphLayout();

        // No método onde você inicializa a câmera (por exemplo, create())
        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(0f, 0f, 200f);
        camera.lookAt(0, 0, 0);
        camera.near = 0.1f;
        camera.far = 1000f;
        camera.update();

        // camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // // camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
        // camera.position.set(0f, 0f, 10f);
        // camera.lookAt(0f,20f,20f);

        // camera.near = 0.1f;
        // camera.far = 10000.0f;

        modelBatch = new ModelBatch();

        UBJsonReader jsonReader = new UBJsonReader();

        G3dModelLoader modelLoader = new G3dModelLoader(jsonReader);
        
        model = modelLoader.loadModel(Gdx.files.getFileHandle("Vodoo/twoAnimations/twoAnim.g3db", Files.FileType.Internal));
        Vodoo = new ModelInstance(model);
        Vodoo2 = new ModelInstance(model);

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight,0.8f,0.8f,0.8f,1.0f));

        controller = new AnimationController(Vodoo);
        controller.setAnimation(currentAnimation, 0, new AnimationController.AnimationListener() {
            @Override
            public void onEnd(AnimationController.AnimationDesc animation) {
                if(currentAnimation.equals(Jogging)){
                    currentAnimation = Macarena;
                    Gdx.app.log("INFO","Jogging");
                } else if(currentAnimation.equals(Macarena)){
                    Gdx.app.log("INFO","Macarena");
                    currentAnimation = Jogging;
                }
                controller.setAnimation(currentAnimation, 1, this);
            }

            @Override
            public void onLoop(AnimationController.AnimationDesc animation) {
            }
        });

        positionVodoo.set(0f, 0f, -100f); // Por exemplo, a uma distância de 500 unidades do centro da câmera no eixo Z
        Vodoo.transform.setToTranslation(positionVodoo);
        Vodoo2.transform.setToTranslation(Vector3.Zero);
        cameraDirection = camera.direction; // Vetor de direção da câmera
        // Vector3 cameraPosition = camera.position;
        angle = (float) Math.atan2(cameraDirection.x, cameraDirection.z) * MathUtils.radiansToDegrees;
        Vodoo.transform.rotate(Vector3.Y, angle);
        // Vodoo.transform.setToLookAt(Vodoo.transform.getTranslation(Vector3.Zero), Vodoo.transform.getTranslation(Vector3.Zero).cpy().add(cameraDirection), Vector3.Y);
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        model.dispose();
    }

    @Override
    public void render() {
        Gdx.gl.glViewport(0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        Gdx.gl.glClearColor(0,0.5f,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT|GL20.GL_DEPTH_BUFFER_BIT);
        camera.update();
        handleInput(); // Pegar as teclas
        rotateVodoo();

        controller.update(Gdx.graphics.getDeltaTime());

        modelBatch.begin(camera);
        modelBatch.render(Vodoo);
        modelBatch.render(Vodoo2);
        modelBatch.end();
    }

    private void handleInput() {
        // Epeciais do Personagem
		if (Gdx.input.isKeyPressed(Input.Keys.M)) {
            controller.setAnimation(Macarena, 1);
        }

        // Movimentação Personagem
        if((Gdx.input.isKeyPressed(Input.Keys.W)) || (Gdx.input.isKeyPressed(Input.Keys.S) ) || 
        (Gdx.input.isKeyPressed(Input.Keys.A)) || (Gdx.input.isKeyPressed(Input.Keys.D))){
            // Pega a Posição atual
            positionVodoo = Vodoo.transform.getTranslation(Vector3.Zero);

            // Reseta a variavel translation
            resetTranslation();

            // Verifica as teclas pressionadas e adiciona o deslocamento apropriadamente
            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                translation.add(0, 0, 1);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                translation.add(0, 0, -1);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                translation.add(-1, 0, 0);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                translation.add(1, 0, 0);
            }

            // Multiplica o deslocamento pela velocidade de movimento
            translation.scl(moveSpeed);

            // Adiciona o deslocamento ao vetor positionVodoo
            positionVodoo.add(translation);

            // Atualiza a matriz de transformação com a nova posiçãoVodoo
            Vodoo.transform.setToTranslation(positionVodoo);
        }


        // Camera
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			camera.translate(-3, 0, 0);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			camera.translate(3, 0, 0);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
			camera.translate(0, 0, 3);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
			camera.translate(0, 0, -3);
		}
	}

    void resetTranslation() {
        translation.set(0, 0, 0);
    }

void rotateVodoo() {
    // Obter a direção da câmera no momento atual
    cameraDirection = camera.direction;

    // Calcular o ângulo de rotação em relação à direção da câmera
    angle = (float) Math.atan2(cameraDirection.x, cameraDirection.z) * MathUtils.radiansToDegrees;

    // Rotacionar o modelo em torno do eixo Y com o novo ângulo
    Vodoo.transform.setToRotation(Vector3.Y, angle);
}
}