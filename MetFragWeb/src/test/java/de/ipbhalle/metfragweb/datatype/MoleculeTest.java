package de.ipbhalle.metfragweb.datatype;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for Molecule chart model behavior.
 * Tests the horizontal bar chart model generation to ensure
 * consistent behavior before and after PrimeFaces 14 migration.
 */
public class MoleculeTest {
    
    @Test
    public void testMoleculeConstructorWithBasicData() {
        String identifier = "CID12345";
        double mass = 180.16;
        String formula = "C6H12O6";
        List<Weight> weights = createTestWeights();
        String imageAddress = "/images/molecule.png";
        ScoreSummary[] scores = createTestScores();
        String inchi = "InChI=1S/C6H12O6/c7-1-2-3(8)4(9)5(10)6(11)12-2/h2-11H,1H2/t2-,3-,4+,5-,6?/m1/s1";
        String smiles = "OCC1OC(O)C(O)C(O)C1O";
        
        Molecule molecule = new Molecule(identifier, mass, formula, weights, 
            imageAddress, scores, inchi, smiles, false);
        
        assertNotNull(molecule, "Molecule should be created");
        assertEquals(identifier, molecule.getIdentifier());
        assertEquals(mass, molecule.getMass(), 0.0001);
        assertEquals(formula, molecule.getFormula());
        assertTrue(molecule.getScore() > 0, "Final score should be calculated");
    }
    
    @Test
    public void testHorizontalBarChartModelCreation() {
        Molecule molecule = createTestMolecule();
        
        Object barModel = molecule.getHorizontalScoreModel();
        
        assertNotNull(barModel, "Horizontal bar chart model should be created");
    }
    
    @Test
    public void testScoreSummaryAvailability() {
        Molecule molecule = createTestMolecule();
        
        ScoreSummary[] summaries = molecule.getScoreSummary();
        
        assertNotNull(summaries, "Score summaries should be available");
        assertTrue(summaries.length > 0, "Should have at least one score");
    }
    
    @Test
    public void testRecalculateScore() {
        Molecule molecule = createTestMolecule();
        double initialScore = molecule.getScore();
        
        // Change weights
        List<Weight> newWeights = new ArrayList<>();
        newWeights.add(new Weight("Score1", 50));
        newWeights.add(new Weight("Score2", 50));
        
        molecule.recalculateScore(newWeights);
        
        // Score may change based on new weights
        assertNotNull(molecule.getScore(), "Score should still be calculated");
    }
    
    @Test
    public void testDisplayFormula() {
        Molecule molecule = createTestMolecule();
        
        String displayFormula = molecule.getDisplayFormula();
        
        assertNotNull(displayFormula, "Display formula should be available");
        assertTrue(displayFormula.contains("<sub>"), 
            "Display formula should contain HTML subscript tags");
    }
    
    @Test
    public void testDatabaseLinkGeneration() {
        Molecule molecule = createTestMolecule();
        molecule.setDatabaseName("PubChem");
        
        assertTrue(molecule.isDatabaseLinkAvailable(), 
            "Database link should be available for PubChem");
        
        String link = molecule.getDatabaseLink();
        assertNotNull(link, "Database link should be generated");
        assertTrue(link.contains("pubchem"), "Link should contain pubchem");
    }
    
    @Test
    public void testImageAddress() {
        String imageAddress = "/test/image.png";
        Molecule molecule = createTestMolecule();
        molecule.setImageAddress(imageAddress);
        
        assertEquals(imageAddress, molecule.getImageAddress());
    }
    
    @Test
    public void testInChIAndSMILES() {
        Molecule molecule = createTestMolecule();
        
        assertNotNull(molecule.getInChI(), "InChI should be available");
        assertNotNull(molecule.getSMILES(), "SMILES should be available");
    }
    
    @Test
    public void testMoleculeEquality() {
        Molecule mol1 = new Molecule("CID12345");
        Molecule mol2 = new Molecule("CID12345");
        Molecule mol3 = new Molecule("CID67890");
        
        assertTrue(mol1.equals(mol2), "Molecules with same ID should be equal");
        assertFalse(mol1.equals(mol3), "Molecules with different IDs should not be equal");
    }
    
    /**
     * Helper method to create a test molecule with basic configuration
     */
    private Molecule createTestMolecule() {
        String identifier = "TEST_123";
        double mass = 180.16;
        String formula = "C6H12O6";
        List<Weight> weights = createTestWeights();
        String imageAddress = "/test.png";
        ScoreSummary[] scores = createTestScores();
        String inchi = "InChI=1S/test";
        String smiles = "CCCCCC";
        
        return new Molecule(identifier, mass, formula, weights, 
            imageAddress, scores, inchi, smiles, false);
    }
    
    /**
     * Helper method to create test weights
     */
    private List<Weight> createTestWeights() {
        List<Weight> weights = new ArrayList<>();
        weights.add(new Weight("FragmenterScore", 60));
        weights.add(new Weight("OfflineMetFusion", 40));
        return weights;
    }
    
    /**
     * Helper method to create test score summaries
     */
    private ScoreSummary[] createTestScores() {
        ScoreSummary[] scores = new ScoreSummary[2];
        scores[0] = new ScoreSummary("FragmenterScore", 0.85, 0.85, "test", false, true);
        scores[1] = new ScoreSummary("OfflineMetFusion", 0.75, 0.75, "test", false, true);
        return scores;
    }
}
