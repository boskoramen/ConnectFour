/**
 *
 * @author isaiah.cruz
 */

package game_framework;

public class ConnectFourPiece {
private boolean isRed = false;

public ConnectFourPiece(boolean red) {
	if(red) 
        isRed = true;
    }

public boolean isRed() {
	return isRed;
    }
}
