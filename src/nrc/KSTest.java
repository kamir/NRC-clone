/**
 *
 * 
 * 
 */
package nrc;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math3.stat.inference.KolmogorovSmirnovTest;
import statistics.DistributionTester;

/**
 *
 * @author kamir
 */
public class KSTest {
   
    /**
     * 
     * @param F1 - a set of double values
     * @param F2 - a second set double values
     * 
     * @param fw - a writer to log intermediate results.
     * 
     * @return
     * @throws IOException 
     */
    private static double twoSideKSTest( Vector<Double> F1, 
                                         Vector<Double> F2, 
                                         Writer fw, 
                                         double min, 
                                         double max ) throws IOException { 
        
        return twoSideKSTest( F1, F2, fw, 2, "DEFAULT", min, max );
    
    }

    /**
     * Returns the p value of the two sided test and writes the analysis results
     * into an Output-Collector. 
     * 
     * @param F1
     * @param F2
     * @return 
     */
    private static double twoSideKSTest( Vector<Double> F1, 
                                         Vector<Double> F2, 
                                         Writer fw, 
                                         int sides, 
                                         String typ, 
                                         double min, 
                                         double max ) throws IOException {
        double p = 1.0;
        
        if ( fw == null ) fw = new PrintWriter( System.out );

        
        int N1 = F1.size();
        int N2 = F2.size();
        
        String q = "[-]";
        String type = "-";
        if ( typ != null ) type = typ;


        if( N1==0 ) {
            String line = String.format("%s\t%5d\t%5d\t%9.2e\t%s\t%5f\t%5f\t%s\n", typ, N1, N2, p, q ,min, max,typ);
            fw.write( line );
            System.out.println( line );
            return p;
        } 
        if( N2==0 ) {
            String line = String.format("%s\t%5d\t%5d\t%9.2e\t%s\t%5f\t%5f\t%s\n", typ, N1, N2, p, q ,min, max,typ);
            fw.write( line );
            System.out.println( line );
            return p;
        }


        int j1 = 0;
        int j2 = 0;
        
        double vF1 = 0.0;
        double vF2 = 0.0;
        double dist = 0.0;
        
        /**
         * 
         * find the supremum ...  
         */
        while( j1 < N1 && j2 < N2 ) { 
           
//            if ( F1.elementAt(j1) <= F2.elementAt(j1) ) vF1 = j1++/N1;
//            if ( F1.elementAt(j1) >= F2.elementAt(j1) ) vF2 = j2++/N2;
            vF1 = F1.elementAt(j1);
            vF2 = F2.elementAt(j2);
            
            j1++;
            j2++;
            
            if ( Math.abs(vF1-vF2) > dist)
              dist = Math.abs(vF1-vF2);
    
//            System.out.println( "j1=" + j1 + " " + "j2=" + j2 + " " + "> SUPREMUM=" + dist );
        }
        
        System.out.println( "> SUPREMUM=" + dist );
        
        //calculate KS test p-value
        double N = Math.sqrt(N1*N2/(N1+N2)); 
        double a = (N+0.12+0.11/N)*dist;
        a = -2.0*a*a;
        double b = 2.0;
        double pval=0.0;
        double termbf=0.0;
        
        int i = 1;
        double term = 0.0;
        for ( i=1; i<=100; i++) {  if (a*i*i > -500)
              term = b* Math.exp(a*i*i);
           else
              term = 0.0;
           pval += term;
           if (Math.abs(term) <= 0.001*termbf || Math.abs(term) <= 1e-8*pval)
              i=102;
           b = -b;
           termbf = Math.abs(term);
        }

        if (sides==1)  pval/=2;
        if (i<101) pval = 1;

        // #   printf("KS test with N1=%5d, N2=%5d data points yields p-value=%9.2e\n", N1, N2, pval);


        if ( pval < 0.03 ) q = "[o]";
        if ( pval < 0.01 ) q = "[+]";
        if ( pval < 0.001 ) q = "[#]";

        String line = String.format("%s\t%5d\t%5d\t%9.2e\t%s\t%5f\t%5f\t%s\n", typ, N1, N2, pval,q ,min, max,typ);
        fw.write( line );
        System.out.println( line );
        
        return pval;
    }
    
    public static double twoSideKSTest( KSTestData d, Writer fw ) throws IOException {
        d.filterAndSort();
        return twoSideKSTest( d.vec1, d.vec2, fw, d.min, d.max );
    }
    
    
    



    // we use generated random sample data ...
    static void runWithSampleData(int z, double min, double max, FileWriter fw ) {
        
        try {
            
            KSTestData test1 = KSTestData.getRandomSamples( z );  
            test1.min = min;
            test1.max = max;
            
            double pNRC = twoSideKSTest( test1, null );
            System.out.println( ">>> pNRC=" + pNRC );
            
            KolmogorovSmirnovTest refTest = new KolmogorovSmirnovTest();
            double pRef = refTest.kolmogorovSmirnovTest(test1.F1, test1.F2);
            System.out.println( ">>> pRef=" + pRef );

            fw.write(  ">>>Kolmogorow-Smirnow Test:    pNRC=" + pNRC );
            fw.write(  ">>>Kolmogorow-Smirnow Test:    pRef=" + pRef );

            test1.dumpRawData( z + "_" + min + "_" + max + "_testdata.tsv" );
            test1.dumpDistributionData( z + "_" + min + "_" + max + "_histogramdata.tsv" );
            test1.dumpDistributionProperties( test1.F1, fw, z + "_" + min + "_" + max + "_F1_distr_test.rdf" );
            test1.dumpDistributionProperties( test1.F2, fw, z + "_" + min + "_" + max + "_F2_distr_test.rdf" );
            
          
            
        } 
        catch (IOException ex) {
            Logger.getLogger(KSTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
    
}
