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
 * Represents Clover data for a single package or module.
 *
 * @version $Id$
 * @since 1.0
 */
public class CloverMetrics
{
    private long conditionals;
    private long coveredConditionals;

    private long coveredMethods;
    private long methods;

    private long coveredStatements;
    private long statements;

    private double tpc;

    /**
     * @return the number of conditionals elements in the package or module
     */
    public long getConditionals()
    {
        return this.conditionals;
    }

    /**
     * @param conditionals see {@link #getConditionals()}
     */
    public void setConditionals(long conditionals)
    {
        this.conditionals = conditionals;
    }

    /**
     * @return the number of covered conditionals in the package or module
     */
    public long getCoveredConditionals()
    {
        return this.coveredConditionals;
    }

    /**
     * @param coveredConditionals see {@link #getCoveredConditionals()}
     */
    public void setCoveredConditionals(long coveredConditionals)
    {
        this.coveredConditionals = coveredConditionals;
    }

    /**
     * @return the number of covered methods in the package or module
     */
    public long getCoveredMethods()
    {
        return this.coveredMethods;
    }

    /**
     * @param coveredMethods see {@link #getCoveredMethods()}
     */
    public void setCoveredMethods(long coveredMethods)
    {
        this.coveredMethods = coveredMethods;
    }

    /**
     * @return the number of methods in the package or module
     */
    public long getMethods()
    {
        return this.methods;
    }

    /**
     * @param methods see {@link #getMethods()}
     */
    public void setMethods(long methods)
    {
        this.methods = methods;
    }

    /**
     * @return the number of covered statements in the package or module
     */
    public long getCoveredStatements()
    {
        return this.coveredStatements;
    }

    /**
     * @param coveredStatements see {@link #getCoveredStatements()}
     */
    public void setCoveredStatements(long coveredStatements)
    {
        this.coveredStatements = coveredStatements;
    }

    /**
     * @return the number of statements in the package or module
     */
    public long getStatements()
    {
        return this.statements;
    }

    /**
     * @param statements see {@link #getStatements()}
     */
    public void setStatements(long statements)
    {
        this.statements = statements;
    }

    /**
     * Add a new set of metrics to the current metric.
     *
     * @param metrics the metrics to add
     */
    public void addMetrics(CloverMetrics metrics)
    {
        setStatements(getStatements() + metrics.getStatements());
        setCoveredStatements(getCoveredStatements() + metrics.getCoveredStatements());
        setConditionals(getConditionals() + metrics.getConditionals());
        setCoveredConditionals(getCoveredConditionals() + metrics.getCoveredConditionals());
        setMethods(getMethods() + metrics.getMethods());
        setCoveredMethods(getCoveredMethods() + metrics.getCoveredMethods());
    }

    /**
     * @return the TPC for the current metrics
     */
    public double getTPC()
    {
        return this.tpc;
    }

    /**
     * Computes the TPC using the formula
     * {@code (coveredConditionals + coveredStatements + coveredMethods)/(conditionals + statements + methods) * 100}.
     */
    public void computeTPC()
    {
        double newTPC;
        long elements = getConditionals() + getStatements() + getMethods();
        if (elements == 0) {
            newTPC = 0;
        } else {
            long coveredElements = getCoveredConditionals() + getCoveredStatements() + getCoveredMethods();
            newTPC = ((double) coveredElements / (double) elements) * 100;
        }
        this.tpc = newTPC;
    }

    /**
     * Substracts metrics to the current metrics.
     *
     * @param metrics the metrics to substract
     * @return the new metrics (current metric - passed metrics)
     */
    public CloverMetrics substract(CloverMetrics metrics)
    {
        CloverMetrics newMetrics = new CloverMetrics();
        newMetrics.setCoveredMethods(getCoveredMethods() - metrics.getCoveredMethods());
        newMetrics.setMethods(getMethods() - metrics.getMethods());
        newMetrics.setCoveredConditionals(getCoveredConditionals() - metrics.getCoveredConditionals());
        newMetrics.setConditionals(getConditionals() - metrics.getConditionals());
        newMetrics.setCoveredStatements(getCoveredStatements() - metrics.getCoveredStatements());
        newMetrics.setStatements(getStatements() - metrics.getStatements());
        return newMetrics;
    }
}
