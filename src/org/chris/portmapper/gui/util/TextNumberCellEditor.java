/**
 * 
 */
package org.chris.portmapper.gui.util;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

/**
 * @author chris
 * 
 */
@SuppressWarnings("serial")
public class TextNumberCellEditor extends AbstractCellEditor implements
		TableCellEditor, KeyListener {

	private Integer currentPortNumber;
	private JTextField textField;

	/**
	 * 
	 */
	public TextNumberCellEditor(int value, int numColumns) {
		this.currentPortNumber = value;
		this.textField = new JTextField(this.currentPortNumber.toString(),
				numColumns);
		this.textField.addKeyListener(this);
	}

	/**
	 * 
	 * @see javax.swing.CellEditor#getCellEditorValue()
	 */
	public Object getCellEditorValue() {
		return this.currentPortNumber;
	}

	/**
	 * 
	 * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing
	 *      .JTable, java.lang.Object, boolean, int, int)
	 */
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		this.currentPortNumber = (Integer) value;
		this.textField.setText(this.currentPortNumber.toString());
		return this.textField;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	public void keyTyped(KeyEvent arg0) {
		// The user has clicked the cell
		try {
			this.currentPortNumber = new Integer(textField.getText());
		} catch (NumberFormatException e) {
			// Do nothing
		}
	}

}
