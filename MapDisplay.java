package com.company;

import com.esri.map.JMap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Map display class.
 *
 * Will attempt to use ArcGIS for this as google was too limited in allowed api calls.
 *
 * @author Ian Weeks (09/07/2015).
 */
public class MapDisplay extends JFrame
{
    /*
     * JFrame jFrame2 = new JFrame("Vessel Travel Simulation");
     * jFrame2.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
     * jFrame2.setLocation(1020, 0);
     * jFrame2.pack();
     */
    private JPanel panel;
    private JMap map;

    public MapDisplay()
    {
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setTitle("Vessel Location");
        this.setLocation(1020, 0);

        panel = (JPanel) this.getContentPane();
        panel.setPreferredSize(new Dimension(600, 400));

        this.getContentPane().setLayout(new BorderLayout());
        this.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                super.windowClosing(e);
                map.dispose();
            }
        });

        this.pack();
    }

}
