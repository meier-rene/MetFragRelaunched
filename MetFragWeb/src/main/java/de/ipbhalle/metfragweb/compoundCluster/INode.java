package de.ipbhalle.metfragweb.compoundCluster;

import de.ipbhalle.metfragweb.datatype.MetFragResult;

public interface INode {
	
	public double getMaxScore();
	public void setMaxScore(double maxScore);
	public boolean hasResult();
	public MetFragResult getResult();
	public void resetMaxScore();
	public int getLeafsUnderneath();
	public void setLeafsUnderneath(int leafsUnderneath);
	public String getNodeColorCSS(double maximumScore);
	
}
