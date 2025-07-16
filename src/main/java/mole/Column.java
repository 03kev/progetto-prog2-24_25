package mole;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;

/**
 * A {@code Column} models an immutable sequence of generic values, indexed by a sequence of labels.
 * Values can be {@code null}.
 * 
 * A column is characterised by
 * <ul>
 *   <li> an optional name (which may be null)</li>
 *   <li> an index, that provides the labels of the column</li>
 *   <li> a non-empty sequence of values</li>
 * </ul>
 * It's important to note that the number of values in a column must match the size of the index.
 * 
 * <p> The type is iterable, allowing iteration over the values in their defined order.</p>
 * The remove operation in the iterator is not supported and will throw an {@link UnsupportedOperationException}.
 * 
 * @param <V> the type of values in the column.
 * 
 * The element type {@code V} must be immutable, otherwise the contract of this class will not be guaranteed.
 */
public final class Column<V> implements Iterable<V> {
    /** Optional name of the column (may be null) */
    private final String name;
    /** The index of the column, providing the labels for each value */
    private final Index index;
    /** The array of values in the column */
    private final V[] values;

    /*-
     * RI:
     * 
     * - index != null
     * - values != null
     * - values.length == index.size()
     *    - from this it follows that values.length > 0
     * - name == null || !name.isBlank()
     * - name, index, and values are immutable for the lifetime of this column
     * 
     * AF:
     * 
     * - AF(this) = a column C, with index I and values array V, of length p = I.size() = V.length such that
     *       for each 0 <= i < p:
     *          - the i-th label of C is I.labelAt(i)
     *          - the i-th value of C is V[i]
     *       That means that every label in the index corresponds to a value in the values array.
     * - The column may have a name, which can be {@code null}.
     */


    /**
     * Creates a {@code Column} with the given {@code name}, {@code index}, and {@code values}.
     *
     * @param name the name of the column (may be {@code null})
     * @param index the index providing labels for the column
     * @param values the array of values
     *
     * @throws NullPointerException     if {@code index} or {@code values} is {@code null}
     * @throws IllegalArgumentException if {@code values} contains {@code null} elements;
     *                                  if {@code name} is blank; or if {@code values.length != index.size()}
     */
    public Column(final String name, final Index index, final V[] values) {
        Objects.requireNonNull(index);
        Objects.requireNonNull(values);
        if (name != null && name.isBlank()) throw new IllegalArgumentException("name cannot be blank");
        if (values.length != index.size()) {
            throw new IllegalArgumentException("values length must match index size");
        }

        this.name = name;
        this.index = index;
        this.values = values.clone();
    }

    /**
     * Creates an unnamed {@code Column} with the given {@code index} and {@code values}.
     *
     * @param index the index providing labels for the column
     * @param values the array of values
     *
     * @throws NullPointerException     if {@code index} or {@code values} is {@code null}
     * @throws IllegalArgumentException if {@code values} contains {@code null} elements;
     *                                  or if {@code values.length != index.size()}
     */
    public Column(final Index index, final V[] values) {
        this(null, index, values);
    }

    /**
     * Returns the name of the column, or {@code null} if it has no name.
     *
     * @return the name of the column, or {@code null}
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the number of rows in the column, which is equal to the size of the index and
     * the length of the values array.
     * 
     * @return the number of rows in the column
     */
    public int size() {
        return values.length;
    }

    /**
     * Returns the index of the column, which provides the labels for each value.
     * 
     * @return the index of the column
     */
    public Index getIndex() {
        return index;
    }

    /**
     * Returns the values of the column
     * 
     * @return an array of values in the column
     */
    public V[] getValues() {
        return values.clone();
    }

    /**
     * Returns the value at the specified position in the column.
     * 
     * @param pos the position in the half-open interval {@code [0, size())}
     * @return the value at that position
     * @throws IndexOutOfBoundsException if {@code pos} is out of bounds
     */
    public V valueAt(final int pos) {
        if (pos < 0 || pos >= values.length) {
            throw new IndexOutOfBoundsException("Position out of bounds");
        }
        return values[pos];
    }

    /**
     * Returns the value at the specified label in the column.
     * 
     * @param label the label of the index
     * @return the value at that label
     * @throws NullPointerException if {@code label} is {@code null}
     * @throws IllegalArgumentException if the label is not present in the index
     */
    public V valueAt(final Object label) {
        Objects.requireNonNull(label);
        final int pos = index.positionOf(label);
        if (pos == -1) {
            throw new IllegalArgumentException("Label not present in the index: " + label);
        }
        return values[pos];
    }

    /**
     * Creates a new {@code Column} with the same index and values, but with a different name.
     * 
     * @param newName the new name for the column, which can be {@code null}
     * @return a new column with the specified name, or {@code this} if the name is unchanged
     * @throws IllegalArgumentException if {@code newName} is blank
     */
    public Column<V> rename(final String newName) {
        if (newName != null && newName.isBlank()) {
            throw new IllegalArgumentException("New name cannot be blank");
        }
        if (Objects.equals(this.name, newName)) return this;
        return new Column<>(newName, index, values);
    }

    /**
     * Creates a new {@code Column} with the same name and values, but with a different index.
     * 
     * @param newIndex the new index for the column
     * @return a new column with the specified index, or {@code this} if the index is unchanged
     * @throws NullPointerException if {@code newIndex} is {@code null}
     * @throws IllegalArgumentException if {@code newIndex.size()} does not match {@code Column.size()}
     */
    public Column<V> changeIndex(final Index newIndex) {
        Objects.requireNonNull(newIndex, "New index cannot be null");

        if (this.index.isEqual(newIndex)) return this;
        if (newIndex.size() != this.values.length) {
            throw new IllegalArgumentException("New index size must match column number of rows");
        }

        return new Column<>(name, newIndex, values);
    }

    /**
     * Reindexes the column to a new index, preserving the values when possible:
     * if a label in the new index does not match any existing label, the corresponding value will be {@code null},
     * otherwise the value will be preserved.
     * 
     * @param newIndex the new index to reindex to
     * @return a new column with the specified index, or {@code this} if the index is unchanged
     * @throws NullPointerException if {@code newIndex} is {@code null}
     */
    public Column<V> reindex(final Index newIndex) {
        Objects.requireNonNull(newIndex);
        if (Objects.equals(this.index, newIndex)) return this;

        final int newSize = newIndex.size();
        
        // This cast is safe because we treat the new array as V[] and never expose it as Object[].
        // Furthermore, we only store values of type V in this array.
        @SuppressWarnings("unchecked")
        V[] newValues = (V[]) new Object[newSize];

        for (int i = 0; i < newSize; i++) {
            Object label = newIndex.labelAt(i);
            int pos = this.index.positionOf(label);
            newValues[i] = pos != -1 ? this.values[pos] : null;
        }

        return new Column<>(name, newIndex, newValues);
    }

    /**
     * Transforms every value in this column by applying the given function,
     * producing a new column of type {@code U} with the same index and name.
     * 
     * @param <U> the target type of the transformed values
     * @param func a function that maps values of type {@code V} to type {@code U}
     * @return a new {@code Column<U>} with the transformed values,
     *         sharing the same index and name as this column
     * @throws NullPointerException if {@code func} is {@code null}
     */
    public <U> Column<U> mapValues(final Function<V, U> func) {
        Objects.requireNonNull(func);

        // This cast is safe because we treat the new array as U[] and never expose it as Object[].
        // Furthermore, we only store values of type U in this array.
        @SuppressWarnings("unchecked")
        U[] newValues = (U[]) new Object[values.length];

        for (int i = 0; i < values.length; i++) {
            newValues[i] = func.apply(values[i]);
        }

        return new Column<>(name, index, newValues);
    }

    /**
     * Returns a new {@code Column} that stacks this column on top of another column.
     * The new column will have a fused index that combines the labels of both columns,
     * and the values will be ordered such that the values of {@code this} column come first,
     * followed by the values of the {@code other} column.
     * 
     * The two columns must not have overlapping labels, otherwise an exception is thrown.
     * 
     * @param other the column to stack on top of this one
     * @return a new {@code Column} with the fused index and stacked values
     * @throws NullPointerException if {@code other} is {@code null}
     * @throws IllegalArgumentException if the two columns have overlapping labels
     */
    public Column<V> stack(final Column<V> other) {
        Objects.requireNonNull(other, "other column cannot be null");
        
        final Index fusedIndex = index.merge(other.index);
        
        if (fusedIndex.size() != this.size() + other.size()) {
            throw new IllegalArgumentException("Cannot stack columns with overlapping labels");
        }
        
        // This cast is safe because we treat the new array as V[] and never expose it as Object[].
        // Furthermore, we only store values of type V in this array.
        @SuppressWarnings("unchecked")
        V[] stackedValues = (V[]) new Object[fusedIndex.size()];
        
        System.arraycopy(values, 0, stackedValues, 0, values.length);
        System.arraycopy(other.values, 0, stackedValues, values.length, other.values.length);
        
        return new Column<>(name, fusedIndex, stackedValues);
    }


    @Override
    public Iterator<V> iterator() {
        return new Iterator<V>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < values.length;
            }

            @Override
            public V next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("No more elements in the column");
                }
                return values[index++];
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Remove operation is not supported");
            }
        };
    }

    @Override
    public String toString() {
        String indexName = index.getName() == null ? "" : index.getName();
        String columnName = name == null ? "" : name;
        
        int labelWidth = indexName.length();
        int valueWidth = columnName.length();
        
        for (int i = 0; i < values.length; i++) {
            labelWidth = Math.max(labelWidth, String.valueOf(index.labelAt(i)).length());
            String valueStr = values[i] == null ? "" : String.valueOf(values[i]);
            valueWidth = Math.max(valueWidth, valueStr.length());
        }
        
        StringBuilder sb = new StringBuilder();
        
        sb.append(String.format("%" + labelWidth + "s | %-" + valueWidth + "s%n", indexName, columnName));
        
        sb.append("-".repeat(labelWidth + 1))
            .append("+")
            .append("-".repeat(valueWidth + 1))
            .append('\n');
        
        for (int i = 0; i < values.length; i++) {
            String valueStr = values[i] == null ? "" : String.valueOf(values[i]);
            sb.append(String.format("%" + labelWidth + "s | %-" + valueWidth + "s%n", index.labelAt(i), valueStr));
        }
        
        return sb.toString();
    }

}
