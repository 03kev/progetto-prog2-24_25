package mole;

import java.util.Iterator;
import java.util.Objects;

/**
 * An {@code Index} models an immutable sequence I of {@linkplain #size() size} > 0 
 * distinct, non-null labels.
 *
 * <p>An index is characterised by
 * <ul>
 *   <li>an optional name (which may be null)</li>
 *   <li>a non-empty sequence of distinct, non-null labels</li>
 * </ul>
 * 
 * <p> The type is iterable, allowing iteration over the labels in their defined order.</p>
 * The remove operation in the iterator is not supported and will throw an {@link UnsupportedOperationException}.
 * 
 * Every implementation of this interface is closed regards to {@code Object.equals}, meaning that an index provided
 * by implementation1 is not equal to an index provided by implementation2, even if they contain the same labels 
 * in the same order. Use ${@link #isEqual(Index)} to check if two indexes have the same labels in the same order.
 * 
 * <em>Project made with the help of chatGPT and Github Copilot.</em>
 */
public interface Index extends Iterable<Object> {

    /** 
     * Returns the length of the index, which is the number of labels it contains.
     * 
     * @return the number of labels in the index
     */
    public int size();

    /**
     * Returns the label stored at position {@code pos}.
     *
     * @param pos position in the half-open interval {@code [0, size())}
     * @return the non-{@code null} label at that position
     * @throws IndexOutOfBoundsException if {@code pos} is out of bounds
     */
    public Object labelAt(final int pos);

    /**
     * Returns the position of the specified {@code label}, or {@code -1} 
     * if the label is not present.
     *
     * @param  label the label to locate
     * @return its position, or {@code -1} when absent
     * @throws NullPointerException if {@code label} is {@code null}
     */
    public int positionOf(Object label);


    /**
     * Returns the name of the index, or {@code null} if it has no name.
     * 
     * @return the name of the index, or {@code null}
     */
    public String getName();

    /**
     * Returns a new identical {@code Index} with the given name, which can be 
     * {@code null} but not blank. The sequence of labels remains unchanged.
     *
     * @param newName the new (possibly {@code null}) name
     * @return an index with the requested name, or {@code this} if the
     *         name is unchanged.
     * @throws IllegalArgumentException if {@code newName} is blank
     */
    public Index rename(String newName);

    @Override
    public Iterator<Object> iterator();

    /**
     * Assesses whether this index is equal to another index based on their labels.
     * Two indexes are considered equal if they have the same labels in the same order.
     * The name of the indexes is not considered in this comparison.
     * 
     * @param other the instance of {@code Index} to compare with
     * @return {@code true} if {@code this} and {@code other} have the same labels in the same order,
     *         {@code false} otherwise.
     * @throws NullPointerException if {@code other} is {@code null}
     */
    default boolean isEqual(Index other) {
        Objects.requireNonNull(other, "other cannot be null");
        if (this == other) return true;
        if (this.size() != other.size()) return false;
        for (int i = 0; i < size(); i++) {
            if (!Objects.equals(labelAt(i), other.labelAt(i))) return false;
        }
        return true;
    }

    /**
     * Returns an index that contains every label of {@code this} followed, in the same order, by the 
     * labels of {@code other} that are not already present in {@code this}. The resulting sequence has 
     * no duplicates and preserves the original ordering. The fused index inherits the name of {@code this}.
     * 
     * Given the indexes
     *   this = [0, 1, 2, 5]
     *   other = [0, 3, 2, 6, 9]
     * the merged index will be
     *   merged = [0, 1, 2, 5, 3, 6, 9]
     *
     * <p>The default implementation returns {@code this} when it's abstractly equal to {@code other},
     * otherwise it creates and returns a new {@link Index} instance implementing the
     * specified behaviour.</p>
     *
     * @param other the index to merge with
     * @return an index exposing the fused sequence
     * @throws NullPointerException if {@code other} is {@code null}
     */
    default Index merge(Index other) {
        Objects.requireNonNull(other, "other");
        if (this.isEqual(other)) return this;
        return new FusionIndex(this.getName(), this, other);
    }

    /**
     * Returns a string representation of the {@code Index} in a column format.
     * The first line contains the name of the index (if not {@code null}),
     * followed by a line of dashes, and then each label on a new line.
     * 
     * Example:
     *
     * Weekdays
     * --------
     *   monday
     *  tuesday
     *      ...
     * 
     * @return a string representation of the index in column format
     */
    default String toColumnString() {
        String name = getName();
        int width = name != null ? name.length() : 0;
        for (Object label : this) {
            String s = Objects.toString(label);
            if (s.length() > width) width = s.length();
        }
        StringBuilder sb = new StringBuilder();
        if (name != null) {
            sb.append(name).append('\n');
        }
        for (int i = 0; i < width; i++) sb.append('-');
        sb.append('\n');
        for (Object label : this) {
            String s = Objects.toString(label);
            int pad = width - s.length();
            for (int j = 0; j < pad; j++) sb.append(' ');
            sb.append(s).append('\n');
        }
        return sb.toString();
    }

   
}