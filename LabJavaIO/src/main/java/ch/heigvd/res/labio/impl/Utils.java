package ch.heigvd.res.labio.impl;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Olivier Liechti
 */
public class Utils {

  private static final Logger LOG = Logger.getLogger(Utils.class.getName());

  /**
   * This method looks for the next new line separators (\r, \n, \r\n) to extract
   * the next line in the string passed in arguments. 
   * 
   * @param lines a string that may contain 0, 1 or more lines
   * @return an array with 2 elements; the first element is the next line with
   * the line separator, the second element is the remaining text. If the argument does not
   * contain any line separator, then the first element is an empty string.
   */
  public static String[] getNextLine(String lines) {
    //throw new UnsupportedOperationException("The student has not implemented this method yet.");

    Pattern pattern = Pattern.compile("(\r\n)|(\n)|(\r)");
    Matcher matcher = pattern.matcher(lines);

    String[] linesTab = new String[]{"", lines};

    int indexBegin = 0;
    int indexTab = 0;
    int matchCount = 0;
    int countIndex = 0;

    // Counting matching pattern in lines
    while(matcher.find(countIndex)){
      matchCount++;
      countIndex = matcher.start() + 1;
    }

    // if there is at least one newline character
    if(matchCount > 0) {
      for (int i = 0; i < lines.length(); ++i) {
        if (lines.charAt(i) == '\r' && lines.indexOf("\r\n") > 0) { // Windows
          i += 2;
          linesTab[indexTab] = lines.substring(indexBegin, i);
          linesTab[++indexTab] = lines.substring(i);
          break;
        }
        if (lines.charAt(i) == '\r' || lines.charAt(i) == '\n') { // MacOS or Linux
          linesTab[indexTab] = lines.substring(indexBegin, ++i);
          linesTab[++indexTab] = lines.substring(i);
          break;
        }

      }
    }

    return linesTab;
  }

}
