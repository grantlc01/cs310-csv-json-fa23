package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;
import java.io.StringReader;
import java.util.List;
import java.util.Iterator;
import java.io.StringWriter;
import java.text.DecimalFormat;

public class Converter {
    
    /*
        
        Consider the following CSV data, a portion of a database of episodes of
        the classic "Star Trek" television series:
        
        "ProdNum","Title","Season","Episode","Stardate","OriginalAirdate","RemasteredAirdate"
        "6149-02","Where No Man Has Gone Before","1","01","1312.4 - 1313.8","9/22/1966","1/20/2007"
        "6149-03","The Corbomite Maneuver","1","02","1512.2 - 1514.1","11/10/1966","12/9/2006"
        
        (For brevity, only the header row plus the first two episodes are shown
        in this sample.)
    
        The corresponding JSON data would be similar to the following; tabs and
        other whitespace have been added for clarity.  Note the curly braces,
        square brackets, and double-quotes!  These indicate which values should
        be encoded as strings and which values should be encoded as integers, as
        well as the overall structure of the data:
        
        {
            "ProdNums": [
                "6149-02",
                "6149-03"
            ],
            "ColHeadings": [
                "ProdNum",
                "Title",
                "Season",
                "Episode",
                "Stardate",
                "OriginalAirdate",
                "RemasteredAirdate"
            ],
            "Data": [
                [
                    "Where No Man Has Gone Before",
                    1,
                    1,
                    "1312.4 - 1313.8",
                    "9/22/1966",
                    "1/20/2007"
                ],
                [
                    "The Corbomite Maneuver",
                    1,
                    2,
                    "1512.2 - 1514.1",
                    "11/10/1966",
                    "12/9/2006"
                ]
            ]
        }
        
        Your task for this program is to complete the two conversion methods in
        this class, "csvToJson()" and "jsonToCsv()", so that the CSV data shown
        above can be converted to JSON format, and vice-versa.  Both methods
        should return the converted data as strings, but the strings do not need
        to include the newlines and whitespace shown in the examples; again,
        this whitespace has been added only for clarity.
        
        NOTE: YOU SHOULD NOT WRITE ANY CODE WHICH MANUALLY COMPOSES THE OUTPUT
        STRINGS!!!  Leave ALL string conversion to the two data conversion
        libraries we have discussed, OpenCSV and json-simple.  See the "Data
        Exchange" lecture notes for more details, including examples.
        
    */
    
    @SuppressWarnings("unchecked")
    public static String csvToJson(String csvString) {
        
        String result = "{}"; // default return value; replace later!
        
        try {
        
            CSVReader reader = new CSVReader(new StringReader(csvString));
            List<String[]> full = reader.readAll();
            
            Iterator<String[]> iterator = full.iterator();
            
            JsonObject jsonObject = new JsonObject();
            
            JsonArray data = new JsonArray();
            JsonArray prodNums = new JsonArray();
            
            String[] headings = iterator.next();

            while(iterator.hasNext()) {
                String[] csvRecord = iterator.next();
                JsonArray currentData = new JsonArray();
                for(int i = 0; i < csvRecord.length; ++i) {
                    if(i == 0) {
                        prodNums.add(csvRecord[i]);
                    } else if(i == 2 || i == 3) {
                        int num = Integer.parseInt(csvRecord[i]);
                        currentData.add(num);
                    } else {
                        currentData.add(csvRecord[i]);
                    }
                }
                data.add(currentData);
            }

            jsonObject.put("ProdNums", prodNums);
            jsonObject.put("ColHeadings", headings);
            jsonObject.put("Data", data);
            
            result = Jsoner.serialize(jsonObject);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
    }
    
    @SuppressWarnings("unchecked")
    public static String jsonToCsv(String jsonString) {
        
        String result = ""; // default return value; replace later!
        
        DecimalFormat decimalFormat = new DecimalFormat("00");
        
        try {
            
            // INSERT YOUR CODE HERE
            JsonObject jsonValues = Jsoner.deserialize(jsonString, new JsonObject());
            
            JsonArray prodNum = new JsonArray();
            prodNum = (JsonArray)(jsonValues.get("ProdNums"));
            
            JsonArray headings = new JsonArray();
            headings = (JsonArray)(jsonValues.get("ColHeadings"));
            
            JsonArray data = new JsonArray();
            data = (JsonArray)(jsonValues.get("Data"));
            
            StringWriter stringWriter = new StringWriter();
            CSVWriter csvWriter = new CSVWriter(stringWriter, ',', '"', '\\', "\n");
            
            String[] headingArray = new String[headings.size()];
            
            for(int i = 0; i < headings.size(); i++) {
                headingArray[i] = headings.getString(i).toString();
            }
            
            csvWriter.writeNext(headingArray);
            
            for(int i = 0; i < prodNum.size(); i++) {
                String[] row = new String[headings.size()];
                JsonArray insideData = ((JsonArray)data.get(i));
                row[0] = prodNum.getString(i).toString();
                
                for(int j = 0; j < insideData.size(); j++) {
                    if(j == headings.indexOf("Episode") - 1) {
                        int num = Integer.parseInt(insideData.getString(j).toString());
                        String formattedNum = "";
                        formattedNum = decimalFormat.format(num);
                        row[j + 1] = formattedNum;
                    } else {
                        row[j + 1] = insideData.get(j).toString();
                    }
                }
                csvWriter.writeNext(row);
            }
            
            result = stringWriter.toString();
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
    }
    
}
