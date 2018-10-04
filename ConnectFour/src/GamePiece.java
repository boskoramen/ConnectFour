/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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
