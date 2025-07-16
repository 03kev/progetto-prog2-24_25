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

import java.util.Scanner;

import mole.Column;
import mole.Index;
import mole.NumericIndex;

public class ColonnaIndice {
    public static void main(String[] args) {
        try (Scanner in = new Scanner(System.in)) {
            Column<Object> column = ClientUtils.readColumn(in);

            Index index = ClientUtils.readArrayIndex(in);
            if (index.size() == column.size()) {
                column = column.changeIndex(index);
            } else {
                index = new NumericIndex(null, 0, column.size(), 1);
                column = column.changeIndex(index);
            }
            System.out.println(column);
        }
    }
}