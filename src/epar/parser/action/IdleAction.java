package epar.parser.action;

/**
 *
 * @author p264360
 */
public class IdleAction extends ActionWithoutCategory {
    
    // Could perhaps conflate IdleAction and FinishAction.
    
    public static final IdleAction INSTANCE = new IdleAction();
    
    private IdleAction() {
    }
    
    @Override
    public String toString() {
        return "IDLE";
    }

    @Override
    public int getType() {
        return Action.TYPE_IDLE;
    }
    
}
