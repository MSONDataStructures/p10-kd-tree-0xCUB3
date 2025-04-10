import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.Queue;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class PointSET {
    private final TreeSet<Point2D> points;

    public PointSET() {
        points = new TreeSet<>();
    }

    public boolean isEmpty() {
        return points.isEmpty();
    }

    public int size() {
        return points.size();
    }

    public void insert(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("argument to insert() is null");
        }
        points.add(p);
    }

    public boolean contains(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("argument to contains() is null");
        }
        return points.contains(p);
    }

    public void draw() {
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        for (Point2D p : points) {
            p.draw();
        }
    }

    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new IllegalArgumentException("argument to range() is null");
        }
        List<Point2D> pointsInRange = new ArrayList<>();
        for (Point2D p : points) {
            if (rect.contains(p)) {
                pointsInRange.add(p);
            }
        }
        return pointsInRange;
    }

    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("argument to nearest() is null");
        }
        if (isEmpty()) {
            return null;
        }

        Point2D nearestPoint = null;
        double minDistanceSquared = Double.POSITIVE_INFINITY;

        for (Point2D pointInSet : points) {
            double distanceSquared = p.distanceSquaredTo(pointInSet);
            if (distanceSquared < minDistanceSquared) {
                minDistanceSquared = distanceSquared;
                nearestPoint = pointInSet;
            }
        }
        return nearestPoint;
    }

    public static void main(String[] args) {
    }
}