package hr.ooup.lab3.model;

import java.util.Objects;

public class LocationRange {
    private Location start;
    private Location end;

    public LocationRange(Location start, Location end) {
        this.start = start;
        this.end = end;
    }

    public Location getStart() {
        return start;
    }

    public Location getEnd() {
        return end;
    }

    public void setStart(Location start) {
        this.start = start;
    }

    public void setEnd(Location end) {
        this.end = end;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocationRange that = (LocationRange) o;
        return Objects.equals(start, that.start) && Objects.equals(end, that.end);
    }

    public int hashCode() {
        return Objects.hash(start, end);
    }
}
