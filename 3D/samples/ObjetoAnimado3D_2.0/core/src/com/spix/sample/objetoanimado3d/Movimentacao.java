package com.spix.sample.objetoanimado3d;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController.AnimationDesc;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.UBJsonReader;
import java.util.Random;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.Color;

public class Movimentacao extends ApplicationAdapter implements AnimationController.AnimationListener{

    // Camera
    PerspectiveCamera camera;
	private float camHeight = 20f;
	private float camPitch = -20f;
    
    // Controlador de Camera
	private FirstPersonCameraController cameraController;

    // Ambiente
    Environment environment;

    // Player Movement
	float speed = 500f;
	float rotationSpeed = 80f;
	private Matrix4 playerTransform = new Matrix4();
	private final Vector3 moveTranslation = new Vector3();
	private final Vector3 currentPosition = new Vector3();

    // Time / Tempo
    float deltaTime;    // atual
    float time;         // total

    // Modelo
    private ModelBatch modelBatch;
    private Model model;
    private ModelInstance modelInstance; // Principal
    public ModelInstance space;
    float scaleAmount = 0.05f; // Escala

    // AssetManager
    AssetManager assets;
    boolean loading = false;

    // Controlador de Animação
    AnimationController controllerAnimation;

    // Variaveis de Animação
    String Jogging = "Vodoo|Jogging";
    String CrouchedWalk = "Vodoo|Crouched_Walking";
    String Macarena = "Vodoo|Macarena";
    String Jump = "Vodoo|Jump";
    String PunchRight = "Vodoo|Punching_Right";
    String PunchLeft = "Vodoo|Punching_Left";
    String idleBreathing = "Vodoo|Idle_Breathing";
    String idleSad = "Vodoo|Idle_Sad";
    String idleStretch= "Vodoo|Idle_Stretch";
    String idleDwarf = "Vodoo|Idle_Dwarf";


    // Variáveis de Controle de Animação
    boolean animationInProgress = false;
    boolean isJumping = false;
    boolean isDancing = false;
    boolean isWalking = false;
    boolean isSpecialIdle = false;
    int idleAnimationsCompleted = 0; // animações realizadas
    int idleTrigger = 1;             // trigger de quantas animações precisam para aleatorizar uma especial
    int idleSpecialAnimations = 3;   // Número de animações Idle Especiais
    int idleDrawn = 3;               // número da animação sorteada

    // Log
    int cont = 0;
    float printVodooPosition = 3f; 

    // Random
    private Random random;

    // Font
    public SpriteBatch batch;
    public BitmapFont font;
    private GlyphLayout glyphLayout;

    @Override
	public void create() {
        // Font
        batch = new SpriteBatch();
        font = new BitmapFont(); // use libGDX's default Arial font
        glyphLayout = new GlyphLayout();

        // Inicialização do objeto Random
        random = new Random();

        // Camera
		camera = new PerspectiveCamera(60f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.near = 1f;
		camera.far = 200;
		camera.position.set(0,camHeight, 4f);

        // Cotrolador de Camera
        cameraController = new FirstPersonCameraController(camera);
		Gdx.input.setInputProcessor(cameraController);

        // Ambiente
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight,0.8f,0.8f,0.8f,1.0f));

        // Modelo
        modelBatch = new ModelBatch();
        UBJsonReader jsonReader = new UBJsonReader();
        G3dModelLoader modelLoader = new G3dModelLoader(jsonReader);
        model = modelLoader.loadModel(Gdx.files.getFileHandle("Vodoo/AllAnimations/Vodoo.g3db", Files.FileType.Internal));
        modelInstance = new ModelInstance(model);
        modelInstance.transform.scl(scaleAmount);

        // Controlador de Animação
        controllerAnimation = new AnimationController(modelInstance);

        // AssetsManager
        assets = new AssetManager();
        assets.load("Space/spacesphere.obj", Model.class);
		loading = true;
    }
    
    @Override
	public void render() {
        // Carregamento de Objetos e Assets
		if (loading && assets.update()){
			doneLoading();
        }

        // Tempo
        updateTimer();

        // Teclas
        processInput(deltaTime);

        // Atualização de Câmera
        updateCamera();

        // Limpeza de Tela
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        // Atualização das Animações
        controllerAnimation.update(deltaTime);
        
        // Renderização do Modelo
        renderModel();

        // Printa a Posição Atual do Vodoo
        currentPositionVodoo();
    }

    @Override
	public void dispose() {
        model.dispose();
        modelBatch.dispose();
    }


    // ================== Funções @Override =============================

    @Override
	public void resize(int width, int height) {
	}
    @Override
	public void onEnd(AnimationController.AnimationDesc animation) {
        animationInProgress = false;
        // se a animação finalizada for igual Jump
        if(animation.animation.id.equals(Jump)){ 
            isJumping = false;
        }
        // se a animação finalizada for igual Macarena
        if(animation.animation.id.equals(Macarena)){ 
            isDancing = false;
        }
        // se a animação finalizada for igual Jogging
        if(animation.animation.id.equals(Jogging)){
            // Jogging();
        }
        // se a animação finalizada for igual idleBreathing
        if(animation.animation.id.equals(idleBreathing)){
            // cont++;
            // Gdx.app.log("INFO", "idleAnimationsCompleted: "+idleAnimationsCompleted+" "+cont+""); // Log
            onIdleAnimationComplete();
        } else{
            // Se qualquer animação que não seja o idle padrão acabar a contagem volta para 0
            idleAnimationsCompleted = 0;
        }
        // se qualquer animação Idle Special for finalizada
        if((animation.animation.id.equals(idleSad)) || (animation.animation.id.equals(idleStretch)) || (animation.animation.id.equals(idleDwarf))){
            isSpecialIdle = false;
        }


	}
	@Override
	public void onLoop(AnimationController.AnimationDesc animation) {
	}

    // ================== Funções Criadas =============================

    // Pega Tempo Atual e Adiciona no Tempo Total
    public void updateTimer(){
        deltaTime = Gdx.graphics.getDeltaTime();
		time += deltaTime;
    }

    // Processamento de Teclas para Controle do Personagem
    private void processInput(float deltaTime) {
        // Controle de Movimentação Personagem
        modelControl(deltaTime);

        // Animações do Personagem
        Jump();
        danceMacarena();
        Punch();

        // Se nenhuma Tecla for Pressionada e se nenhuma animação estiver em progresso
        if(!isWalking && !animationInProgress && !isJumping && !isDancing && !isSpecialIdle && noKeyPressed()){
            // Seta a Animação Idle
            Idle();
        }
    }

    // Controle do Personagem
    private void modelControl(float deltaTime) {
		// Update the player transform
		playerTransform.set(modelInstance.transform);

        // Se não estiver Dançando e tentar se mover
        if(!isDancing && !animationInProgress  && ((Gdx.input.isKeyPressed(Input.Keys.W)) || (Gdx.input.isKeyPressed(Input.Keys.S) ) || 
        (Gdx.input.isKeyPressed(Input.Keys.A)) || (Gdx.input.isKeyPressed(Input.Keys.D)))){
            // Se não estiver Pulando
            if(!isJumping){
                isWalking = true;
                // Ativa Jogging Animation
                if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)){
                    CrouchedWalk();
                }else{
                    Jogging();
                }
            }
            // animationInProgress = false;

            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                moveTranslation.z += speed * deltaTime;
            }
    
            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                moveTranslation.z -= speed * deltaTime;
            }
    
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                playerTransform.rotate(Vector3.Y, rotationSpeed * deltaTime);
            }
    
            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                playerTransform.rotate(Vector3.Y, -rotationSpeed * deltaTime);
            }
        }else{
            isWalking = false;
        }


		// Apply the move translation to the transform
		playerTransform.translate(moveTranslation);

		// Set the modified transform
		modelInstance.transform.set(playerTransform);

		// Update vector position
		modelInstance.transform.getTranslation(currentPosition);

		// Clear the move translation out
		moveTranslation.set(0,0,0);
	}

    // Jump
    public void Jump(){
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
            controllerAnimation.animate(Jump, 1, 1f, this, 0.5f);
            // animationInProgress = true;
            isSpecialIdle = false;
            isJumping = true;
        }
    }

    // Dançar Macarena
    public void danceMacarena(){
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)){
            // controllerAnimation.animate(Macarena, 1);
            // controllerAnimation.setAnimation(Macarena, 1);
            controllerAnimation.animate(Macarena, 1, 1f, this, 0.5f);
            animationInProgress = true;
            isDancing = true;
            isSpecialIdle = false;
        }
    }

    // Idle
    public void Idle(){
        // cont++;
        // Gdx.app.log("INFO", idleBreathing+" "+cont+""); // Log
        controllerAnimation.animate(idleBreathing, 1, 1f, this, 0.2f);
    }

    // Jogging
    public void Jogging(){
        isSpecialIdle = false;
        controllerAnimation.animate(Jogging, 1, 1f, this, 0.5f);
    }

    // Crouched Walk
    public void CrouchedWalk(){
        isSpecialIdle = false;
        controllerAnimation.animate(CrouchedWalk, 1, 1f, this, 0.5f);
    }

    // Punch
    public void Punch(){
        isSpecialIdle = false;
        if (Gdx.input.isKeyPressed(Input.Keys.Q)){
            controllerAnimation.animate(PunchRight, 1, 1f, this, 0.5f);
            animationInProgress = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.E)){
            controllerAnimation.animate(PunchLeft, 1, 1f, this, 0.5f);
            animationInProgress = true;
        }
    }

    // Método chamado quando uma animação de idle é concluída
    private void onIdleAnimationComplete() {
        idleAnimationsCompleted++;

        // Verifica se as duas animações de idle foram concluídas
        if (idleAnimationsCompleted >= idleTrigger) {
            // Sorteia o index de uma animação
            raflleIntegerNumber(1, idleSpecialAnimations);
            specialIdle();
        }
    }

    // Sortear números inteiros
    public void raflleIntegerNumber(int minNumber, int maxNumber){
        // Random para gerar um número inteiro aleatório entre minNumber e maxNumber
        idleDrawn = random.nextInt(maxNumber) + minNumber;
    }

    // Specials Idle
    public void specialIdle(){
        isSpecialIdle = true;
        if(idleDrawn == 1) {
            // Sad / Triste
            blendAnimations(idleSad, 2f);

        } else if(idleDrawn == 2) {
            // Espreguiçando / Stretch
            controllerAnimation.animate(idleStretch, 1, 1f, this, 2f);
            // blendAnimations(idleStretch, 2f);


        } else if(idleDrawn == 3) {
            // Peido / Dwarf
            blendAnimations(idleDwarf, 2f);

        }
    }

    // Método para iniciar a transição entre as animações
    public void blendAnimations(String animation, float blendingSeconds) {
        // Adiciona a animação de 'animation' com blending de 'blendingSeconds' segundos
        controllerAnimation.queue(animation, 1, 1f, this, blendingSeconds);
    }

    // Atualização da Camera (Também atualiza a Posição e a Direção)
    public void updateCamera(){
        camera.position.set(currentPosition.x, camHeight, currentPosition.z - camPitch);
		camera.lookAt(currentPosition);
		camera.update();
    }

    // Renderização do Modelo
    public void renderModel(){
        modelBatch.begin(camera);
        // modelBatch.render(modelInstance);
        modelBatch.render(modelInstance, environment);
		if (space != null){
			modelBatch.render(space);
        }
        modelBatch.end();
    }

    // Quando o Carregamento dos Objetos e Imagens for Finalizado
    private void doneLoading() {
        space = new ModelInstance(assets.get("Space/spacesphere.obj", Model.class));
        loading = false;
    }

    // Método para verificar se nenhuma tecla está sendo pressionada
    private boolean noKeyPressed() {
        for (int i = 0; i < 256; i++) {
            if (Gdx.input.isKeyPressed(i)) {
                return false; // Retorna falso se qualquer tecla estiver sendo pressionada
            }
        }
        return true; // Retorna verdadeiro se nenhuma tecla estiver sendo pressionada
    }

    // Pausar a animação atual imediatamente
    public void clearAnimations(){
        // seta a Animação atual como null
        controllerAnimation.current = null;
    }

    // Escrever Informações na Tela
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

    // Pega a Posição do Vodoo
    public void currentPositionVodoo(){
        writeInfo(currentPosition+"", Gdx.graphics.getWidth()/2, 100f, 2f, 2f, Color.WHITE);
        if(time >= printVodooPosition){
            System.out.println("Posição do modelo: " + currentPosition);
            printVodooPosition += 3f;
        }
    }
}
