/**
 *
 * @author isaiah.cruz
 */

package game_framework;

import java.util.HashMap;

public class Player {
	public HashMap<String, Object> identifiers;
	public boolean playedTurn;
        
	public Player() {
		identifiers = new HashMap<>();
        playedTurn = false;
	}
	
	public void setIdentifier(String key, Object value) {
		identifiers.put(key, value);
	}
	
	public Object getIdentifier(String key) {
		return identifiers.get(key);
	}
        
        public void hasPlayedTurn(boolean hasPlayed) {
            playedTurn = hasPlayed;
        }
        
        public boolean isPlayingTurn() {
            return playedTurn;
        }
        
        public boolean isAI() {
            return false;
        }
        
        
        
        
}
