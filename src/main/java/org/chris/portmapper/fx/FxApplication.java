package org.chris.portmapper.fx;

import org.chris.portmapper.fx.main.MainView;

import com.airhacks.afterburner.injection.Injector;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FxApplication extends Application {

    @Override
    public void start(final Stage stage) throws Exception {
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
        Injector.forgetAll();
    }

    public static void main(final String[] args) {
        launch(args);
    }
}
