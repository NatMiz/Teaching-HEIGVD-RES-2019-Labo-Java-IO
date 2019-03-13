package ch.heigvd.res.labio.impl.filters;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class transforms the streams of character sent to the decorated writer.
 * When filter encounters a line separator, it sends it to the decorated writer.
 * It then sends the line number and a tab character, before resuming the write
 * process.
 *
 * Hello\n\World -> 1\Hello\n2\tWorld
 *
 * @author Olivier Liechti
 */
public class FileNumberingFilterWriter extends FilterWriter {

  private static final Logger LOG = Logger.getLogger(FileNumberingFilterWriter.class.getName());

  // Indicate whether the line we are working on is the first or not
  private boolean lineBefore = false;

  // Indicate that the last char was a '\r'
  private boolean winNewLineChar = false;

  private int counter = 1;

  private char tab = '\t';

  public FileNumberingFilterWriter(Writer out) {
    super(out);
  }

  @Override
  public void write(String str, int off, int len) throws IOException {

    Pattern pattern = Pattern.compile("(\r\n|\r|\n)");
    Matcher matcher = pattern.matcher(str);

    StringBuffer strBldr = new StringBuffer();

    // If only a part of the string is required
    if(str.length() > len){
      strBldr.append(str.substring(off, off + len));
    }else{
      strBldr.append(str);
    }

    if(!lineBefore) {
      // Adding line number and tab at the beginning of the string
      strBldr.insert(0, counter);
      // https://stackoverflow.com/questions/1306727/way-to-get-number-of-digits-in-an-int
      strBldr.insert(String.valueOf(counter).length(), tab);
    }

    // Further transform the string only if there a newline character in it
    if(matcher.groupCount() > 0){
      for(int i = off; i < strBldr.length(); i++){
        if(strBldr.charAt(i) == '\r' && strBldr.indexOf("\r\n") > 0){ // Windows
          i+=2;
          // Adding line number and tab at the beginning of a new line
          strBldr.insert(i, ++counter);
          strBldr.insert(i + String.valueOf(counter).length(), tab);
        }

        if(strBldr.charAt(i) == '\r' || strBldr.charAt(i) == '\n'){ // MacOS or Linux
          strBldr.insert(++i, ++counter);
          strBldr.insert(i + String.valueOf(counter).length(), tab);
        }
      }
    }

    lineBefore = true;
    // Sending new string to parent
    super.write(strBldr.toString(), 0, strBldr.length());

  }

  @Override
  public void write(char[] cbuf, int off, int len) throws IOException {

    for(int i = off; i < len; ++i){
      this.write((int)cbuf[i]);
    }
  }

  @Override
  public void write(int c) throws IOException {

    String str = new String();

    char character = (char) c;

    if (!lineBefore || winNewLineChar) {
      if(!lineBefore)
        lineBefore = true;

      if (winNewLineChar) {
        winNewLineChar = false;
        if(character == '\n'){ // Windows
          str += character + String.valueOf(counter) + tab;
        }

      }else{ // MacOS or 1st line
        str += String.valueOf(counter) + tab + character;
      }
      super.write(str, 0, str.length());
      ++counter;

    }else{

      switch (character){
        case '\n': // Linux
          str += character + String.valueOf(counter) + tab;
          super.write(str, 0, str.length());
          ++counter;
          break;

        case '\r': // Windows or MacOS, next char needed to determine
          winNewLineChar = true;

          default:
            super.write(c);
      }
    }
  }
}
