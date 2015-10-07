package epar;

import java.io.File;

import epar.model.UpdatableModel;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InitModel {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("USAGE: java InitModel MODELFILE");
            System.exit(1);
        }

        try {
            (new UpdatableModel()).save(new File(args[0]));
        } catch (IOException ex) {
            Logger.getLogger(InitModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
