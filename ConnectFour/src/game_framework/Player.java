package game_framework;

import java.util.HashMap;

public abstract class Player {
	public HashMap<String, Object> identifiers;
	
	public void setIdentifier(String key, Object value) {
		identifiers.put(key, value);
	}
}
