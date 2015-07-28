package com.company;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.LinkedList;

/**
 * LineReader class.
 *
 * Reads a text file line by line until the end.
 *
 * @author Ian Weeks (09/07/2015).
 * @author Max Bloy (07/07/2015).
 */
public class LineReader
{
    private LinkedList<Line> lineList;

    /**
     * LineReader constructor.
     *
     * @param filePath The file path of the text file.
     * @throws IOException If the file cannot be read.
     */
    public LineReader(String filePath) throws IOException
    {
        File inputData = new File(filePath);


        BufferedReader reader = new BufferedReader(new FileReader(inputData));

        lineList = new LinkedList<>();
        //Skip the first line which only contains column titles.
        reader.readLine();

        DateTimeFormatter format = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss.SSS");
        String nextLine;

        //Load the entire file into memory.
        while ((nextLine = reader.readLine()) != null)
        {
            //Split the file at each comma.
            String[] splitLine = nextLine.split(",");

            //Return a Line object. Felt this was clearer than returning a raw array.
            Line line = new Line
                    (
                            format.parseDateTime(splitLine[0] + " " + splitLine[1]),
                            Double.parseDouble(splitLine[2]),
                            Double.parseDouble(splitLine[3]),
                            Double.parseDouble(splitLine[4]),
                            Double.parseDouble(splitLine[5]),
                            Double.parseDouble(splitLine[12]),
                            Double.parseDouble(splitLine[13]),
                            Double.parseDouble(splitLine[14]),
                            Double.parseDouble(splitLine[23]),
                            Double.parseDouble(splitLine[24])
                    );
            lineList.add(line);
        }

        /*
         * If the line is null, i.e. The reader has reached the end
         * of the file. Close the buffered read stream and return null.
         */
        reader.close();
        System.out.printf("Lines loaded: %d.\n", lineList.size());
    }

    /**
     * Get the next line of the file.
     *
     * @return The next line.
     * @throws IOException If the file cannot be read.
     * @throws ParseException If the information cannot be parsed.
     */
    public Line getNextLine() throws IOException, ParseException
    {
        System.out.println(lineList.size());
        if (!lineList.isEmpty())
        {
            return lineList.removeFirst();
        }
        return null;
    }

    /**
     * Line class.
     *
     * Clarity only.
     */
    public class Line
    {
        /*
         * Raw data column format.
         *
         *0 Date
         *1 Time
         *2 Heave (m)
         *3 Pitch (degrees)
         *4 Roll (degrees)
         *5 Heading (degrees)
         *6 Heave Min (m)
         *7 Pitch Min (degrees)
         *8 Roll Min (degrees)
         *9 Heave Max (m)
         *10 Pitch Max (degrees)
         *11 Roll Max (degrees)
         *12 Acceleration X (ms2)
         *13 Acceleration Y (ms2)
         *14 Acceleration Z (ms2)
         *15 Heave RMS (degrees)
         *16 Pitch RMS (degrees)
         *17 Roll RMS (degrees)
         *18 Heave Tz (seconds)
         *19 Pitch Tz (seconds)
         *20 Roll Tz (seconds)
         *21 Within Preset Limits
         *22 Transferring
         *23 Latitude (degrees)
         *24 Longitude (degrees)
         */

        private DateTime date;
        private double heave, pitch, roll, heading, accelX, accelY, accelZ, longitude, latitude;

        /**
         * Line constructor.
         *
         * @param date The date/time value for this line.
         * @param heave The heave value for this line.
         * @param pitch The pitch value for this line.
         * @param roll The roll value for this line.
         * @param heading The heading value for this line.
         * @param accelX The x axis acceleration value for this line.
         * @param accelY The y axis acceleration value for this line.
         * @param accelZ The z axis acceleration value for this line.
         * @param longitude The longitude value for this line.
         * @param latitude The latitude value for this line.
         */
        public Line(DateTime date, double heave,
                    double pitch, double roll,
                    double heading, double accelX,
                    double accelY, double accelZ,
                    double longitude, double latitude)
        {
            this.date = date;
            this.heave = heave;
            this.pitch = pitch;
            this.roll = roll;
            this.heading = heading;
            this.accelX = accelX;
            this.accelY = accelY;
            this.accelZ = accelZ;
            this.longitude = longitude;
            this.latitude = latitude;
        }

        /**
         * Get this line's date/time.
         *
         * @return The date/time value for this line.
         */
        public DateTime getDate()
        {
            return date;
        }

        /**
         * Get this line's heave value.
         *
         * @return The heave value for this line.
         */
        public double getHeave()
        {
            return heave;
        }

        /**
         * Get this line's pitch value.
         *
         * @return The pitch value for this line.
         */
        public double getPitch()
        {
            return pitch;
        }

        /**
         * Get this line's roll value.
         *
         * @return The roll value for this line.
         */
        public double getRoll()
        {
            return roll;
        }

        /**
         * Get this line's heading value.
         *
         * @return The heading value for this line.
         */
        public double getHeading()
        {
            return heading;
        }

        /**
         * Get this line's x axis acceleration value.
         *
         * @return The x axis acceleration value for this line.
         */
        public double getAccelX()
        {
            return accelX;
        }

        /**
         * Get this line's y axis acceleration value.
         *
         * @return The y axis acceleration value for this line.
         */
        public double getAccelY()
        {
            return accelY;
        }

        /**
         * Get this line's z axis acceleration value.
         *
         * @return The z axis acceleration value for this line.
         */
        public double getAccelZ()
        {
            return accelZ;
        }

        /**
         * Get this line's longitude value.
         *
         * @return The longitude value for this line.
         */
        public double getLongitude()
        {
            return longitude;
        }

        /**
         * Get this line's latitude value.
         *
         * @return The latitude value for this line.
         */
        public double getLatitude()
        {
            return latitude;
        }

        /**
         * Get a string representation of this object.
         *
         * @return A string representation of this object.
         */
        @Override
        public String toString()
        {
            DateTimeFormatter out = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
            return "Line:\n" +
                    "Date       = " + out.print(date) + "\n" +
                    "Heave      = " + heave           + "\n" +
                    "Pitch      = " + pitch           + "\n" +
                    "Roll       = " + roll            + "\n" +
                    "Heading    = " + heading         + "\n" +
                    "AccelX     = " + accelX          + "\n" +
                    "AccelY     = " + accelY          + "\n" +
                    "AccelZ     = " + accelZ          + "\n" +
                    "Longitude  = " + longitude       + "\n" +
                    "Latitude   = " + latitude        + "\n";
        }
    }
}
