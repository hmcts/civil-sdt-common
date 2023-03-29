/* Copyrights and Licenses
 *
 * Copyright (c) 2013 by the Ministry of Justice. All rights reserved.
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 * - Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials
 * provided with the distribution.
 * - All advertising materials mentioning features or use of this software must display the
 * following acknowledgment: "This product includes Money Claims OnLine."
 * - Products derived from this software may not be called "Money Claims OnLine" nor may
 * "Money Claims OnLine" appear in their names without prior written permission of the
 * Ministry of Justice.
 * - Redistributions of any form whatsoever must retain the following acknowledgment: "This
 * product includes Money Claims OnLine."
 * This software is provided "as is" and any expressed or implied warranties, including, but
 * not limited to, the implied warranties of merchantability and fitness for a particular purpose are
 * disclaimed. In no event shall the Ministry of Justice or its contributors be liable for any
 * direct, indirect, incidental, special, exemplary, or consequential damages (including, but
 * not limited to, procurement of substitute goods or services; loss of use, data, or profits;
 * or business interruption). However caused any on any theory of liability, whether in contract,
 * strict liability, or tort (including negligence or otherwise) arising in any way out of the use of this
 * software, even if advised of the possibility of such damage.
 *
 * $Id: $
 * $LastChangedRevision: $
 * $LastChangedDate: $
 * $LastChangedBy: $ */
package uk.gov.moj.sdt.utils.mbeans;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.moj.sdt.utils.mbeans.api.ICustomerCounter;
import uk.gov.moj.sdt.utils.mbeans.api.ISdtMetricsMBean;

import java.lang.management.ManagementFactory;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class SdtMetricsMBeansTest{

    static final String OBJECT_SHOULD_BE_CREATED = "Object should be created";
    @Mock
    ICustomerCounter customerCounterMock;

    ISdtMetricsMBean sdtMetricsMBean;

    ManagementFactory managementFactoryMock;

    @BeforeEach
    public void setUpLocalTests() {
         sdtMetricsMBean = new SdtMetricsMBean();
         managementFactoryMock = Mockito.spy(ManagementFactory.class);

    }

    @Test
    void sdtMBeanCreateTest(){
        //given/when
        sdtMetricsMBean = new SdtMetricsMBean();
        //then
        assertNotNull(sdtMetricsMBean,OBJECT_SHOULD_BE_CREATED);
    }

    @Test
    void testReset() {
        //Given
        sdtMetricsMBean = Mockito.spy(SdtMetricsMBean.class);
        sdtMetricsMBean.upRequestQueueLength();
        sdtMetricsMBean.upDomainObjectsCount();
        //When
        sdtMetricsMBean.reset();
        sdtMetricsMBean.dumpMetrics();
        sdtMetricsMBean.upTargetAppMiscErrors();
        sdtMetricsMBean.upXmlValidationFailureCount();
        sdtMetricsMBean.decrementRequestQueueLength();
        sdtMetricsMBean.upActiveBulkCustomers();
        sdtMetricsMBean.upBulkFeedbackCount();
        sdtMetricsMBean.addBulkFeedbackTime(10000000L);
        sdtMetricsMBean.addDatabaseWritesTime(1000000L);
        sdtMetricsMBean.addDatabaseReadsTime(1000000L);
        sdtMetricsMBean.upDatabaseReadsCount();
        sdtMetricsMBean.downDomainObjectsCount();
        sdtMetricsMBean.upDatabaseWritesCount();
        sdtMetricsMBean.setPerformanceLoggingFlags((short)3);
        sdtMetricsMBean.upBulkSubmitCount();
        sdtMetricsMBean.addSubmitQueryTime(1000000L);
        sdtMetricsMBean.upSubmitQueryCount();
        sdtMetricsMBean.addStatusUpdateTime(1000000L);
        sdtMetricsMBean.upStatusUpdateCount();
        sdtMetricsMBean.addBulkSubmitTime(1000000L);
        sdtMetricsMBean.addRequestQueueTime(1000000L);


        //Then
        verify(sdtMetricsMBean).reset();
        verify(sdtMetricsMBean).dumpMetrics();
        verify(sdtMetricsMBean).upTargetAppMiscErrors();
        verify(sdtMetricsMBean).upXmlValidationFailureCount();
        verify(sdtMetricsMBean).decrementRequestQueueLength();
        verify(sdtMetricsMBean).upActiveBulkCustomers();
        verify(sdtMetricsMBean).upBulkFeedbackCount();
        verify(sdtMetricsMBean).addBulkFeedbackTime(10000000L);
        verify(sdtMetricsMBean).addDatabaseWritesTime(1000000L);
        verify(sdtMetricsMBean).addDatabaseReadsTime(1000000L);
        verify(sdtMetricsMBean).upDatabaseReadsCount();
        verify(sdtMetricsMBean).downDomainObjectsCount();
        verify(sdtMetricsMBean).upDatabaseWritesCount();
        verify(sdtMetricsMBean).setPerformanceLoggingFlags((short)3);
        verify(sdtMetricsMBean).upBulkSubmitCount();
        verify(sdtMetricsMBean).addSubmitQueryTime(1000000L);
        verify(sdtMetricsMBean).upSubmitQueryCount();
        verify(sdtMetricsMBean).addStatusUpdateTime(1000000L);
        verify(sdtMetricsMBean).upStatusUpdateCount();
        verify(sdtMetricsMBean).addSubmitQueryTime(1000000L);
        verify(sdtMetricsMBean).addBulkSubmitTime(1000000L);
        verify(sdtMetricsMBean).addRequestQueueTime(1000000L);

    }


    @Test
    void getMetricsTest(){

        //given //when
        SdtMetricsMBean newSdtMetricsBean = SdtMetricsMBean.getMetrics();

        //Then
        assertNotNull(newSdtMetricsBean);
    }

    @Test
    void getTimeTest(){
         //given
         sdtMetricsMBean = new SdtMetricsMBean();
          managementFactoryMock = Mockito.spy(ManagementFactory.class);
         //when
        String actual = sdtMetricsMBean.getOsStats();
        //then
        assertFalse(actual.isEmpty());

    }

    @Test
    void getCustomerCounterTest(){
        SdtMetricsMBean sdtMetricsMBean1 = Mockito.mock(SdtMetricsMBean.class);
        //when
        sdtMetricsMBean1.setCustomerCounter(customerCounterMock);
        //then
        verify(sdtMetricsMBean1).setCustomerCounter(customerCounterMock);
    }

    @Test
    void upRequestRequeuesTest(){
        //given
        SdtMetricsMBean sdtMetricsMBean1 = Mockito.mock(SdtMetricsMBean.class);
        //when
        sdtMetricsMBean1.upRequestRequeues();
        //then
        verify(sdtMetricsMBean1).upRequestRequeues();
    }
}
