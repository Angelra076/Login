import javax.swing.*;
import javax.swing.table.*;

import java.awt.*;
import java.awt.event.*;

public class ButtonColumn extends AbstractCellEditor implements TableCellRenderer, TableCellEditor, ActionListener {
    private JButton button;
    private JTable table;
    private Action action;

    public ButtonColumn(JTable table, Action action, int column) {
        this.table = table;
        this.action = action;
        button = new JButton();
        button.addActionListener(this);

        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(column).setCellRenderer(this);
        columnModel.getColumn(column).setCellEditor(this);
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        button.setText(value == null ? "" : value.toString());
        return button;
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        button.setText(value == null ? "" : value.toString());
        return button;
    }

    public Object getCellEditorValue() {
        return button.getText();
    }

    public void actionPerformed(ActionEvent e) {
        int editingRow = table.getEditingRow();
        if (editingRow != -1) {
            int row = table.convertRowIndexToModel(editingRow);
            ActionEvent event = new ActionEvent(table, ActionEvent.ACTION_PERFORMED, "" + row);
            action.actionPerformed(event);
            fireEditingStopped();
        } else {
            // Manejo cundo no hay ninguna fila seleccionada para editar
            System.out.println("No hay fila seleccionada para editar.");
        }
    }
}
