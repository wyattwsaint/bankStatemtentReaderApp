package readPDF.celleditor;

import java.awt.Component;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

public class CategoryEditor extends AbstractCellEditor implements TableCellEditor {
	private List<String> categories;
	private JComboBox combo;
	
	public CategoryEditor(List<String> cats) {
		this.categories = cats;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		combo = new JComboBox();
		
		for (String category : categories) {
			combo.addItem(category);
		}
		
		combo.setSelectedItem(value);
		
		if (isSelected) {
			combo.setBackground(table.getSelectionBackground());
		} else {
			combo.setBackground(table.getSelectionForeground());
		}
		
		return combo;
	}
	
	@Override
	public Object getCellEditorValue() {
		return combo.getSelectedItem();
	}
}
