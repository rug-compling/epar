package epar;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import epar.data.Sentence;
import epar.grammar.Grammar;
import epar.node.Node;
import epar.oracle.MultiActionSequenceOracle;
import epar.oracle.Oracle;
import epar.parser.OracleAgenda;
import epar.util.ListUtil;
import epar.util.StringUtil;

public class ActionsToTrees {

    private final static Logger LOGGER = Logger.getLogger(ActionsToTrees.class.getName());

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        if (args.length != 4) {
            System.err.println(
                    "USAGE: java Decode INPUT GRAMMAR ACTIONS TREES");
            System.exit(1);
        }

        try {
            List<Sentence> sentences = Sentence.readSentences(new File(args[0]));
            Grammar grammar = Grammar.load(new File(args[1]));
            List<Oracle> oracles = MultiActionSequenceOracle.load(new File(args[2]));
            int length = sentences.size();

            if (length != oracles.size()) {
                throw new IllegalArgumentException("Lengths of sentences and oracles doesn't match");
            }

            try (Writer writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(new File(args[3])), "utf-8"))) {
                for (int i = 0; i < length; i++) {
                    OracleAgenda agenda = OracleAgenda.initial(sentences.get(i));
                    Oracle oracle = oracles.get(i);
                    int j = 0;

                    while (!agenda.allFinished()) {
                        agenda = agenda.nextAgenda(grammar, oracle);
                        j++;
                        
                        if (agenda.getItems().size() != 1) {
                            throw new RuntimeException("More than one item on agenda!");
                        }
                    }

                    List<Node> nodes = ListUtil.listFromIterable(agenda.getItems().get(0).stack);
                    Collections.reverse(nodes);
                    writer.write(StringUtil.join(nodes, " "));
                    writer.write("\n");
                }
            }
        } catch (IOException e) {
            System.err.println("ERROR: " + e.getLocalizedMessage());
            System.exit(1);
        }
    }

}
