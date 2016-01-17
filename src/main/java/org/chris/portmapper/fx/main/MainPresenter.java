package org.chris.portmapper.fx.main;

import java.net.URL;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.fxml.Initializable;

public class MainPresenter implements Initializable {

    private static final Logger LOG = LoggerFactory.getLogger(MainPresenter.class);

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        LOG.debug("Initializing: location={}, resources={}", location, resources);
    }

}
