package com.mardpop.jrocket;

import com.mardpop.jrocket.atmosphere.Air;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * JavaFX App
 */
public class App extends Application {

    private Scene scene;
    
    public void runTests()
    {
        Logger logger 
            = Logger.getLogger(App.class.getName()); 
          
        double Mtest = 1.2;
        double A = Air.areaRatio(Mtest, 1.4);
        double M = Air.supersonicMachFromAreaRatio(A, 1.4);
        logger.info("Result " + A);
        logger.info("Result " + M);
    }

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("primary"), 640, 480);
        stage.setScene(scene);
        stage.show();
        stage.setMaximized(true);
    }

    private static Parent loadFXML(String fxml) throws IOException {
        String fxmlFile =  fxml + ".fxml";
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxmlFile));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}