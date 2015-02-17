package my.potty.flashlight;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.view.MotionEvent;




public interface Flashlight 
{

	/** Draws the flashlight onto the supplied canvas.
	 * 
	 * @param canvas - the canvas on which we will be drawing
	 */
	public abstract void draw(Canvas canvas); 

	/** Called when the user double taps onto the display */
	public abstract boolean doubleTap();

	/** Called when some action was performed by the user.
	 * 
	 * @param event - the type of action
	 * @return - if the event was processed
	 */
	public abstract boolean motionEvent(MotionEvent event);

	
	/** Tells the flashlight that user has made scroll.
	 * @param distanceX - scroll distance across the abscissa (could be negative)
	 * @param distanceY - scroll distance across the ordinate (could be negative)
	 * @return - if the event was processed
	 */
	public abstract boolean scroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY);

	
	/** Returns the user relevant information
	 * 
	 * @return
	 */
	public abstract String getFeedbackInfo();
	
	
	public abstract void saveUserPreferences(SharedPreferences preferences);
	public abstract void loadUserPreferences(SharedPreferences preferences);
	

}