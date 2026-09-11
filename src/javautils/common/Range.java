package javautils.common;

/**
 * This class supports infinite ranges by specifying a null value
 */
public class Range {
    public final Number start;
    public final Number end;

    public static final Range INFINITE_RANGE = new Range(null, null);
    public static final Range NO_RANGE = new Range(0D, 0D);
    public static final Range POSITIVE_NUMBERS = new Range(0D, null);
    public static final Range NEGATIVE_NUMBERS = new Range(null, 0D);

    public Range(final Number start, final Number end) {
        if (start != null && end != null && start.doubleValue() <= end.doubleValue()) {
            this.start = start;
            this.end = end;
        } else {
            this.start = end;
            this.end = start;
        }
    }

    public boolean contains(final Range range) {
        if (range.start == null && start != null) {
            return false;
        } else if (range.end == null && end != null) {
            return false;
        } else if (range.start == null) {
            return contains(range.end);
        } else if (range.end == null) {
            return contains(range.start);
        } else {
            return (contains(range.start) && contains(range.end));
        }
    }

    public boolean contains(final Number n) {
        if (n==null) {
            return false;
        } else if (start==null && end==null) {
            return true;
        } else if (start==null) {
            return (n.doubleValue() < end.doubleValue());
        } else if (end==null) {
            return (n.doubleValue() >= start.doubleValue());
        } else {
            return (n.doubleValue() >= start.doubleValue()) && (n.doubleValue() < end.doubleValue());
        }
    }

    public final boolean isInfinite() {
        return (start==null) || (end==null);
    }

    public Double size() {
        if (isInfinite()) {
            return Double.POSITIVE_INFINITY;
        } else {
            return end.doubleValue() - start.doubleValue();
        }
    }
}
