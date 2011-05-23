package util;

public class TypeUtil {
	
	public static boolean isNumeric(Object o) {
		return (o instanceof Integer || o instanceof Double || o instanceof Float);
	}

}
