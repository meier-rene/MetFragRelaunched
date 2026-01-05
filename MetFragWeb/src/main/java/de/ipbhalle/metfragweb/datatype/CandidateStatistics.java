package de.ipbhalle.metfragweb.datatype;

import java.util.List;

import jakarta.faces.model.SelectItem;

import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.line.LineChartDataSet;
import org.primefaces.model.charts.line.LineChartModel;
import org.primefaces.model.charts.line.LineChartOptions;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearAxes;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearTicks;

public class CandidateStatistics {
	
	protected LineChartModel scoreDistributionModel;
	protected String scoreDistributionModelPointLabels;
	protected boolean showPointLabels;
	protected int selectedCandidate = 0;
	protected int[] showScoreGraphs;
	protected String[] showScoreGraphsString;
	protected String[] scoreGraphNames;
	protected String legendLabels;
	
	public CandidateStatistics() {
		this.legendLabels = "";
		this.showPointLabels = false;
		this.showScoreGraphs = new int[0];
		this.scoreGraphNames = new String[0];
	}
	
	public void generateScoreDistributionModelView(List<MetFragResult> results) {
		if(results.size() <= 1) {
			this.scoreDistributionModel = null;
			return;
		}
		this.scoreDistributionModel = new LineChartModel();
		ChartData data = new ChartData();
		
		// Create datasets
		LineChartDataSet mainDataSet = new LineChartDataSet();
		mainDataSet.setLabel("Final Score");
		mainDataSet.setBorderColor("rgb(75, 192, 192)");
		mainDataSet.setBackgroundColor("rgba(75, 192, 192, 0.2)");
		mainDataSet.setShowLine(false);
		mainDataSet.setPointRadius(7);
		
		this.legendLabels = "['Final Score'";
		
		LineChartDataSet[] scoreDataSets = new LineChartDataSet[this.showScoreGraphs.length];
		String[] colors = {"rgb(255, 99, 132)", "rgb(54, 162, 235)", "rgb(255, 206, 86)", "rgb(75, 192, 192)", "rgb(153, 102, 255)"};
		for(int k = 0; k < this.showScoreGraphs.length; k++) {
			scoreDataSets[k] = new LineChartDataSet();
			scoreDataSets[k].setLabel(this.scoreGraphNames[k]);
			String color = colors[k % colors.length];
			scoreDataSets[k].setBorderColor(color);
			scoreDataSets[k].setBackgroundColor(color.replace("rgb", "rgba").replace(")", ", 0.2)"));
			scoreDataSets[k].setPointRadius(0);
			this.legendLabels += ",'" + this.scoreGraphNames[k] + "'";
		}
		
		this.scoreDistributionModelPointLabels = "['" + results.get(0).getOriginalIdentifier() + "'";
		
		// Add data points
		List<Object> mainData = new java.util.ArrayList<>();
		List<List<Object>> scoreData = new java.util.ArrayList<>();
		for(int k = 0; k < this.showScoreGraphs.length; k++) {
			scoreData.add(new java.util.ArrayList<>());
		}
		
		List<Object> labels = new java.util.ArrayList<>();
		for(int i = 0; i < results.size(); i++) {
			labels.add(String.valueOf(i + 1));
			mainData.add(results.get(i).getScore());
			this.scoreDistributionModelPointLabels += (i == 0 ? "" : ",'" + results.get(i).getOriginalIdentifier() + "'");
			for(int k = 0; k < this.showScoreGraphs.length; k++) {
				scoreData.get(k).add(results.get(i).getRoot().getSingleScore(this.showScoreGraphs[k]));
			}
		}
		
		mainDataSet.setData(mainData);
		data.addChartDataSet(mainDataSet);
		
		for(int k = 0; k < this.showScoreGraphs.length; k++) {
			scoreDataSets[k].setData(scoreData.get(k));
			data.addChartDataSet(scoreDataSets[k]);
		}
		
		data.setLabels(labels);
		this.scoreDistributionModel.setData(data);
		
		// Configure options
		LineChartOptions options = new LineChartOptions();
		options.setMaintainAspectRatio(false);
		
		org.primefaces.model.charts.optionconfig.legend.Legend legend = new org.primefaces.model.charts.optionconfig.legend.Legend();
		legend.setDisplay(true);
		legend.setPosition("top");
		options.setLegend(legend);
		
		CartesianScales cScales = new CartesianScales();
		CartesianLinearAxes xAxis = new CartesianLinearAxes();
		CartesianLinearTicks xTicks = new CartesianLinearTicks();
		xAxis.setTicks(xTicks);
		cScales.addXAxesData(xAxis);
		
		CartesianLinearAxes yAxis = new CartesianLinearAxes();
		cScales.addYAxesData(yAxis);
		options.setScales(cScales);
		
		this.scoreDistributionModel.setOptions(options);
		this.scoreDistributionModel.setExtender("extenderScore");
		
		this.scoreDistributionModelPointLabels += "]";
		this.legendLabels += "]";
	}

	public LineChartModel getScoreDistributionModel() {
		return this.scoreDistributionModel;
	}

	public String getScoreDistributionModelPointLabels() {
		return this.scoreDistributionModelPointLabels;
	}

	public String getLegendLabels() {
		return this.legendLabels;
	}
	
	public int getSelectedCandidate() {
		return this.selectedCandidate;
	}
	
	public String[] getShowScoreGraphs() {
		return this.showScoreGraphsString;
	}

	public void setShowScoreGraphs(String[] showScoreGraphsString, java.util.List<SelectItem> availableScoreNamesForScoreGraph) {
		this.showScoreGraphsString = showScoreGraphsString;
		this.showScoreGraphs = new int[showScoreGraphsString.length];
		this.scoreGraphNames = new String[showScoreGraphsString.length];
		for(int i = 0; i < this.showScoreGraphs.length; i++) {
			this.showScoreGraphs[i] = Integer.parseInt(showScoreGraphsString[i]);
			this.scoreGraphNames[i] = availableScoreNamesForScoreGraph.get(this.showScoreGraphs[i]).getLabel();
		}
	}

	public void setSelectedCandidate(int selectedCandidate) {
		this.selectedCandidate = selectedCandidate;
	}

	public boolean isShowPointLabels() {
		return this.showPointLabels;
	}
	
	public void setShowPointLabels(boolean showPointLabels) {
		this.showPointLabels = showPointLabels;
	}
}
