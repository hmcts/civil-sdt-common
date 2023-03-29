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

package uk.gov.moj.sdt.domain.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.moj.sdt.domain.api.IDomainObject;
import uk.gov.moj.sdt.domain.cache.api.ICacheable;
import uk.gov.moj.sdt.utils.mbeans.api.ISdtManagementMBean;

/**
 * An abstract class to provide a cache/uncache facility to classes that cache data from the database.
 *
 * @author Robin Compston
 */
@Transactional(propagation = Propagation.SUPPORTS)
public abstract class AbstractCacheControl implements ICacheable {
    /**
     * Static logging object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCacheControl.class);

    /**
     * MBean which holds value controlling uncache operation.
     */
    private ISdtManagementMBean managementMBean;

    protected AbstractCacheControl(ISdtManagementMBean managementMBean) {
        this.managementMBean = managementMBean;
    }

    /**
     * Value of the cache count for this {@link AbstractCacheControl} instance need to be uncached.
     */
    private int localCacheResetControl;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)

    @SuppressWarnings("squid:S5852")
    public <D extends IDomainObject> D getValue(final Class<D> domainType,
                                                final String key) {
        // Should cache be discarded?
        if (this.uncacheRequired()) {
            LOGGER.debug("Uncaching {}", this.getClass().getCanonicalName());

            this.uncache();
        }
        return this.getSpecificValue(domainType, key);
    }

    /**
     * Get the implementation specific value from the sub class. This should be responsible for loading the cache if it
     * is not loaded and then returning the value to the caller.
     *
     * @param key key to cached object to be retrieved.
     * @return the Object from the cache.
     */

    /**
     * Gets the value associated with the parameter from the cache.
     *
     * @param <D> of entity to be retrieved.
     * @param domainType   of entity to load.
     * @param key          key to cached object to be retrieved.
     * @return D instance retrieved.
     */
    protected abstract <D extends IDomainObject> D getSpecificValue(
        final Class<D> domainType, final String key);

    /**
     * Load the cache with appropriate source data for this cache object. Each implementing class must load its own
     * specific data. Note that the map itself is an instance variable and will already exist.
     */
    protected abstract void loadCache();

    /**
     * Clears the cache so that fresh values can be loaded from source.
     */
    protected abstract void uncache();

    /**
     * Check whether an uncache is required.
     *
     * @return true - uncache required, false - uncache not required.
     */
    protected final boolean uncacheRequired() {
        if (this.localCacheResetControl < managementMBean.getCacheResetControl()) {
            LOGGER.debug("Uncache required for {}", this.getClass().getCanonicalName());
            // Bring current value up to date and report uncache required.

            this.localCacheResetControl = managementMBean.getCacheResetControl();
            return true;
        }
        return false;
    }

    /**
     * Allow Spring to wire up the bean.
     *
     * @param managementMBean new value of the management bean.
     */
    public void setManagementMBean(final ISdtManagementMBean managementMBean) {
        this.managementMBean = managementMBean;
    }
}
