/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.clover.internal;

/**
 * Converts a {@code DiffDataSet} into a serialized version (HTML, wiki markup, etc).
 *
 * @version $Id$
 * @since 1.0
 */
public interface DiffMetricsSerializer
{
    /**
     * @param diffDataSet the TPC differences
     * @param oldDataSet the first report being compared
     * @param newDataSet the second newer report being reported
     * @param oldReportId the id to use for the first report in the serialization (used in titles, file names, etc)
     * @param newReportId the id to use for the second report in the serialization (used in titles, file names, etc)
     * @return the serialized data as a String
     */
    String serialize(DiffDataSet diffDataSet, XMLDataSet oldDataSet, XMLDataSet newDataSet, String oldReportId,
        String newReportId);
}
