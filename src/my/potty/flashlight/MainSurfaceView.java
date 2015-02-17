package my.potty.flashlight;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;




class MainSurfaceView extends SurfaceView implements Runnable
{	
    
	private static final int THREAD_CALLING_PERIOD = 40; // in [mseconds]
	
	
	private Flashlight         flashlight;
	private Thread 			   drawingThread;
	private volatile boolean   isThreadRunning = false;
	private SurfaceHolder 	   surfaceHolder;
	private Context			   context;
	private TextView           infoText;          // Place where we can paste feedback info for the user      
	private Handler            handlerToUiThread; // Used for manipulating the infoText from the thread
	
    
	
	
	
	public MainSurfaceView(Context context) 
	{  		
		super(context);
		
		this.context  = context;
		
		handlerToUiThread = new Handler(); 
		surfaceHolder     = getHolder();

		// Read which flashlight was used last by the user and start with it
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		int lastFlashlightUsed = preferences.getInt("lastFlashlightUsed", Flashlights.Type.Normal.ordinal());
		flashlight = Flashlights.getInstance(Flashlights.Type.values()[lastFlashlightUsed]);
		// Read the user settings 
		flashlight.loadUserPreferences(preferences);
	}
	
	
	@Override
	/**
	 *  Drawing of the SurfaceView area is done here.
	 */
	public void run() 
	{
		while(isThreadRunning)
		{
			if(surfaceHolder.getSurface().isValid() == false)
				continue;
			if(infoText == null)
			    continue;
			Canvas canvas = surfaceHolder.lockCanvas();
			flashlight.draw(canvas);// <--------------------------------------- Drawing here
			drawTextInfo(); // Draw the feedback text for the user
			//handlerToUiThread.
			surfaceHolder.unlockCanvasAndPost(canvas);
			
			
			// Sleep the thread for some time before drawing again...
			try {
				Thread.sleep(THREAD_CALLING_PERIOD);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} //while(running)
			
	} // run()
	
	
	
	/** Called when the user does something
	 * 
	 * @param event
	 * @return - if the event was processed
	 */
	public boolean onEvent(MotionEvent event) 
	{
		return flashlight.motionEvent(event);
	}
	
	

	/**
	 *  Called by the Activity when the user double taps
	 */
	public boolean onDoubleTap()
	{
	    return flashlight.doubleTap();
	}
	
	
	/**
	 *  Called by the Activity  when the user scrolls across the Y axis
	 *  
	 * @param iDelta - the amount of scroll on the ordinate
	 * @return - if the event was processed
	 */
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
	{
		return flashlight.scroll( e1,  e2,  distanceX,  distanceY);
	}

	
	
	/**
	 * Called by the Activity when it resumes
	 */
	public void onResume()
	{
		   isThreadRunning = true;
		   drawingThread = new Thread(this);
		   drawingThread.start();
	}
		 
	
	/**
	 * Called by the Activity when paused
	 */
	public void onPause()
	{
		boolean retry = true;
		isThreadRunning = false;

		while(retry)
		{
			try 
			{
				drawingThread.join();
				retry = false;
		    } catch (InterruptedException e) 
		    {
		    	// TODO Auto-generated catch block
		    	e.printStackTrace();
		    }
		}
	}
		  

	/** 
	 * Called when the view is about to close
	 */
	public void onDestroy()
	{
		// Save user preferences on the flash
	    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		flashlight.saveUserPreferences(preferences);
	}
	
	
	
	/** User selected a menu Item
	 * 
	 * @param menuItem
	 */
	public void onMenuItemSelection(MenuItem menuItem)
	{
	    // Save the userPreferences of the current flashlight
	    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
	    flashlight.saveUserPreferences(preferences);
	    
	    
		// Create the desired flashlight
	    int itemId = menuItem.getItemId();
	    flashlight = Flashlights.getInstance(Flashlights.Type.values()[itemId]);
	    flashlight.loadUserPreferences(preferences);
	    
		// Save which was the flashlight last used
		SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("lastFlashlightUsed", itemId);
        editor.commit();
	}
	
	
	/** Fills the menu with the different types of  flashlights
	 * 
	 * @param menu
	 */
	public void onCreateOptionsMenu(Menu menu) 
	{
	    for(Flashlights.Type type : Flashlights.Type.values())
	    {
	        menu.add(Menu.NONE, type.ordinal(),  Menu.NONE, type.toString());
	    }
	}
	
	/** Sets the TextView to which we can write some control information
	 * 
	 * @param textview
	 */
	public void setInfoTextView(TextView textview)
	{
	    infoText = textview;
	}
	
	
	
	/**
	 *  Updates the ViewText responsible for visualizing info regarding the status of the flashlight.
	 *  This gets executed on the UI thread so it can safely modify infoText.
	 */
	private void drawTextInfo()
	{      
        handlerToUiThread.post(new Runnable()
        { 
            @Override
            public void run()
            {
                // Update the textView if the text has changed..
                if(flashlight.getFeedbackInfo().compareTo(infoText.getText().toString())!= 0)
                {
                    infoText.setText(flashlight.getFeedbackInfo()); 
                }
                
            }
        });
	}
	
	
} // MyView
