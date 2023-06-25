package uk.gov.moj.sdt.producers.sdtws.config;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.jmx.export.assembler.MBeanInfoAssembler;
import org.springframework.jmx.export.assembler.MethodNameBasedMBeanInfoAssembler;
import org.springframework.jmx.support.RegistrationPolicy;
import uk.gov.moj.sdt.utils.AbstractSdtUnitTestBase;
import uk.gov.moj.sdt.utils.mbeans.api.ISdtManagementMBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ProducersConfigTest extends AbstractSdtUnitTestBase {

    private ProducersConfig producersConfig;

    @Mock
    ISdtManagementMBean mockSdtManagementMBean;

    @BeforeEach
    @Override
    public void setUp() {
        producersConfig = new ProducersConfig();
    }

    @Test
    void testMBeanExporter() {
        MBeanExporter result = producersConfig.mBeanExporter(mockSdtManagementMBean);

        Map<String, Object> resultBeans = (Map<String, Object>) getAccessibleField(
            MBeanExporter.class,
            "beans",
            Map.class,
            result);
        MBeanInfoAssembler resultAssembler = (MBeanInfoAssembler) getAccessibleField(
            MBeanExporter.class,
            "assembler",
            MBeanInfoAssembler.class,
            result);
        Set<String> resultManagedMethods = (Set<String>) getAccessibleField(
            MethodNameBasedMBeanInfoAssembler.class,
            "managedMethods",
            Set.class,
            resultAssembler);
        RegistrationPolicy resultRegistrationPolicy = (RegistrationPolicy) getAccessibleField(
            MBeanExporter.class,
            "registrationPolicy",
            RegistrationPolicy.class,
            result);

        assertEquals(1, resultBeans.size());
        assertTrue(resultBeans.containsKey("bean:name=sdtProducersManagement"), "beans map does not contain entry with expected key");
        assertEquals(mockSdtManagementMBean, resultBeans.get("bean:name=sdtProducersManagement"), "beans map does not contain expected bean");
        assertEquals(4, resultManagedMethods.size());
        HashSet<String> expectedMethods = new HashSet<>();
        expectedMethods.addAll(List.of(
            "uncache",
            "setMdbPoolSize",
            "requeueOldIndividualRequests",
            "processDLQRequest"
        ));
        assertTrue(resultManagedMethods.containsAll(expectedMethods), "Managed methods do not contain all expected methods");
        assertEquals(RegistrationPolicy.REPLACE_EXISTING, resultRegistrationPolicy);
    }

    @Test
    void testMBeanExporterProducerMetrics() {
        MBeanExporter result = producersConfig.mBeanExporterProducerMetrics();

        Map<String, Object> resultBeans = (Map<String, Object>) getAccessibleField(
            MBeanExporter.class,
            "beans",
            Map.class,
            result);
        MBeanInfoAssembler resultAssembler = (MBeanInfoAssembler) getAccessibleField(
            MBeanExporter.class,
            "assembler",
            MBeanInfoAssembler.class,
            result);
        Set<String> resultManagedMethods = (Set<String>) getAccessibleField(
            MethodNameBasedMBeanInfoAssembler.class,
            "managedMethods",
            Set.class,
            resultAssembler);
        RegistrationPolicy resultRegistrationPolicy = (RegistrationPolicy) getAccessibleField(
            MBeanExporter.class,
            "registrationPolicy",
            RegistrationPolicy.class,
            result);

        assertEquals(1, resultBeans.size());
        assertTrue(resultBeans.containsKey("bean:name=sdtProducersMetrics"), "beans map does not contain entry with expected key");
        assertEquals(19, resultManagedMethods.size());
        HashSet<String> expectedMethods = new HashSet<>();
        expectedMethods.addAll(List.of(
            "getTime",
            "getOsStats",
            "getBulkSubmitStats",
            "getBulkFeedbackStats",
            "getSubmitQueryStats",
            "getStatusUpdateStats",
            "getDomainObjectsStats",
            "getDatabaseCallsStats",
            "getDatabaseReadsStats",
            "getDatabaseWritesStats",
            "getActiveCustomersStats",
            "getRequestQueueStats",
            "getTargetAppStats",
            "getErrorStats",
            "getLastRefStats",
            "getPerformanceLoggingString",
            "uncache",
            "reset",
            "dumpMetrics"
        ));
        assertTrue(resultManagedMethods.containsAll(expectedMethods), "Managed methods do not contain all expected methods");
        assertEquals(RegistrationPolicy.REPLACE_EXISTING, resultRegistrationPolicy);
    }
}
