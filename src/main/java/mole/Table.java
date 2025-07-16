package mole;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

/**
 * A {@code Table} represents an immutable sequenze of distinctly named columns, each of them having
 * the same number of rows and the same index. Every column has values of the same type {@code V}.
 * 
 * A table is characterized by
 * <ul>
 *    <li>its {@link Index}, which defines the labels of its rows</li>
 *    <li>a sequence of {@link Column}s, each of them having the same index and the same number of rows</li>
 * </ul>
 * 
 * It's important to note that all columns in a table have distinct names. On the other hand,
 * tables, differently from indexes and columns, do not have a name.
 * 
 * <p>The type is iterable, allowing to iterate over its columns in their defined order. 
 * Remove operation is not supported.</p>
 * 
 * @param <V> the type of values in the columns of the table
 */
public final class Table<V> implements Iterable<Column<V>>  {
    /** Row index of the table */
    private final Index rowIndex;
    /** List of columns of the table */
    private final List<Column<V>> columns;

    /*-
     * RI:
     *  - rowIndex != null
     *  - columns != null
     *  - columns does not contain null elements
     *  - columns.size() > 0
     *  - columns.get(i).getIndex() == rowIndex     for all i in [0, columns.size())
     *      - it follows that columns.get(i).size() == rowIndex.size()      for all i in [0, columns.size())
     *  - all columns have distinct names and no column has a null name
     * 
     * AF:
     * 
     *     A Table is a collection of columns, each identified by a unique name, that share the same row index 
     *     (and therefore the same number of rows). The row index provides labels for the rows, 
     *     while each column contains values of type {@code V}.
     * 
     *     Formally:
     *         AF(this) = matrix of size RxC (R = rowIndex.size(), C = columns.size())
     *               where each cell (i, j) contains the value at row i and column j.
     *         AF(this).valueAt(i, j) = columns.get(j).valueAt(i)
     */


    /**
     * Constructs a new {@code Table} with the specified index and columns.
     * Columns with {@code null} names are automatically renamed to "Column_i" where i is their position.
     * 
     * Note: if by chance the "Column_i" name is already used by one of the other columns, it will be 
     * renamed to "Column_{i+1}" and so on, until a unique name is found.
     * 
     * @param index the index of the table
     * @param columns the columns of the table
     * @throws NullPointerException if {@code index} or {@code columns} is {@code null}
     * @throws IllegalArgumentException if {@code columns} is empty or contains {@code null} elements;
     *                                  if there is a column with a different index than {@code index};
     *                                  if there are columns with duplicate names.
     */
    public Table(final Index index, final List<Column<V>> columns) {
        Objects.requireNonNull(index, "Index cannot be null");
        Objects.requireNonNull(columns, "Columns cannot be null");
        if (columns.isEmpty()) throw new IllegalArgumentException("Columns cannot be empty");
        for (Column<V> column : columns) {
            Objects.requireNonNull(column, "Columns cannot contain null elements");
            if (!column.getIndex().isEqual(index)) { // follows that column.size() == index.size()
                throw new IllegalArgumentException("Column index does not match table index");
            }
        }
        this.rowIndex = index;

        List<Column<V>> newColumns = new ArrayList<>();
        Set<String> usedNames = new HashSet<>();

        for (int i = 0; i < columns.size(); i++) {
            Column<V> column = columns.get(i);
            String columnName = column.getName();
            
            if (columnName == null) {
                int suffix = i;
                do {
                    columnName = "Column_" + suffix;
                    suffix++;
                } while (usedNames.contains(columnName));
            }

            if (!usedNames.add(columnName)) {
                throw new IllegalArgumentException("Duplicate column name");
            }
            
            if (!columnName.equals(column.getName())) {
                newColumns.add(column.rename(columnName));
            } else {
                newColumns.add(column);
            }
        }

        this.columns = newColumns;
    }

    /**
     * Returns the index of the table, which provides the labels for its rows.
     * 
     * @return the index of the table
     */
    public Index getIndex() {
        return rowIndex;
    }

    /**
     * Returns the number of rows in the table, which is equal to the size of the index.
     * 
     * @return the number of rows in the table
     */
    public int size() {
        return rowIndex.size();
    }

    /**
     * Returns the number of columns in the table.
     * 
     * @return the number of columns in the table
     */
    public int columnCount() {
        return columns.size();
    }

    /**
     * Returns the column at the specified position.
     * 
     * @param pos the position of the column to return 
     * @return the column at the specified position
     * @throws IndexOutOfBoundsException if {@code pos} is out of bounds
     */
    public Column<V> columnAt(final int pos) {
        if (pos < 0 || pos >= columns.size()) {
            throw new IndexOutOfBoundsException("Column position out of bounds: " + pos);
        }
        return columns.get(pos);
    }

    /**
     * Returns the column with the specified name, if it exists.
     * 
     * @param name the name of the column to return
     * @return the column with the specified name, or {@code null} if no such column exists
     * @throws NullPointerException if {@code name} is {@code null}
     */
    public Column<V> columnByName(final String name) {
        Objects.requireNonNull(name, "Column name cannot be null");
        for (Column<V> column : columns) {
            if (name.equals(column.getName())) {
                return column;
            }
        }
        return null;
    }

    /**
     * Returns the value at the specified row and column positions.
     * 
     * @param row the row position
     * @param column the column position
     * @return the value at the cell identified by the specified row and column
     * @throws IndexOutOfBoundsException if {@code row} or {@code column} is out of bounds
     */
    public V valueAt(final int row, final int column) {
        if (row < 0 || row >= rowIndex.size()) throw new IndexOutOfBoundsException("Row index out of bounds");
        if (column < 0 || column >= columns.size()) throw new IndexOutOfBoundsException("Column index out of bounds");
        return columns.get(column).valueAt(row);
    }

    /**
     * Returns the value at the specified row label and column name.
     * 
     * @param rowLabel the label of the row
     * @param columnName the name of the column
     * @return the value at the cell identified by the specified row label and column name
     * @throws NullPointerException if {@code rowLabel} or {@code columnName} is {@code null}
     * @throws IllegalArgumentException if there is no column with the specified name
     *                                  or if the row label is not found in the index
     */
    public V valueAt(Object rowLabel, String columnName) {
        int rowPos = rowIndex.positionOf(rowLabel);
        if (rowPos == -1) throw new IllegalArgumentException("Row label not found");

        Column<V> column = columnByName(columnName);
        return column.valueAt(rowPos);
    }

    /**
     * Creates a new {@code Table} with the same columns but a different index. Note that
     * the new index must have the same size as the current index. All columns be updated
     * to reflect the new index, but their names and values remain unchanged.
     * 
     * @param newIndex the new index for the table
     * @return a new table with the specified index and the same columns, or {@code this} 
     *         if the new index is equal to the current one.
     * @throws NullPointerException if {@code newIndex} is {@code null}
     * @throws IllegalArgumentException if {@code newIndex} has a different size than the current index
     */
    public Table<V> changeIndex(Index newIndex) {
        Objects.requireNonNull(newIndex, "New index cannot be null");

        if (rowIndex.isEqual(newIndex)) return this;

        if (newIndex.size() != rowIndex.size()) {
            throw new IllegalArgumentException("New index must have the same size as the current index");
        }

        List<Column<V>> newColumns = new ArrayList<>();
        for (Column<V> column : columns) {
            newColumns.add(column.changeIndex(newIndex));
        }
        
        return new Table<>(newIndex, newColumns);
    }


    /**
     * Creates a new {@code Table} with columns renamed according to the provided array.
     * The array must have the same length as the number of columns.
     * 
     * @param newNames array of new names for the columns, in order
     * @return a new table with renamed columns, or {@code this} if no changes are needed
     * @throws NullPointerException if {@code newNames} is {@code null}
     * @throws IllegalArgumentException if {@code newNames.length != columnCount()},
     *                                  if any new name is blank, or if there are duplicate names
     */
    public Table<V> renameColumns(final String[] newNames) {
        Objects.requireNonNull(newNames, "New names array cannot be null");
        
        if (newNames.length != columns.size()) {
            throw new IllegalArgumentException("New names array length must match number of columns");
        }
        
        boolean hasChanges = false;
        Set<String> seen = new HashSet<>();
        for (int i = 0; i < newNames.length; i++) {
            String nm = newNames[i];
            if (nm != null && nm.isBlank()) {
                throw new IllegalArgumentException("newNames cannot contain blank names");
            }
            if (nm != null && !seen.add(nm)) {
                throw new IllegalArgumentException("newNames cannot contain duplicates");
            }
            if (!Objects.equals(columns.get(i).getName(), nm) && !hasChanges) {                
                hasChanges = true;
            }
        }
        
        if (!hasChanges) return this;
        
        List<Column<V>> newColumns = new ArrayList<>();
        for (int i = 0; i < columns.size(); i++) {
            newColumns.add(columns.get(i).rename(newNames[i]));
        }
        
        return new Table<>(rowIndex, newColumns);
    }

    /**
     * Returns a new {@code Table} by appending all the columns of {@code other} to the columns
     * of {@code this}. The two row indexes are fused together, without any duplicate, and every 
     * column of both tables is reindexed to match the new index.
     * 
     * <p>The resulting table will contain first all the columns of {@code this}, in order, then those 
     * of {@code other}. Column names must remain distinct; if any name from {@code other} clashes 
     * with one in {@code this}, an exception is thrown.</p>
     *
     * @param other the table whose columns are to be appended
     * @return a new table containing the columns of {@code this} then those of {@code other}
     * @throws NullPointerException if {@code other} is {@code null}
     * @throws IllegalArgumentException if any column name in {@code other} duplicates one in {@code this}.
     */
    public Table<V> flank(final Table<V> other) {
        Objects.requireNonNull(other, "other table cannot be null");

        Set<String> names = new HashSet<>();
        for (Column<V> c : this.columns) {
            names.add(c.getName());
        }
        for (Column<V> c : other.columns) {
            String nm = c.getName();
            if (!names.add(nm)) {
                throw new IllegalArgumentException("Duplicate column name");
            }
        }
        
        Index fusedIndex = rowIndex.merge(other.rowIndex);
        List<Column<V>> sideColumns = new ArrayList<>(this.columns.size() + other.columns.size());
        for (Column<V> c : this.columns) {
            sideColumns.add(c.reindex(fusedIndex));
        }
        for (Column<V> c : other.columns) {
            sideColumns.add(c.reindex(fusedIndex));
        }
        return new Table<>(fusedIndex, sideColumns);
    }

    /**
     * Returns a new {@code Table} by stacking vertically {@code other} under {@code this}.
     * The resulting table will have a row index that is the fusion of the two, and every column of
     * both tables will be reindexed to match the new index.
     * 
     * <p>The resulting table will contain first all the rows of {@code this}, then those of {@code other}.
     * Row labels must remain distinct; if any label from the {@code other} row index clashes with one in 
     * the {@code this} row index, an exception is thrown.</p>
     *
     * @param other the table to stack under {@code this}
     * @return a new table with the rows of {@code this} followed by those of {@code other}
     * @throws NullPointerException if {@code other} is {@code null}
     * @throws IllegalArgumentException if any row label of {@code other} duplicates one in {@code this}
     */
    public Table<V> stack(final Table<V> other) {
        Objects.requireNonNull(other, "other table cannot be null");
        
        Index fusedIndex = rowIndex.merge(other.rowIndex);
        if (fusedIndex.size() != rowIndex.size() + other.rowIndex.size()) {
            throw new IllegalArgumentException("Duplicate row labels");
        }

        LinkedHashSet<String> colNames = new LinkedHashSet<>();
        for (Column<V> col : this.columns) colNames.add(col.getName());
        for (Column<V> col : other.columns) colNames.add(col.getName());

        List<Column<V>> stacked = new ArrayList<>(colNames.size());
        for (String name : colNames) {
            Column<V> thisCol = this.columnByName(name);
            Column<V> otherCol = other.columnByName(name);
            
            if (thisCol != null && otherCol != null) {
                stacked.add(thisCol.stack(otherCol));
            } else if (thisCol != null) {
                stacked.add(thisCol.reindex(fusedIndex));
            } else if (otherCol != null) {
                stacked.add(otherCol.reindex(fusedIndex));
            }
        }

        return new Table<>(fusedIndex, stacked);
    }

    /**
     * Transforms the values of each column in the table using the provided function,
     * producing a new table {@code U} with the modified values but keeping the same row index.
     * 
     * @param <U> the target type of the transformed values
     * @param func a function that maps values of type {@code V} to type {@code U}
     * @return a new {@code Table<U>} with the transformed values, sharing the same row index
     *         as this table.
     * @throws NullPointerException if {@code func} is {@code null}
     */
    public <U> Table<U> map(Function<V, U> func) {
        Objects.requireNonNull(func, "Function cannot be null");
        List<Column<U>> newCols = new ArrayList<>(columns.size());
        for (Column<V> c : columns) {
            newCols.add(c.mapValues(func));
        }
        return new Table<>(rowIndex, newCols);
    }

    /**
     * Transforms each column in the table using the provided function,
     * producing a new table with the same row index but with columns transformed.
     * 
     * @param <U> the target type of the transformed columns
     * @param func a function that maps each column of type {@code Column<V>} to a new column of type {@code Column<U>}
     * @return a new {@code Table<U>} with the transformed columns, sharing the same row index
     *         as this table.
     * @throws NullPointerException if {@code func} is {@code null}, or if {@code func.apply(col)} returns {@code null} for any column
     */
    public <U> Table<U> mapColumn(Function<Column<V>, Column<U>> func) {
        Objects.requireNonNull(func, "Function cannot be null");
        List<Column<U>> newCols = new ArrayList<>(columns.size());
        for (Column<V> col : columns) {
            Column<U> transformed = Objects.requireNonNull(
                func.apply(col),
                "mapColumn function must not return null for column " + col.getName()
            );
            newCols.add(transformed);
        }
        return new Table<>(rowIndex, newCols);
    }

    @Override
    public Iterator<Column<V>> iterator() {
        return Collections.unmodifiableList(columns).iterator();
    }

    @Override
    public String toString() {
        int nCols = columns.size();
        int nRows = rowIndex.size();

        int[] colWidths = new int[nCols];
        for (int j = 0; j < nCols; j++) {
            Column<V> col = columns.get(j);
            String hdr = col.getName() == null ? "" : col.getName();
            int w = hdr.length();
            // celle
            for (int i = 0; i < nRows; i++) {
                Object v = col.valueAt(i);
                String cell = (v == null ? "" : v.toString());
                w = Math.max(w, cell.length());
            }
            colWidths[j] = w;
        }

        String idxHdr = rowIndex.getName() == null ? "" : rowIndex.getName();
        int rowLabelWidth = idxHdr.length();
        for (int i = 0; i < nRows; i++) {
            String lbl = String.valueOf(rowIndex.labelAt(i));
            rowLabelWidth = Math.max(rowLabelWidth, lbl.length());
        }

        StringBuilder sb = new StringBuilder();

        sb.append(String.format("%" + rowLabelWidth + "s |", idxHdr));
        for (int j = 0; j < nCols; j++) {
            String hdr = columns.get(j).getName();
            hdr = hdr == null ? "" : hdr;
            sb.append(" ")
            .append(String.format("%-" + colWidths[j] + "s", hdr))
            .append(" |");
        }
        sb.setLength(sb.length() - 1);
        sb.append("\n");

        for (int k = 0; k < rowLabelWidth; k++) sb.append('-');
        sb.append("-+");
        for (int j = 0; j < nCols; j++) {
            for (int k = 0; k < colWidths[j]; k++) sb.append('-');
            sb.append("--");
            if (j + 1 < nCols) sb.append('+');
        }
        sb.setLength(sb.length() - (nCols > 0 ? 1 : 0));
        sb.append("\n");

        for (int i = 0; i < nRows; i++) {
            String rlbl = String.format("%" + rowLabelWidth + "s", rowIndex.labelAt(i));
            sb.append(rlbl).append(" |");
            for (int j = 0; j < nCols; j++) {
                Object v = columns.get(j).valueAt(i);
                String cell = (v == null ? "" : v.toString());
                sb.append(" ")
                .append(String.format("%-" + colWidths[j] + "s", cell))
                .append(" |");
            }
            sb.setLength(sb.length() - 1);
            sb.append("\n");
        }

        return sb.toString();
    }
}
