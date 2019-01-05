package publicapi;

import java.util.HashMap;

// Helper class summarizing search results.
// Provided so that the Jackson JSON library
// can easily format this data into JSON.
public class Results {

    private final String matchTerm;
    private final int frequency;
    private HashMap<String,Integer> similarHits;
    private boolean enforceWordStartBoundary;
    private boolean enforceWordEndBoundary;
        
    public Results(final int frequency,
		   final String matchTerm,
		   final HashMap<String,Integer> similarHits,
		   final boolean enforceWordStartBoundary,
		   final boolean enforceWordEndBoundary) {
	this.matchTerm = matchTerm;
	this.frequency = frequency;
	this.similarHits = similarHits;
	this.enforceWordStartBoundary = enforceWordStartBoundary;
	this.enforceWordEndBoundary = enforceWordEndBoundary;
    }

    public String getMatchTerm() {
	return matchTerm;
    }
    public int getFrequency() {
	return frequency;
    }
    public HashMap<String,Integer> getSimilarHits() {
	return similarHits;
    }
    public boolean getEnforceWordStartBoundary() {
	return enforceWordStartBoundary;
    }
    public boolean getEnforceWorEndBoundary() {
	return enforceWordEndBoundary;
    }
}
