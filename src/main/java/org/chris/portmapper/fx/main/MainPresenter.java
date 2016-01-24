package org.chris.portmapper.fx.main;

import java.net.URL;
import java.util.ResourceBundle;

import javax.inject.Inject;

import org.chris.portmapper.PortMapperApp;
import org.chris.portmapper.fx.mappings.MappingsView;
import org.chris.portmapper.fx.presets.PresetsView;
import org.chris.portmapper.fx.router.RouterView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.airhacks.afterburner.views.FXMLView;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

public class MainPresenter implements Initializable {

    private static final Logger LOG = LoggerFactory.getLogger(MainPresenter.class);

    @FXML
    private AnchorPane mappingsPane;
    @FXML
    private AnchorPane routerPane;
    @FXML
    private AnchorPane presetsPane;
    @FXML
    private TextArea logTextArea;

    @Inject
    private PortMapperApp app;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        LOG.debug("Initializing main presenter views and log text area");
        addChild(mappingsPane, new MappingsView());
        addChild(routerPane, new RouterView());
        addChild(presetsPane, new PresetsView());
        app.setLogMessageListener(logTextArea::appendText);
    }

    private void addChild(final AnchorPane pane, final FXMLView view) {
        view.getViewAsync(pane.getChildren()::add);
    }
}
