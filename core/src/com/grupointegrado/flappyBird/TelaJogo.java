package com.grupointegrado.flappyBird;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FillViewport;

import static com.grupointegrado.flappyBird.Util.ALTURA_BORDA;
import static com.grupointegrado.flappyBird.Util.ESCALA;
import static com.grupointegrado.flappyBird.Util.FPS;
import static com.grupointegrado.flappyBird.Util.PIXELS_METRO;

/**
 * Created by Douglas on 24/09/2015.
 */
public class TelaJogo extends TelaBase {

    private static final String CORPO_BORDA = "CORPO_BORDA";

    private OrthographicCamera camera;
    private OrthographicCamera cameraInfo;
    private World mundo;
    private Box2DDebugRenderer debug;
    private Passaro passaro;
    private Body borda1;
    private boolean iniciou = false;
    private float larguraBorda;
    private Array<Obstaculo> obstaculos = new Array<Obstaculo>();
    private int pontuacao = 0;

    private Stage palco;
    private Label lbPontuacao;
    private BitmapFont fonte;

    private Music musicaFundo;
    private Sound somAsas;
    private Sound somGameover;
    private boolean gameover = false;

    private SpriteBatch batch;
    private Texture[] texturaPassaro = new Texture[3];
    private Texture texturaFundo;
    private Texture texturaChao;
    private Texture texturaObstaculoCima;
    private Texture texturaObstaculoBaixo;
    private Texture texturaGameover;


    public TelaJogo(MainGame game) {
        super(game);
    }

    @Override
    public void show() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth() / ESCALA, Gdx.graphics.getHeight() / ESCALA);
        cameraInfo = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        debug = new Box2DDebugRenderer();
        batch = new SpriteBatch();

        initTexturas();
        initMundo();
        initPassaro();
        initBordas();
        initAudio();
        initFontes();
        initInformacoes();
    }

    private void initTexturas() {
        texturaPassaro[0] = new Texture("sprites/bird-1.png");
        texturaPassaro[1] = new Texture("sprites/bird-2.png");
        texturaPassaro[2] = new Texture("sprites/bird-3.png");
        texturaFundo = new Texture("sprites/bg.png");
        texturaChao = new Texture("sprites/ground.png");
        texturaObstaculoCima = new Texture("sprites/toptube.png");
        texturaObstaculoBaixo = new Texture("sprites/bottomtube.png");
        texturaGameover = new Texture("sprites/gameover.png");
    }

    private void initFontes() {
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 24;
        param.color = Color.WHITE;
        param.shadowColor = Color.BLACK;
        param.shadowOffsetX = 2;
        param.shadowOffsetY = 2;

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/roboto.ttf"));

        fonte = generator.generateFont(param);

        generator.dispose();
    }

    private void initInformacoes() {
        palco = new Stage(new FillViewport(cameraInfo.viewportWidth, cameraInfo.viewportHeight, cameraInfo));

        Label.LabelStyle estilo = new Label.LabelStyle();
        estilo.font = fonte;

        lbPontuacao = new Label("Pontuação: 0", estilo);
        palco.addActor(lbPontuacao);
    }

    private void initAudio() {
        musicaFundo = Gdx.audio.newMusic(Gdx.files.internal("songs/music.mp3"));
        musicaFundo.setLooping(true);
        musicaFundo.setVolume(0.3f);

        somAsas = Gdx.audio.newSound(Gdx.files.internal("songs/wing.ogg"));

        somGameover = Gdx.audio.newSound(Gdx.files.internal("songs/game-over.mp3"));
    }

    private void initMundo() {
        mundo = new World(new Vector2(0f, -9.8f), false);
        mundo.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                detectarColisao(contact.getFixtureA(), contact.getFixtureB());
            }

            @Override
            public void endContact(Contact contact) {

            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {

            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {

            }
        });
    }

    private void detectarColisao(Fixture fixtureA, Fixture fixtureB) {
        if (Passaro.CORPO_PASSARO.equals(fixtureA.getUserData()) ||
                Passaro.CORPO_PASSARO.equals(fixtureB.getUserData())) {
            if (!gameover)
                somGameover.play(1);
            gameover = true;
            System.out.println(fixtureA.getUserData() + " ... " + fixtureB.getUserData());
        }
    }

    private void initPassaro() {
        passaro = new Passaro(mundo, camera, texturaPassaro);
    }

    private void initBordas() {
        borda1 = Util.criarCorpo(mundo, BodyDef.BodyType.StaticBody, 0, 0);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.25f, .25f, .25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        atualizar(delta);

        //renderizar(delta);

        palco.act(delta);
        palco.draw();

        debug.render(mundo, camera.combined.cpy().scl(PIXELS_METRO));
    }

    private void renderizar(float delta) {
        batch.begin();

        batch.setProjectionMatrix(cameraInfo.combined);
        batch.draw(texturaFundo, 0, 0, cameraInfo.viewportWidth, cameraInfo.viewportHeight);

        passaro.renderizar(batch);
        for (Obstaculo obs : obstaculos) {
            obs.renderizar(batch);
        }

        batch.setProjectionMatrix(cameraInfo.combined);
        batch.draw(texturaChao, 0, ALTURA_BORDA * PIXELS_METRO - texturaChao.getHeight(), cameraInfo.viewportWidth, texturaChao.getHeight());
        if (gameover)
            batch.draw(texturaGameover, cameraInfo.viewportWidth / 2 - texturaGameover.getWidth() / 2, cameraInfo.viewportHeight / 2);

        batch.end();
    }

    private void atualizar(float delta) {
        if (gameover) {
            if (musicaFundo.isPlaying())
                musicaFundo.stop();
        } else {
            if (!musicaFundo.isPlaying())
                musicaFundo.play();
        }
        capturaTeclas(delta);
        passaro.getCorpo().setFixedRotation(!gameover);
        passaro.atualizar(delta, !gameover);
        if (iniciou)
            mundo.step(1f / FPS, 6, 2);
        if (!gameover) {
            atualizarBorda();
            atualizarObstaculos();
            atualizarCamera();
        }
        atualizarInformacoes();
    }

    private void atualizarInformacoes() {
        lbPontuacao.setText("Pontuação: " + pontuacao);
        lbPontuacao.setPosition(10, palco.getHeight() - lbPontuacao.getPrefHeight() - 10);
    }

    private void atualizarObstaculos() {
        if (obstaculos.size < 4) {
            Obstaculo ultimoObstaculo = null;
            if (obstaculos.size > 0) {
                ultimoObstaculo = obstaculos.peek();
            }
            Obstaculo obstaculo = new Obstaculo(mundo, camera, ultimoObstaculo, texturaObstaculoCima, texturaObstaculoBaixo);
            obstaculos.add(obstaculo);
        }
        for (Obstaculo obstaculo : obstaculos) {
            float inicioCameraX = (camera.position.x - camera.viewportWidth / 2) / PIXELS_METRO - obstaculo.getLargura();
            if (inicioCameraX > obstaculo.getX()) {
                obstaculo.remover();
                obstaculos.removeValue(obstaculo, true);
            } else if (!obstaculo.isPassou() && passaro.getCorpo().getPosition().x > obstaculo.getX()) {
                obstaculo.setPassou(true);
                pontuacao++;
            }
        }
    }

    private void atualizarBorda() {
        Vector2 posicao = passaro.getCorpo().getPosition();
        borda1.setTransform(posicao.x + passaro.getLargura() / 2, 0, 0);
    }

    private void atualizarCamera() {
        camera.position.x = (passaro.getCorpo().getPosition().x + passaro.getLargura() / 2) * PIXELS_METRO;
        camera.update();
    }

    private void capturaTeclas(float delta) {
        if (Gdx.input.isTouched()) {
            iniciou = true;
            passaro.pular();
            if (Gdx.input.justTouched()) {
                somAsas.play(1);
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width / ESCALA, height / ESCALA);
        camera.update();
        cameraInfo.setToOrtho(false, width, height);
        cameraInfo.update();
        dimensionaBorda();
    }

    private void dimensionaBorda() {
        borda1.getFixtureList().clear();
        larguraBorda = camera.viewportWidth / PIXELS_METRO;
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(larguraBorda / 2, ALTURA_BORDA / 2);
        Util.criarForma(borda1, shape, CORPO_BORDA);
        shape.dispose();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        mundo.dispose();
        debug.dispose();
        musicaFundo.dispose();
        somAsas.dispose();
        fonte.dispose();
        palco.dispose();
        for (Texture text : texturaPassaro) {
            text.dispose();
        }
        texturaFundo.dispose();
        texturaChao.dispose();
        texturaObstaculoCima.dispose();
        texturaObstaculoBaixo.dispose();
        texturaGameover.dispose();
    }
}
