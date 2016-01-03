
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;



public class FileUtilsTest {

    @Test
    public void testGetWeights(){
        ArrayList<Integer> netWeights = FileUtils.getWeights(HandFigureTypes.NET);
        assertNotNull(netWeights);
        for (Integer netWeight : netWeights) {
            System.out.println(netWeight);
        }
    }


    @Test
    public void testSetWeights() throws FileNotFoundException {
        ArrayList<Integer> netWeights = FileUtils.getWeights(HandFigureTypes.NET);
        assertNotNull(netWeights);
        netWeights = new ArrayList<Integer>();
        netWeights.add(1);
        netWeights.add(3);
        netWeights.add(2);
        FileUtils.setWeights(HandFigureTypes.NET, netWeights);
        netWeights = FileUtils.getWeights(HandFigureTypes.NET);
        assertNotNull(netWeights);
        assertTrue(1 == netWeights.get(0));
        assertTrue(3 == netWeights.get(1));
        assertTrue(2 == netWeights.get(2));
    }
}
