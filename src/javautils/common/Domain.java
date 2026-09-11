package javautils.common;

import java.util.Objects;

public class Domain<ID> {
    protected ID id;

    public final ID getId() {
        return this.id;
    }

    public final void setId(ID id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public boolean equals(Object other) {
        if (other==null) {
            return false;
        } else if (other instanceof Domain && getClass().isAssignableFrom(other.getClass())) {
            final Domain otherDomain = (Domain) other;
            return Objects.equals(getId(), otherDomain.getId());
        } else {
            return false;
        }
    }
}
