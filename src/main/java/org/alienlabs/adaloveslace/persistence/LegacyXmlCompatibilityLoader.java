package org.alienlabs.adaloveslace.persistence;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.alienlabs.adaloveslace.domain.Diagram;
import org.alienlabs.adaloveslace.domain.Knot;
import org.alienlabs.adaloveslace.domain.Step;
import org.alienlabs.adaloveslace.util.FileUtil;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.alienlabs.adaloveslace.App.PATTERNS_DIRECTORY_NAME;

/**
 * Isolated compatibility loader for legacy JAXB/XML-based save.xml descriptors.
 */
public final class LegacyXmlCompatibilityLoader {

    private LegacyXmlCompatibilityLoader() {
        // Utility
    }

    public static Diagram loadDiagram(ZipFile zipFile, ZipEntry xmlEntry) throws IOException, JAXBException {
        try (InputStream in = zipFile.getInputStream(xmlEntry)) {
            JAXBContext context = JAXBContext.newInstance(Diagram.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            Diagram diagram = (Diagram) unmarshaller.unmarshal(in);
            postProcess(diagram);
            return diagram;
        }
    }

    private static void postProcess(Diagram diagram) {
        // Rebuild absolute filenames for patterns and knots, like the legacy path.
        diagram.getPatterns().forEach(p ->
                p.setAbsoluteFilename(FileUtil.APP_FOLDER_IN_USER_HOME + PATTERNS_DIRECTORY_NAME + java.io.File.separator + p.getFilename()));

        for (Step step : diagram.getAllSteps()) {
            loadPatterns(step.getDisplayedKnots());
            loadPatterns(step.getSelectedKnots());
        }
    }

    private static void loadPatterns(List<Knot> knots) {
        for (Knot k : knots) {
            if (k.getPattern().isPresent()) {
                k.getPattern().get().setAbsoluteFilename(
                        FileUtil.APP_FOLDER_IN_USER_HOME + PATTERNS_DIRECTORY_NAME + java.io.File.separator +
                                k.getPattern().get().getFilename()
                );
            }
        }
    }
}

