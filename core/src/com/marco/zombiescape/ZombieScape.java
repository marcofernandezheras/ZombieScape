package com.marco.zombiescape;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.physics.box2d.Box2D;

public class ZombieScape extends ApplicationAdapter{
    private Level currentLevel;
    private SpriteBatch hudBatch;
    private OrthographicCamera camera;
    private LifeHud lifeHud;
    private State currentState = State.MAIN_MENU;
    private BitmapFont bigFont;
    private GlyphLayout pauseGlyph;
    private GlyphLayout gameOverGlyph;
    private BitmapFont smallFont;
    private GlyphLayout toContinueGlyph;
    private GlyphLayout toStartGlyphAgain;
    private GlyphLayout titleGlyph;
    private GlyphLayout toStartGlyph;

    private enum State{
        MAIN_MENU, PLAYING, PAUSE, DEAD, BETWEEN_LEVELS
    }

    @Override
	public void create () {
		Box2D.init();
		currentLevel = new Level().init(1);
        hudBatch = new SpriteBatch();
        camera = new OrthographicCamera();
        lifeHud = new LifeHud(currentLevel.getPlayer());
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Bloodthirsty.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 112;
        parameter.color = Color.RED;
        bigFont = generator.generateFont(parameter);

        FreeTypeFontGenerator.FreeTypeFontParameter parameter2 = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter2.size = 40;
        parameter2.color = Color.RED;
        smallFont = generator.generateFont(parameter2);

        generator.dispose();

        pauseGlyph = new GlyphLayout();
        pauseGlyph.setText(bigFont, "Pause");
        gameOverGlyph = new GlyphLayout();
        gameOverGlyph.setText(bigFont, "GAME OVER");

        titleGlyph = new GlyphLayout();
        titleGlyph.setText(bigFont, "Zombie\n Scape");

        toContinueGlyph = new GlyphLayout();
        toContinueGlyph.setText(smallFont, "press space/escape to continue");

        toStartGlyphAgain = new GlyphLayout();
        toStartGlyphAgain.setText(smallFont, "press space to try scape again");

        toStartGlyph = new GlyphLayout();
        toStartGlyph.setText(smallFont, "press space to try scape");
    }

	@Override
	public void render () {
        switch (currentState){
            case MAIN_MENU:
                renderMainMenuState();
                break;
            case PLAYING:
                renderPlayingState();
                break;
            case PAUSE:
                renderPauseState();
                break;
            case DEAD:
                renderDeadState();
                break;
            case BETWEEN_LEVELS:
                break;
        }
	}

    private void renderMainMenuState() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.setToOrtho(false);
        hudBatch.setProjectionMatrix(camera.combined);
        hudBatch.begin();

        float w = titleGlyph.width;
        float h = titleGlyph.height;

        float w2 = toStartGlyph.width;
        bigFont.draw(hudBatch, titleGlyph, (Gdx.graphics.getWidth() - w)/2.0f, (Gdx.graphics.getHeight() + h*2)/2.0f);
        smallFont.draw(hudBatch, toStartGlyph, (Gdx.graphics.getWidth() - w2)/2.0f, (Gdx.graphics.getHeight())/5.0f);
        hudBatch.end();
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) currentState = State.PLAYING;
    }

    private void renderPauseState(){
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.setToOrtho(false);
        hudBatch.setProjectionMatrix(camera.combined);
        hudBatch.begin();

        float w = pauseGlyph.width;
        float h = pauseGlyph.height;

        float w2 = toContinueGlyph.width;
        bigFont.draw(hudBatch, pauseGlyph, (Gdx.graphics.getWidth() - w)/2.0f, (Gdx.graphics.getHeight() + h)/2.0f);
        smallFont.draw(hudBatch, toContinueGlyph, (Gdx.graphics.getWidth() - w2)/2.0f, (Gdx.graphics.getHeight() + h)/5.0f);
        hudBatch.end();
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) currentState = State.PLAYING;
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) currentState = State.PLAYING;
    }

    private void renderDeadState() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.setToOrtho(false);
        hudBatch.setProjectionMatrix(camera.combined);
        hudBatch.begin();

        float w = gameOverGlyph.width;
        float w2 = toStartGlyphAgain.width;
        bigFont.draw(hudBatch, gameOverGlyph, (Gdx.graphics.getWidth() - w)/2.0f, Gdx.graphics.getHeight());
        smallFont.draw(hudBatch, toStartGlyphAgain, (Gdx.graphics.getWidth() - w2)/2.0f, (Gdx.graphics.getHeight())/5.0f);
        hudBatch.end();
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) currentState = State.PLAYING;
    }

    private void renderPlayingState(){

        Level.CODE code = currentLevel.render();
        if(code == Level.CODE.NEXT_LEVEL) {
            Player player = currentLevel.getPlayer();
            int levelNumber = currentLevel.getLevelNumber();
            currentLevel.dispose();
            currentLevel = new Level(player).init(levelNumber + 1);
            lifeHud.setHittable(currentLevel.getPlayer());
        }
        else if(code == Level.CODE.GAME_OVER){
            currentLevel.dispose();
            currentLevel = new Level().init(1);
            lifeHud.setHittable(currentLevel.getPlayer());
            currentState = State.DEAD;
        }
        else if(code == Level.CODE.PAUSE){
            currentState = State.PAUSE;
        }
        else {
            camera.zoom = 1.5f;
            camera.update();
            camera.setToOrtho(false);
            hudBatch.setProjectionMatrix(camera.combined);
            hudBatch.begin();
            lifeHud.draw(hudBatch);
            hudBatch.end();
            camera.zoom = 1f;
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        currentLevel.dispose();
        WorldMapFactory.rayHandler.dispose();
        Resources.instance.dispose();
    }
}
