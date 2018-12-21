/**
 * 
 * Some implementations from the NRC book.
 * 
 */
package nrc;

import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author kamir
 */
public class NRC {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        
//        KSTest.runWithSampleData(1000, -100.0, 2.5);
//        KSTest.runWithSampleData(1000, 2.5, 100);
//        KSTest.runWithSampleData(1000, -100.0, 100.0);
        FileWriter fw = new FileWriter("DUMP-DATA.txt");

        KSTest.runWithSampleData(1000, -100.0, 2.5, fw);
        KSTest.runWithSampleData(1000, 2.5, 100, fw);
        KSTest.runWithSampleData(1000, -100.0, 100.0, fw);
        
    }

    
}
