package com.company;

import org.joda.time.DateTime;
import org.joda.time.Seconds;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

/**
 * Parser class.
 *
 * @author Ian Weeks, Max Bloy (09/07/2015).
 * @author Max Bloy
 * @author Amine Hajier
 */
public class Parser
{
    private LineReader lineReader;
    private DateTime startDate, dateTime;
    private double startLat, startLong, latitude, longitude, totalDistance, distFromStart;
    private int timeSkipper = 240;
    private ArrayList<Double> timeList, speedList, rpmList;
    private ArrayList<String> dayTimeList;
    private LineReader.Line line;
    private Seconds seconds;

    /**
     * Parser constructor.
     *
     * @param fileName The csv file to be parsed.
     */
    public Parser(String fileName)
    {
        try
        {
            //Line reader init and get the first usable line.
            lineReader = new LineReader(fileName);
            //First line for distance from base and time from start calculations.
            LineReader.Line firstLine = lineReader.getNextLine();
            //Rolling line to compare current and last line.
            line = firstLine;

            //Get the starting date and time.
            startDate = firstLine.getDate();
            dateTime = startDate;

            startLat = firstLine.getLatitude();
            startLong = firstLine.getLongitude();
            latitude = startLat;
            longitude = startLong;

            //Storage lists for writing output csv.
            timeList = new ArrayList<>();
            speedList = new ArrayList<>();
            rpmList = new ArrayList<>();
            dayTimeList = new ArrayList<>();

        } catch (IOException | ParseException e)
        {
            e.printStackTrace();
        }
    }


    /**
     * Class updates contained within.
     */
    public void update()
    {
        seconds = Seconds.secondsBetween(startDate, line.getDate());
        Seconds sectionSeconds = Seconds.secondsBetween(dateTime, line.getDate());
        dateTime = line.getDate();

        double sectionDist = getDistance(latitude, longitude, line.getLatitude(), line.getLongitude());
        totalDistance += sectionDist;

        latitude = line.getLatitude();
        longitude = line.getLongitude();

        double sectionSpeed = sectionDist / (sectionSeconds.getSeconds() * 0.000277778);
        speedList.add(Double.isNaN(sectionSpeed) ? 0 : sectionSpeed);

        double mcr = Math.exp(0.149993656 * sectionSpeed);


        double rpm = 12 * mcr + 800;

        rpmList.add(rpm);

        distFromStart = getDistance(startLat, startLong, line.getLatitude(), line.getLongitude());

        for (int i = 0; i < 1 + (timeSkipper); i++)
        {
            try
            {
                line = lineReader.getNextLine();
                if (line == null)
                {
                    //TODO: Name files based on date and time created.
                    FileWriter fileWriter = new FileWriter("output.csv");
                    /*
                    If the returned line is null that means the program
                    has reached the end of the file.
                    At this point write the stored data to a file and
                    exit the program.
                     */
                    for (int j = 0; j < timeList.size() && j < speedList.size(); j++)
                    {
                        fileWriter.write(String.format("%s, %f, %f, %f\n",
                                                       dayTimeList.get(i),
                                                       timeList.get(i),
                                                       speedList.get(i),
                                                       rpmList.get(i)));
                    }

                    System.exit(0);
                }
            } catch (IOException | ParseException e)
            {
                e.printStackTrace();
            }

        }
    }

    /**
     * Render class information to the screen.
     *
     * @param g2d The Graphics2D object in use.
     */
    public void render(Graphics2D g2d)
    {
        //Set text colour and font.
        g2d.setColor(Color.black);
        Font oldFont = g2d.getFont();
        Font newFont = oldFont.deriveFont(oldFont.getSize() * 2.0f);
        g2d.setFont(newFont);

        //Date time and elapsed time display.
        g2d.drawString("DATE/TIME:", 100, 30);
        DateTimeFormatter out = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
        g2d.drawString(String.format("%s", out.print(line.getDate())), 250, 30);
        dayTimeList.add(out.print(line.getDate()));

        g2d.drawString("ELAPSED:", 550, 30);
        g2d.drawString(String.format("%-4.1f hours @ %dx speed", seconds.getSeconds() * 0.000277778, (timeSkipper / 15)), 680, 30);
        timeList.add(seconds.getSeconds() * 0.000277778);

        //Pitch, roll, heave and heading display.
        g2d.drawString("PITCH", 100, 70);
        g2d.drawString(String.format("%5.2f", line.getPitch()), 100, 100);

        g2d.drawString("ROLL" , 330, 70);
        g2d.drawString(String.format("%5.2f", line.getRoll()), 325 , 100);

        g2d.drawString("HEAVE", 550, 70);
        g2d.drawString(String.format("%5.2f", line.getHeave()), 550, 100);

        g2d.drawString("HEADING", 750, 70);
        g2d.drawString(String.format("%5.2f", line.getHeading()), 770, 100);



        g2d.drawString("X Acceleration" , 100, 180);
        g2d.drawString(String.format("%5.2f", line.getAccelX()), 100 , 210);

        g2d.drawString("Y Acceleration", 330, 180);
        g2d.drawString(String.format("%5.2f", line.getAccelY()), 325, 210);

        g2d.drawString("Z Acceleration", 550, 180);
        g2d.drawString(String.format("%5.2f", line.getAccelZ()), 550, 210);

        g2d.drawString("LOCATION:", 100, 310);
        g2d.drawString(String.format("%f, %f", line.getLongitude(), line.getLatitude()), 250, 310);

        g2d.drawString("DISTANCE TRAVELLED:", 100, 380);
        g2d.drawString(String.format("%f", totalDistance), 390, 380);

        g2d.drawString("DISTANCE FROM BASE:", 100, 410);
        g2d.drawString(String.format("%f", distFromStart), 390, 410);
    }

    /**
     *
     */
    public void increaseParseSpeed()
    {
        if (timeSkipper < 240)
        {
            timeSkipper += 15;
        }
    }

    /**
     *
     */
    public void decreaseParseSpeed()
    {
        if (timeSkipper > 0)
        {
            timeSkipper -= 15;
        }
    }

    /**
     * Get the distance between two points on earth using latitude and longitude.
     *
     * @param startLat The starting latitude.
     * @param startLong The starting longitude.
     * @param endLat The ending latitude.
     * @param endLong The ending longitude.
     * @return The distance between the two points.
     */
    private double getDistance(double startLat, double startLong, double endLat, double endLong)
    {
        double latDistance = Math.toRadians(startLat - endLat);
        double lngDistance = Math.toRadians(startLong - endLong);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(startLat)) * Math.cos(Math.toRadians(endLat))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        //Distance in nautical miles.
        final double AVERAGE_RADIUS_OF_EARTH = 6371;
        return AVERAGE_RADIUS_OF_EARTH * c * 0.539956803;
    }
}
