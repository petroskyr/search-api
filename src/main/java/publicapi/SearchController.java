package publicapi;

import java.util.Optional;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// Maps the search api endpoints into these functions.
@RestController
public class SearchController {

    // Handle POST requests, intended to facilitate manual testing.
    // example invocation from terminal:
    // $ curl -v \
    // > --header "Content-Type: application/json"\
    // > --data '{"entry":"arbitrary text words words...", "term":"searchTerm" }'\
    // > http://localhost:8080/search
    @RequestMapping(value="/search", method=RequestMethod.POST)
    public Results postSearch(@RequestBody PostSearchRequest input) {
	
	return new Search(input.getEntry(), input.getTerm(), true, true).Execute();
    }
    
    // Not yet functional endpoint...
    @RequestMapping("/search")
    public Results search(@RequestParam(value="entryid") int entryid,
			  @RequestParam(value="term") String term,
			  @RequestParam(value="ownerid") Optional<Integer> ownerid,
			  @CookieValue("auth") Optional<String> authCookie) {

	// Request from unathenticated user on any public journal entry is permitted.
	// Request from authenticated user on any of their own entries are permitted.
	// Request from authenticated user against another user's entry is permitted when the target user has granted read permissions to the authenticated user.
	// All other requests should return no usable information.

	// TODO:
	// Build authentication service, and user permissions capabilities.
	// Map authCookie to user ID, check appropriate read permissions.

	if (!authCookie.isPresent() && !ownerid.isPresent()) {
	    // Nothing to do, the journal entry is ambiguous among users.
	    return null;
	}
	
	if (!authCookie.isPresent()) {
	    // Indicates unathenticated, guest user... 
	    // TODO: ensure the journal mapping to ownerid/entryid has public read permission.
	}

	if (authCookie.isPresent() && ownerid.isPresent()) {
	    // TODO: enable following check.
	    // if (Identity.isDifferent(authCookie.get(), ownerid.get())) {
		// Authenticated user must have read
	        // permissions on this joural entry...
	    // }
	}


	String entry = "";
	// TODO: retrieve journal entry matching the id.
	
	return new Search(entry, term, true, true).Execute();
    }
}
