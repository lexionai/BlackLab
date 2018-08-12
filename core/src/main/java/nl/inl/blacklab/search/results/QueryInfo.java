package nl.inl.blacklab.search.results;

import nl.inl.blacklab.search.BlackLabIndex;
import nl.inl.blacklab.search.indexmetadata.AnnotatedField;

/**
 * Information about the original query.
 */
public final class QueryInfo {

    public static QueryInfo create(BlackLabIndex index) {
        return new QueryInfo(index, null);
    }

    public static QueryInfo create(BlackLabIndex index, AnnotatedField field) {
        return new QueryInfo(index, field);
    }

    private BlackLabIndex index;

    /** The field these hits came from (will also be used as concordance field) */
    private AnnotatedField field;

    /** The results object id of the original query (for debugging). */
    private int resultsObjectId = -1;

    private QueryInfo(BlackLabIndex index, AnnotatedField field) {
        super();
        this.index = index;
        this.field = field == null ? index.mainAnnotatedField() : field;
    }

    /** @return the index that was searched. */
    public BlackLabIndex index() {
        return index;
    }

    /** @return field that was searched */
    public AnnotatedField field() {
        return field;
    }

    /** @return the results object id of the original query. */
    public int resultsObjectId() {
        return resultsObjectId;
    }

    /**
     * Set the results object id of the original query.
     * 
     * This is only done exactly once, by the original query as it's constructed.
     * Attempting to change it later throws an exception. It is only used for debugging.
     * 
     * @param resultsObjectId results object id
     * @throws UnsupportedOperationException if you attempt to set it again
     * 
     */
    public void setResultsObjectId(int resultsObjectId) {
        if (this.resultsObjectId != -1)
            throw new UnsupportedOperationException("Cannot change resultsObjectId");
        if (resultsObjectId == -1)
            throw new UnsupportedOperationException("Invalid resultsObjectId: -1");
        this.resultsObjectId = resultsObjectId;
    }
}

