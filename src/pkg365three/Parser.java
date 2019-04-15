/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg365three;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author smm-pc
 */
public class Parser {

    // **************************************************
    // Fields
    // **************************************************
    private static final char DEFAULT_SEPARATOR = ',';
    private static final char DEFAULT_QUOTE = '"';

    // **************************************************
    // Public Methods
    // **************************************************
    /**
     * creates data array of size 10000 from business.xml file
     *
     * @return data array size 10000
     * @throws IOException
     * @throws java.lang.ClassNotFoundException
     */
    public ArrayList<Data> load() throws IOException, ClassNotFoundException {
        ArrayList<Data> d = new ArrayList<>();
        int count = 0;
        String csvFile = "business.csv";
        //line 14 id, line 19 catagories, line 20 name, 56 city, longitude 72, latitude 10, state 37
        Scanner scanner = new Scanner(new File(csvFile));
        while (scanner.hasNext() && count < 10001) {
            List<String> line = parseLine(scanner.nextLine());
            if (count > 0) {//0 holds atribute names(lattitude, name, city, etc.)
                String name = line.get(20);
                    name = name.replace("'", "");
                    name = name.replace("[", "");
                    name = name.replace("]", "");
                    name = name.replace("\"", "");
                    name = name.trim();
                double lat = Double.parseDouble(line.get(10));
                double lon = Double.parseDouble(line.get(72));
              
                //System.out.println(line.get(20));
                Data temp = new Data(line.get(14), name.toLowerCase(), line.get(56), line.get(37), lat, lon);
                d.add(temp);
            }
            count++;
        }
        scanner.close();
        return d;

    }

    /**
     *
     * @param cvsLine
     * @return
     */
    public static List<String> parseLine(String cvsLine) {
        return parseLine(cvsLine, DEFAULT_SEPARATOR, DEFAULT_QUOTE);
    }

    /**
     *
     * @param cvsLine
     * @param separators
     * @return
     */
    public static List<String> parseLine(String cvsLine, char separators) {
        return parseLine(cvsLine, separators, DEFAULT_QUOTE);
    }

    /**
     *
     * @param cvsLine
     * @param separators
     * @param customQuote
     * @return
     */
    public static List<String> parseLine(String cvsLine, char separators, char customQuote) {

        List<String> result = new ArrayList<>();

        //if empty, return!
        if (cvsLine == null && cvsLine.isEmpty()) {
            return result;
        }

        if (customQuote == ' ') {
            customQuote = DEFAULT_QUOTE;
        }

        if (separators == ' ') {
            separators = DEFAULT_SEPARATOR;
        }

        StringBuffer curVal = new StringBuffer();
        boolean inQuotes = false;
        boolean startCollectChar = false;
        boolean doubleQuotesInColumn = false;

        char[] chars = cvsLine.toCharArray();

        for (char ch : chars) {

            if (inQuotes) {
                startCollectChar = true;
                if (ch == customQuote) {
                    inQuotes = false;
                    doubleQuotesInColumn = false;
                } else {

                    //Fixed : allow "" in custom quote enclosed
                    if (ch == '\"') {
                        if (!doubleQuotesInColumn) {
                            curVal.append(ch);
                            doubleQuotesInColumn = true;
                        }
                    } else {
                        curVal.append(ch);
                    }

                }
            } else {
                if (ch == customQuote) {

                    inQuotes = true;

                    //Fixed : allow "" in empty quote enclosed
                    if (chars[0] != '"' && customQuote == '\"') {
                        curVal.append('"');
                    }

                    //double quotes in column will hit this!
                    if (startCollectChar) {
                        curVal.append('"');
                    }

                } else if (ch == separators) {

                    result.add(curVal.toString());

                    curVal = new StringBuffer();
                    startCollectChar = false;

                } else if (ch == '\r') {
                    //ignore LF characters
                    continue;
                } else if (ch == '\n') {
                    //the end, break!
                    break;
                } else {
                    curVal.append(ch);
                }
            }

        }

        result.add(curVal.toString());

        return result;
    }
}
