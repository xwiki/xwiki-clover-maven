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

public class CloverMetrics
{
    private long conditionals;
    private long coveredConditionals;

    private long coveredMethods;
    private long methods;

    private long coveredStatements;
    private long statements;

    private double tpc;

    public long getConditionals()
    {
        return this.conditionals;
    }

    public void setConditionals(long conditionals)
    {
        this.conditionals = conditionals;
    }

    public long getCoveredConditionals()
    {
        return this.coveredConditionals;
    }

    public void setCoveredConditionals(long coveredConditionals)
    {
        this.coveredConditionals = coveredConditionals;
    }

    public long getCoveredMethods()
    {
        return this.coveredMethods;
    }

    public void setCoveredMethods(long coveredMethods)
    {
        this.coveredMethods = coveredMethods;
    }

    public long getMethods()
    {
        return this.methods;
    }

    public void setMethods(long methods)
    {
        this.methods = methods;
    }

    public long getCoveredStatements()
    {
        return this.coveredStatements;
    }

    public void setCoveredStatements(long coveredStatements)
    {
        this.coveredStatements = coveredStatements;
    }

    public long getStatements()
    {
        return this.statements;
    }

    public void setStatements(long statements)
    {
        this.statements = statements;
    }

    public void addMetrics(CloverMetrics metrics)
    {
        setStatements(getStatements() + metrics.getStatements());
        setCoveredStatements(getCoveredStatements() + metrics.getCoveredStatements());
        setConditionals(getConditionals() + metrics.getConditionals());
        setCoveredConditionals(getCoveredConditionals() + metrics.getCoveredConditionals());
        setMethods(getMethods() + metrics.getMethods());
        setCoveredMethods(getCoveredMethods() + metrics.getCoveredMethods());
    }

    public double getTPC()
    {
        return this.tpc;
    }

    public void computeTPC()
    {
        double tpc;
        long elements = getConditionals() + getStatements() + getMethods();
        if (elements == 0) {
            tpc = 0;
        } else {
            long coveredElements = getCoveredConditionals() + getCoveredStatements() + getCoveredMethods();
            tpc = ((double)coveredElements/(double)elements) * 100;
        }
        this.tpc = tpc;
    }

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
