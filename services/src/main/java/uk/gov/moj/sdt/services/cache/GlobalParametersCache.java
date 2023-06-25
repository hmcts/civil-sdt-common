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

package uk.gov.moj.sdt.services.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.moj.sdt.dao.api.IGenericDao;
import uk.gov.moj.sdt.domain.GlobalParameter;
import uk.gov.moj.sdt.domain.api.IDomainObject;
import uk.gov.moj.sdt.domain.api.IGlobalParameter;
import uk.gov.moj.sdt.domain.cache.AbstractCacheControl;
import uk.gov.moj.sdt.services.cache.api.IGlobalParametersCache;
import uk.gov.moj.sdt.utils.mbeans.api.ISdtManagementMBean;

import java.util.HashMap;
import java.util.Map;

/**
 * Cache bean for the Global parameters.
 *
 * @author Manoj Kulkarni/Robin Compston
 */
@Transactional(propagation = Propagation.SUPPORTS)
@Component("GlobalParametersCache")
public class GlobalParametersCache extends AbstractCacheControl implements IGlobalParametersCache {
    /**
     * Logger object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalParametersCache.class);

    /**
     * DAO to retrieve error messages.
     */
    private IGenericDao genericDao;

    /**
     * The cache variable that holds the global parameters as a key-value pair for this singleton.
     */
    private Map<String, IGlobalParameter> globalParameters = new HashMap<String, IGlobalParameter>();

    @Autowired
    public GlobalParametersCache(@Qualifier("SdtManagementMBean") ISdtManagementMBean managementMBean,
                              @Qualifier("GlobalParametersDao") IGenericDao genericDao) {
        super(managementMBean);
        this.genericDao = genericDao;
    }

    /**
     * Get the map holding cached values.
     *
     * @return the map holding cached values.
     */
    protected Map<String, IGlobalParameter> getGlobalParameters() {
        return globalParameters;
    }

    @Override
    protected <D extends IDomainObject> D getSpecificValue(final Class<D> domainType,
                                                           final String paramName) {
        // Assume map is uninitialised if empty.
        if (this.getGlobalParameters().isEmpty()) {
            loadCache();
        }

        LOGGER.debug("Retrieving global parameter with key [" + paramName + "]");

        // Get the value of the named parameter.
        final Object someObject = this.getGlobalParameters().get(paramName);

        D domainObject = null;

        if (someObject == null) {
            LOGGER.warn("Parameter with name [" + paramName + "] not found.");
            return null;
        }

        // Double check that the expected class matches the retrieved class.
        if (domainType.isAssignableFrom(someObject.getClass())) {
            // Prepare object of correct type to return to caller.
            domainObject = domainType.cast(someObject);
        } else {
            // Unsupported entity type.
            throw new UnsupportedOperationException("Expected class [" + domainType.getCanonicalName() +
                    "] does not match retrieved class [" + someObject.getClass().getCanonicalName() + "].");
        }

        return domainObject;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    protected void loadCache() {
        // This object should be a singleton but play safe and only let one instance at a time refresh the cache.
        synchronized (this.getGlobalParameters()) {
            // Assume map is uninitialised if empty.
            if (this.getGlobalParameters().isEmpty()) {
                LOGGER.info("Loading global parameters into cache.");

                // Retrieve all rows from global parameters table.
                final IGlobalParameter[] result = genericDao.query(GlobalParameter.class);

                for (IGlobalParameter globalParameter : result) {
                    // Add all retrieved parameters to a map, keyed by the global parameter name.
                    this.getGlobalParameters().put(globalParameter.getName(), globalParameter);
                }
            }
        }
    }

    @Override
    protected void uncache() {
        // This object should be a singleton but play safe and only let one instance at a time refresh the cache.
        synchronized (this.getGlobalParameters()) {
            // Clear map but do not destroy it.
            this.getGlobalParameters().clear();

            LOGGER.info("Uncaching global parammeters.");
        }
    }

    /**
     * Setter for generic DAO.
     *
     * @param genericDao the genericDao to set.
     */
    public void setGenericDao(final IGenericDao genericDao) {
        this.genericDao = genericDao;
    }
}
