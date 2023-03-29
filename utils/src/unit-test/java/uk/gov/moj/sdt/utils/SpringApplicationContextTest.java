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
package uk.gov.moj.sdt.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class SpringApplicationContextTest extends AbstractSdtUnitTestBase
{
    ApplicationContext applicationContext;

    SpringApplicationContext springApplicationContext;

    @BeforeEach
    @Override
    public void setUp() {
        applicationContext = Mockito.spy(ApplicationContext.class);
        springApplicationContext = Mockito.spy(SpringApplicationContext.class);
        springApplicationContext.setApplicationContext(applicationContext);
    }


    @Test
    void setApplicationContextTest(){

        //given

        //when
        springApplicationContext.setApplicationContext(applicationContext);
        //then
        assertNotNull(springApplicationContext.getContext());

    }


    @Test
    void testGetBeanByName() {
        String beanName = "testBean";
        Object expectedBean = new Object();

        try (MockedStatic<SpringApplicationContext> springApplicationContextMock = Mockito.mockStatic(
            SpringApplicationContext.class)) {
            springApplicationContextMock.when(SpringApplicationContext::getContext).thenReturn(
                applicationContext);
        }

        when(springApplicationContext.getBean(beanName)).thenReturn(expectedBean);

        Object bean = SpringApplicationContext.getBean(beanName);

        assertEquals(expectedBean, bean);
    }

    @Test
    void testGetBeanByType() {
        Class<Object> requiredType = Object.class;
        Object expectedBean = new Object();
        try (MockedStatic<SpringApplicationContext> springApplicationContextMock = Mockito.mockStatic(
            SpringApplicationContext.class)) {
            springApplicationContextMock.when(SpringApplicationContext::getContext).thenReturn(
                applicationContext);
        }
        when(SpringApplicationContext.getBean(requiredType)).thenReturn(expectedBean);

        Object bean = SpringApplicationContext.getBean(requiredType);

        assertEquals(expectedBean, bean);
    }
}
