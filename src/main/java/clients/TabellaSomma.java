/*

Copyright 2024 Massimo Santini

This file is part of "Programmazione 2 @ UniMI" teaching material.

This is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This material is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this file.  If not, see <https://www.gnu.org/licenses/>.

*/

package clients;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import mole.Column;
import mole.NumericIndex;
import mole.Table;

public class TabellaSomma {
    public static void main(String[] args) {
        try (Scanner in = new Scanner(System.in)) {
            Table<Object> table = ClientUtils.readTable(in);

            NumericIndex sumIndex = new NumericIndex(0, 1, 1);

            List<Column<Integer>> sumCols = new ArrayList<>();
            for (Column<Object> col : table) {
                int sum = 0;
                for (Object o : col) {
                    if (o != null) sum += (Integer) o;
                }
                Integer[] data = new Integer[] { sum };
                sumCols.add(new Column<>(col.getName(), sumIndex, data));
            }

            Table<Integer> result = new Table<>(sumIndex, sumCols);
            System.out.println(result);
        }
    }
}