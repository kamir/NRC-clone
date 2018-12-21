package nrc;

import data.export.MesswertTabelle;
import data.export.OriginProject;
import data.series.Messreihe;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.Vector;
import org.apache.commons.math3.random.RandomDataGenerator;
import statistics.DistributionTester;
import statistics.HaeufigkeitsZaehlerDouble;

/**
 *
 * @author kamir
 */
public class KSTestData {
    
    public static final double MIN = -100.0;
    public static final double MAX = 100.0;
    
    /**
     * Create some TEST data.
     * 
     * @param i
     * @return 
     */
    static KSTestData getRandomSamples(int i) {
        
        KSTestData d = new KSTestData();
        d.F1 = new double[i];
        d.F2 = new double[i];
        d.vec1 = new Vector<Double>();
        d.vec2 = new Vector<Double>();

        RandomDataGenerator randomData = new RandomDataGenerator(); 
        for (int j = 0; j < i; j++) {
            d.F1[j] = randomData.nextGaussian(0.0, 2.5);
        }
        for (int j = 0; j < i; j++) {
            d.F2[j] = randomData.nextGaussian(0.0, 2.5);
        }
        return d;
    }
    
    // boundaries 
    double min = MIN;
    double max = MAX;
    
    // Raw Data
    double[] F1 = null;
    double[] F2 = null;
    
    // filtered and sorted
    Vector<Double> vec1 = null;
    Vector<Double> vec2 = null;
    
    /**
     * 
     */
    public void filterAndSort() throws IOException {
        filterAndSort( F1, F2, min, max, null );
    }
    
    private void filterAndSort( double[] F1, double[] F2, double min, double max, Writer fw ) throws IOException {
        
        if ( fw == null ) fw = new PrintWriter( System.out );

        int outside1 = 0;
        int outside2 = 0;
         
        for( double v : F1) {
            if ( v > min && v < max ) vec1.add(v);
            else outside1++;
        }
         
        for( double v : F2) {
            if ( v > min && v < max ) vec2.add(v);
            else outside2++;
        }
        
        Collections.sort(vec1);
        Collections.sort(vec2);

        fw.write( "> (min:" + min + ", max:" +  max + ")\n");
        
        fw.write( "> ratio of removed values: r1=" + (double)outside1/(double)F1.length + ", r2=" + (double)outside2/(double)F2.length +  "\n");
        fw.flush();
        
    }

    void dumpRawData(String name) throws IOException {
        
        OriginProject op = new OriginProject();
        op.initBaseFolder("./KS-TEST/");
        op.initFolder("dump");

        Messreihe mr1 = new Messreihe( "s1" );
        Messreihe mr2 = new Messreihe( "s2" );
        mr1.addValues(vec1);
        mr2.addValues(vec2);
        
        MesswertTabelle mwt = new MesswertTabelle();
        mwt.setLabel(name);
        mwt.addMessreihe( mr1 );
        mwt.addMessreihe( mr2 );
        
        Vector<Messreihe> vec = new Vector<Messreihe>();
        vec.add(mr1);
        vec.add(mr2);
        
        op.storeMesswertTabelle(mwt);
        op.storeChart(vec, true, name, name);
    }

    void dumpDistributionData(String name) throws IOException {
            
        OriginProject op = new OriginProject();
        op.initBaseFolder("./KS-TEST/");
        op.initFolder("dump");

        HaeufigkeitsZaehlerDouble z1 = new HaeufigkeitsZaehlerDouble();
        HaeufigkeitsZaehlerDouble z2 = new HaeufigkeitsZaehlerDouble();
        
        z1.min = min;
        z2.min = min;

        z1.max = max;
        z2.max = max;

        z1.addData(vec1);
        z2.addData(vec2);

        z1.calcWS();
        z2.calcWS();
        

        Messreihe mr1 = z1.getHistogram();
        Messreihe mr2 = z2.getHistogram();
                
        MesswertTabelle mwt = new MesswertTabelle();
        mwt.setLabel(name);
        mwt.addMessreihe( mr1 );
        mwt.addMessreihe( mr2 );
        
        Vector<Messreihe> vec = new Vector<Messreihe>();
        vec.add(mr1);
        vec.add(mr2);
        
        op.storeMesswertTabelle(mwt);
        op.storeChart(vec, true, name, name);
    
    }

    void dumpDistributionProperties(double[] d, FileWriter fw, String name) throws IOException {
        System.out.println("### " + name + "###");
        fw.write("### " + name + "###");
        
        DistributionTester dtester = DistributionTester.getDistributionTester();
        dtester.init();
        dtester.fw=fw;
        dtester.testDataset( d );
        dtester.testLogDataset( d );
        
    }
    
}
