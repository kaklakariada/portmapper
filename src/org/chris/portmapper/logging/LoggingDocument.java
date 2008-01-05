package org.chris.portmapper.logging;

import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

@SuppressWarnings("serial")
public class LoggingDocument extends PlainDocument {

	public void addLine(String line) {
		try {
			this.insertString(this.getLength(), line, null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

	}

}
