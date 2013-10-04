package IO;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class TxtFileIO {

	public HashMap importSingularPlural(String fileName)
			throws IOException {
		FileReader file = null;
		HashMap sigPluMap = new HashMap();
		file = new FileReader(fileName);
		BufferedReader reader = new BufferedReader(file);

		String line = "";
		line = reader.readLine();
		while (line != null && line != "") { // && i<10
			String line1 = line;
			System.out.println(line);
			int idx = 0;
			String singular = line.substring(0, line.indexOf("\t"));
			idx = line.indexOf("\t") + 1;
			line = line.substring(idx);
			String plural = line;
			sigPluMap.put(singular, plural);
			line = reader.readLine();
		}

		return sigPluMap;
	}
	

}
