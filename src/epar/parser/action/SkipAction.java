package epar.parser.action;

/**
 *
 * @author p264360
 */
public class SkipAction extends ActionWithoutCategory {
    
    public static final SkipAction INSTANCE = new SkipAction();
    
    private SkipAction() {
    }
    
    @Override
    public String toString() {
        return "SKIP";
    }

    @Override
    public short getType() {
        return Action.TYPE_SKIP;
    }
    
}
