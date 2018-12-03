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
 * Represents TPC differences and contribution to the global TPC for a single module or a package.
 *
 * @version $Id$
 * @since 1.0
 */
public class DiffMetrics
{
    private Double oldTPC;

    private Double newTPC;

    private Double contribution;

    /**
     * @return the old TPC
     */
    public Double getOldTPC()
    {
        return this.oldTPC;
    }

    /**
     * @param oldTPC see {@link #getOldTPC()}
     */
    public void setOldTPC(Double oldTPC)
    {
        this.oldTPC = oldTPC;
    }

    /**
     * @return the new TPC
     */
    public Double getNewTPC()
    {
        return this.newTPC;
    }

    /**
     * @param newTPC see {@link #getNewTPC()}
     */
    public void setNewTPC(Double newTPC)
    {
        this.newTPC = newTPC;
    }

    /**
     * @return the contribution to the global TPC
     */
    public Double getContribution()
    {
        return contribution;
    }

    /**
     * @param contribution see {@link #getContribution()}
     */
    public void setContribution(Double contribution)
    {
        this.contribution = contribution;
    }
}
