package org.decta.bpmnparser;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FileReaderImplTest {

    private static final String PATH = "src/test/resources/bpmn";

    @Autowired
    FileReader reader;

    @Test
    void readFile() {
        List<File> files = reader.readFile(PATH);
        assertEquals(6, files.size());
        assertEquals(new File("src/test/resources/bpmn/acquiring/main/AcquiringMain.bpmn"), files.get(0));
        assertEquals(new File("src/test/resources/bpmn/card/suite/report/delivery/CardSuiteReportDelivery.bpmn"), files.get(1));
        assertEquals(new File("src/test/resources/bpmn/common/acquiring.eod/Common_Acquiring_EOD.bpmn"), files.get(2));
        assertEquals(new File("src/test/resources/bpmn/incoming.daily.reports/Incoming_dailyReports.bpmn"), files.get(3));
        assertEquals(new File("src/test/resources/bpmn/mc.api.update.queues/MC_API.UpdateQueues.bpmn"), files.get(4));
        assertEquals(new File("src/test/resources/bpmn/send.bi.processing.notifications/SendBIProcessingNotifications.bpmn"), files.get(5));
    }

    @Test
    void readFileWithWrongPath() {
        List<File> files = reader.readFile("wrong/path");
        assertEquals(0, files.size());
    }
}