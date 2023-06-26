/* Copyrights and Licenses
 *
 * Copyright (c) 2012-2014 by the Ministry of Justice. All rights reserved.
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
package uk.gov.moj.sdt.interceptors.in;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.moj.sdt.utils.AbstractSdtUnitTestBase;
import uk.gov.moj.sdt.utils.mbeans.SdtMetricsMBean;

import java.lang.reflect.Field;
import java.util.TreeSet;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test class.
 *
 * @author d195274
 */
@ExtendWith(MockitoExtension.class)
class SdtUnmarshallInboundInterceptorTest extends AbstractSdtUnitTestBase {

    SdtUnmarshallInterceptor sdtUnmarshallInterceptor;

    PhaseInterceptorChain phaseInterceptorChain;

    @Mock
    SoapMessage mockSoapMessage;

    @BeforeEach
    @Override
    public void setUp() {
        sdtUnmarshallInterceptor = new SdtUnmarshallInterceptor();
        phaseInterceptorChain = new PhaseInterceptorChain(new TreeSet<>());
        when(mockSoapMessage.getInterceptorChain()).thenReturn(phaseInterceptorChain);
    }

    /**
     * Tests handleMessage and confirms there was a fault in the SoapMessage InterceptorChain
     * @throws Exception
     */
    @Test
    void handleMessage() throws Exception {
        setFaultOccured();

        try (MockedStatic<SdtMetricsMBean> mockSdtMetricsMBean = Mockito.mockStatic(SdtMetricsMBean.class)) {
            SdtMetricsMBean mockMetrics = mock(SdtMetricsMBean.class);
            mockSdtMetricsMBean.when(SdtMetricsMBean::getMetrics).thenReturn(mockMetrics);

            sdtUnmarshallInterceptor.handleMessage(mockSoapMessage);

           verify(mockMetrics).upXmlValidationFailureCount();
        }
    }

    private void setFaultOccured() throws Exception {
        final Field faultOccuredField = PhaseInterceptorChain.class.getDeclaredField("faultOccurred");
        faultOccuredField.setAccessible(true);
        faultOccuredField.set(phaseInterceptorChain, true);
        faultOccuredField.setAccessible(false);
    }
}
