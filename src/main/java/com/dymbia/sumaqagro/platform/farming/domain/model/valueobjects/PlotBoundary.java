package com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Polygon of a field plot
 *
 * @param vertices the vertices of the polygon in order
 * @since 1.0
 */
public record PlotBoundary(List<GeoCoordinate> vertices) {

    /**
         * The radius of the Earth in meters.
     */
    private static final double EARTH_RADIUS_METERS = 6_371_008.8;
    private static final double SQUARE_METERS_PER_HECTARE = 10_000;

    /**
     * Creates a new plot boundary with the specified vertices.
     *
     * @param vertices the vertices of the polygon in order
     * @throws IllegalArgumentException if the vertices are invalid
     */
    public PlotBoundary {
        if (vertices == null || vertices.isEmpty()) {
            throw new IllegalArgumentException("field.plot.error.boundary.minVertices");
        }
        var openRing = new ArrayList<>(vertices);
        if (openRing.size()>1 && openRing.getFirst().equals(openRing.getLast())) {
            openRing.removeLast();
        }
       if (openRing.stream().distinct().count() < 3) {
           throw new IllegalArgumentException("field.plot.error.boundary.minVertices");
       }
       if (hasSelfIntersection(openRing)) {
           throw new IllegalArgumentException("field.plot.error.boundary.selfIntersection");
       }
       vertices=List.copyOf(openRing);

    }

    /**
     * Calculates the area of the polygon in hectares.
     *
     * @return the area in hectares with 2 decimals
     */
    public BigDecimal calculateArea()
    {
        double meanLatitude =
                Math.toRadians(vertices.stream().mapToDouble(GeoCoordinate::latitude).average().orElse(0));
        double doubleArea = 0;
        for (int i = 0; i < vertices.size(); i++) {
            var current = vertices.get(i);
            var next = vertices.get((i + 1) % vertices.size());
            doubleArea += x(current, meanLatitude) * y(next) - x(next, meanLatitude) * y(current);
        }
       double squareMeters = Math.abs(doubleArea) / 2;
       return BigDecimal.valueOf(squareMeters/SQUARE_METERS_PER_HECTARE).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Returns a new plot boundary with the vertices in counter-clockwise order.
     *
     * @return a new plot boundary with the vertices in counter-clockwise order
     */
    public PlotBoundary counterClockwise() {
        if (signedArea()>0) {
            return this;
        }
        var reversed = new ArrayList<>(vertices);
        Collections.reverse(reversed);
        return new PlotBoundary(reversed);
    }

    /**
     * Returns the vertices with the first one repeated at the end.
     *
     * @return the closed ring of vertices
     */
    public List<GeoCoordinate> closedRing()
    {
        var ring = new ArrayList<>(vertices);
        ring.add(vertices.getFirst());
        return List.copyOf(ring);
    }

    // Calculates the signed area of the polygon (x = longitude, y = latitude)
    private double signedArea(){
        double sum = 0;
        for (int i =0; i<vertices.size(); i++)
        {
            var current = vertices.get(i);
            var next = vertices.get((i+1)% vertices.size());
            sum += current.longitude()*next.latitude()-next.longitude()*current.latitude();
        }
        return sum/2;
    }

    private static double x(GeoCoordinate coordinate, double meanLatitude) {
        return Math.toRadians(coordinate.longitude())*Math.cos(meanLatitude)*EARTH_RADIUS_METERS;
    }

    private static double y(GeoCoordinate coordinate)
    {
        return Math.toRadians(coordinate.latitude())*EARTH_RADIUS_METERS;
    }

    /**
     * Checks if the given ring has self-intersections.
     *
     * @param ring the ring of geo coordinates
     * @return true if the ring has self-intersections, false otherwise
     */
    private static boolean hasSelfIntersection(List<GeoCoordinate> ring) {
        int size=ring.size();
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                boolean adjacent = j == i+1 || (i==0 && j==size-1);
                if (!adjacent && segmentIntersects(ring.get(i), ring.get((i + 1) % size), ring.get(j),
                        ring.get((j + 1) % size))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if two line segments intersect.
     *
     * @param a the first endpoint of the first segment
     * @param b the second endpoint of the first segment
     * @param c the first endpoint of the second segment
     * @param d the second endpoint of the second segment
     *
     * @return true if the segments intersect, false otherwise
     */
    private static boolean segmentIntersects(GeoCoordinate a, GeoCoordinate b,
                                             GeoCoordinate c, GeoCoordinate d) {
        int o1 = orientation(a, b, c);
        int o2 = orientation(a, b, d);
        int o3 = orientation(c, d, a);
        int o4 = orientation(c, d, b);
        if (o1 != o2 && o3 != o4) {
            return true;
        }
        return (o1 == 0 && onSegment(a,c,b) || o2 == 0 && onSegment(a,d,b)) || (o3 == 0 && onSegment(c,a,d) || o4 == 0 && onSegment(c,b,d));
    }

    /**
     * Calculates the orientation of three geo coordinates.
     *
     * @return 0 if the points are collinear, 1 if clockwise, 2 if counter-clockwise
     */
    private static int orientation(GeoCoordinate a, GeoCoordinate b, GeoCoordinate c) {
        double val =
                (b.latitude() - a.latitude()) * (c.longitude() - b.longitude()) - (b.longitude() - a.longitude()) * (c.latitude() - b.latitude());
        if (val == 0) {
            return 0;
        }
        return (val > 0) ? 1 : 2;
    }

    /**
     * Checks if a geo coordinate lies on a line segment.
     *
     * @return true if the geo coordinate lies on the segment, false otherwise
     */
    private static boolean onSegment(GeoCoordinate a, GeoCoordinate b, GeoCoordinate c) {
        return b.longitude() <= Math.max(a.longitude(), c.longitude()) && b.longitude() >= Math.min(a.longitude(),
                c.longitude()) &&
                b.latitude() <= Math.max(a.latitude(), c.latitude()) && b.latitude() >= Math.min(a.latitude(),
                c.latitude());

    }

}
