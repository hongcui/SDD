package conversion.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import states.IState;
import taxonomy.ITaxon;
import taxonomy.SubTaxonException;
import taxonomy.TaxonFactory;
import taxonomy.TaxonHierarchy;
import taxonomy.TaxonRank;
import tree.TreeNode;
import conversion.DescriptionParser;
import conversion.TaxonCharacterMatrix;
import dao.FilenameTaxonDao;
import dao.SingularPluralDao;


public class TaxonCharacterMatrixTest {

	private FilenameTaxonDao dao = new FilenameTaxonDao();
	private SingularPluralDao singularPluralDao = new SingularPluralDao();
	private HashMap sigPluMap;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testTaxonCharacterMatrix() {
		dao.getDBConnection();
		sigPluMap = singularPluralDao.getAllSingularForPlural();
	//	TaxonHierarchy th = makeHierarchyGenus("phytolacca");
		//TaxonHierarchy th = makeHierarchyGenus("achillea");
	//	TaxonHierarchy th = makeHierarchyTwoLevel("nyctaginaceae", TaxonRank.FAMILY, TaxonRank.SPECIES);

//		TaxonHierarchy th = makeHierarchyMultipleLevels("porifera", TaxonRank.PHYLUM, TaxonRank.GENUS);
		//TaxonHierarchy th = makeHierarchyMultipleLevelsLoop("porifera", TaxonRank.PHYLUM);
		TaxonHierarchy th = makeHierarchyMultipleLevelsLoop("ARENARIA", TaxonRank.GENUS);	
	//	outputAllTaxons("porifera", TaxonRank.PHYLUM);
	//	TaxonHierarchy th = makeHierarchyMultipleLevelsCascade("porifera", TaxonRank.PHYLUM);
	//	TaxonHierarchy th = makeHierarchyFromPseudoRootMultipleLevels(TaxonRank.PHYLUM, TaxonRank.SUBGENUS);
	//	th.printSimple();
		TaxonCharacterMatrix matrix = new TaxonCharacterMatrix(th);
//		Map<String, Map<ITaxon, List<IState>>> map = matrix.getTable();
//		matrix.printSimple();
		//File outputdir = new File("C:\\Users\\jingliu5\\UFLwork\\SDD\\Spongesmatrices\\");	
		File outputdir = new File("C:\\Users\\updates\\workspace-CharaParser\\matrix-generator\\TestData\\matrices\\");		
		if(!outputdir.exists()) outputdir.mkdir();
		//matrix.generateMatrixFile(new File(outputdir,"Spongesmatrices.txt"));
		matrix.generateMatrixFile(new File(outputdir,"test.txt"));
		dao.closeDBConnection();
	}
	
	/**
	 * 
	 */
	private TaxonHierarchy makeHierarchyGenus(String genusName) {
		DescriptionParser parser = new DescriptionParser(genusName, TaxonRank.GENUS,dao,sigPluMap);
		ITaxon taxon = parser.parseTaxon();
		String cirsiumGenusFilename = dao.getFilenameForDescription(TaxonRank.GENUS, genusName);
		List<String> cirsiumSpecies = dao.getFilenamesForManyDescriptions(TaxonRank.GENUS, genusName, TaxonRank.SPECIES);//TODO: always stop at SPECIES level?
		cirsiumSpecies.remove(cirsiumGenusFilename);
		TaxonHierarchy h = new TaxonHierarchy(taxon);
		DescriptionParser speciesParser;
		for(String s : cirsiumSpecies) {
			String speciesName = dao.getTaxonValues(s).get("species");
			System.out.println("Species name: " + speciesName);
			speciesParser = new DescriptionParser(speciesName, TaxonRank.SPECIES,dao,sigPluMap);
			//must have species files
			ITaxon speciesTaxon = speciesParser.parseTaxon();
			try {
				h.addSubTaxon(speciesTaxon);
			} catch (SubTaxonException e) {
				e.printStackTrace();
			}
		}
		return h;
	}
	
	private TaxonHierarchy makeHierarchyTwoLevel(String topName, TaxonRank topRank, TaxonRank bottomRank) {
		System.out.println("Making hierarchy for family: " + topName);
		DescriptionParser parser = new DescriptionParser(topName, topRank,dao,sigPluMap);
		ITaxon taxon = parser.parseTaxon();
		String rankTop = dao.getFilenameForDescription(topRank, topName);
		List<String> rankLower = dao.getFilenamesForManyDescriptions(topRank, topName, bottomRank);
		rankLower.remove(rankTop);
		TaxonHierarchy h = new TaxonHierarchy(taxon);
		DescriptionParser bottomParser;
		for(String s : rankLower) {
			String bottomName = dao.getTaxonValues(s).get(bottomRank.toString().toLowerCase());
			System.out.println("Bottom name: " + bottomName);
			bottomParser = new DescriptionParser(bottomName, bottomRank,dao,sigPluMap);
			ITaxon bottomTaxon = bottomParser.parseTaxon();
			try {
				h.addSubTaxon(bottomTaxon);
			}
			catch (SubTaxonException e) {
				System.out.println(e.getMessage());
			}
		}
		return h;
	}
	
	
	private TaxonHierarchy makeHierarchyMultipleLevels(String topName, TaxonRank topRank, TaxonRank bottomRank) {
		System.out.println("Making hierarchy for family: " + topName);
		DescriptionParser parser = new DescriptionParser(topName, topRank,dao,sigPluMap);
		ITaxon taxon = parser.parseTaxon();
		String rankTop = dao.getFilenameForDescription(topRank, topName);
		List<String> rankLower = dao.getFilenamesForManyDescriptionsByOrder(topRank, topName, bottomRank);
		rankLower.remove(rankTop);
		TaxonHierarchy h = new TaxonHierarchy(taxon);
		DescriptionParser bottomParser;
		for(String s : rankLower) {
			String rs = dao.getTaxonRank(s);
			String bottomName = dao.getTaxonValues(s).get(rs);			
			TaxonRank botRank = TaxonRank.valueOf(rs.toUpperCase());
			if (rs.equals("species")) {
				String genusName = dao.getTaxonValues(s).get("genus");	
				bottomName =  genusName+"_"+bottomName;
			}			
			System.out.println("Bottom name: " + bottomName);
			System.out.println("Bottom file: " + s);
			bottomParser = new DescriptionParser(bottomName, botRank,dao,sigPluMap);
			ITaxon bottomTaxon = bottomParser.parseTaxon(s);
			try {
				if (bottomTaxon!=null)
					h.addSubTaxon(bottomTaxon);
			}
			catch (SubTaxonException e) {
				System.out.println(e.getMessage());
			}
		}
		return h;
	}
	
	
	private TaxonHierarchy makeHierarchyMultipleLevelsLoop(String topName, TaxonRank topRank) {
		System.out.println("Making hierarchy for : "+topRank.name()+" " + topName);
		DescriptionParser parser = new DescriptionParser(topName, topRank,dao,sigPluMap);
		ITaxon taxon = parser.parseTaxon();
		String rankTop = dao.getFilenameForDescription(topRank, topName);
		List<TaxonRank> ranklist = new LinkedList<TaxonRank>();
		ranklist.add(topRank);
		List<String> ranknamelist = new LinkedList<String>();
		ranknamelist.add(topName);		
		TaxonHierarchy h = new TaxonHierarchy(taxon);
		makeHierarchyMultipleLevelsLoop1(h);
		return h;
	}
	
	private void makeHierarchyMultipleLevelsLoop1(TaxonHierarchy h) {
		String lowestRank = dao.getLowestRank();
		List<String> nonEmptyRankList = dao.getNonEmptyRankList();
		nonEmptyRankList.remove(0);
		for (String r:nonEmptyRankList){
			List<String> filenames =  dao.getFilenamesForGivenRank(r);
			for (String file:filenames){
				List<String> ranklist = null,namelist = null;
				namelist = dao.getTaxonRankNameList(file);
				ranklist = dao.getTaxonRankList(file);
				TaxonRank botRank = TaxonRank.valueOf(r.toUpperCase());
				String bottomName = dao.getTaxonValues(file).get(r);
				DescriptionParser bottomParser;
				if (r.equals("species")) {
					String genusName = dao.getTaxonValues(file).get("genus");
					bottomName = genusName + "_" + bottomName;
				}
				System.out.println("Taxon name: " + bottomName);
				System.out.println("File name: " + file);
				bottomParser = new DescriptionParser(bottomName, botRank,
						dao, sigPluMap);
				ITaxon bottomTaxon = bottomParser.parseTaxon(file);
				List<TaxonRank> rl = new LinkedList<TaxonRank>();
				for (String rs : ranklist){
					rl.add(TaxonRank.valueOf(rs.toUpperCase()));
				}
				rl.remove(rl.size()-1);
				namelist.remove(namelist.size()-1);
				try {
					h.addSubTaxon(rl, namelist,bottomTaxon);
				} catch (SubTaxonException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}
	private TaxonHierarchy makeHierarchyMultipleLevelsCascade(String topName, TaxonRank topRank) {
		System.out.println("Making hierarchy for : "+topRank.name()+" " + topName);
		DescriptionParser parser = new DescriptionParser(topName, topRank,dao,sigPluMap);
		ITaxon taxon = parser.parseTaxon();
		String rankTop = dao.getFilenameForDescription(topRank, topName);
		List<TaxonRank> ranklist = new LinkedList<TaxonRank>();
		ranklist.add(topRank);
		List<String> ranknamelist = new LinkedList<String>();
		ranknamelist.add(topName);		
		TaxonHierarchy h = new TaxonHierarchy(taxon);
		makeHierarchyMultipleLevelsCascade1(ranklist,ranknamelist,h);
		return h;
	}
	
	
	private void makeHierarchyMultipleLevelsCascade1(List<TaxonRank> ranklist,
			List<String> ranknamelist, TaxonHierarchy h) {
		List<String> rankLower = dao.getFilenamesForManyDescriptionsNextOrder(
				ranklist, ranknamelist);
		DescriptionParser bottomParser;

		try {
			if (rankLower.isEmpty()
					&& !dao.reachedLowestRank(ranklist, ranknamelist)) {
				List<String> rankNames = dao.getRankNamesForNextOrder(ranklist,
						ranknamelist);
				for (String rankname : rankNames) {
					TaxonRank rank = TaxonRank.valueOf(dao.getNextRank(
							ranklist, ranknamelist).toUpperCase());
					ITaxon taxon = TaxonFactory.getTaxonObject(rank, rankname);
					h.addSubTaxon(ranklist, ranknamelist,taxon);
					System.out.println("Taxon name: " + rankname);
					System.out.println("File name: " + "None");
					ranklist.add(rank);
					ranknamelist.add(rankname);
					bottomParser = new DescriptionParser(rankname, rank, dao,
							sigPluMap);
					makeHierarchyMultipleLevelsCascade1(ranklist, ranknamelist,
							h);
					ranklist.remove(ranklist.size()-1);
					ranknamelist.remove(ranknamelist.size()-1);
				}
			} else if (!rankLower.isEmpty()) {
				for (String s : rankLower) {
					String rs = dao.getTaxonRank(s);
					String bottomName = dao.getTaxonValues(s).get(rs);
					TaxonRank botRank = TaxonRank.valueOf(rs.toUpperCase());
					if (rs.equals("species")) {
						String genusName = dao.getTaxonValues(s).get("genus");
						bottomName = genusName + "_" + bottomName;
					}
					System.out.println("Taxon name: " + bottomName);
					System.out.println("File name: " + s);
					bottomParser = new DescriptionParser(bottomName, botRank,
							dao, sigPluMap);
					ITaxon bottomTaxon = bottomParser.parseTaxon(s);
					h.addSubTaxon(ranklist, ranknamelist,bottomTaxon);
					ranklist.add(botRank);
					ranknamelist.add(bottomName);
					makeHierarchyMultipleLevelsCascade1(ranklist, ranknamelist,
							h);
					ranklist.remove(ranklist.size()-1);
					ranknamelist.remove(ranknamelist.size()-1);
				}
				// return h;
			}
		} catch (SubTaxonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	

	
	
	private TaxonHierarchy makeHierarchyFromPseudoRootMultipleLevels(TaxonRank topRank, TaxonRank bottomRank) {
		
		String existTopRank = dao.getHigestRank();
		String existBottomRank = dao.getLowestRank();
		TaxonHierarchy h;

		if (dao.compareRanks(topRank.name().toLowerCase(), existTopRank)){
			String rootName = "PseudoROOT";
			System.out.println("Making hierarchy for "+topRank);
			DescriptionParser rootParser = new DescriptionParser(rootName, topRank,dao,sigPluMap);
			ITaxon rootTaxon = rootParser.parsePeudoTaxon();
			h = new TaxonHierarchy(rootTaxon);
			List<String> rankNames = dao.getRankNamesForGivenRank(TaxonRank.valueOf(existTopRank.toUpperCase()));
			for (String topName : rankNames) {
				System.out.println("Making hierarchy for family: " + topName);
				DescriptionParser parser = new DescriptionParser(topName,
						TaxonRank.valueOf(existTopRank.toUpperCase()),dao,sigPluMap);
				ITaxon taxon = parser.parseTaxon();
				try {
					h.addSubTaxon(taxon);
				} catch (SubTaxonException e) {
					System.out.println(e.getMessage());
				}
				String rankTop = dao
						.getFilenameForDescription(TaxonRank.valueOf(existTopRank.toUpperCase()), topName);
				List<String> rankLower = dao.getFilenamesForManyDescriptionsByOrder(     //getFilenamesForManyDescriptions
						TaxonRank.valueOf(existTopRank.toUpperCase()), topName, bottomRank);
				rankLower.remove(rankTop);
				DescriptionParser bottomParser;
				for(String s : rankLower) {
					String rs = dao.getTaxonRank(s);
					String bottomName = dao.getTaxonValues(s).get(rs);			
					TaxonRank botRank = TaxonRank.valueOf(rs.toUpperCase());
					if (rs.equals("species")) {
						String genusName = dao.getTaxonValues(s).get("genus");	
						bottomName =  genusName+"_"+bottomName;
					}			
					System.out.println("Bottom name: " + bottomName);
					System.out.println("Bottom file: " + s);
					if (bottomName.equals("dudleya_traskiae")){
						int i=0;
						i++;
						
					}
					bottomParser = new DescriptionParser(bottomName, botRank,dao,sigPluMap);
					ITaxon bottomTaxon = bottomParser.parseTaxon(s);
					try {
						if (bottomTaxon!=null)
							h.addSubTaxon(bottomTaxon);
					}
					catch (SubTaxonException e) {
						System.out.println(e.getMessage());
					}
				}
			}


		}else{
			h = new TaxonHierarchy();
		}
		return h;
	}
	
	
	private void outputAllTaxons(String topName, TaxonRank topRank) {
		List<String> ranklist = dao.getAllRankNames();
		String path = "C:\\Users\\jingliu5\\Desktop\\names.txt" ; 
		
		BufferedReader in;
		try {
			in = new BufferedReader(new FileReader(path));
		    String line = null;
		    while((line = in.readLine())!=null)
		    {
		    	//System.out.println(line);
		    	if (ranklist.indexOf(line.toLowerCase())>=0)
		    		ranklist.remove(ranklist.indexOf(line.toLowerCase()));
		    	else
		    		System.out.println("in names.txt  "+line);
		    }
		    for (String s: ranklist){
		    	System.out.println(s);
		    }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


}
