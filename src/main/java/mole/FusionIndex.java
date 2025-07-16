package mole;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * {@code FusionIndex} is an immutable index (optionally named) that represents the fusion of two 
 * other indexes: it contains every label of the first index followed, in order, by the labels 
 * of the second one that are not already present in the first one.
 * 
 * Given the indexes
 *    this = [0, 1, 2, 5]
 *    other = [0, 3, 2, 6, 9]
 * the merged index will be
 *    merged = [0, 1, 2, 5, 3, 6, 9]
 * 
 * This "intersection" of two indexes holds the contract of {@link Index}, as each label is distinct and non-null.
 *
 * <p>Two {@code FusionIndex} instances are equal iff they expose the same
 * sequence of labels in the same order.</p>
 *
 * <p>Note: the operation is not commutative; {@code FusionIndex(a,b)}
 * and {@code FusionIndex(b,a)} generally differ.</p>
 */
public final class FusionIndex implements Index {

    /** Optional name of this index (may be {@code null}). */
    private final String name;
    /** The first index, whose labels always appear first in the fusion. */
    private final Index first;
    /** The second index, from which only labels not in the first are taken. */
    private final Index second;
    /** The total number of labels in this fusion view. */
    private final int size;

    /*-
     * RI:
     * 
     *  - first != null
     *  - second != null
     *     (internal conditions of first and second are garanteed by their contracts)
     *  - name == null || !name.isBlank()
     *  - size == first.size() + |{label | label \in second, first.positionOf(l) == -1}|
     *  - name and size are immutable for the lifetime of this index
     * 
     * AF:
     * 
     *  - AF(this) = the abstract index I of length size such that
     *                   I[i] = first.labelAt(i) for 0 <= i < first.size()
     *               else
     *                   I[i] = the (i - first.size())-th label of second not in first
     *  - The index may have a name.
     *  - The labels are ordered in the same way as they were provided at construction time 
     *    (containing all labels of the first index, followed by those of the second index that are not in the first).
     */

    /**
     * Creates a {@code FusionIndex} with the given {@code name}, {@code first} and {@code second}.
     *
     * @param name the name of the index (may be {@code null})
     * @param first the first index
     * @param second the second index
     * @throws NullPointerException if {@code first} or {@code second} is {@code null}
     * @throws IllegalArgumentException if {@code name} is blank
     */
    public FusionIndex(final String name, final Index first, final Index second) {
        Objects.requireNonNull(first);
        Objects.requireNonNull(second);
        if (name != null && name.isBlank()) {
            throw new IllegalArgumentException("name cannot be blank");
        }
        int extraCount = 0;
        for (Object lbl : second) {
            if (first.positionOf(lbl) < 0) extraCount++;
        }

        this.first = first;
        this.second = second;
        this.name = name;
        this.size = first.size() + extraCount;
    }

    /**
     * Creates an unnamed {@code FusionIndex} with the given {@code first} and {@code second}.
     *
     * @param first the first index
     * @param second the second index
     * @throws NullPointerException if {@code first} or {@code second} is {@code null}
     */
    public FusionIndex(final Index first, final Index second) {
        this(null, first, second);
    }

    @Override
    public FusionIndex rename(final String newName) {
        if (newName != null && newName.isBlank()) {
            throw new IllegalArgumentException("name cannot be blank");
        }
        if (Objects.equals(this.name, newName)) return this;
        return new FusionIndex(newName, this.first, this.second);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Object labelAt(final int pos) {
        if (pos < 0 || pos >= size) {
            throw new IndexOutOfBoundsException("Position out of bounds");
        }
        int fsz = first.size();
        if (pos < fsz) {
            return first.labelAt(pos);
        } else {
            int target = pos - fsz;
            int count = 0;
            for (Object lbl : second) {
                if (first.positionOf(lbl) < 0) {
                    if (count++ == target) return lbl;
                }
            }
            throw new IndexOutOfBoundsException("Position out of bounds");
        }
    }

    @Override
    public int positionOf(final Object label) {
        Objects.requireNonNull(label, "label cannot be null");

        int p = first.positionOf(label);
        if (p >= 0) return p;
        int idx = first.size();

        for (Object lbl : second) {
            if (first.positionOf(lbl) < 0) {
                if (Objects.equals(lbl, label)) return idx;
                idx++;
            }
        }
        return -1;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Iterator<Object> iterator() {
        return new Iterator<Object>() {
            private final Iterator<Object> it1 = first.iterator();
            private final Iterator<Object> it2 = second.iterator();
            private Object nextFrom2 = null;

            @Override
            public boolean hasNext() {
                if (it1.hasNext()) return true;
                if (nextFrom2 != null) return true;
                while (it2.hasNext()) {
                    Object cand = it2.next();
                    if (first.positionOf(cand) < 0) {
                        nextFrom2 = cand;
                        return true;
                    }
                }
                return false;
            }

            @Override
            public Object next() {
                if (it1.hasNext()) {
                    return it1.next();
                }
                if (hasNext()) {
                    Object r = nextFrom2;
                    nextFrom2 = null;
                    return r;
                }
                throw new NoSuchElementException("No more elements in the index");
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Remove operation is not supported");
            }
        };
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof FusionIndex other)) return false;
        if (size != other.size) return false;
        var a = this.iterator();
        var b = other.iterator();
        while (a.hasNext()) {
            if (!Objects.equals(a.next(), b.next())) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int h = 1;
        for (Object label : this) {
            h = 31 * h + (label == null ? 0 : label.hashCode());
        }
        return h;
    }

    @Override
    public String toString() {
        return toColumnString();
    }
}