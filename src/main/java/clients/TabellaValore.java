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

import mole.Table;
import utils.InputParsing;

public class TabellaValore {
    public static void main(String[] args) {
        Object rowLabel = InputParsing.parseValues(args[0], 1)[0];
        Object columnLabelRaw = InputParsing.parseValues(args[1], 1)[0];
        String columnLabel = String.valueOf(columnLabelRaw);

        try (Scanner in = new Scanner(System.in)) {
            Table<Object> table = ClientUtils.readTable(in);
            Object value = table.valueAt(rowLabel, columnLabel);
            System.out.println(value);
        }
    }
}
