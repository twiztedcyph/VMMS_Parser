package com.company;

/**
 * Main class.
 * <p/>
 * Contains main method.
 * <p/>
 * InfoFrame initialisation and loop start.
 *
 * @author Ian Weeks (09/07/2015).
 */
public class Main
{
    /**
     * Main method.
     *
     * Must have ArcGIS development kit installed for this to work.
     *
     * @param args Command line arguments. **NOT USED**
     */
    public static void main(String[] args)
    {
        //Set path to ArcGIS.
        System.setProperty("user.dir", "C:\\Program Files (x86)\\ArcGIS SDKs\\java10.2.4\\");

        //Init main frame.
        InfoFrame infoFrame = new InfoFrame();

        //Start the main "game" loop.
        infoFrame.startLoop();
    }
}

