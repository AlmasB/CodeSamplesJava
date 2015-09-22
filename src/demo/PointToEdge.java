package demo;

public class PointToEdge {

    public static void main(String[] args) {

    }

    private static float distBetweenPointAndLine(float x, float y, float x1, float y1, float x2, float y2) {
        // A - the standalone point (x, y)
        // B - start point of the line segment (x1, y1)
        // C - end point of the line segment (x2, y2)
        // D - the crossing point between line from A to BC

        float AB = distBetween(x, y, x1, y1);
        float BC = distBetween(x1, y1, x2, y2);
        float AC = distBetween(x, y, x2, y2);

        // Heron's formula
        float s = (AB + BC + AC) / 2;
        float area = (float) Math.sqrt(s * (s - AB) * (s - BC) * (s - AC));

        // but also area == (BC * AD) / 2
        // BC * AD == 2 * area
        // AD == (2 * area) / BC
        float AD = (2 * area) / BC;
        return AD;
    }

    private static float distBetween(float x, float y, float x1, float y1) {
        float xx = x1 - x;
        float yy = y1 - y;

        return (float) Math.sqrt(xx * xx + yy * yy);
    }
}
