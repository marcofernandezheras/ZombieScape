package pathfind;

import mapgenerator.MapStage;

/**
 * Created by marco on 13/02/16.
 */
public class ClosestHeuristic implements AStarHeuristic {

    public float getCost(MapStage map, int x, int y, int tx, int ty) {
        float dx = tx - x;
        float dy = ty - y;

        return (float) (Math.sqrt((dx*dx)+(dy*dy)));
    }
}
