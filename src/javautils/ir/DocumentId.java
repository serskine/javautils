package javautils.ir;

public class DocumentId {
    public final String path;

    public DocumentId(final String path) {
        this.path = path;
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }

    @Override
    public String toString() {
        return path;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof DocumentId) {
            return this.path.equals(((DocumentId) other).path);
        } else {
            return false;
        }
    }
}
