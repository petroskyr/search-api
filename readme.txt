Search API Design

Endpoint: site/search
Method: GET

Query parameters:
entryid - the unique ID of the ELN entry to be searched
term - the term to search for
owner - (optional) the unique ID of the owner of the journal entry. If omitted, the user id is assumed to be that of the caller.

Examples:

GET site/search?entryid=3&term=Words
Searches for the term "Words" in your 4th ELN entry.


GET site/search?entryid=0&owner=12&term=Words
Searches for the term "Words" in the first ELN entry
in the journal for owner id 12


Method: POST
parameters:
term - the term to search for
entry - the text to search

Example:

POST site/search
{
	"term": "Words",
	"entry": "Text containing words words more Words"
}
Sends the text to the server, and searches for "Words",
where the response contains similar words with
Levenshtein Distance of 1.  


Assumptions:
This API design assumes there is a secure cookie available indicating
the identity of the caller. This cookie is accessed so that
    - providing your own id is not necessary as a query parameter when searching your own journal entries.
    - read privledges can be checked, if the journal entry ID of another user is passed

This design also assumed it would be beneficial to provide the POST request
in cases when the search text is small, or not already stored on the server,
or for testing purposes, or perhaps allowing unauthenticated users the ability
to search.

The intent of the design is to meet the requirements of the user story,
and allow future expansion of the search capabilities. For example,
adding an optional range parameter to filter the  results to a
limited subset of the text could be a worthy endeavour. Another
expansion could be to add a new query parameter to toggle the sensitivity
to capitalization differences.


I was only able to perform manual testing, but would typically
add unit tests.

To run a test server:
$ gradle bootRun

To send a POST request:
$ curl -v --header "Content-Type application:json" --data '{"entry":"Any arbitrary text...","term":"searchTerm" }' http://localhost:8080/search

$ curl -v --header "Content-Type application:json" --data '{"entry":"Bat man. Who is bat man? Can Batman put the Badman in the can? Maybe the cat can.","term":"bat man" }' http://localhost:8080/search

$ curl -v --header "Content-Type application:json" --data '{"entry":"Simple example.","term":"example" }' http://localhost:8080/search

$ curl -v --header "Content-Type application:json" --data '{"entry":"Simple example of swap.","term":"on" }' http://localhost:8080/search

$ curl -v --header "Content-Type application:json" --data '{"entry":"Simple example of delete.","term":"o" }' http://localhost:8080/search

$ curl -v --header "Content-Type application:json" --data '{"entry":"Simple example of insert.","term":"off" }' http://localhost:8080/search


A batch script could be written to perform automated testing against the test server. I have not yet learned how to unit test using gradle, but running the command "gradle tasks" shows that if there are units tests, it would be able to run them. 

Lastly, I would like to mention a few things that, although I got this code functional and I deliberately chose not to implement the brute force Levenshtein Distance, or the dynamic Levenshtein Distance, I regretted that decision. Resources online clearly show those implementations, but I decided on this implementation believing the state management would be easy for a distance of 1, and that the implementation would offer better flexibility in terms of making word boundaries, capitalization, etc... as configurable inputs to the algorithm.

I would also like to add that my implementation currently does not add any metrics for analyzing the searches over time, or does not cache searches. I believe adding somemetrics into the search would be a good idea. For example, tracking what users search what words and how long the searches take, would be beneficial for product development and learning how to improve the search capabilities over time. If searches tend to be unique, caching results does not seem helpful. However, if data shows many similar or repeated searches, caching the results would be great. I would imagine that we cold store the GET query URL parameters, and the last modified time of the journal entry, and the results of the search. Then, when the search is repeated, we could compare the last modified time on the journal entry to that in the search cache, and determine if a new search is needed or not.

I had a good time thinking about many different aspects of this code challenge.

Thank you.
