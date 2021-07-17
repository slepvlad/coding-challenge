package org.decta;

import java.io.File;
import java.util.List;

public interface FileReader {

    List<File> readFile(String path);
}
