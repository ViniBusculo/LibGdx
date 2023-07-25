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

public class ObjetoAnimado3D extends ApplicationAdapter {
    private PerspectiveCamera camera;
    private ModelBatch modelBatch;
    private Model model;
    private ModelInstance modelInstance;
    private Environment environment;
    private AnimationController controller;

    @Override
    public void create() {
        camera = new PerspectiveCamera(75,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

        // Posição da Camera
        camera.position.set(0f,100f,100f);
        // Direção de foco da Câmera
        camera.lookAt(0f,100f,0f);

        // Distancia mínima e máxima de Renderização da Câmera (se passar dos limites o objeto fica transparente)
        camera.near = 0.1f;
        camera.far = 300.0f;

        modelBatch = new ModelBatch();

        UBJsonReader jsonReader = new UBJsonReader();

        G3dModelLoader modelLoader = new G3dModelLoader(jsonReader);
        model = modelLoader.loadModel(Gdx.files.getFileHandle("Walking/model.g3db", Files.FileType.Internal));
        modelInstance = new ModelInstance(model);

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight,0.8f,0.8f,0.8f,1.0f));

        // Controlador de Animação
        controller = new AnimationController(modelInstance);
        controller.setAnimation("mixamo.com", -1, new AnimationController.AnimationListener() {
            @Override
            public void onEnd(AnimationController.AnimationDesc animation) {
            }

            @Override
            public void onLoop(AnimationController.AnimationDesc animation) {
                Gdx.app.log("INFO","Animation Ended");
            }
        });
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        model.dispose();
    }

    @Override
    public void render() {
        Gdx.gl.glViewport(0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        Gdx.gl.glClearColor(1,1,1,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT|GL20.GL_DEPTH_BUFFER_BIT);

        camera.update();
        controller.update(Gdx.graphics.getDeltaTime());

        modelBatch.begin(camera);
        modelBatch.render(modelInstance);
        modelBatch.end();
    }
}