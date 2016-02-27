package com.marco.zombiescape;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by marco on 27/02/16.
 */
public class Resources implements Disposable{

    public static Resources instance = new Resources();
    private final TextureAtlas atlas;

    private Resources(){
        atlas = new TextureAtlas("resources.atlas");
    }

    public TextureAtlas.AtlasRegion getRegion(String name){
        return atlas.findRegion(name);
    }

    public TextureAtlas.AtlasRegion getRegion(String name, int index){
        return atlas.findRegion(name,index);
    }

    @Override
    public void dispose() {
        atlas.dispose();
    }
}
