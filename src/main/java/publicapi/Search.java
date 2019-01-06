package publicapi;

import java.util.HashMap;
import java.util.regex.Pattern;

// Resource implementing the search algorithm.
public class Search {

    private final String text;
    private final String term;
    private final boolean mustEndOnWordBoundary;
    private final boolean mustStartOnWordBoundary;

    // text: the text to search through.
    // term: the search term.
    // mustEndOnWordBoundary: decides if a match must end on a word boundary in order to be counted.
    // mustStartOnWordBoundary: decides if a match must start on a word boundary in order to be counted.
    public Search(final String text, final String term, boolean mustEndOnWordBoundary, boolean mustStartOnWordBoundary) {
	this.text = text;
	this.term = term;
	this.mustEndOnWordBoundary = mustEndOnWordBoundary;
	this.mustStartOnWordBoundary = mustStartOnWordBoundary;
    }


    // Perform the actual search algorithm for exact matches
    // and Levenshtein 1 distance.
    public Results Execute() {
	// Holds the counts of Levenshtein 1 distance hits.
	HashMap<String, Integer> similarHits = new HashMap<String, Integer>();
	int exactMatchHits = 0;

	if (text == null || term == null || text == "" || term == "") {
	    return new Results(exactMatchHits, term, similarHits, mustStartOnWordBoundary, mustEndOnWordBoundary); 
	}

	// Search the text for matches on term.

	// This approach attempts to search for the term
	// in the text, by considering the text a stream
	// of characters.
	
	// Consider: this approach does not allow for
	// Levenshtein Distances other than 0 and 1,
	// but does change the runtime for Levenstein
	// Distance from O(n^2), to O(3n) in the inner for loop.
	// However, the approach requires intense state
	// management and highly bug prone. TODO: add unit tests!

	// Overall runtime of this method is expected to be O(n * m),
	// where n and m are the lengths of the search term and the text.
	
	// Also consider:
	// This approach more easily allows, for example, "bat man"
	// to be a similar match when "batman" is searched, whereas
	// attmepting to isolate words from the text and individually
	// search them for the search-term would prove more difficult.

	int i = 0; // index into text, from which the search begins.
	int end = text.length() - term.length() + 2; // searching into text at or beyond this index is fruitless - there would need to be at least 2 edits to match the search term.
	while(i < end && Character.isWhitespace(text.charAt(i))) {
	    i++;
	}
	
	while (i < end) {
	    
	    int discrepancies = 0;
	    int j = 0; // index into term.
	    
	    while (j < term.length() && i + j < text.length() && discrepancies == 0) {
		if (term.charAt(j) != text.charAt(i + j)) {
		    discrepancies++;
		} else {
		    j++;
		}
	    }

	    if (discrepancies == 0) {
		if (j == term.length()) {
		    if (!mustEndOnWordBoundary || isWordEndBoundary(text, i + j)) {
			// Exact match.
			exactMatchHits++;
		    } else if (isWordEndBoundary(text, i + j + 1)) {
			// Similar match.
			String similar = text.substring(i, i + j + 1);
			similarHits.put(similar, similarHits.getOrDefault(similar, 0) + 1);
		    }
		} else if (i + j == text.length()) {
		    // Remaining text was 1 character shorter than the search term.
		    // Similar match.
		    // ex: term=AAA, text=AA
		    String similar = text.substring(i, i + j);
		    similarHits.put(similar, similarHits.getOrDefault(similar, 0) + 1);
		}
	    } else if (j == term.length() - 1 && isWordEndBoundary(text, i + j)) {
		// Similar match, ex: term: Word, text: "Wor XXXXX"
		// In this case, the loop ended because the last character in
		// the term lined up with a word end boundary. Just call this
		// a similar match, and move on in the search.
		String similar = text.substring(i, i + j);
		similarHits.put(similar, similarHits.getOrDefault(similar, 0) + 1);
	    } else if (discrepancies == 1) {
		// Need to simulate swap, delete, insert operations and
		// check for a similar match. No hope for an exact match anymore.
		int jj = j;

		// Simulate swap.
		j++;
		while (j < term.length() && i + j < text.length() && discrepancies == 1) {
		    if (term.charAt(j) != text.charAt(i + j)) {
			discrepancies++;
		    } else {
			j++;
		    }
		}

		if (discrepancies == 1 && j == term.length()) {
		    if (!mustEndOnWordBoundary || isWordEndBoundary(text, i + j)) {
			// Similar match due to swap.
			String similar = text.substring(i, i + j);
			similarHits.put(similar, similarHits.getOrDefault(similar, 0) + 1);
		    }
		} else {
		    // Swapping didn't work, try insert.
		    j = jj;
		    discrepancies = 1;

		    while (j < term.length() && i + j + 1 < text.length() && discrepancies == 1) {
			if (term.charAt(j) != text.charAt(i + j + 1)) {
			    discrepancies++;
			}
			else {
			    j++;
			}
		    }

		    if (discrepancies == 1 && j == term.length()) {
			if (!mustEndOnWordBoundary || isWordEndBoundary(text, i + j +  1)) {
			    // Similar match due to insert.
			    String similar = text.substring(i, i + j + 1);
			    similarHits.put(similar, similarHits.getOrDefault(similar, 0) + 1);
			}
		    } else  {
			// Finally, try delete because swap and insert failed.
			j = jj + 1;
			discrepancies = 1;
			
			while (j < term.length() && i + j - 1 < text.length() && discrepancies == 1) {
			    if (term.charAt(j) != text.charAt(i + j - 1)) {
				discrepancies++;
			    } else {
				j++;
			    }
			}

			if (discrepancies == 1 && j == term.length()) {
			    if (!mustEndOnWordBoundary || isWordEndBoundary(text, i + j - 1)) {
				// Similar math due to delete.
				String similar = text.substring(i, i + j - 1);
				similarHits.put(similar, similarHits.getOrDefault(similar, 0) + 1);
			    }
			}
		    }
		}
	    }
		
	    // Progress forward in the text string.
	    i++;
	    if (mustStartOnWordBoundary) {
		// If necessary, keep moving to a word start boundary.
		while (i < end && !(Character.isWhitespace(text.charAt(i - 1)) && !Character.isWhitespace(text.charAt(i)))) {
		    i++;
		}
	    }
	}

	return new Results(exactMatchHits, term, similarHits, mustStartOnWordBoundary, mustEndOnWordBoundary);
    }
    

    // https://stackoverflow.com/a/49289766
    // TODO: test and improve this manner of detecting punctuation. Using regex and matching on a single character is over kill.
    private boolean isWordEndBoundary(String s, int i) {
	return s == null ? false :
	    s.length() <= i || Character.isWhitespace(s.charAt(i)) || Pattern.matches("[\\p{Punct}\\p{IsPunctuation}]", s.substring(i, i+1));
    }
}
