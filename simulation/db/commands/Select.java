package simulation.db.commands;

import simulation.db.*;
import java.sql.*;

/** Select command.
 * @author ykk
 */
public class Select
    extends DBTable
    implements SQLCommand
{
    //Members
    /** Debug flag.
     * Default to false.
     */
    public boolean debug = false;
    /** Name of table.
     * Default to null, i.e., no table referenced.
     */
    public DBTable tableName = null;
    /** Select expression.
     */
    public String expr;
    /** Where condition.
     * Default to null, i.e., no condition.
     */
    public Condition condition = null;
    /** Ordering.
     * Default to null, i.e., no ordering.
     */
    public String order = null;
    /** Grouping.
     * Default to null, i..e, no grouping.
     */
    public String group = null;
    /** Result in array of {@link DBRow}.
     */
    public DBRow[] result;

    //Methods
    /** Constructor.
     * @param expr select expression
     * @param table database table
     * @param condition condition to apply
     */
    public Select(String expr, DBTable table, Condition condition)
    {
	this.expr = expr;
	tableName = table;
	this.condition = condition;
    }

    /** Constructor.
     * @param expr select expression
     * @param table database table
     */
    public Select(String expr, DBTable table)
    {
	this.expr = expr;
	tableName = table;
    }

    public String sqlQuery()
    {
	String output = "SELECT "+expr;
	if (tableName != null) output +=  " FROM "+tableName.tableString();
	if (condition != null) output +=  " WHERE "+condition;
	if (order != null) output +=  " ORDER BY "+order;
	if (group != null) output +=  " GROUP BY "+group;

	return output;
    }

    public int queryType()
    {
	return queryCmd;
    }

    public boolean run(Database db)
    {
	if (debug) System.out.println(sqlQuery());
	SQLExecute execute = new SQLExecute(db, this);
	int colCount = execute.rsProc.colCount();
	int rowCount = execute.rsProc.rowCount();

	if (debug) System.out.println("Run error: "+execute.err+
				      "\tResult="+rowCount+" x "+colCount);
	if (rowCount != 0)
	    result = new DBRow[rowCount];
	else
	{
	    result = null;
	    return (execute.err == null);
	}

	//Get result
	try
	{
	    execute.rs.first();
	    for (int i = 0; i < rowCount; i++) 
	    {
		result[i] = new DBRow();
		result[i].rowDef = execute.rsProc.rowDef;
		for (int j = 0; j < colCount ; j++)
		{
		    result[i].add(execute.rs.getObject(j+1));
		}

		if (debug) System.out.println(i+"\t"+result[i]);
		execute.rs.next();
	    }
	} catch (SQLException sqlEx)
	{
	    throw new RuntimeException(this+" encounters SQL exception "
				       +sqlEx);
	}

	execute.close();
	return (execute.err == null);
    }

    public String tableString()
    {
	return "("+sqlQuery()+")";
    }
}
