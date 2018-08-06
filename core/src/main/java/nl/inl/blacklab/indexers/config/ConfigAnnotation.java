package nl.inl.blacklab.indexers.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import nl.inl.blacklab.index.complex.AnnotationWriter.SensitivitySetting;

/**
 * Configuration for a single annotation ("property") of an annotated field
 * ("complex field").
 */
public class ConfigAnnotation {

    /** Annotation name, or forEach (or name XPath, if forEach) */
    private String name;

    /** If specified, all other XPath expression are relative to this */
    private String basePath = null;

    /** Where to find body text */
    private String valuePath;

    /**
     * If valuePath consists only of digits, this is the integer value. Otherwise,
     * it is Integer.MAX_VALUE
     */
    private int valuePathInt = Integer.MAX_VALUE;

    /**
     * If null: regular annotation definition. Otherwise, find all nodes matching
     * this XPath, then evaluate name and valuePath as XPaths for each matching
     * node, adding a subannotation value for each. NOTE: forEach is only supported
     * for subannotations, because all main annotations (complex field properties)
     * need to be known from the start.
     */
    private String forEachPath;

    /** How to process annotation values (if at all) */
    private List<ConfigProcessStep> process = new ArrayList<>();

    /**
     * What sensitivity setting to use to index this annotation (optional, default
     * depends on field name)
     */
    private SensitivitySetting sensitivity = SensitivitySetting.DEFAULT;

    /** XPaths to capture the value of, to substitute for $1-$9 in valuePath */
    private List<String> captureValuePaths = new ArrayList<>();

    /**
     * Our subannotations. Note that only 1 level of subannotations is processed
     * (i.e. there's no subsubannotations), although we could process more levels if
     * desired.
     */
    private List<ConfigAnnotation> subAnnotations = new ArrayList<>();

    /** Our subannotations (except forEach's) by name. */
    private Map<String, ConfigAnnotation> subAnnotationsByName = new LinkedHashMap<>();

    /** Should we create a forward index for this annotation? */
    private boolean forwardIndex = true;

    /** How to display the field in the interface (optional) */
    private String displayName = "";

    /** How to describe the field in the interface (optional) */
    private String description = "";

    /** What UI element to show in the interface (optional) */
    private String uiType = "";

    /** Can this annotation have multiple values at one token position? [false] */
    private boolean multipleValues = false;

    public ConfigAnnotation() {
    }

    public ConfigAnnotation(String name, String valuePath) {
        this(name, valuePath, null);
    }

    public ConfigAnnotation(String name, String valuePath, String forEachPath) {
        setName(name);
        setValuePath(valuePath);
        setForEachPath(forEachPath);
    }

    public void validate() {
        String t = "annotation";
        ConfigInputFormat.req(name, t, isForEach() ? "namePath" : "name");
        ConfigInputFormat.req(valuePath, t, "valuePath");
        for (ConfigAnnotation s : subAnnotations)
            s.validate();
        for (ConfigProcessStep step : process)
            step.validate();
    }

    public ConfigAnnotation copy() {
        ConfigAnnotation result = new ConfigAnnotation(name, valuePath, forEachPath);
        result.setProcess(process);
        result.setDisplayName(displayName);
        result.setDescription(description);
        result.setSensitivity(sensitivity);
        result.setUiType(uiType);
        result.setBasePath(basePath);
        result.captureValuePaths.addAll(captureValuePaths);
        for (ConfigAnnotation a : subAnnotations) {
            result.addSubAnnotation(a.copy());
        }
        result.setForwardIndex(forwardIndex);
        result.setMultipleValues(multipleValues);
        return result;
    }

    public String getName() {
        return name;
    }

    public String getValuePath() {
        return valuePath;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValuePath(String valuePath) {
        this.valuePath = valuePath;
        if (valuePath.matches("\\d+"))
            valuePathInt = Integer.parseInt(valuePath);
    }

    public boolean isValuePathInteger() {
        return valuePathInt != Integer.MAX_VALUE;
    }

    public int getValuePathInt() {
        return valuePathInt;
    }

    public List<String> getCaptureValuePaths() {
        return Collections.unmodifiableList(captureValuePaths);
    }

    public void addCaptureValuePath(String captureValuePath) {
        captureValuePaths.add(captureValuePath);
    }

    public List<ConfigAnnotation> getSubAnnotations() {
        return Collections.unmodifiableList(subAnnotations);
    }

    public ConfigAnnotation getSubAnnotation(String name) {
        return subAnnotationsByName.get(name);
    }

    public void addSubAnnotation(ConfigAnnotation subAnnotation) {
        subAnnotations.add(subAnnotation);
        if (!subAnnotation.isForEach())
            subAnnotationsByName.put(subAnnotation.getName(), subAnnotation);
    }

    public String getForEachPath() {
        return forEachPath;
    }

    public void setForEachPath(String forEachPath) {
        this.forEachPath = forEachPath;
    }

    public boolean isForEach() {
        return forEachPath != null;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUiType() {
        return uiType;
    }

    public void setUiType(String uiType) {
        this.uiType = uiType;
    }

    public SensitivitySetting getSensitivity() {
        return sensitivity;
    }

    public void setSensitivity(SensitivitySetting sensitivity) {
        this.sensitivity = sensitivity;
    }

    public List<ConfigProcessStep> getProcess() {
        return process;
    }

    public void setProcess(List<ConfigProcessStep> process) {
        this.process.clear();
        this.process.addAll(process);
    }

    public boolean createForwardIndex() {
        return forwardIndex;
    }

    public void setForwardIndex(boolean forwardIndex) {
        this.forwardIndex = forwardIndex;
    }

    public boolean isMultipleValues() {
        return multipleValues;
    }

    public void setMultipleValues(boolean multipleValues) {
        this.multipleValues = multipleValues;
    }

}
