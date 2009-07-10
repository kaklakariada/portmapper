package org.chris.portmapper.logging;

import java.io.IOException;
import java.io.Writer;

public class DocumentWriter extends Writer {

	private final LoggingDocument document;

	public DocumentWriter(LoggingDocument document) {
		super();
		this.document = document;
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
		this.document.addLine(line);
	}

}
