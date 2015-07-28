package com.company;

import com.esri.client.local.LocalMapService;
import com.esri.client.local.WorkspaceInfo;
import com.esri.client.local.WorkspaceInfoSet;
import com.esri.core.geometry.*;
import com.esri.core.geometry.Point;
import com.esri.core.map.*;
import com.esri.core.renderer.SimpleRenderer;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.map.*;
import com.esri.runtime.ArcGISRuntime;

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
 * With ArcGIS.
 *
 * @author Ian Weeks (09/07/2015).
 */
public class MapDisplay extends JFrame
{
    private final int TRANSPARENCY = 20; // 0 is opaque, 100 is transparent
    private final String FSP = System.getProperty("file.separator");
    private JMap map;
    private int pointerID, lineID, counter;
    private double currentLat, currentLong;
    private boolean firstUpdate = true;
    private Timer timer;
    private Point point;
    private SimpleLineSymbol simpleLineSymbol;
    private Polyline polyline;
    private SpatialReference sr;
    private SimpleRenderer simpleRenderer = new SimpleRenderer(new SimpleLineSymbol(new Color(0, 100, 250), 3));
    private PictureMarkerSymbol pms;

    /**
     * MapDisplay class.
     *
     * ArcGIS api used exclusively for map display.
     *
     * Quick disclaimer... This was my first time using the API...
     *
     * @throws IOException If the image file cannot be found or read.
     */
    public MapDisplay() throws IOException
    {
        final Image pointerImage = ImageIO.read(new File("RedShinyPin.png"));
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

        // create a local map service and enable dynamic layers
        LocalMapService localMapService = new LocalMapService(
                getPathSampleData() + "mpks" + FSP + "mpk_blank.mpk");
        localMapService.setEnableDynamicLayers(true);

        // get dynamic workspaces from service
        WorkspaceInfoSet workspaceInfoSet = localMapService.getDynamicWorkspaces();

        WorkspaceInfo workspaceInfo = WorkspaceInfo.CreateShapefileFolderConnection(
                "WORKSPACE", "C:\\Users\\Twiz\\Dropbox\\Java Projects\\VMMS_Parser");

        // set dynamic workspaces for our local map service
        workspaceInfoSet.add(workspaceInfo);
        localMapService.setDynamicWorkspaces(workspaceInfoSet);

        // now start service...
        localMapService.start();

        // set up a local dynamic layer
        final ArcGISDynamicMapServiceLayer localDynamicLayer = new ArcGISDynamicMapServiceLayer(
                localMapService.getUrlMapService());

        // add the layer to the map
        map.getLayers().add(localDynamicLayer);

        localDynamicLayer
                .addLayerInitializeCompleteListener(new LayerInitializeCompleteListener()
                {
                    @Override
                    public void layerInitializeComplete(LayerInitializeCompleteEvent arg0)
                    {
                        if (arg0.getID() == LayerInitializeCompleteEvent.LOCALLAYERCREATE_ERROR)
                        {
                            String errMsg = "Failed to initialize due to "
                                    + localDynamicLayer.getInitializationError();
                            showErrorMsg(errMsg);
                        }
                        DynamicLayerInfoCollection layerInfoCollection = localDynamicLayer
                                .getDynamicLayerInfos();
                        DynamicLayerInfo layerInfo = layerInfoCollection.get(0);

            /*
             * Apply a renderer for dynamic layers. Note: It is always necessary
             * to provide a renderer, but the renderer provided does not need to
             * be valid with regard to the actual layer and geometry type, it
             * simply needs to be a valid renderer. If the renderer specified
             * here is not appropriate for the geometry type of the layer the
             * symbology will fall back to a default SimpleMarkerSymbol,
             * SimpleLineSymbol or SimpleFillSymbol.
             */
                        DrawingInfo drawingInfo = new DrawingInfo(simpleRenderer,
                                                                  TRANSPARENCY);
                        layerInfo.setDrawingInfo(drawingInfo);

                        // Create the data source
                        TableDataSource dataSource = new TableDataSource();
                        dataSource.setWorkspaceId("WORKSPACE");
                        dataSource.setDataSourceName("GREATER GABBARD.shp");

                        // Set the data source
                        LayerDataSource layerDataSource = new LayerDataSource();
                        layerDataSource.setDataSource(dataSource);
                        layerInfo.setLayerSource(layerDataSource);

                        localDynamicLayer.refresh();
                    }
                });

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
                pms = new PictureMarkerSymbol((BufferedImage) pointerImage);
                Graphic pointGraphic = new Graphic(point, pms);
                Graphic lineGraphic = new Graphic(polyline, simpleLineSymbol);

                lineID = graphicsLayer.addGraphic(lineGraphic);
                pointerID = graphicsLayer.addGraphic(pointGraphic);
                System.out.println("POINTER ID : " + pointerID);
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


                if (counter > 10 && map.isReady())
                {
                    if (firstUpdate)
                    {
                        polyline.setEmpty();
                        polyline.startPath(GeometryEngine.project(195692.61151359137, 6886509.9789550705, sr));
                        firstUpdate = false;
                        System.out.println(currentLat + " " + currentLong);
                    }
                    else
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

    /**
     * Set the current position of the vessel.
     *
     * @param currentLat The current latitude of the vessel.
     * @param currentLong The current longitude of the vessel.
     */
    public void setCurrentPos(double currentLat, double currentLong)
    {
        this.currentLat = currentLat;
        this.currentLong = currentLong;
    }

    /**
     * Start the map update timer.
     */
    public void startTimer()
    {
        timer.start();
    }

    /**
     * Get the path to the sample data.
     *
     * @return The path to the sample data.
     */
    private String getPathSampleData()
    {
        String dataPath = null;
        String javaPath = ArcGISRuntime.getInstallDirectory();
        if (javaPath != null)
        {
            if (!(javaPath.endsWith("/") || javaPath.endsWith("\\")))
            {
                javaPath += FSP;
            }
            dataPath = javaPath + "sdk" + FSP + "samples" + FSP + "data" + FSP;
        }
        assert dataPath != null;
        File dataFile = new File(dataPath);
        if (!dataFile.exists())
        {
            dataPath = ".." + FSP + "data" + FSP;
        }
        return dataPath;
    }

    /**
     * Display a given error message in a pop up window.
     *
     * @param message The error message to display.
     */
    private void showErrorMsg(String message)
    {
        JOptionPane.showMessageDialog(null, message, "", JOptionPane.ERROR_MESSAGE);
    }
}
