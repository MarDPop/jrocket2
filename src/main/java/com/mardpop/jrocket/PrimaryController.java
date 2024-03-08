package com.mardpop.jrocket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.*;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import org.json.*;

import com.mardpop.jrocket.util.*;

public class PrimaryController 
{
    String currentProjectName = "SimpleRocket";

    @FXML
    CheckBox useDesignerToggle;

    @FXML
    TextField thrustEntry;

    @FXML
    TextField ispEntry;

    @FXML
    TextField emptyMassEntry;

    @FXML
    TextField ixxEmptyEntry;

    @FXML
    TextField irrEmptyEntry;

    @FXML
    TextField propellantEntry;

    @FXML
    TextField ixxPropEntry;

    @FXML
    TextField irrPropEntry;

    @FXML
    TextField cdEntry;

    @FXML
    TextField clAlphaEntry;

    @FXML
    TextField cmAlphaEntry;

    @FXML
    TextField referenceAreaEntry;

    @FXML
    TextField liftInducedDragEntry;

    @FXML
    TextField pressureEntry;

    @FXML
    TextField temperatureEntry;

    @FXML
    TextField gravityEntry;

    @FXML
    TextField latitudeEntry;

    @FXML
    TextField pitchEntry;

    @FXML
    TextField headingEntry;

    @FXML
    Label maxHeightLabel;

    @FXML
    Label maxDistanceLabel;

    @FXML
    Label deltaVLabel;

    @FXML
    VBox SimpleRocketForm;

    @FXML
    LineChart<Double, Double> distanceChart;

    @FXML
    LineChart<Double, Double> speedChart;

    @FXML
    LineChart<Double, Double> angleChart;

    @FXML
    LineChart<Double, Double> massChart;

    @FXML
    void saveSimpleRocket() 
    {
        JSONObject json = ControllerHelper.createSimulationSimpleJson();

        JSONObject ground = json.getJSONObject("Ground");
        try {
            ground.put("Gravity", Double.parseDouble(gravityEntry.getText()));
        } catch (Exception e) { }

        try {
            ground.put("Pressure", Double.parseDouble(pressureEntry.getText()));
        } catch (Exception e) {}

        try {
            ground.put("Temperature", Double.parseDouble(temperatureEntry.getText()));
        } catch (Exception e) {}
        
        JSONObject launch = json.getJSONObject("Launch");
        try {
            launch.put("Latitude", Double.parseDouble(latitudeEntry.getText()));
        } catch (Exception e) {}

        try {
            launch.put("Pitch", Double.parseDouble(pitchEntry.getText()));
        } catch (Exception e) {}

        try {
            launch.put("Heading", Double.parseDouble(headingEntry.getText()));
        } catch (Exception e) {}

        JSONObject rocket = json.getJSONObject("Rocket");

        JSONObject inertiaEmpty = rocket.getJSONObject("InertiaEmpty");

        try {
            inertiaEmpty.put("Mass", Double.parseDouble(emptyMassEntry.getText()));
        } catch (Exception e) {}

        try {
            inertiaEmpty.put("Ixx", Double.parseDouble(ixxEmptyEntry.getText()));
        } catch (Exception e) {}

        try {
            inertiaEmpty.put("Irr", Double.parseDouble(irrEmptyEntry.getText()));
        } catch (Exception e) {}

        JSONObject inertiaPropellant = rocket.getJSONObject("InertiaFuel");

        try {
            inertiaPropellant.put("Mass", Double.parseDouble(propellantEntry.getText()));
        } catch (Exception e) {}

        try {
            inertiaPropellant.put("Ixx", Double.parseDouble(ixxPropEntry.getText()));
        } catch (Exception e) {}

        try {
            inertiaPropellant.put("Irr", Double.parseDouble(irrPropEntry.getText()));
        } catch (Exception e) {}

        JSONObject aerodynamics = rocket.getJSONObject("Aerodynamics");

        try {
            aerodynamics.put("CD", Double.parseDouble(cdEntry.getText()));
        } catch (Exception e) {}

        try {
            aerodynamics.put("CL_alpha", Double.parseDouble(clAlphaEntry.getText()));
        } catch (Exception e) {}

        try {
            aerodynamics.put("CM_alpha", Double.parseDouble(cmAlphaEntry.getText()));
        } catch (Exception e) {}

        try {
            aerodynamics.put("LiftInducedDrag", Double.parseDouble(liftInducedDragEntry.getText()));
        } catch (Exception e) {}

        try {
            aerodynamics.put("Area", Double.parseDouble(referenceAreaEntry.getText()));
        } catch (Exception e) {}

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.currentProjectName +".sRocket"))) 
        {
            writer.write(json.toString());
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }

    @FXML
    void loadSimpleRocket()
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Simple Rocket File");
        File selectedFile = fileChooser.showOpenDialog(null);
        
        if (selectedFile != null) {
            try {
                String content = new String(Files.readAllBytes(Paths.get(selectedFile.getPath())));
                JSONObject json = new JSONObject(content);
                if(json.has("Ground"))
                {
                    JSONObject obj = json.getJSONObject("Ground");
                    double g = obj.getDouble("Gravity");
                    double p = obj.getDouble("Pressure");
                    double t = obj.getDouble("Temperature");
                    gravityEntry.setText(Double.toString(g));
                    pressureEntry.setText(Double.toString(p));
                    temperatureEntry.setText(Double.toString(t));
                }

                if(json.has("Launch"))
                {
                    JSONObject obj = json.getJSONObject("Launch");
                    latitudeEntry.setText(Double.toString(obj.getDouble("Latitude")));
                    pitchEntry.setText(Double.toString(obj.getDouble("Pitch")));
                    headingEntry.setText(Double.toString(obj.getDouble("Heading")));
                }

                if(!json.has("Rocket"))
                { 
                    throw new Exception("Missing Rocket");
                }

                JSONObject rocket = json.getJSONObject("Rocket");

                if(rocket.has("InertiaEmpty"))
                {
                    JSONObject obj = rocket.getJSONObject("InertiaEmpty");
                    emptyMassEntry.setText(Double.toString(obj.getDouble("Mass")));
                    ixxEmptyEntry.setText(Double.toString(obj.getDouble("Ixx")));
                    irrEmptyEntry.setText(Double.toString(obj.getDouble("Irr")));
                }
                else
                {
                    throw new Exception("Missing InertiaEmpty");
                }

                if(rocket.has("InertiaFuel"))
                {
                    JSONObject obj = rocket.getJSONObject("InertiaFuel");
                    propellantEntry.setText(Double.toString(obj.getDouble("Mass")));
                    ixxPropEntry.setText(Double.toString(obj.getDouble("Ixx")));
                    irrPropEntry.setText(Double.toString(obj.getDouble("Irr")));
                }
                else
                {
                    throw new Exception("Missing InertiaFuel");
                }

                if(rocket.has("Thruster"))
                {
                    JSONObject obj = rocket.getJSONObject("Thruster");
                    thrustEntry.setText(Double.toString(obj.getDouble("Thrust")));
                    ispEntry.setText(Double.toString(obj.getDouble("ISP")));
                }
                else
                {
                    throw new Exception("Missing Thruster");
                }

                if(rocket.has("Aerodynamics"))
                {
                    JSONObject obj = rocket.getJSONObject("Aerodynamics");
                    cdEntry.setText(Double.toString(obj.getDouble("CD")));
                    referenceAreaEntry.setText(Double.toString(obj.getDouble("Area")));
                    clAlphaEntry.setText(Double.toString(obj.getDouble("CL_alpha")));
                    cmAlphaEntry.setText(Double.toString(obj.getDouble("CM_alpha")));
                    liftInducedDragEntry.setText(Double.toString(obj.getDouble("LiftInducedDrag")));
                }
                else
                {
                    throw new Exception("Missing Aerodynamics");
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        }
    }

    @SuppressWarnings("unchecked")
    private void loadCharts(String fileName)
    {
        this.massChart.getData().clear();
        this.distanceChart.getData().clear();
        this.speedChart.getData().clear();
        this.angleChart.getData().clear();

        try(BufferedReader reader = new BufferedReader(new FileReader(fileName)))
        {
            System.out.println("Found data file. Loading...");
            String line;
            line = reader.readLine(); // skip header

            ArrayList<Double> times = new ArrayList<>();
            ArrayList<Double> masses = new ArrayList<>();
            ArrayList<Vec3> positions = new ArrayList<>();
            ArrayList<Vec3> velocities = new ArrayList<>();
            ArrayList<Quaternion> orientations = new ArrayList<>();
            ArrayList<Vec3> angular_velocities = new ArrayList<>();

            while((line = reader.readLine()) != null)
            {
                String[] split = line.trim().split("\\s+");
                if(split.length < 15) continue;
                double time = Double.parseDouble(split[0]);
                double mass = Double.parseDouble(split[1]);
                double X = Double.parseDouble(split[2]);
                double Y = Double.parseDouble(split[3]);
                double Z = Double.parseDouble(split[4]);
                double Vx = Double.parseDouble(split[5]);
                double Vy = Double.parseDouble(split[6]);
                double Vz = Double.parseDouble(split[7]);
                double qw = Double.parseDouble(split[8]);
                double qx = Double.parseDouble(split[9]);
                double qy = Double.parseDouble(split[10]);
                double qz = Double.parseDouble(split[11]);
                double ax = Double.parseDouble(split[12]);
                double ay = Double.parseDouble(split[13]);
                double az = Double.parseDouble(split[14]);

                times.add(time);
                masses.add(mass);
                positions.add(new Vec3(X, Y, Z));
                velocities.add(new Vec3(Vx, Vy, Vz));
                orientations.add(new Quaternion(qw, qx, qy, qz));
                angular_velocities.add(new Vec3(ax, ay, az));
            }

            final double timeMax = times.getLast();
            final int TIME_INTERVALS = 100;
            final double timeIncrement = timeMax / TIME_INTERVALS;

            Series<Double, Double> massSeries = new Series<>();
            Series<Double, Double> speedSeries = new Series<>();
            Series<Double, Double> accelerationSeries = new Series<>();
            Series<Double, Double> altitudSeries = new Series<>();
            Series<Double, Double> groundDistanceSeries = new Series<>();
            Series<Double, Double> pitchSeries = new Series<>();
            Series<Double, Double> headingSeries = new Series<>();

            massSeries.setName("Mass");
            speedSeries.setName("Speed");
            accelerationSeries.setName("Acceleration");
            altitudSeries.setName("Altitude");
            groundDistanceSeries.setName("Distance");
            pitchSeries.setName("Pitch");
            headingSeries.setName("Heading");

            double maxHeight = 0;
            double maxDistance = 0;
            
            double massRatio = masses.get(0)/ masses.getLast();
            double deltaV = Double.parseDouble(this.ispEntry.getText())*9.806*Math.log(massRatio);

            int tIdx = 0;
            final int tIdxFinal = times.size() - 1;
            for(int i = 0; i < TIME_INTERVALS; i++)
            {
                double time = i * timeIncrement;
                while(tIdx < tIdxFinal && times.get(tIdx + 1) < time)
                {
                    tIdx++;
                }

                double deltaT = time - times.get(tIdx);

                double mass = masses.get(tIdx) + deltaT*(masses.get(tIdx+1) - masses.get(tIdx));
                massSeries.getData().add(new Data<Double,Double>(time, mass));

                double speedlo = velocities.get(tIdx).magnitude();
                double speedhi = velocities.get(tIdx+1).magnitude();
                double speed = speedlo + deltaT*(speedhi - speedlo);
                speedSeries.getData().add(new Data<Double,Double>(time, speed));

                double acceleration = (speedhi - speedlo)/(times.get(tIdx+1) - times.get(tIdx)); // simple for now
                accelerationSeries.getData().add(new Data<Double,Double>(time, acceleration));

                double altitudelo = positions.get(tIdx).z();
                double altitudehi = positions.get(tIdx+1).z();
                double altitude = altitudelo + deltaT*(altitudehi - altitudelo);
                altitudSeries.getData().add(new Data<Double,Double>(time, altitude));

                double distancelo = Math.sqrt(positions.get(tIdx).x()*positions.get(tIdx).x() + 
                    positions.get(tIdx).y()*positions.get(tIdx).y());
                double distancehi = Math.sqrt(positions.get(tIdx+1).x()*positions.get(tIdx+1).x() +
                    positions.get(tIdx+1).y()*positions.get(tIdx+1).y());
                double distance = distancelo + deltaT*(distancehi - distancelo);
                groundDistanceSeries.getData().add(new Data<Double,Double>(time, distance));

                maxHeight = Double.max(maxHeight, altitudelo);
                maxDistance = Double.max(maxDistance, distancelo);

                Matrix3 CSlo = orientations.get(tIdx).toRotationMatrix();
                Matrix3 CShi = orientations.get(tIdx+1).toRotationMatrix();
                Vec3 xAxislo = CSlo.getCol(0);
                Vec3 xAxishi = CShi.getCol(0);
                double pitchlo = Math.acos(xAxislo.z());
                double pitchhi = Math.acos(xAxishi.z());
                double headinglo = Math.atan2(xAxislo.y(), xAxislo.x());
                double headinghi = Math.atan2(xAxishi.y(), xAxishi.x());
                double pitch = pitchlo + deltaT*(pitchhi - pitchlo);
                double heading = headinglo + deltaT*(headinghi - headinglo);
                pitchSeries.getData().add(new Data<Double,Double>(time, Math.toDegrees(pitch)));
                headingSeries.getData().add(new Data<Double,Double>(time, Math.toDegrees(heading)));
            }

            this.massChart.getData().add(massSeries);
            this.speedChart.getData().add(speedSeries);
            this.speedChart.getData().add(accelerationSeries);
            this.distanceChart.getData().add(altitudSeries);
            this.distanceChart.getData().add(groundDistanceSeries);
            this.angleChart.getData().add(pitchSeries);
            this.angleChart.getData().add(headingSeries);

            maxHeightLabel.setText("Max Height (m): " + maxHeight);
            maxDistanceLabel.setText("Max Distance (m): " + maxDistance);
            deltaVLabel.setText("Delta V (m/s) : " + deltaV);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    @FXML
    void launchSimpleRocket()
    {
        this.saveSimpleRocket();
        SimulationSimpleRocket rocket = new SimulationSimpleRocket();
        try 
        {
            rocket.load(this.currentProjectName + ".sRocket");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        Platform.runLater(() -> {
            try
            {
                System.out.println("Running...");
                rocket.run();
                System.out.println("Done! Saving...");
                rocket.save(currentProjectName + ".sData");
                System.out.println("Done! Loading...");
                loadCharts(currentProjectName + ".sData");
            } 
            catch(Exception e)
            {
                e.printStackTrace();
            }
        });

    }


}
