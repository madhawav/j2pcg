package org.renovelabz.j2pcodegen;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Scanner;

/**
 * Created by madhawa on 8/13/17.
 */

/**
 * Contain utilities used by system
 */
public class Util {

    /**
     * Reads a template file
     * @param c Type to obtain resources
     * @param resourceName name of resource
     * @return
     */
    public static String readTemplate(Class c, String resourceName)
    {
        InputStream stream = c.getResourceAsStream(resourceName);
        Scanner sc = new Scanner(stream);
        String text = sc.useDelimiter("\\A").next();
        sc.close();
        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;
    }

    /**
     * Replace a token in block of text with replaced
     * @param text text domain in which replacement is to be done
     * @param token token to be replaced
     * @param replacement replaced text
     * @return
     */
    public static String replaceBlock(String text, String token, String replacement)
    {
        String newText = "";
        String remainder = text;
        while(true){
            int index = remainder.indexOf(token);
            if(index < 0)
                break;
            // Determine indent level
            String left = remainder.substring(0,index);
            int newLineIndex = left.lastIndexOf("\n");
            int indent = index - newLineIndex - 1;
            String indented = hangingIndent(replacement,indent);
            String right = remainder.substring(index);
            String newRight = right.replaceFirst(token,indented);
            newText += left + newRight.substring(0,indented.length());
            remainder = newRight.substring(indented.length());
            remainder = remainder;
        }
        return newText + remainder;
    }

    /**
     * hanging indent a block of text using given offset
     * @param text
     * @param offset
     * @return
     */
    public static String hangingIndent(String text, int offset)
    {
        String lines[] = text.split("\\r?\\n");
        String output = lines[0];
        for(int i = 1; i < lines.length; i++)
        {
            output += "\n";
            output += StringUtils.repeat(" ",offset);
            output += lines[i];
        }
        return output;
    }

}
