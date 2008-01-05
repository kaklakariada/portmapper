package org.chris.portmapper.logging;

import java.io.IOException;
import java.io.Writer;

import javax.swing.JTextArea;

public class TextAreaWriter extends Writer {

	private JTextArea textArea;

	public TextAreaWriter(JTextArea textArea) {
		super();
		this.textArea = textArea;
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
		this.textArea.append(line);
		textArea.setCaretPosition(textArea.getDocument().getLength());
	}

}
