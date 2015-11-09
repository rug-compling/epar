package epar.parser.action;

/**
 *
 * @author p264360
 */
public abstract class ActionWithCategory extends Action {
    
    public final int category;
    
    public ActionWithCategory(int category) {
        this.category = category;
    }
    
    @Override
    public int getCategory() {
        return category;
    }
    
}
