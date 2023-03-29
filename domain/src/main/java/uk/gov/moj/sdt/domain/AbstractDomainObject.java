/* Copyrights and Licenses
 *
 * Copyright (c) 2012-2013 by the Ministry of Justice. All rights reserved.
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

package uk.gov.moj.sdt.domain;

import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.proxy.HibernateProxy;
import uk.gov.moj.sdt.domain.api.IDomainObject;
import uk.gov.moj.sdt.utils.mbeans.SdtMetricsMBean;
import uk.gov.moj.sdt.utils.visitor.api.ITree;
import uk.gov.moj.sdt.utils.visitor.api.IVisitable;
import uk.gov.moj.sdt.utils.visitor.api.IVisitor;

/**
 * Abstract class for all domain objects.
 *
 * @author Robin Compston
 */
public abstract class AbstractDomainObject implements IDomainObject, IVisitable {

    /**
     * Constructor for {@link AbstractDomainObject}.
     */
    protected AbstractDomainObject() {
        super();

        // Beware of timing problems when Spring has not finished initialising
        // and has not created the metics bean.
        if (SdtMetricsMBean.getMetrics() != null) {
            SdtMetricsMBean.getMetrics().upDomainObjectsCount();
        }
    }

    @Override
    public void accept(final IVisitor visitor, final ITree tree) {
        // Call any visitor, passing a reference to this class so that it can
        // act on this class.
        visitor.visit(this, tree);
    }

    /**
     * Get the Object class name and hash id. This is used to wrap domain objects in order to prevent recursive toString
     * () calls and should be applied in the toString () method of all domain objects which are associated with other
     * domain objects.
     *
     * @param object whose details are to be returned.
     * @return details of object concerned.
     */
    protected String getHashId(final Object object) {
        if (object instanceof PersistentCollection) {
            return "PersistentCollection";
        } else if (object instanceof HibernateProxy) {
            return "HibernateProxy";
        } else {
            return object == null ? null : object.getClass().getSimpleName() + "@"
                    + Integer.toHexString(object.hashCode());
        }
    }

    @Override
    public Long getId() {
        return null;
    }

    @Override
    public void setId(final Long id) {
    }

    @Override
    public int getVersion() {
        return 0;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AbstractEntity[");
        sb.append("id=" + this.getId());
        sb.append(", version=").append(this.getVersion());
        sb.append("]");
        return sb.toString();
    }
}
