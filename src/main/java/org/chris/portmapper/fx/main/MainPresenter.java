package org.chris.portmapper.fx.main;

import java.net.URL;
import java.util.ResourceBundle;

import org.chris.portmapper.fx.log.LogView;
import org.chris.portmapper.fx.mappings.MappingsView;
import org.chris.portmapper.fx.presets.PresetsView;
import org.chris.portmapper.fx.router.RouterView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.airhacks.afterburner.views.FXMLView;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
    private AnchorPane logPane;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        LOG.debug("Initializing: location={}, resources={}", location, resources);
        addChild(mappingsPane, new MappingsView());
        addChild(routerPane, new RouterView());
        addChild(presetsPane, new PresetsView());
        addChild(logPane, new LogView());
    }

    private void addChild(final AnchorPane pane, final FXMLView view) {
        view.getViewAsync(pane.getChildren()::add);
    }
}
