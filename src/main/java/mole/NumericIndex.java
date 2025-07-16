package mole;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Immutable implementation of {@link Index} whose labels are the terms of an arithmetic progression 
 * {@code start, start + step, …} (exclusive {@code end}).
 * The arithmetic progression garantees that all labels are distinct and non-null, holding the contract of {@link Index}.
 * 
 * Implements all the operations defined by {@code Index}. All labels are {@link Integer} instances.
 * 
 * <p>Two {@code NumericIndex} instances are equal iff they contain the same labels in the same order.</p>
 */
public final class NumericIndex implements Index {

    /** Inclusive lower bound of the progression */
    private final int start;
    /** Exclusive upper bound of the progression */
    private final int end;
    /** The step used in the progression */
    private final int step;
    /** Optional name of the index (may be {@code null}). */
    private final String name;

    /*-
     * RI:
     * 
     * - step != 0
     * - coherent sign of step with the given range:
     *      (end - start) * step >= 0
     * - the length of numbers in the progression is greater than zero:
     *      computeSize(start, end, step) > 0
     *     - from which it follows that start != end, as computeSize(start, end, step) would be 0
     * - name == null || !name.isBlank()
     * 
     * AF: 
     * 
     *  - AF(this) = the abstract index I of length l = computeSize(start, end, step) such that
     *               I_{l}[i] = start + i*step for every 0 <= i < l.
     *  - The index may have a name.
     *  - The labels are ordered in the same way as they were defined by the arithmetic progression.
     *  - The labels are all instances of {@link Integer}.
     */


    /**
     * Creates a numeric index with the given {@code name}.
     *
     * @param name the name (may be {@code null})
     * @param start inclusive lower bound
     * @param end exclusive upper bound
     * @param step non-zero increment;
     *
     * @throws IllegalArgumentException if {@code step == 0}, if its sign is
     *                                  inconsistent with the range ({@code (end - start)*step < 0})
     *                                  or if the resulting index would be empty;
     *                                  if {@code name} is blank.
     */
    public NumericIndex(final String name, final int start, final int end, final int step) {
        if (step == 0) throw new IllegalArgumentException("step cannot be zero");

        final long spanTimesStep = (long) (end - start) * step;
        if (spanTimesStep < 0) throw new IllegalArgumentException("step sign inconsistent with range");

        final int len = computeSize(start, end, step);
        if (len <= 0) throw new IllegalArgumentException("range produces no labels");
        if (name != null && name.isBlank()) throw new IllegalArgumentException("name cannot be blank");

        this.name  = name;
        this.start = start;
        this.end   = end;
        this.step  = step;
    }

    /**
     * Creates an unnamed numeric index.
     *
     * @param start inclusive lower bound
     * @param end   exclusive upper bound
     * @param step  non-zero increment;
     *
     * @throws IllegalArgumentException if {@code step == 0}, if its sign is
     *                                  inconsistent with the range ({@code (end - start)*step < 0})
     *                                  or if the resulting index would be empty.
     */
    public NumericIndex(final int start, final int end, final int step) {
        this(null, start, end, step);
    }

    /**
     * Computes the size of the numeric index defined by the given parameters.
     * 
     * @param start inclusive lower bound
     * @param end   exclusive upper bound
     * @param step  non-zero increment;
     * @return the number of labels in the index
     */
    private static int computeSize(final int start, final int end, final int step) {
        long diff = (long) end - start;
        long s = Math.abs((long) step);
        return (int) ((Math.abs(diff) + (s - 1)) / s);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Note: For a {@code NumericIndex} the returned object is always an {@link Integer}.</p>
     */
    @Override
    public Object labelAt(final int pos) {
        if (pos < 0 || pos >= size())
            throw new IndexOutOfBoundsException("Position out of bounds");

        return Integer.valueOf(start + pos * step);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Note: In a {@code NumericIndex} the only possible labels are instances of {@link Integer}. 
     * If {@code label} is not an {@code Integer}, or if its value is not a term of the progression, 
     * this method returns {@code -1}.</p>
     */
    @Override
    public int positionOf(Object label) {
        Objects.requireNonNull(label,"label cannot be null");

        if (!(label instanceof Integer v)) return -1;
        long delta = (long) v - start;
        if (delta * step < 0 || delta % step != 0) return -1;
        int p = (int) (delta / step);
        return p < size() ? p : -1;
    }

    @Override
    public int size() {
        return computeSize(start, end, step);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public NumericIndex rename(final String newName) {
        if (newName != null && newName.isBlank()) {
            throw new IllegalArgumentException("New name cannot be blank");
        }
        if (Objects.equals(newName, name)) return this;
        return new NumericIndex(newName, start, end, step);
    }

    @Override
    public Iterator<Object> iterator() {
        return new Iterator<Object>() {

            private int index   = 0;
            private int value = start;
            private final int size = size();

            @Override
            public boolean hasNext() {
                return index < size;
            }

            @Override
            public Object next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("No more elements in the index");
                }

                int v = value;
                value += step;
                index++;
                return Integer.valueOf(v);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Remove operation is not supported");
            }
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NumericIndex other)) return false;
        return start == other.start &&
            end   == other.end   &&
            step  == other.step;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end, step);
    }

   @Override
    public String toString() {
        return toColumnString();
    }
}
