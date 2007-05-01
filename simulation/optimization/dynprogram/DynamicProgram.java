package simulation.optimization.dynprogram;

import java.util.*;

/** Definition dynamic program.
 * @author ykk
 */
public class DynamicProgram
{
    //Members
    /** Vector of state.
     */
    public Vector states = new Vector();
    /** Vector of actions.
     */
    public Vector actions = new Vector();
    /** Array of cost.
     */
    public double[][] costs;
    /** Array of transition probabilities.
     */
    public TransitProb[][] transitProb;

    //Methods
    /** Initiate transition probability array.
     * Default all entries to null.
     */
    public void initiateProb()
    {
	transitProb = new TransitProb[actions.size()][states.size()];

	for (int i = 0 ; i < actions.size(); i++)
	    for (int j = 0; j < states.size(); j++)
		transitProb[i][j] = null;
    }

    /** Check transition probabilities, verify all valid action-state pairs has one.
     */
    public void checkProb()
    {
	Action act;
	for (int i = 0; i < actions.size(); i++)
	{
	    act = (Action) actions.get(i);
	    for (int j = 0; j < act.validStates.size(); j++)
		if (transitProb[i][states.indexOf(act.validStates.get(j))] == null)
		    throw new RuntimeException("Action-state pair\n\t("+act+","+
					       act.validStates.get(j)+
					       ")\n\trequires a transition probability.");
	}
    }

    /** Add transition prob to all valid states of given action.
     * @param action reference to action
     * @param prob transmit probability associated
     */
    public void addProb(Action action, TransitProb prob)
    {
	for (int i = 0; i < action.validStates.size(); i++)
	    transitProb[actions.indexOf(action)][states.indexOf(action.validStates.get(i))] = prob;
    }

    /** Initiate cost array.
     * Default all entries to Double.MAX_VALUE.
     */
    public void initiateCost()
    {
	costs = new double[actions.size()][states.size()];

	for (int i = 0 ; i < actions.size(); i++)
	    for (int j = 0; j < states.size(); j++)
		costs[i][j] = Double.MAX_VALUE;
    }

    /** Check cost, verify all valid action-state pairs has a cost.
     */
    public void checkCost()
    {
	Action act;
	for (int i = 0; i < actions.size(); i++)
	{
	    act = (Action) actions.get(i);
	    for (int j = 0; j < act.validStates.size(); j++)
		if (costs[i][states.indexOf(act.validStates.get(j))] == Double.MAX_VALUE)
		    throw new RuntimeException("Action-state pair\n\t("+act+","+
					       act.validStates.get(j)+")\n\trequires a cost.");
	}
    }

    /** Add cost to all valid states of given action.
     * @param action reference to action
     * @param cost cost associated
     */
    public void addCost(Action action, double cost)
    {
	for (int i = 0; i < action.validStates.size(); i++)
	    costs[actions.indexOf(action)][states.indexOf(action.validStates.get(i))] = cost;
    }

    /** Add cost.
     * @param state reference to state
     * @param action reference to action
     * @param cost cost associated
     */
    public void addCost(State state, Action action, double cost)
    {
	costs[actions.indexOf(action)][states.indexOf(states)] = cost;
    }

    /** String representation.
     * @return string describing program
     */
    public String toString()
    {
	String result = super.toString();

	result += "\nStates:\n\t"+states;

	Action act;
	result += "\nActions:";
	for (int i = 0; i < actions.size(); i++)
	{
	    act = (Action) actions.get(i);
	    result += "\n\t"+act.name;
	    for (int j = 0; j < act.validStates.size(); j++)
		result+= "\n\t\t"+act.validStates.get(j)+"\t"+
		    costs[i][states.indexOf(act.validStates.get(j))]+
		    "\t"+transitProb[i][states.indexOf(act.validStates.get(j))];
	}

	return result;
    }
}