package pt.ulusofona.aed.deisimdb;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class ActorsTableModel extends AbstractTableModel {
    private final String[] columns = {"Nome", "ID", "Género"};
    private List<Actor> data = new ArrayList<>();

    public void setData(List<Actor> data) {
        this.data = data != null ? data : new ArrayList<>();
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Actor a = data.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> a.getName();
            case 1 -> a.getActorId();
            case 2 -> a.getGeneroLongo();
            default -> "";
        };
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnIndex == 1 ? Integer.class : String.class;
    }
}

