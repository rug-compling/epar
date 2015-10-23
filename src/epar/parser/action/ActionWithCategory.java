package epar.parser.action;

/**
 *
 * @author p264360
 */
public abstract class ActionWithCategory extends Action {
    
    public final short category;
    
    public ActionWithCategory(short category) {
        this.category = category;
    }
    
    @Override
    public short getCategory() {
        return category;
    }
    
}
