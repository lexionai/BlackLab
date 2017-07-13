package nl.inl.blacklab.index.xpath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigInputFormat {

    // TODO: add configurable indexing support for:
    // - additional operations (using plugin classes)
    // - extra element processing (e.g. timesegment)
    // - subannotations(FUTURE)

    /**
     * Prepends the given XPath expression with a dot if it starts with a slash.
     *
     * This means "/bla" will become "./bla", "//bla" will become ".//bla",
     * but "bla" will remain "bla". This ensures the given XPath is always interpreted
     * relatively to the current node by VTD-XML.
     *
     * @param xp input XPath, possibly starting with one or two slashes
     * @return relativized XPath expression
     */
    public static String relXPath(String xp) {
        if (xp.startsWith("/"))
            return "." + xp;
        return xp;
    }

    /** Pay attention to namespaces while parsing? */
    private boolean isNamespaceAware = true;

    /** XML namespace declarations */
    private Map<String, String> namespaces = new HashMap<>();

    /** How to find our documents */
    private String xpDocuments;

    /** Where our metadata (if any) is located (relative to the document element) */
    private String xpMetadata;

    /** Annotated fields (usually just "contents") */
    private List<ConfigAnnotatedField> annotatedFields = new ArrayList<>();

    /** Metadata fields */
    private List<ConfigMetadataField> metadataFields = new ArrayList<>();

    public void setNamespaceAware(boolean isNamespaceAware) {
        this.isNamespaceAware = isNamespaceAware;
    }

    public void addNamespace(String name, String uri) {
        namespaces.put(name, uri);
    }

    public void setXPathDocument(String xpDocuments) {
        this.xpDocuments = xpDocuments;
    }

    public void setXPathMetadata(String xpMetadata) {
        this.xpMetadata = ConfigInputFormat.relXPath(xpMetadata);
    }

    public void addAnnotatedField(ConfigAnnotatedField f) {
        this.annotatedFields.add(f);
    }

    public void addMetadataField(ConfigMetadataField f) {
        this.metadataFields.add(f);
    }

    public boolean isNamespaceAware() {
        return isNamespaceAware;
    }

    public Map<String, String> getNamespaces() {
        return namespaces;
    }

    public String getXPathDocuments() {
        return xpDocuments;
    }

    public String getXPathMetadata() {
        return xpMetadata;
    }

    public List<ConfigAnnotatedField> getAnnotatedFields() {
        return Collections.unmodifiableList(annotatedFields);
    }

    public List<ConfigMetadataField> getMetadataFields() {
        return Collections.unmodifiableList(metadataFields);
    }


}