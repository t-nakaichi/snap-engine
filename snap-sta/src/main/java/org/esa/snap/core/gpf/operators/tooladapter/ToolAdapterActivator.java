package org.esa.snap.core.gpf.operators.tooladapter;

import org.esa.snap.core.gpf.GPF;
import org.esa.snap.core.gpf.OperatorSpiRegistry;
import org.esa.snap.core.util.SystemUtils;
import org.esa.snap.core.util.io.FileUtils;
import org.esa.snap.runtime.Activator;
import org.esa.snap.runtime.EngineConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.stream.Stream;

/**
 * Created by kraftek on 12/28/2015.
 * <p>
 * TODO: 1. Search for uncompressed adapters
 * TODO: 2. Look for jar adapters that have not been uncompressed
 * TODO: 3. Look for nbm modules that have not been uncompressed
 */
public class ToolAdapterActivator implements Activator {

    @Override
    public void start() {
        HashMap<String, Path> adapterJars = new HashMap<>();
        Path winJarPath = Paths.get(System.getProperty("user.home")).resolve("AppData").resolve("Roaming").resolve("SNAP").resolve("modules");
        Path unixJarPath = EngineConfig.instance().userDir().resolve("system").resolve("modules");
        try {
            collectAdapterJars(winJarPath, adapterJars);
            collectAdapterJars(unixJarPath, adapterJars);
        } catch (IOException e) {
            SystemUtils.LOG.warning("Could not scan directories for tool adapter");
        }

        for (Map.Entry<String, Path> stringFileEntry : adapterJars.entrySet()) {
            try {
                final Path pathFromURI = FileUtils.getPathFromURI(FileUtils.ensureJarURI(stringFileEntry.getValue().toUri()));
                ToolAdapterOpSpi spi = ToolAdapterIO.createOperatorSpifromJar(pathFromURI);
                ToolAdapterRegistry.INSTANCE.registerOperator(spi);
            } catch (Exception e) {
                SystemUtils.LOG.log(Level.WARNING, "Not able to register SPI for " + stringFileEntry.getKey(), e);
            }

        }

        Collection<ToolAdapterOpSpi> spiCollection = ToolAdapterIO.searchAndRegisterAdapters();
        OperatorSpiRegistry spiRegistry = GPF.getDefaultInstance().getOperatorSpiRegistry();
        for (ToolAdapterOpSpi toolAdapterOpSpi : spiCollection) {
            spiRegistry.addOperatorSpi(toolAdapterOpSpi);
        }
    }

    @Override
    public void stop() {

    }

    private void collectAdapterJars(Path fromPath, Map<String, Path> namedAdapterJars) throws IOException {
        if (Files.isDirectory(fromPath)) {
            String descriptionKeyName = "OpenIDE-Module-Short-Description";
            Attributes.Name typeKey = new Attributes.Name("OpenIDE-Module-Type");
            Stream<Path> pathStream = Files.list(fromPath).filter(path -> path.toString().endsWith(".jar"));
            pathStream.forEach(path -> {
                Manifest manifest = null;
                try {
                    JarFile jarFile = new JarFile(path.toFile());
                    manifest = jarFile.getManifest();
                } catch (IOException e) {
                    SystemUtils.LOG.warning("Illegal jar file found: " + path);
                }
                if (manifest != null) {
                    Attributes manifestEntries = manifest.getMainAttributes();
                    if (manifestEntries.containsKey(typeKey) &&
                        "STA".equals(manifestEntries.getValue(typeKey.toString()))) {
                        namedAdapterJars.put(manifestEntries.getValue(descriptionKeyName), path);
                    }
                }
            });
        }
    }

}