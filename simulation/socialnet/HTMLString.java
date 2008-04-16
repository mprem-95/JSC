package simulation.socialnet;

import java.util.*;
import java.util.regex.*;

/** Class representing a HTML string.
 * @author ykk
 */

public class HTMLString
    extends RegexString
{
    //Methods
    /** Constructor.
     */
    public HTMLString()
    {
    }

    /** Constructor.
     * @param content content
     */
    public HTMLString(String content)
    {
	super(content);
    }

    /** Parse nonbreaking spaces.
     */
    public void parseNBSpace()
    {
	replace("&nbsp"," ");
    }

    /** Parse away HTML tags.
     */
    public void parseHTML()
    {
	remove("\\<.*?>");
    }
}