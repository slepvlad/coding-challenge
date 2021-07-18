package org.decta.bpmnparser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class FileReaderImpl implements FileReader {

    private final Logger logger = LoggerFactory.getLogger(FileReaderImpl.class);

    private static final String EMPTY_STRING = "";
    private static final String BPMN_EXTENSION = "bpmn";

    @Override
    public List<File> readFile(String path) {
        logger.info("Start reading files");
        List<File> bpmnFiles = new ArrayList<>();
        File file = new File(path);
        readFile(bpmnFiles, file);
        logger.info("Found {} bpmn files: {}", bpmnFiles.size(), bpmnFiles);
        return bpmnFiles;
    }

    private void readFile(List<File> bpmnFiles, File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File value : files) {
                    readFile(bpmnFiles, value);
                }
            }
        } else {
            if (BPMN_EXTENSION.equals(getExtension(file.getName()))) {
                bpmnFiles.add(file);
            }
        }
    }

    private String getExtension(String filename) {
        if (Objects.isNull(filename) || filename.isEmpty()) {
            return EMPTY_STRING;
        }
        if (!filename.contains(".")) {
            return EMPTY_STRING;
        }

        String[] fileNames = filename.split("\\.");

        if (filename.length() < 2) {
            return EMPTY_STRING;
        }

        return fileNames[fileNames.length - 1];
    }
}
