package nl.inl.blacklab.indexers.preprocess;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.jar.Manifest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class TagPluginDutchTagger implements TagPlugin {
    private static final String PROP_JAR     = "jarPath";
    private static final String PROP_VECTORS = "vectorFile";
    private static final String PROP_MODEL   = "modelFile";
    private static final String PROP_LEXICON = "lexiconFile";

	private static final String VERSION      = "0.2";
    private ClassLoader loader;

    /** The object doing the actual conversion */
    private Object converter = null;

	private Method handleFile;

    @Override
    public void init(ObjectNode config) throws PluginException {

        if (config == null)
            throw new PluginException("This plugin requires configuration");

		File jar = new File(configStr(config, PROP_JAR));
		if (!jar.exists())
			throw new PluginException("Could not find the dutchTagger jar at location " + jar.toString());
		if (!jar.canRead())
			throw new PluginException("Could not read the dutchTagger jar at location " + jar.toString());
        try {
			URL jarUrl = jar.toURI().toURL();
			loader = new URLClassLoader(new URL[]{jarUrl}, null);
			assertVersion(loader);

            Properties base = new Properties();
            base.setProperty("word2vecFile", configStr(config, PROP_VECTORS));
            base.setProperty("taggingModel", configStr(config, PROP_MODEL));
            base.setProperty("lexiconPath", configStr(config, PROP_LEXICON));
            base.setProperty("tokenize", "true");

            Class<?> converterClass = loader.loadClass("nl.namescape.tagging.ImpactTaggerLemmatizerClient");
            Method setProperties = converterClass.getMethod("setProperties", Properties.class);
            handleFile = converterClass.getMethod("handleFile", String.class, String.class);

            converter = converterClass.newInstance();
            setProperties.invoke(converter, base);
        } catch (Exception e) {
            throw new PluginException("Error initializing DutchTaggerLemmatizer plugin", e);
        }
    }

    @Override
    public synchronized void perform(Reader reader, Writer writer) throws PluginException {
        // Set the ContextClassLoader to use the UrlClassLoader we pointed at the OpenConvert jar.
        // This is required because OpenConvert implicitly loads some dependencies through locators/providers (such as its xml transformers)
		// and these locators/providers sometimes prefer to use the ContextClassLoader, which may have been set by a servlet container or the like.
		// If those cases, the contextClassLoader does not have the jar we loaded on its classpath, and so it cannot find the correct classes.
        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(loader);
        try {
            performImpl(reader, writer);
        } finally {
            Thread.currentThread().setContextClassLoader(originalClassLoader);
        }
    }

    private synchronized void performImpl(Reader reader, Writer writer) throws PluginException {
        Path tmpInput = null;
        Path tmpOutput = null;
        try {
            tmpInput = Files.createTempFile("", ".xml");
            tmpOutput = Files.createTempFile("", ".xml");

            final Charset intermediateCharset = Charset.defaultCharset(); // Use this, as the tagger is a little dumb and doesn't allow us to specify a charset
            try (FileOutputStream os = new FileOutputStream(tmpInput.toFile())) {
                IOUtils.copy(reader, os, intermediateCharset);
            }

            handleFile.invoke(converter, tmpInput.toString(), tmpOutput.toString());

            try (FileInputStream fis = new FileInputStream(tmpOutput.toFile())) {
                IOUtils.copy(fis, writer, intermediateCharset);
            }
        } catch (Exception e) {
			throw new PluginException("Could not tag file: " + e.getMessage(), e);
        } finally {
            if (tmpInput != null) FileUtils.deleteQuietly(tmpInput.toFile());
            if (tmpOutput != null) FileUtils.deleteQuietly(tmpOutput.toFile());
        }
    }

    /**
     * Read a value from our config if present.
     *
     * @param config root node of our config object
     * @param nodeName node to read
     * @return the value as a string
     * @throws PluginException on missing key or null value
     */
    private static String configStr(ObjectNode config, String nodeName) throws PluginException {
        JsonNode n = config.get(nodeName);
        if (n == null || n instanceof NullNode)
            throw new PluginException("Missing configuration value " + nodeName);

        return n.asText();
    }

    @Override
    public String getInputFormat() {
        return "tei";
    }

    @Override
    public String getOutputFormatIdentifier() {
        return "tei";
    }

    @Override
    public String getOutputFileName(String inputFileName) {
        return FilenameUtils.removeExtension(inputFileName).concat(".xml");
    }

    @Override
    public String getId() {
        return "DutchTagger";
    }

    @Override
    public String getDisplayName() {
        return "DutchTagger";
    }

    @Override
    public String getDescription() {
        return "";
	}

	/**
	 * Ensure that the maven artifact version matches VERSION
	 *
	 * @param loader
	 * @throws PluginException
	 */
	private static void assertVersion(ClassLoader loader) throws PluginException {
		try (InputStream is = loader.getResourceAsStream("META-INF/MANIFEST.MF")) {
			Manifest manifest = new Manifest(is);
			String version = manifest.getMainAttributes().getValue("Specification-Version");
			if (!version.equals(VERSION))
				throw new PluginException("Mismatched version! Expected " + VERSION + " but found " + version);
		} catch (IOException e) {
			throw new PluginException("Could not read manifest: " + e.getMessage(), e);
		}
    }
}