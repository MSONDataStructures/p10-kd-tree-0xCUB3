import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.Queue;


public class KdTree {

    private static final boolean VERTICAL = true;
    private static final boolean HORIZONTAL = false;

    private Node root;
    private int count;

    private static class Node {
        private final Point2D p;
        private final RectHV rect;
        private Node lb;
        private Node rt;

        public Node(Point2D p, RectHV rect) {
            this.p = p;
            this.rect = rect;
        }
    }

    public KdTree() {
        root = null;
        count = 0;
    }

    public boolean isEmpty() {
        return count == 0;
    }

    public int size() {
        return count;
    }

    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException("argument to insert() is null");
        root = insert(root, p, 0.0, 0.0, 1.0, 1.0, VERTICAL);
    }

    private Node insert(Node h, Point2D p, double xmin, double ymin, double xmax, double ymax, boolean orientation) {
        if (h == null) {
            count++;
            RectHV rect = new RectHV(xmin, ymin, xmax, ymax);
            return new Node(p, rect);
        }

        if (h.p.equals(p)) {
            return h; // Point alr exists
        }

        double cmp;
        if (orientation == VERTICAL) {
            cmp = p.x() - h.p.x();
            if (cmp < 0) {
                h.lb = insert(h.lb, p, xmin, ymin, h.p.x(), ymax, HORIZONTAL);
            } else {
                h.rt = insert(h.rt, p, h.p.x(), ymin, xmax, ymax, HORIZONTAL);
            }
        } else {
            cmp = p.y() - h.p.y();
            if (cmp < 0) {
                h.lb = insert(h.lb, p, xmin, ymin, xmax, h.p.y(), VERTICAL);
            } else {
                h.rt = insert(h.rt, p, xmin, h.p.y(), xmax, ymax, VERTICAL);
            }
        }

        return h;
    }

    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException("argument to contains() is null");
        return contains(root, p, VERTICAL);
    }

    private boolean contains(Node h, Point2D p, boolean orientation) {
        if (h == null) {
            return false;
        }
        if (h.p.equals(p)) {
            return true;
        }

        double cmp;
        if (orientation == VERTICAL) {
            cmp = p.x() - h.p.x();
        } else {
            cmp = p.y() - h.p.y();
        }

        if (cmp < 0) {
            return contains(h.lb, p, !orientation);
        } else {
            return contains(h.rt, p, !orientation);
        }
    }

    public void draw() {
        draw(root, VERTICAL);
    }

    private void draw(Node h, boolean orientation) {
        if (h == null) return;

        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        h.p.draw();

        StdDraw.setPenRadius();
        if (orientation == VERTICAL) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(h.p.x(), h.rect.ymin(), h.p.x(), h.rect.ymax());
        } else {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.line(h.rect.xmin(), h.p.y(), h.rect.xmax(), h.p.y());
        }

        draw(h.lb, !orientation);
        draw(h.rt, !orientation);
    }

    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException("argument to range() is null");
        Queue<Point2D> pointsInRange = new Queue<>();
        range(root, rect, pointsInRange);
        return pointsInRange;
    }

    private void range(Node h, RectHV queryRect, Queue<Point2D> pointsInRange) {
        if (h == null) return;
        if (!queryRect.intersects(h.rect)) return;

        if (queryRect.contains(h.p)) {
            pointsInRange.enqueue(h.p);
        }

        range(h.lb, queryRect, pointsInRange);
        range(h.rt, queryRect, pointsInRange);
    }


    private Point2D champion;
    private double championDistSq;

    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException("argument to nearest() is null");
        if (isEmpty()) return null;

        champion = null;
        championDistSq = Double.POSITIVE_INFINITY;

        nearest(root, p, VERTICAL);
        return champion;
    }

    private void nearest(Node h, Point2D queryPoint, boolean orientation) {
        if (h == null) return;

        // rule: If the node's rectangle is farther than the champion, stop.
        if (h.rect.distanceSquaredTo(queryPoint) >= championDistSq) {
            return;
        }

        // find out if the current node's point is closer than the champion
        double distSq = h.p.distanceSquaredTo(queryPoint);
        if (distSq < championDistSq) {
            champion = h.p;
            championDistSq = distSq;
        }

        // see which subtree is potentially closer
        Node first, second;
        double cmp;
        if (orientation == VERTICAL) {
            cmp = queryPoint.x() - h.p.x();
        } else { // HORIZONTAL SKULA IT'S HORIZONTAL
            cmp = queryPoint.y() - h.p.y();
        }

        if (cmp < 0) {
            first = h.lb;
            second = h.rt;
        } else {
            first = h.rt;
            second = h.lb;
        }

        // Recursively search
        nearest(first, queryPoint, !orientation);
        nearest(second, queryPoint, !orientation);
    }


    public static void main(String[] args) {
        // Optional unit testing
        KdTree kdtree = new KdTree();
        kdtree.insert(new Point2D(0.7, 0.2));
        kdtree.insert(new Point2D(0.5, 0.4));
        kdtree.insert(new Point2D(0.2, 0.3));
        kdtree.insert(new Point2D(0.4, 0.7));
        kdtree.insert(new Point2D(0.9, 0.6));

        System.out.println("Size: " + kdtree.size()); // Expected 5
        System.out.println("Contains (0.4, 0.7): " + kdtree.contains(new Point2D(0.4, 0.7)));
        System.out.println("Contains (0.5, 0.5): " + kdtree.contains(new Point2D(0.5, 0.5)));

        // Test range search
        RectHV rect = new RectHV(0.1, 0.1, 0.6, 0.8);
        System.out.println("Points in rectangle " + rect + ":");
        for (Point2D p : kdtree.range(rect)) {
            System.out.println("  " + p);
        }

        // Test nearest neighbor
        Point2D query = new Point2D(0.81, 0.3);
        Point2D nearest = kdtree.nearest(query);
        System.out.println("Nearest point to " + query + " is " + nearest);

        Point2D query2 = new Point2D(0.1, 0.9);
        Point2D nearest2 = kdtree.nearest(query2);
        System.out.println("Nearest point to " + query2 + " is " + nearest2);

    }
}