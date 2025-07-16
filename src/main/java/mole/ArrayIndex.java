package mole;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.NoSuchElementException;

/**
 * Concrete, immutable implementation of {@link Index} whose content is the fixed sequence of 
 * labels supplied at construction time.
 *
 * <p>Besides the operations defined by {@code Index}, this class offers {@link #getLabels()}, 
 * which returns the labels in the defined order.</p>
 * 
 * <p>Two {@code ArrayIndex} instances are equal iff they contain the same labels in the same order.</p>
 *
 * The labels passed to the constructor must behave as immutable values and must not change 
 * their notion of equality for the entire lifetime of the index; otherwise the contract of 
 * this class will not be guaranteed.
 */
public final class ArrayIndex implements Index {
    /** Optional name of the index (may be {@code null}). */
    private final String name;
    /** The array of distinct, non-null labels in the index. The {@code labels[i]} is the label at position {@code i}.*/
    private final Object[] labels;

    /*-
     * RI:
     * 
     * - labels != null
     * - labels.length > 0
     * - no element of labels is null
     * - all labels are distinct (labels has no duplicates)
     * - name == null || !name.isBlank()
     * - labels and name are immutable for the lifetime of this index
     * 
     * AF:
     * 
     *  - AF(this) = the abstract index I of length l = |labels| such that
     *               I_{l}[i] = labels[i] for every 0 <= i < l.
     *  - The index may have a name.
     *  - The labels are ordered in the same way as they were provided at construction time.
     *  - The labels are all instances of {@link Object}.
     */

     
     /**
     * Creates an {@code Index} with the given {@code name} and {@code labels}.
     * 
     * @param name the name (may be {@code null})
     * @param labels the non-empty array of distinct and non-null labels.
     *
     * @throws NullPointerException     if {@code labels} is {@code null}
     * @throws IllegalArgumentException if {@code labels} is empty or contains {@code null}/duplicate elements;
     *                                  if {@code name} is blank.
     */
    public ArrayIndex(final String name, final Object[] labels) {
        Objects.requireNonNull(labels, "labels cannot be null");
        if (labels.length == 0) throw new IllegalArgumentException("labels cannot be empty");
        if (name != null && name.isBlank()) throw new IllegalArgumentException("name cannot be blank");

        this.name = name;

        this.labels = labels.clone();

        Set<Object> seen = new HashSet<>();
        for (Object label : this.labels) {
            if (label == null) {
                throw new IllegalArgumentException("labels must not contain null elements");
            }
            if (!seen.add(label)) {
                throw new IllegalArgumentException("labels must not contain duplicates");
            }
        }
    }

    /**
     * Creates an unnamed {@code Index} with the given labels.
     *
     * @param labels the non-empty array of distinct and non-null labels.
     * 
     * @throws NullPointerException if {@code labels} is {@code null}
     * @throws IllegalArgumentException if {@code labels} is empty or contains {@code null}/duplicate elements
     */
    public ArrayIndex(final Object[] labels) {
        this(null, labels);
    }

    @Override
    public Object labelAt(final int position) {
        if (position < 0 || position >= labels.length) {
            throw new IndexOutOfBoundsException("Position out of bounds");
        }
        return labels[position];
    }

    @Override
    public int positionOf(final Object label) {
        Objects.requireNonNull(label, "label cannot be null");

        for (int i = 0; i < labels.length; i++) {
            if (labels[i].equals(label)) {
                return i;
            }
        }
       return -1;
    }

    @Override
    public int size() {
        return labels.length;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Returns the {@code ArrayIndex}’s labels in positional order.
     * @return an array of labels
     */
    public Object[] getLabels() {
        return labels.clone();
    }


    @Override
    public ArrayIndex rename(final String newName) {
        if (newName != null && newName.isBlank()) {
            throw new IllegalArgumentException("New name cannot be blank");
        }
        if (Objects.equals(name, newName)) return this;
        return new ArrayIndex(newName, labels);
    }


    @Override
    public Iterator<Object> iterator() {
        return new Iterator<Object>() {
            private int index = 0;
            
            @Override
            public boolean hasNext() {
                return index < labels.length;
            }
            @Override
            public Object next() {
                if (!hasNext())
                    throw new NoSuchElementException("No more elements in the index");
                return labels[index++];
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
        if (!(o instanceof ArrayIndex other)) return false;
        return Arrays.equals(this.labels, other.labels);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(labels);
    }

    @Override
    public String toString() {
        return toColumnString();
    }
}
