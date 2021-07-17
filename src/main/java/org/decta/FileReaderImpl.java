package org.decta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FileReaderImpl implements FileReader{

    private static final String EMPTY_STRING = "";
    private static final String BPMN_EXTENSION = "bpmn";

    @Override
    public List<File> readFile(String path) {
        List<File> bpmnFiles = new ArrayList<>();
        File file = new File(path);
        readFile(bpmnFiles, file);
        return bpmnFiles;
    }

    private void readFile(List<File> bpmnFiles, File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            //ToDo fix this ugly
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
