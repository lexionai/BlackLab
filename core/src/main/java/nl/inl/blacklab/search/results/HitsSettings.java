package nl.inl.blacklab.search.results;

import java.util.Collection;

import nl.inl.blacklab.search.ConcordanceType;
import nl.inl.blacklab.search.Searcher;
import nl.inl.blacklab.search.indexmetadata.AnnotatedField;

public class HitsSettings {

    /**
     * Stop retrieving hits after this number. (NO_LIMIT = -1 = don't stop
     * retrieving)
     */
    private int maxHitsToRetrieve;

    /**
     * Stop counting hits after this number. (NO_LIMIT = -1 = don't stop counting)
     */
    private int maxHitsToCount;

    /** What to use to make concordances: forward index or content store */
    private ConcordanceType concsType;

    /**
     * The default field to use for retrieving concordance information.
     */
    private AnnotatedField concordanceField;

    /**
     * Forward index to use as text context of &lt;w/&gt; tags in concordances
     * (words; null = no text content)
     */
    private String concWordProps;

    /**
     * Forward index to use as text context between &lt;w/&gt; tags in concordances
     * (punctuation+whitespace; null = just a space)
     */
    private String concPunctProps;

    /**
     * Forward indices to use as attributes of &lt;w/&gt; tags in concordances (null
     * = the rest)
     */
    private Collection<String> concAttrProps; // all other FIs are attributes

    /** Our desired context size */
    private int desiredContextSize;

    /** Index object, so we can ask for the default contents field if none was set yet */
    private Searcher searcher;

    public HitsSettings(HitsSettings defaults) {
        concordanceField = defaults.concordanceField();
        maxHitsToRetrieve = defaults.maxHitsToRetrieve();
        maxHitsToCount = defaults.maxHitsToCount();
        concsType = defaults.concordanceType();
        concWordProps = defaults.concWordProp();
        concPunctProps = defaults.concPunctProp();
        concAttrProps = defaults.concAttrProps();
        desiredContextSize = defaults.contextSize();
    }

    public HitsSettings(Searcher searcher) {
        this.searcher = searcher;
        this.concordanceField = null; // later on we'll ask for the main contents field (when the index knows that)
        maxHitsToRetrieve = Searcher.DEFAULT_MAX_RETRIEVE;
        maxHitsToCount = Searcher.DEFAULT_MAX_COUNT;
        concsType = Searcher.DEFAULT_CONC_TYPE;
        concWordProps = Searcher.DEFAULT_CONC_WORD_PROP;
        concPunctProps = Searcher.DEFAULT_CONC_PUNCT_PROP;
        concAttrProps = Searcher.DEFAULT_CONC_ATTR_PROP;
        desiredContextSize = Searcher.DEFAULT_CONTEXT_SIZE;
    }

    /** @return the maximum number of hits to retrieve. */
    public int maxHitsToRetrieve() {
        return maxHitsToRetrieve;
    }

    /**
     * Set the maximum number of hits to retrieve
     * 
     * @param n the number of hits, or HitsSettings.UNLIMITED for no limit
     */
    public void setMaxHitsToRetrieve(int n) {
        this.maxHitsToRetrieve = n;
    }

    /** @return the maximum number of hits to count. */
    public int maxHitsToCount() {
        return maxHitsToCount;
    }

    /**
     * Set the maximum number of hits to count
     * 
     * @param n the number of hits, or HitsSettings.UNLIMITED for no limit
     */
    public void setMaxHitsToCount(int n) {
        this.maxHitsToCount = n;
    }

    /**
     * Are we making concordances using the forward index (true) or using the
     * content store (false)? Forward index is more efficient but returns
     * concordances that don't include XML tags.
     *
     * @return true iff we use the forward index for making concordances.
     */
    public ConcordanceType concordanceType() {
        return concsType;
    }

    /**
     * Do we want to retrieve concordances from the forward index or from the
     * content store? Forward index is more efficient but doesn't exactly reproduces
     * the original XML.
     *
     * The default type can be set by calling Searcher.setDefaultConcordanceType().
     *
     * @param type the type of concordances to make
     */
    public void setConcordanceType(ConcordanceType type) {
        this.concsType = type;
    }

    /**
     * Returns the field to use for retrieving concordances.
     *
     * @return the field name
     */
    public AnnotatedField concordanceField() {
        // If not set yet, ask the index for the default field
        if (concordanceField == null)
            concordanceField = searcher.getIndexMetadata().annotatedFields().main();
        return concordanceField;
    }

    /**
     * Sets the field to use for retrieving concordances.
     *
     * @param concordanceField the field 
     */
    public void setConcordanceField(AnnotatedField concordanceField) {
        this.concordanceField = concordanceField;
    }

    /**
     * Indicate what properties to use to build concordances.
     *
     * This configuration is only valid when using forward indices to build
     * concordances.
     *
     * @param wordFI FI to use as the text content of the &lt;w/&gt; tags (default
     *            "word"; null for no text content)
     * @param punctFI FI to use as the text content between &lt;w/&gt; tags (default
     *            "punct"; null for just a space)
     * @param attrFI FIs to use as the attributes of the &lt;w/&gt; tags (null for
     *            all other FIs)
     */
    public void setConcordanceProperties(String wordFI, String punctFI, Collection<String> attrFI) {
        concWordProps = wordFI;
        concPunctProps = punctFI;
        concAttrProps = attrFI;
    }

    public String concWordProp() {
        return concWordProps;
    }

    public String concPunctProp() {
        return concPunctProps;
    }

    public Collection<String> concAttrProps() {
        return concAttrProps;
    }

    public int contextSize() {
        return desiredContextSize;
    }

    public void setContextSize(int n) {
        desiredContextSize = n;
    }

}
