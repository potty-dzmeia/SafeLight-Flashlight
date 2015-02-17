package my.potty.flashlight;

public class Flashlights
{
    public enum Type
    {
        Normal,
        Astro,
        Warning,
        Morse,
        SOS
    }
    
    private Flashlights(){};
    
    
    /** Factory method 
     * 
     * @param type - The type of flashlight that we want to have
     * @return - Flashlight object
     */
    public static Flashlight getInstance(Type type)
    {
        switch(type)
        {
        case Normal:
            return new NormalFlashlight();
            
        case Astro:
            return new AstroFlashlight();
          
        case Warning:
            return new WarningFlashlight();
 
        case Morse:
            return new MorseFlashlight();
            
        case SOS:
            return new SosFlashlight();
            
        default:
            return new NormalFlashlight();
        }
    
    }

}
