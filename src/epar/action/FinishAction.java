package epar.action;

/**
 *
 * @author p264360
 */
public class FinishAction extends ActionWithoutCategory {
    
    public static final FinishAction INSTANCE = new FinishAction();
    
    private FinishAction() {
    }
    
    @Override
    public String toString() {
        return "FINISH";
    }

    @Override
    public short getType() {
        return Action.TYPE_FINISH;
    }
    
}
