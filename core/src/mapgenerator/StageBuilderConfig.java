package mapgenerator;

/**
 * Created by marco on 6/02/16.
 */
public class StageBuilderConfig {
    //Chance to put an extra connector between regions
    int extraConnectorChance = 20;

    // Increasing this allows rooms to be larger.
    int roomExtraSize = 1;

    int windingPercent = 0;

    int numRoomTries = 20;

    public StageBuilderConfig( int numRoomTries) {
        this.numRoomTries = numRoomTries;
    }
}
