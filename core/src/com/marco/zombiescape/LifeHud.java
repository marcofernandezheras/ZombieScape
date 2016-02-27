package com.marco.zombiescape;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by marco on 26/02/16.
 */
public class LifeHud implements Disposable {

    private final Sprite base;
    private final Sprite bar;
    private Hittable hittable;
    private final float maxWidth;

    private int hittableLife;

    public LifeHud(Hittable hittable) {
        this.hittable = hittable;
        Resources resources = Resources.instance;
        this.base = new Sprite(resources.getRegion("hudLife"));//new Texture("hud/hudLife.png")
        this.bar = new Sprite(resources.getRegion("hudLifeBar"));//new Texture("hud/hudLifeBar.png")
        base.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        bar.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        bar.setPosition(32,7);
        maxWidth = bar.getWidth();
        hittableLife = hittable.getLife();
    }

    public Hittable getHittable() {
        return hittable;
    }

    public void setHittable(Hittable hittable) {
        this.hittable = hittable;
    }

    public void draw(SpriteBatch batch){
        hittableLife = hittable.getLife();
        if(hittableLife > 0) {
            float lifePercent = hittableLife * 100.0f / hittable.getMaxLife() ;

            bar.setBounds(32,7, maxWidth * lifePercent/100.0f  ,bar.getHeight());
            bar.draw(batch);
        }
        base.draw(batch);
    }

    @Override
    public void dispose() {
        bar.getTexture().dispose();
        base.getTexture().dispose();
    }
}
