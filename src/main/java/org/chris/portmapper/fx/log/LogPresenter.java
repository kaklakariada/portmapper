package org.chris.portmapper.fx.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class LogPresenter {

    private static final Logger LOG = LoggerFactory.getLogger(LogPresenter.class);

    @FXML
    private TextArea logTextArea;

    public void initialize() {
        LOG.debug("Initializing");
    }
}
