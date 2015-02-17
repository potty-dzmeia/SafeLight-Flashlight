package my.potty.flashlight;

import android.app.Activity;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;



public class FlashlightActivity extends Activity 
{
	
	private MainSurfaceView	   flashlightView; 
	private GestureDetector    gestureDetector; 
    
	
	/** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Full-screen mode 
        requestWindowFeature(Window.FEATURE_NO_TITLE); 
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN , 
                       		 WindowManager.LayoutParams.FLAG_FULLSCREEN ); 
        
        setContentView(R.layout.main);
        
        // Setup the flashlight SurfaceView
        flashlightView = new MainSurfaceView(this);
        ((LinearLayout) findViewById(R.id.FlashLightSurface1)).addView(flashlightView);
        // Tell flashlighView where it can output some useful info for the user
        flashlightView.setInfoTextView( (TextView)findViewById(R.id.infoTextView1));
            
        
    	
    	 // Handler for double tapping and other actions
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener()
                        {    	
        					@Override
        					public boolean onDoubleTap(MotionEvent e)
        					{   
     						
        						return flashlightView.onDoubleTap();
                              }
        					 
        					@Override
        					public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
        					{     				
        						return flashlightView.onScroll(e1, e2, distanceX, distanceY);
        					}
        				});
        
    } //onCreate()

      
   
    
	@Override
	public boolean onTouchEvent(MotionEvent event) 
	{
		if(flashlightView.onEvent(event))
			return true;
		
		// Manages the DoubleTap and the Drag
    	// It is done here because if inside the View class the GestureDetector does not recognizes double tap
    	if (gestureDetector.onTouchEvent(event))
    		return true;
    	
		return false;	
	}  
	
	 @Override
	 protected void onResume()
	 {
		 super.onResume();
		 flashlightView.onResume();
	 }
	 
	 @Override
	 protected void onPause() 
	 {
		 super.onPause();
		 flashlightView.onPause();
	 }
	 
	 
	 @Override
	 protected void onDestroy()
	 {
		 super.onDestroy();
		 flashlightView.onDestroy();
	 }
	 
	 
	 @Override
	 public boolean onCreateOptionsMenu(Menu menu) 
	 {
		 super.onCreateOptionsMenu(menu);
	   		
		 // Inform the View
		 flashlightView.onCreateOptionsMenu(menu);
	   
		 return true;
	 }	 
	 
	 @Override
	 public boolean onOptionsItemSelected(MenuItem item) 
	 {
		 // mainView will handle the action
		 flashlightView.onMenuItemSelection(item); 	
		 return true;
	 }
   
}
    