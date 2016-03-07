package epar.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import epar.data.Sentence;
import epar.grammar.Grammar;
import epar.model.Model;
import epar.oracle.Oracle;

/**
 * An agenda represents one generation of parse items. This implementation
 * uses beam search.
 * @author p264360
 */
public class Agenda {

    private static final Logger LOGGER = Logger.getLogger(Agenda.class.getName());

    static {
        //LOGGER.setLevel(Level.FINE);
    }

    private static final int BEAM_WIDTH = 16; // TODO make configurable

    public final int generation;

    private final List<Candidate> candidates;

    private final List<Candidate> beam;

    private Agenda(int generation, List<Candidate> candidates) {
        this.generation = generation;
        this.candidates = candidates;

        // Keep the best candidates in beam
        beam = new ArrayList<>(candidates.subList(0, Math.min(BEAM_WIDTH, candidates.size())));

        // Also keep the best finished candidate, if any
        boolean gotFinishedCandidate = false;

        for (Candidate nextCandidate : beam) {
            if (nextCandidate.item.finished) {
                gotFinishedCandidate = true;
                break;
            }
        }

        if (!gotFinishedCandidate && candidates.size() > BEAM_WIDTH) {
            for (Candidate successor : candidates.subList(BEAM_WIDTH, candidates.size())) {
                if (successor.item.finished) {
                    beam.add(successor);
                    break;
                }
            }
        }
    }

    public Agenda nextAgenda(Grammar grammar, Model model, Oracle oracle) {
        List<Candidate> beamSuccessors = new ArrayList<>();

        // Find all successors of candidates on the beam
        for (Candidate candidate : beam) {
            candidate.findSuccessors(generation, beamSuccessors, grammar, model, oracle);
        }

        LOGGER.log(Level.FINE, "Successors: {0}", beamSuccessors);

        // Sort by score, descending
        Collections.sort(beamSuccessors, new Comparator<Candidate>() {

            @Override
            public int compare(Candidate arg0, Candidate arg1) {
                if (arg1.score > arg0.score) {
                    return 1;
                } else if (arg1.score == arg0.score) {
                    return 0;
                } else {
                    return -1;
                }
            }

        });

        return new Agenda(generation + 1, beamSuccessors);
    }

    public boolean noneCorrectWithinBeam() {
        for (Candidate candidate : beam) {
            if (candidate.correct) {
                return false;
            }
        }

        return true;
    }

    public boolean allFinishedWithinBeam() {
        for (Candidate candidate : beam) {
            if (!candidate.item.finished) {
                return false;
            }
        }

        return true;
    }

    public Candidate getHighestScoring() {
        return candidates.get(0);
    }

    public Candidate getHighestScoringCorrect() {
        for (Candidate candidate : candidates) {
            if (candidate.correct) {
                return candidate;
            }
        }

        throw new IndexOutOfBoundsException("No correct candidate");
    }

    public static Agenda initial(Sentence sentence) {
        return new Agenda(0, Collections.singletonList(Candidate.initial(sentence)));
    }

    /**
     * Returns an unmodifiable list of the candidates within the beam in this
     * agenda, sorted by score, highest-scoring first.
     *
     * @return
     */
    public List<Candidate> getBeam() {
        return Collections.unmodifiableList(beam);
    }

}
