package publicapi;

// Helper class allowing Jackson JSON library
// to deserialize JSON into a simple java object.
public class PostSearchRequest {

    private String term;
    private String entry;

    public PostSearchRequest() { }

    public String getTerm() {
	return term;
    }
    public void setTerm(String term) {
	this.term = term;
    }
    public String getEntry() {
	return entry;
    }
    public void setEntry() {
	this.entry = entry;
    }
}
