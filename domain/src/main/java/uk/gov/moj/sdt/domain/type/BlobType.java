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

package uk.gov.moj.sdt.domain.type;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Mapping type for mapping sql BLOB type to user defined type for Hibernate.
 *
 * @author Manoj Kulkarni
 */
public class BlobType implements UserType {

    @Override
    public int[] sqlTypes() {
        return new int[]{Types.BLOB};
    }

    /**
     * Returns the class that this Blob is converted to.
     *
     * @return class
     */
    @Override
    @SuppressWarnings("rawtypes")
    public Class returnedClass() {
        return String.class;
    }

    @Override
    public boolean equals(final Object x, final Object y) throws HibernateException {
        if (x == null && y == null) {
            return true;
        }

        if (x == null || y == null) {
            return false;
        }

        if (!(x instanceof String) || !(y instanceof String)) {
            return false;
        }

        return ((String) x).equals(y);
    }

    @Override
    public int hashCode(final Object x) throws HibernateException {
        return x.hashCode();
    }

    @Override
    public Object nullSafeGet(final ResultSet rs, final String[] names,
                              final SharedSessionContractImplementor sharedSessionContractImplementor, Object o)
            throws HibernateException, SQLException {
        // First we get the byte array
        final byte[] byteStream = rs.getBytes(names[0]);
        if (byteStream == null) {
            return null;
        }
        return new String(byteStream);
    }

    @Override
    public void nullSafeSet(final PreparedStatement st, final Object value, final int index,
                            final SharedSessionContractImplementor sharedSessionContractImplementor)
            throws HibernateException, SQLException {
        final Object val = value == null ? "" : value;
        final String inValue = (String) val;
        st.setBytes(index, inValue.getBytes());
    }

    @Override
    public Object deepCopy(final Object value) throws HibernateException {
        if (value == null) {
            return null;
        }
        final String in = (String) value;
        final int len = in.length();
        final char[] buf = new char[len];

        for (int i = 0; i < len; i++) {
            buf[i] = in.charAt(i);
        }
        return new String(buf);
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(final Object value) throws HibernateException {
        return (String) value;
    }

    @Override
    public Object assemble(final Serializable cached, final Object owner) throws HibernateException {
        return this.deepCopy(cached);
    }

    @Override
    public Object replace(final Object original, final Object target, final Object owner) throws HibernateException {
        return this.deepCopy(original);
    }

}
