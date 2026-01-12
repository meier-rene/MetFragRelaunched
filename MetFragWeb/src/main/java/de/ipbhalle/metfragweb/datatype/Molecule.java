package de.ipbhalle.metfragweb.datatype;

import java.io.Serializable;
import java.util.List;

import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.bar.BarChartModel;
import org.primefaces.model.charts.bar.BarChartOptions;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearAxes;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearTicks;
import org.primefaces.model.charts.optionconfig.legend.Legend;

import de.ipbhalle.metfraglib.additionals.MathTools;
import de.ipbhalle.metfraglib.candidate.PrecursorCandidate;
import de.ipbhalle.metfraglib.interfaces.ICandidate;
import de.ipbhalle.metfraglib.list.MatchList;
import de.ipbhalle.metfraglib.parameter.VariableNames;

public class Molecule implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7059246953749018121L;
	
	protected String identifier;
	protected double mass;
	protected String formula;
	protected double finalScore;
	protected String imageAddress;
	protected String name;
	protected String inchi;
	protected String smiles;
	protected boolean useSmiles = false;
	
	protected int numberPeaksExplained;
	
	protected String displayFormula;
	//scores for final score calculation and to display
	protected ScoreSummary[] scoresSummaries;

	protected String[] additionalValueNames;
	protected String[] additionalValueNamesShort;
	protected int[] additionalValues;
	protected MatchList matchList;
	
	protected BarChartModel horizontalBarModel;
	protected Object database;
	protected Integer simScoreIndex;
	
	public Molecule(String identifier) {
		this.identifier = identifier;
	}

	public Molecule(String identifier, double mass, String formula, final List<Weight> weights, 
			String imageAddress, final ScoreSummary[] scoresSummaries, String inchi, String smiles, boolean useSmiles) {
		super();
		this.additionalValues = new int[] {100, 200, 110};
		
		this.useSmiles = useSmiles;
		this.identifier = identifier;
		this.mass = mass;
		this.formula = formula;
		this.displayFormula = this.formula.replaceAll("([0-9]+)", "<sub>$1</sub>");
		
		this.scoresSummaries = scoresSummaries;
		
		this.recalculateScore(weights);
		this.inchi = inchi;
		this.smiles = smiles;
		this.imageAddress = imageAddress;
		
		this.horizontalBarModel = new BarChartModel();
		ChartData chartData = new ChartData();
		
		BarChartDataSet dataSet = new BarChartDataSet();
		dataSet.setLabel("Scores");
		dataSet.setBackgroundColor("rgba(75, 192, 192, 0.6)");
		dataSet.setBorderColor("rgb(75, 192, 192)");
		dataSet.setBorderWidth(1);
		
		int scoreNotForGraphNumber = 0;
		int numberNotForScore = 0;
		for(int i = this.scoresSummaries.length - 1; i >= 0; i--)
			if(!this.scoresSummaries[i].isUsedForScoring()) numberNotForScore++;
		
		List<Object> values = new java.util.ArrayList<>();
		List<Object> labels = new java.util.ArrayList<>();
    	for(int i = this.scoresSummaries.length - 1; i >= 0; i--) {
    		if(!this.scoresSummaries[i].isUsedForScoring()) {
    			scoreNotForGraphNumber++;
    			continue;  
    		}
    		String end = "th";
    		int scoreIndex = i - numberNotForScore + 1 + scoreNotForGraphNumber;
    		if(scoreIndex == 1) end = "st";
    		if(scoreIndex == 2) end = "nd";
    		if(scoreIndex == 3) end = "rd";
    		labels.add(scoreIndex + end);
    		values.add(scoresSummaries[i].getValue());
        }
    	
    	dataSet.setData(values);
    	chartData.addChartDataSet(dataSet);
    	chartData.setLabels(labels);
    	this.horizontalBarModel.setData(chartData);
    	
    	// Configure options for horizontal bar chart
    	BarChartOptions options = new BarChartOptions();
    	options.setIndexAxis("y"); // Makes the bar chart horizontal
    	
    	org.primefaces.model.charts.optionconfig.legend.Legend legend = new org.primefaces.model.charts.optionconfig.legend.Legend();
    	legend.setDisplay(false);
    	options.setLegend(legend);
    	
    	CartesianScales cScales = new CartesianScales();
    	CartesianLinearAxes xAxis = new CartesianLinearAxes();
    	CartesianLinearTicks xTicks = new CartesianLinearTicks();
    	// Note: Min/Max configuration will be handled via extender function for compatibility
    	xAxis.setTicks(xTicks);
    	cScales.addXAxesData(xAxis);
    	options.setScales(cScales);
    	
    	this.horizontalBarModel.setOptions(options);
    	this.horizontalBarModel.setExtender("extender");
    	// Note: Bar width adjustment for few scores can be handled via Chart.js extender function
    	
    }
	
	public void recalculateScore(List<Weight> weights) {
		this.finalScore = 0.0;
		int weightIndex = 0;
		int scoreIndex = 0;
		while(scoreIndex < this.scoresSummaries.length) {
			if(this.scoresSummaries[scoreIndex].isUsedForScoring()) {
				this.finalScore += ((double)weights.get(weightIndex).getValue() / 100.0) * this.scoresSummaries[scoreIndex].getValue();
				weightIndex++;
			}
			scoreIndex++;
		}
		this.finalScore = MathTools.round(this.finalScore, 4);
	}
	
	public ScoreSummary[] getScoreSummary() {
		return this.scoresSummaries;
	}
	
    public BarChartModel getHorizontalScoreModel() {
        return this.horizontalBarModel;
    }
	
	public String getIdentifier() {
		return identifier;
	}

	public String getOriginalIdentifier() {
		return identifier.replaceAll("\\|[0-9]+", "");
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public double getMass() {
		return mass;
	}

	public void setMass(double mass) {
		this.mass = mass;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public String getDisplayFormula() {
		return displayFormula;
	}

	public void setDisplayFormula(String displayFormula) {
		this.displayFormula = displayFormula;
	}

	public double getScore() {
		return finalScore;
	}

	public String getInChI() {
		return inchi;
	}

	public void setInChI(String inchi) {
		this.inchi = inchi;
	}

	public void setScore(double score) {
		this.finalScore = score;
	}
	
	public String getImageAddress() {
		return this.imageAddress;
	}
	
	public boolean isDatabaseLinkAvailable() {
		if(database == null) return false;
		if(database instanceof AdditionalFileDatabase) {
			if(this.identifier.startsWith("DTXSID")) return true;
		} else {
			if(database.equals("PubChem") || database.equals("KEGG") 
				|| database.equals("ChemSpider") || database.equals("MetaCyc") || database.equals("ChemSpiderRest")
				|| database.equals("LipidMaps") || database.equals("LocalHMDB")
				|| database.equals("LocalChEBI") || ((database.equals("LocalSDF") || database.equals("LocalCSV") || database.equals("LocalPSV")) && this.identifier.startsWith("DTXSID"))) return true;
		}
		return false;
	}
	
	public Object getDatabase() {
		return database;
	}
	
	public String getDatabaseLink() {
		if(database instanceof AdditionalFileDatabase) {
			if(this.identifier.startsWith("DTXSID")) return "https://comptox.epa.gov/dashboard/dsstoxdb/results?search=" + this.getOriginalIdentifier();
		} else {
			if(database.equals("PubChem")) return "https://pubchem.ncbi.nlm.nih.gov/compound/" + this.getOriginalIdentifier();
			else if(database.equals("KEGG")) return "https://www.kegg.jp/entry/" + this.getOriginalIdentifier();
			else if(database.equals("ChemSpider") || database.equals("ChemSpiderRest")) return "http://www.chemspider.com/Chemical-Structure." + this.getOriginalIdentifier() + ".html";
			else if(database.equals("MetaCyc")) return "http://metacyc.org/META/NEW-IMAGE?type=COMPOUND&object=" + this.getOriginalIdentifier();
			else if(database.equals("LipidMaps")) return "http://www.lipidmaps.org/data/LMSDRecord.php?LMID=" + this.getOriginalIdentifier();
			else if(database.equals("LocalHMDB")) return "http://www.hmdb.ca/metabolites/" + this.getOriginalIdentifier();
			else if(database.equals("LocalChEBI")) return "https://www.ebi.ac.uk/chebi/searchId.do?chebiId=" + this.getOriginalIdentifier();
			else if((database.equals("LocalSDF") || database.equals("LocalCSV") || database.equals("LocalPSV")) && this.identifier.startsWith("DTXSID")) return "https://comptox.epa.gov/dashboard/dsstoxdb/results?search=" + this.getOriginalIdentifier();
		}
		return this.identifier;
	}
	
	public void setDatabaseName(Object database) {
		this.database = database;
	}
	
	public void setImageAddress(String imageAddress) {
		this.imageAddress = imageAddress;
	}

	public String[] getAdditionalValueNames() {
		return this.additionalValueNames;
	}

	public void setAdditionalValueNames(String[] additionalValueNames) {
		this.additionalValueNames = additionalValueNames;
	}
	
	@Override
	public boolean equals(Object mol) {
		return this.identifier.equals(((Molecule)mol).getIdentifier());
	}

	public String getSMILES() {
		return this.smiles;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int[] getAdditionalValues() {
		return additionalValues;
	}
	
	public int getNumberPeaksExplained() {
		return numberPeaksExplained;
	}

	public void setNumberPeaksExplained(int numberPeaksExplained) {
		this.numberPeaksExplained = numberPeaksExplained;
	}

	public void setAdditionalValues(int[] additionalValues) {
		this.additionalValues = additionalValues;
	}
	
	public MatchList getMatchList() {
		return this.matchList; 
	}
	
	public void setMatchList(MatchList matchList) {
		this.matchList = matchList; 
	}
	
	public double getSingleScore(int index) {
		return this.scoresSummaries[index].getValue();
	}

	public void setSimScoreIndex(Integer simScoreIndex) {
		this.simScoreIndex = simScoreIndex;
	}
	
	/**
	 * convert the MetFragWeb molecule to a scored candidate
	 * provides opportunity to be stored as candidate list by
	 * an IWriter
	 * 
	 * @return
	 */
	public ICandidate getCandidate() {
		ICandidate candidate = new PrecursorCandidate(this.inchi, this.identifier, this.smiles);
		candidate.setProperty(VariableNames.FINAL_SCORE_COLUMN_NAME, this.finalScore);
		candidate.setUseSmiles(this.useSmiles);
		candidate.setProperty(VariableNames.MONOISOTOPIC_MASS_NAME, this.mass);
		candidate.setProperty(VariableNames.MOLECULAR_FORMULA_NAME, this.formula);
		candidate.setProperty(VariableNames.NUMBER_EXPLAINED_PEAKS_COLUMN, this.numberPeaksExplained);
		if(this.name != null && this.name.length() != 0) candidate.setProperty(VariableNames.COMPOUND_NAME_NAME, this.name);
		candidate.setMatchList(this.matchList);
		
		for(int i = 0; i < this.scoresSummaries.length; i++) {
			String scoreValue = "NA";
			if(this.scoresSummaries[i].isAvailable()) scoreValue = String.valueOf(this.scoresSummaries[i].getRawValue());
			candidate.setProperty(this.scoresSummaries[i].getName(), scoreValue);
			if(!this.scoresSummaries[i].isDatabaseScore()) {
				candidate.setProperty(this.scoresSummaries[i].getName() + "_Values", this.scoresSummaries[i].getInfo());
			}
				
		}
		
		return candidate;
	}
	
	public double getSimScore() {
		try {
			if(this.simScoreIndex != null) return this.scoresSummaries[this.simScoreIndex].getValue();
			else return 0.0;
		}
		catch(Exception e) {
			System.out.println(this.scoresSummaries.length + " " + this.simScoreIndex);
			return 0.0;
		}
	}
}
