package br.grupointegrado.ads.flappyBird;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by douglas on 30/09/15.
 */
public class Util {

    public static final float ESCALA = 1.4f + Gdx.graphics.getDensity(); // escala da camera
    public static final float PIXELS_METRO = 32f; // pixels por metro
    public static final float FPS = 60f;
    public static final float ALTURA_CHAO = 80f / PIXELS_METRO;
    public static final float ALTURA_TELA = 640f; // altura padrão da tela

    public static Body criarCorpo(World mundo, BodyDef.BodyType tipo, float x, float y) {
        BodyDef def = new BodyDef();
        def.type = tipo;
        def.fixedRotation = true;
        def.position.set(x, y);
        Body corpo = mundo.createBody(def);
        return corpo;
    }

    public static Fixture criarForma(Body corpo, Shape shape, String userData) {
        FixtureDef def = new FixtureDef();
        def.shape = shape;
        def.density = 1;
        def.friction = 0.06f;
        def.restitution = 0.3f;
        Fixture fixacao = corpo.createFixture(def);
        fixacao.setUserData(userData);
        return fixacao;
    }

}
