package com.mardpop.jrocket;

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
    
    public void runSim() throws IOException 
    {
        Logger logger 
            = Logger.getLogger(App.class.getName()); 
        logger.info("Running Sim.");
        
        SimulationSimpleRocket sim = new SimulationSimpleRocket();
        
        sim.load("simple_rocket.json");
        
        sim.run();
        
        sim.save("simple_rocket.output");
    }

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("primary"), 640, 480);
        stage.setScene(scene);
        stage.show();
        
        runSim();
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}