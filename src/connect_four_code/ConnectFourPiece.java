/**
 *
 * @author isaiah.cruz
 */

package connect_four_code;

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
