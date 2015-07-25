package com.evive.ImageScanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Utils {

    private static final Logger LOG = LoggerFactory.getLogger(Utils.class);

    public static <T extends Number> Double EUCLIDEAN_DISTANCE(final T[] coordinates1, final T[] coordinates2) {
        final Double xdiff = (Double) coordinates1[0] - (Double) coordinates2[0];
        final Double ydiff = (Double) coordinates1[1] - (Double) coordinates2[1];

        return xdiff * xdiff + ydiff * ydiff;
    }

    public static <T extends Number> Double EUCLIDEAN_DISTANCE(final T[][] coordinates, final int i) {
        final Double xdiff = (Double) coordinates[i][0] - (Double) coordinates[i - 1][0];
        final Double ydiff = (Double) coordinates[i][1] - (Double) coordinates[i - 1][1];

        return xdiff * xdiff + ydiff * ydiff;
    }

    public static <T> List<T> SWAP(T x, T y) {
        final List<T> vals = new ArrayList<T>();
        final T tmp = x;
        x = y;
        y = tmp;
        vals.add(x);
        vals.add(y);
        return vals;
    }

    /**
     * Loads the property file.
     */
    public static Properties getProperties() {
        final Properties properties = new Properties();
        try (FileInputStream fileInput = new FileInputStream(new File("src/main/resources/data.properties"))) {
            properties.load(fileInput);

        } catch (final FileNotFoundException e) {
            LOG.error(e.getMessage(), e);

        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
        }
        return properties;
    }

}
