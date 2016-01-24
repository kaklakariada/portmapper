package org.chris.portmapper.fx;

import org.chris.portmapper.PortMapperApp;
import org.chris.portmapper.fx.main.MainView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.airhacks.afterburner.injection.Injector;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FxApplication extends Application {

    private static final Logger LOG = LoggerFactory.getLogger(FxApplication.class);
    private PortMapperApp app;

    @Override
    public void start(final Stage stage) throws Exception {
        LOG.info("Starting FxApp");

        app = new PortMapperApp();
        app.startup();
        Injector.setLogger(s -> LOG.debug("Injector: {}", s));
        Injector.setModelOrService(PortMapperApp.class, app);

        final MainView appView = new MainView();
        final Scene scene = new Scene(appView.getView());
        stage.setTitle("PortMapper");
        final String uri = getClass().getResource("app.css").toExternalForm();
        scene.getStylesheets().add(uri);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        app.disconnectRouter();
        Injector.forgetAll();
    }

    public static void main(final String[] args) {
        launch(args);
    }
}
