package com.company;

import com.esri.core.geometry.*;
import com.esri.core.geometry.Point;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.map.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Map display class.
 * <p/>
 * Will attempt to use ArcGIS for this as google was too limited in allowed api calls.
 *
 * @author Ian Weeks (09/07/2015).
 */
public class MapDisplay extends JFrame
{
    private JMap map;
    private int pointerID, lineID, counter;
    private double currentLat, currentLong;
    private boolean firstUpdate = true;
    private Timer timer;
    private Point point;
    private SimpleLineSymbol simpleLineSymbol;
    private Polyline polyline;
    private SpatialReference sr;

    public MapDisplay() throws IOException
    {
        final Image pointerImage = ImageIO.read(new File("red_dot.png"));
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setTitle("Vessel Location");
        this.setLocation(1020, 0);

        polyline = new Polyline();

        JPanel panel = (JPanel) this.getContentPane();
        panel.setPreferredSize(new Dimension(800, 600));

        MapOptions mapOptions = new MapOptions(MapOptions.MapType.TOPO);
        map = new JMap(mapOptions);
        map.setWrapAroundEnabled(true);
        map.setBounds(0, 0, 800, 600);
        panel.add(map);

        map.setExtent(new Envelope(192627.89248559574, 6883772.075054788, 199142.69342394127, 6888658.175758547));
        sr = map.getSpatialReference();

        final GraphicsLayer graphicsLayer = new GraphicsLayer();
        map.getLayers().add(graphicsLayer);

        simpleLineSymbol = new SimpleLineSymbol(Color.green, 3, SimpleLineSymbol.Style.DASH);


        map.addMapEventListener(new MapEventListenerAdapter()
        {
            @Override
            public void mapExtentChanged(MapEvent arg0)
            {
                if (map.isReady())
                {
                    System.out.println(String.format("MinX: %f\n", map.getExtent().getXMin()) +
                            String.format("MinY: %f\n", map.getExtent().getYMin()) +
                            String.format("MaxX: %f\n", map.getExtent().getXMax()) +
                            String.format("MaxY: %f\n", map.getExtent().getYMax()) +
                            String.format("Cent: %f\t%f\n", map.getExtent().getCenter().getX(),
                                    map.getExtent().getCenter().getY()));
                }
            }

            @Override
            public void mapReady(MapEvent event)
            {
                PictureMarkerSymbol pms = new PictureMarkerSymbol((BufferedImage) pointerImage);
                Graphic pointGraphic = new Graphic(point, pms);
                Graphic lineGraphic = new Graphic(polyline, simpleLineSymbol);

                lineID = graphicsLayer.addGraphic(lineGraphic);
                pointerID = graphicsLayer.addGraphic(pointGraphic);
            }
        });


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

        ActionListener actionListener = new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                counter++;


                if (counter > 100)
                {
                    if (firstUpdate)
                    {
                        polyline.setEmpty();
                        polyline.startPath(GeometryEngine.project(195692.61151359137, 6886509.9789550705, sr));
                        firstUpdate = false;
                        System.out.println(currentLat + " " + currentLong);
                    } else
                    {

                        point = GeometryEngine.project(currentLat, currentLong, map.getSpatialReference());

                        polyline.lineTo(point);
                        counter = 0;
                    }

                }

                graphicsLayer.updateGraphic(lineID, polyline);
                graphicsLayer.updateGraphic(pointerID, point);
            }
        };

        timer = new Timer(10, actionListener);

        this.pack();
    }

    public void setCurrentPos(double currentLat, double currentLong)
    {
        this.currentLat = currentLat;
        this.currentLong = currentLong;
    }

    public void startTimer()
    {
        timer.start();
    }
}
