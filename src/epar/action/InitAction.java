package epar.action;

/**
 *
 * @author p264360
 */
public class InitAction extends ActionWithoutCategory {
    
    public static final InitAction INSTANCE = new InitAction();
    
    private InitAction() {
    }
    
    @Override
    public String toString() {
        return "INIT";
    }

    @Override
    public short getType() {
        return Action.TYPE_INIT;
    }
    
}
