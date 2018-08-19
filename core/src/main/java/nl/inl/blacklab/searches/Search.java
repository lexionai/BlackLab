package nl.inl.blacklab.searches;

import java.util.concurrent.CompletableFuture;

import nl.inl.blacklab.exceptions.InvalidQuery;
import nl.inl.blacklab.search.results.QueryInfo;
import nl.inl.blacklab.search.results.SearchResult;

/**
 * A 'recipe' of search operations.
 */
public interface Search {

    /**
     * Execute the search operation, returning the final response.
     * 
     * Executes the search synchronously.
     *  
     * @return result of the operation 
     * @throws InvalidQuery
     */
    SearchResult execute() throws InvalidQuery;

    /**
     * Execute the search operation asynchronously.
     * 
     * Runs the search in a separate thread and passes the result through
     * the returned Future.
     * 
     * @return future result
     */
    default CompletableFuture<? extends SearchResult> executeAsync() {
        return null;
    }
    
    @Override
    boolean equals(Object obj);

    @Override
    int hashCode();

    QueryInfo queryInfo();

}