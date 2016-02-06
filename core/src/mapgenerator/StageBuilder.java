package mapgenerator;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by marco on 5/02/16.
 */
public abstract class StageBuilder {
    protected MapStage stage;

    public abstract void generate(MapStage stage);

    void bindStage(MapStage stage) {
        this.stage = stage;
    }

    Tile getTile(int x, int y)
    {
        return stage.getTiles()[x][y];
    }

    void setTile(Vector2 pos, Tile type){
        setTile((int) pos.x, (int)pos.y, type);
    }

    void setTile(int x, int y, Tile tile) {
        stage.getTiles()[x][y] = tile;
    }

    void fill(Tile tile) {
        for (int y = 0; y < stage.height(); y++) {
            for (int x = 0; x < stage.width(); x++) {
                setTile(x, y, tile);
            }
        }
    }
}
