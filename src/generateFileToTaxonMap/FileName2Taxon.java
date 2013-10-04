/**
 * 
 */
package generateFileToTaxonMap;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * @author Hong Updates
 * This class use some sort of input to generate a database table call filename2taxon
 */
public abstract class FileName2Taxon {
	protected String inputfilepath;
	protected static final Logger LOGGER = Logger.getLogger(FileName2Taxon.class);
	protected HashMap valuesMap = new HashMap();
	protected Hashtable<String, String> values = new Hashtable<String, String>();
	
	
	
	/**
	 * 
	 */
	public FileName2Taxon(String inputfilepath) {
		resetValues();		
		this.inputfilepath = inputfilepath;
	}
	
	protected void resetValues() {
		values.put("filename", "");
		values.put("hasdescription", "");
		values.put("domain", "");
		values.put("kingdom", "");
		values.put("phylum", "");
		values.put("subphylum", "");
		values.put("superdivision", "");
		values.put("division", "");
		values.put("subdivision", "");
		values.put("superclass", "");
		values.put("class", "");
		values.put("subclass", "");
		values.put("superorder", "");
		values.put("order", "");
		values.put("suborder", "");
		values.put("superfamily", "");
		values.put("family", "");
		values.put("subfamily", "");
		values.put("tribe", "");
		values.put("subtribe", "");
		values.put("genus", "");
		values.put("subgenus", "");
		values.put("section", "");
		values.put("subsection", "");
		values.put("species", "");
		values.put("subspecies", "");
		values.put("variety", "");
	}

	protected void populateFilename2TaxonTable(){
		File[] xmls = (new File(this.inputfilepath)).listFiles();
		int[] filenames = new int[xmls.length];
		//from 1.xml 10.xml, 100.xml ...
		
		int i = 0;
		int j = 0;
		HashMap<Integer, String> fnMap = new HashMap();
		for(File xml: xmls){
			String name = xml.getName().replace(".xml", "");
			if (name.indexOf("_")>0){
				j++;
				filenames[i]= Integer.parseInt(name.substring(0,name.length()-2))+j;
				fnMap.put(filenames[i], name);
				i++;
				continue;
			}			
			filenames[i]= Integer.parseInt(name)+j;
			fnMap.put(filenames[i], name);
			i++;			
		}
		//to 1, 2, 3, 4
		Arrays.sort(filenames); 
		//int size = xmls.length;
		//must be in the original order in the original volume.
		for(i = 0; i <= filenames.length-1; i++){
			System.out.println(filenames[i]+".xml");
			populateFilename2TaxonTableUsing(new File(this.inputfilepath, fnMap.get(filenames[i])+".xml"));
		}
	}
	
	//added by Jing Liu
	// used when the file names are not integer
	protected void populateFilename2TaxonTable_AlphebeticNames(){
		File[] xmls = (new File(this.inputfilepath)).listFiles();
		int[] filenames = new int[xmls.length];
		
		int i = 0;
		int j = 0;
		for(File xml: xmls){
			System.out.println(xml.getName());
			populateFilename2TaxonTableUsing(new File(this.inputfilepath, xml.getName()));
		}	
	
	}
	
	protected abstract void populateFilename2TaxonTableUsing(File xml);
	public void addToList(){
		valuesMap.put(values.get("filename"),values);
		values = new Hashtable<String, String>(); 
		
	}

	
	public HashMap getValuesMap(){
		return valuesMap;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
