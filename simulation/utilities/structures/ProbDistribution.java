package simulation.utilities.structures;

import java.util.*;

/** Class to hold distribution.
 * @author ykk
 */
public class ProbDistribution
    extends Vector
{
    //Members 
    /** Number of bins
     */
    public int binNumber;
    /** Minimum value of bin values. 
     */
    public double binMin;
    /** Size of bins.
     */
    public double binSize;


    //Members
    /** Constructor.
     * @param binNumber number of bins
     * @param binMin minimum value of all bins
     * @param binSize size of each bin
     */
    public ProbDistribution(int binNumber, double binMin, double binSize)
    {
	this.binNumber = binNumber;
	this.binMin = binMin;
	this.binSize = binSize;
	for (int i = 0; i < binNumber; i++)
	    this.add(new Integer(0));
    }

    /** Constructor.
     * @param min minimum of sample values
     * @param max maximum of sample values
     * @param binNumber number of bins
     */
    public ProbDistribution(double min, double max, int binNumber)
    {
	this(binNumber, min,(max-min)/((double) binNumber));
    }

    /** Return maximum value accommodated in bins.
     * @return maximum value
     */
    public double max()
    {
	return binMin+(binSize*binNumber);
    }

    /** Get bin index.
     * Return -1 if out of range.
     * @param sampleValue value of sample
     * @return index of bin that sample belongs to
     */
    public int binIndex(double sampleValue)
    {
	if ((sampleValue < binMin) || sampleValue > max())
	    return -1;
	else
	    return (int) Math.floor((sampleValue-binMin)/binSize);
    }

    /** Read bin value.
     * Returns -1 if bin is out of range
     * @param binIndex index of bin to read
     * @return value of bin
     */
    public int readBin(int binIndex)
    {
	if ((binIndex < 0) || (binIndex >= this.size()))
	    return -1;
	else
	    return ((Integer) this.get(binIndex)).intValue();
    }

    /** Write bin with new value.
     * @param binIndex index of bin to overwrite
     * @param value to overwrite bin value with
     * @return if successful
     */
    public boolean writeBin(int binIndex, int value)
    {
	if ((binIndex < 0) || (binIndex >= this.size()))
	    return false;
	else
        {
	    this.remove(binIndex);
	    this.add(binIndex, new Integer(value));
	    return true;
	}
    }

    /** String representation of distribution.
     * @return probability mass function of distribution
     */
    public String toString()
    {
	String output = "#Bin Min\tBin Max\tBin Count";
	for (int i = 0; i < this.size(); i++)
	    output += "\n"+
		(binMin+i*binSize)+"\t"+
		(binMin+(i+1)*binSize)+"\t"+
		this.readBin(i);
	return output;
    }
}