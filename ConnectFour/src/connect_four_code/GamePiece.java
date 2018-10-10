package connect_four_code;
/**
 *
 * @author isaiah.cruz
 */
public class GamePiece {
private boolean isRed = false;

public GamePiece(boolean red) {
	if(red) 
        isRed = true;
    }

public boolean isRed() {
	return isRed;
    }
}
