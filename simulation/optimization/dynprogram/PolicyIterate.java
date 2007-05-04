package simulation.optimization.dynprogram;

import Jama.*;
import simulation.utilities.structures.*;
import java.util.*;

/** Class to perform dynamic programming policy iteration.
 * Uses Jama from 
 * <A HREF="http://math.nist.gov/javanumerics/jama/">math.nist.gov/javanumerics/jama/</A>.
 * @author ykk
 */
public class PolicyIterate
{
    //Members
    /** Flag for verbosity.
     * Default to true.
     */
    public boolean verbose = true;
    /** Reference to last found policy.
     */
    public Policy lastResult;
    /** Cost of last result.
     */
    public double lastCost;

    //Methods
    /** Find optimal policy, starting from random policy. 
     * @param dp dynamic program reference
     * @return optimal policy
     */
    public Policy optimalPolicy(DynamicProgram dp)
    {
	Policy newPol = new Policy(dp.states);
	newPol.getLast(dp.actions);

	return optimalPolicy(dp, newPol);
    }

    /** Find optimal policy, starting from given policy. 
     * Stores results in {@link #lastResult} and {@link @lastCost}.
     * @param dp dynamic program reference
     * @param iniPolicy initial policy
     * @return optimal policy
     */
    public Policy optimalPolicy(DynamicProgram dp, Policy iniPolicy)
    {
	//Verification
	dp.checkCost();
	dp.checkProb();

	//Grab starting policy
	Policy currPolicy = new Policy(dp.states);
	for (int i = 0; i < iniPolicy.actions.size(); i++)
	    currPolicy.actions.add(iniPolicy.actions.get(i));

	//Policy iteration
	lastResult = new Policy(dp.states);
	lastResult.addNullAct();
	double[] values;
	if (verbose) System.out.println("Start "+currPolicy);
	while (currPolicy.compareTo(lastResult) != 0)
	{
	    if (verbose) System.out.println("Iterating");

	    //Value determination
	    values = valDet(dp, currPolicy);
	    if (verbose) System.out.println("\tValues [v1 v2... g] = "+Array.print(values));

	    //Policy improvement
	    lastResult = currPolicy;
	    lastCost = values[values.length-1];
	    currPolicy = polImprove(dp, currPolicy, values);
	    if (verbose) System.out.println("\tNew "+currPolicy);
	}

	lastResult.getProb(dp);
	return lastResult;
    }

    /** Return values [v(0) v(1) ... v(N-1) g] for policy improvement.
     * @param dp reference to dynamic program
     * @param policy current policy
     * @param values values for current policy
     * @return return improved policy
     */
    protected Policy polImprove(DynamicProgram dp, Policy policy, double[] values)
    {
	Policy newPolicy = new Policy(policy.states);
	Vector vAct;

	//Iterate through states
	for (int i = 0; i < policy.states.size(); i++)
	{
	    State tmpState = (State) policy.states.get(i);

	    double minCost = Double.MAX_VALUE;
	    Action minAction = null;
	    double tmpCost;
	    Action tmpAct;
	    TransitProb tmpProb;
	    vAct = dp.getValidAct(tmpState);
	    for (int j = 0; j < vAct.size(); j++)
	    {
		//Get value for state-action
		tmpAct = (Action) vAct.get(j);
		tmpCost = dp.getCost(tmpState, tmpAct);
		tmpProb = dp.getProb(tmpState, tmpAct);
		for (int k = 0; k < values.length-1; k++)
		{
		    tmpCost += tmpProb.prob[k]*values[k];
		    if (k == i) tmpCost -= values[k];
		}

		//Compare to minimum
		if (minCost > tmpCost)
		{
		    minCost = tmpCost;
		    minAction = tmpAct;
		}
	    }

	    newPolicy.actions.add(minAction);
	}
	
	return newPolicy;
    }

    /** Return values [v(0) v(1) ... v(N-1) g] for policy improvement.
     * @param dp reference to dynamic program
     * @param policy current policy
     * @return return values for policy improvement
     */
    protected double[] valDet(DynamicProgram dp, Policy policy)
    {
	//Form matrices
	double[][] c = new double[dp.states.size()][1];
	double[][] probDistri = new double[dp.states.size()][dp.states.size()];
	TransitProb tProb;
	for (int i = 0; i < dp.states.size(); i++)
	{
	    tProb = dp.getProb((State) dp.states.get(i), (Action) policy.actions.get(i));
	    c[i][0] = -1.0 * dp.getCost((State) dp.states.get(i), (Action) policy.actions.get(i));
	    for (int j = 0; j < dp.states.size(); j++)
		probDistri[i][j] = tProb.prob[j];
 
	    probDistri[i][i] -= 1.0;
	    probDistri[i][dp.states.size()-1] = -1.0;
	}

	Matrix pM = new Matrix(probDistri);
	Matrix cM = new Matrix(c);
	Matrix val = pM.solve(cM);

	return Array.changeDim(val.getArray());
    }   
}