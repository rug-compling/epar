package epar;

import epar.model.Model;
import java.io.File;
import java.io.IOException;

public class CountNonZeroFeatures {

    public static void main(String[] args) throws IOException {
        Model model = Model.loadAveraged(new File(args[0]));
        int count = 0;
        
        for (int i = 0; i < Model.WEIGHT_VECTOR_SIZE; i++) {
            if (model.weights[i] != 0.0) {
                count++;
            }
        }
        
        System.out.println(count);
    }

}
