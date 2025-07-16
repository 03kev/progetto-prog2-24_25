package clients;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import mole.ArrayIndex;
import mole.Column;
import mole.Index;
import mole.NumericIndex;
import mole.Table;
import utils.InputParsing;
import utils.InputParsing.ColumnDescriptor;
import utils.InputParsing.IndexDescriptor;
import utils.InputParsing.TableDescriptor;

/**
 * Utility methods for reading mole classes from a Scanner.
 */
public final class ClientUtils {
    // Private constructor to prevent instantiation
    private ClientUtils() {}

    /**
     * Provided a Scanner {@code in} that contains an index descriptor
     * followed by a line of labels, reads the index and returns it.
     * 
     * @param in the Scanner to read from
     * @return an {@code ArrayIndex} instance containing the labels read from the Scanner
     * @throws NullPointerException if {@code in} is {@code null}
     */
    public static Index readArrayIndex(Scanner in) {
        if (in == null) {
            throw new NullPointerException("Scanner cannot be null");
        }
        IndexDescriptor desc = (IndexDescriptor) InputParsing.parseDescriptor(in.nextLine());
        Object[] labels = InputParsing.parseValues(in.nextLine(), desc.len());
        return desc.name() == null
             ? new ArrayIndex(labels)
             : new ArrayIndex(desc.name(), labels);
    }

    /**
     * Provided a Scanner {@code in} that contains an index descriptor
     * followed by start, end, and step values, returns the index containing
     * the arithmetic progression defined by those values.
     * 
     * @param in the Scanner to read from
     * @return a {@code NumericIndex} instance containing the arithmetic progression
     * @throws NullPointerException if {@code in} is {@code null}
     */
    public static Index readNumericIndex(Scanner in, int step) {
        Object[] pair = InputParsing.parseValues(in.nextLine(), 2);
        int start = (Integer) pair[0];
        int end = (Integer) pair[1];
        return new NumericIndex(start, end, step);
    }

    /**
     * Returns a string representation of the last {@code N} labels
     * of the given index {@code idx}.
     * 
     * @param idx the index from which to take the labels
     * @param N the number of labels to take from the end of the index
     * @return a string containing the last {@code N} labels, separated by commas
     * @throws NullPointerException if {@code idx} is {@code null}
     * @throws IllegalArgumentException if {@code N} is negative
     * @throws IndexOutOfBoundsException if {@code N} is greater than the size
     *         of the index
     */
    public static String joinLastLabels(Index idx, int N) {
        if (idx == null) {
            throw new NullPointerException("Index cannot be null");
        }
        if (N < 0) {
            throw new IllegalArgumentException("N cannot be negative");
        }
        if (N > idx.size()) {
            throw new IndexOutOfBoundsException("N cannot be greater than the size of the index");
        }
        int sz = idx.size();
        int take = Math.min(N, sz);
        StringBuilder sb = new StringBuilder();
        for (int i = sz - take; i < sz; i++) {
            if (i > sz - take) sb.append(", ");
            sb.append(idx.labelAt(i));
        }
        return sb.toString();
    }

    /**
     * Provided a Scanner {@code in} that contains a column descriptor
     * followed by a line of values, reads the column and returns it.
     * 
     * @param <V> the type of values in the column
     * @param in the Scanner to read from
     * @return a Column instance containing the values read from the Scanner
     * @throws NullPointerException if {@code in} is {@code null}
     */
    public static <V> Column<V> readColumn(Scanner in) {
        if (in == null) {
            throw new NullPointerException("Scanner cannot be null");
        }
        ColumnDescriptor colDesc = (ColumnDescriptor) InputParsing.parseDescriptor(in.nextLine());
        int rows = colDesc.rows();
        String nextLine = in.nextLine().trim();
        Index index;
        Object[] values;
        
        if (nextLine.startsWith("#index")) {
            String labelsLine = in.nextLine();
            Scanner tempScanner = new Scanner(nextLine + "\n" + labelsLine);
            index = readArrayIndex(tempScanner);
            values = InputParsing.parseValues(in.nextLine(), rows);
        } else {
            index = new NumericIndex(null, 0, rows, 1);
            values = InputParsing.parseValues(nextLine, rows);
        }
        
        @SuppressWarnings("unchecked")
        V[] typedValues = (V[]) values;
        
        return colDesc.name() == null ?
            new Column<>(index, typedValues) :
            new Column<>(colDesc.name(), index, typedValues);
    }

    /**
     * Provided a Scanner {@code in} that contains a table descriptor
     * followed by the columns of the table, reads the table and returns it.
     *
     * @param in the Scanner to read from
     * @return a {@code Table<Object>} with the parsed columns
     * @throws NullPointerException if {@code in} is null
     */
    public static Table<Object> readTable(Scanner in) {
        if (in == null) throw new NullPointerException("Scanner cannot be null");

        TableDescriptor tblDesc = (TableDescriptor) InputParsing.parseDescriptor(in.nextLine());
        int expectedCols = tblDesc.cols();

        List<Column<Object>> cols = new ArrayList<>(expectedCols);
        for (int i = 0; i < expectedCols; i++) {
            Column<Object> col = readColumn(in);
            cols.add(col);
        }

        return new Table<>(cols.get(0).getIndex(), cols);
    }
}