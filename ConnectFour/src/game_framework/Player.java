/**
 *
 * @author isaiah.cruz
 */

package game_framework;

import java.util.HashMap;

public class Player {
	public HashMap<String, Object> identifiers;
	
	public void addIdentifier(String key, Object value) {
		identifiers.put(key, value);
	}
	public Object getIdentifier(String key) {
		return identifiers.get(key);
	}
}
