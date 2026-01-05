package de.ipbhalle.metfragweb.datatype;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for CandidateStatistics chart model behavior.
 * Tests the score distribution model generation to ensure
 * consistent behavior before and after PrimeFaces 14 migration.
 */
public class CandidateStatisticsTest {
    
    private CandidateStatistics statistics;
    
    @BeforeEach
    public void setUp() {
        statistics = new CandidateStatistics();
        statistics.setShowScoreGraphs(new String[0], new ArrayList<>());
    }
    
    @Test
    public void testGenerateScoreDistributionModelView_EmptyResults() {
        List<MetFragResult> results = new ArrayList<>();
        
        statistics.generateScoreDistributionModelView(results);
        
        assertNull(statistics.getScoreDistributionModel(), 
            "Model should be null for empty results");
    }
    
    @Test
    public void testGenerateScoreDistributionModelView_SingleResult() {
        List<MetFragResult> results = new ArrayList<>();
        Molecule mol = new Molecule("test_identifier");
        mol.setScore(0.85);
        MetFragResult result = new MetFragResult(mol, 0);
        results.add(result);
        
        statistics.generateScoreDistributionModelView(results);
        
        assertNull(statistics.getScoreDistributionModel(), 
            "Model should be null for single result");
    }
    
    @Test
    public void testGenerateScoreDistributionModelView_MultipleResults() {
        List<MetFragResult> results = createTestResults(5);
        
        statistics.generateScoreDistributionModelView(results);
        
        assertNotNull(statistics.getScoreDistributionModel(), 
            "Model should not be null for multiple results");
        
        // Verify model has series
        Object model = statistics.getScoreDistributionModel();
        assertNotNull(model, "Chart model should be created");
        
        // Verify point labels are generated
        String pointLabels = statistics.getScoreDistributionModelPointLabels();
        assertNotNull(pointLabels, "Point labels should be generated");
        assertTrue(pointLabels.contains("ID_0"), "Point labels should contain identifier");
        
        // Verify legend labels
        String legendLabels = statistics.getLegendLabels();
        assertNotNull(legendLabels, "Legend labels should be generated");
        assertTrue(legendLabels.contains("Final Score"), "Legend should contain 'Final Score'");
    }
    
    @Test
    public void testScoreDistributionModelPointLabelsFormat() {
        List<MetFragResult> results = createTestResults(3);
        
        statistics.generateScoreDistributionModelView(results);
        
        String pointLabels = statistics.getScoreDistributionModelPointLabels();
        // Expected format: ['ID_0','ID_1','ID_2']
        assertTrue(pointLabels.startsWith("["), "Point labels should start with [");
        assertTrue(pointLabels.endsWith("]"), "Point labels should end with ]");
        assertTrue(pointLabels.contains("'"), "Point labels should contain quotes");
    }
    
    @Test
    public void testShowPointLabelsProperty() {
        assertFalse(statistics.isShowPointLabels(), 
            "Show point labels should default to false");
        
        statistics.setShowPointLabels(true);
        assertTrue(statistics.isShowPointLabels(), 
            "Show point labels should be true after setting");
    }
    
    @Test
    public void testSelectedCandidateProperty() {
        assertEquals(0, statistics.getSelectedCandidate(), 
            "Selected candidate should default to 0");
        
        statistics.setSelectedCandidate(5);
        assertEquals(5, statistics.getSelectedCandidate(), 
            "Selected candidate should be updated");
    }
    
    /**
     * Helper method to create test results with basic data
     */
    private List<MetFragResult> createTestResults(int count) {
        List<MetFragResult> results = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Molecule mol = new Molecule("ID_" + i);
            mol.setScore(0.9 - (i * 0.1)); // Decreasing scores
            MetFragResult result = new MetFragResult(mol, i);
            results.add(result);
        }
        return results;
    }
}
