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
     * @param args Command line arguments. **NOT USED**
     */
    public static void main(String[] args)
    {
        System.setProperty("user.dir", "C:\\Program Files (x86)\\ArcGIS SDKs\\java10.2.4\\");

        InfoFrame infoFrame = new InfoFrame();
        MapDisplay mapDisplay = new MapDisplay();
        mapDisplay.setVisible(true);

        infoFrame.startLoop();
    }
}

