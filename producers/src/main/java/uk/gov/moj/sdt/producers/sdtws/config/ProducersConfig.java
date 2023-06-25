package uk.gov.moj.sdt.producers.sdtws.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.jmx.export.assembler.MethodNameBasedMBeanInfoAssembler;
import org.springframework.jmx.support.RegistrationPolicy;
import uk.gov.moj.sdt.utils.mbeans.SdtMetricsMBean;
import uk.gov.moj.sdt.utils.mbeans.api.ISdtManagementMBean;

@ComponentScan("uk.gov.moj.sdt")
@Configuration
@EnableAutoConfiguration
public class ProducersConfig {

    @Bean
    @Qualifier("sdtProducersManagement")
    public MBeanExporter mBeanExporter(ISdtManagementMBean sdtManagementMBean) {
        MBeanExporter mBeanExporter = new MBeanExporter();
        Map<String, Object> beans = new HashMap<>();
        beans.put("bean:name=sdtProducersManagement", sdtManagementMBean);
        mBeanExporter.setBeans(beans);
        MethodNameBasedMBeanInfoAssembler methodNameBasedMBeanInfoAssembler = new MethodNameBasedMBeanInfoAssembler();
        methodNameBasedMBeanInfoAssembler.setManagedMethods(
            "uncache",
            "setMdbPoolSize",
            "requeueOldIndividualRequests",
            "processDLQRequest"
        );
        mBeanExporter.setAssembler(methodNameBasedMBeanInfoAssembler);
        mBeanExporter.setRegistrationPolicy(RegistrationPolicy.REPLACE_EXISTING);
        return mBeanExporter;
    }

    @Bean
    @Qualifier("sdtProducersMetrics")
    public MBeanExporter mBeanExporterProducerMetrics() {
        MBeanExporter mBeanExporter = new MBeanExporter();
        Map<String, Object> beans = new HashMap<>();
        beans.put("bean:name=sdtProducersMetrics", SdtMetricsMBean.getMetrics());
        mBeanExporter.setBeans(beans);
        MethodNameBasedMBeanInfoAssembler methodNameBasedMBeanInfoAssembler = new MethodNameBasedMBeanInfoAssembler();
        methodNameBasedMBeanInfoAssembler.setManagedMethods(
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
        );
        mBeanExporter.setAssembler(methodNameBasedMBeanInfoAssembler);
        mBeanExporter.setRegistrationPolicy(RegistrationPolicy.REPLACE_EXISTING);
        return mBeanExporter;
    }
}
