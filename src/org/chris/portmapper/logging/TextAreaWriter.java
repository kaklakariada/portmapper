package org.chris.portmapper.logging;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JTextArea;

public class TextAreaWriter extends Writer {

	private JTextArea textArea;
	private List<String> unprocessedText;

	/**
	 * 
	 */
	public TextAreaWriter() {
		super();
		unprocessedText = new LinkedList<String>();
	}

	@Override
	public void close() throws IOException {
	}

	@Override
	public void flush() throws IOException {
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		String line = new String(cbuf, off, len);
		if (this.textArea != null) {
			this.textArea.append(line);
			textArea.setCaretPosition(textArea.getDocument().getLength());
		} else {
			unprocessedText.add(line);
		}
	}

	public void setTextArea(JTextArea textArea) {
		this.textArea = textArea;
		for (String line : unprocessedText) {
			this.textArea.append(line);
		}
		textArea.setCaretPosition(textArea.getDocument().getLength());
		this.unprocessedText = null;
	}
}
