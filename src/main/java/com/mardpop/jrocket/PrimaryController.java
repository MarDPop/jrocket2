package com.mardpop.jrocket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// import javafx.swing.SwingNode;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import org.json.*;

import com.mardpop.jrocket.designer.AeroCalc;
import com.mardpop.jrocket.designer.Curve;
import com.mardpop.jrocket.designer.InertiaCalc;
import com.mardpop.jrocket.designer.RocketParameters;
import com.mardpop.jrocket.designer.Curve.CurvePoint;
import com.mardpop.jrocket.designer.RocketShape;
import com.mardpop.jrocket.util.*;
import com.mardpop.jrocket.vehicle.InertiaSimple;
import com.mardpop.jrocket.vehicle.propulsion.CommercialMotor;

public class PrimaryController implements Initializable
{
    String currentProjectName = "SimpleRocket";

    @FXML
    CheckBox overrideValuesBox;

    @FXML
    TextField emptyMassEntry;

    @FXML
    TextField ixxEmptyEntry;

    @FXML
    TextField irrEmptyEntry;

    @FXML
    TextField cGxEmptyEntry;

    @FXML
    TextField cGxFullEntry;

    @FXML
    TextField propellantMassEntry;

    @FXML
    TextField cdEntry;

    @FXML
    TextField cnAlphaEntry;

    @FXML
    TextField centerOfPressureEntry;

    @FXML
    TextField referenceAreaEntry;

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
    TextField noseConeLengthEntry, noseSphericalEntry;

    @FXML
    ChoiceBox<String> noseConeTypeEntry;

    @FXML 
    TextField payloadRadiusEntry, payloadLengthEntry, payloadFlangeLengthEntry;

    @FXML
    TextField tubeRadiusEntry, tubeLengthEntry, tubeFlangeLengthEntry;

    @FXML
    TextField motorRadiusEntry, motorLengthEntry;

    @FXML
    TextField finBaseChordEntry, finTipChordEntry, finChordOffsetEntry, finSweepEntry, finSpanEntry;

    @FXML
    ChoiceBox<Integer> numberOfFinsEntry;

    @FXML
    TextField payloadMassEntry, payloadCOMEntry;

    @FXML
    TextField motorXOffsetEntry;

    @FXML
    ChoiceBox<String> structureMaterialEntry;

    @FXML
    TextField structureThicknessEntry;

    @FXML
    ChoiceBox<String> motorSelection;

    HashMap<String, String> motorFiles = new HashMap<>();

    HashMap<String, CommercialMotor> motorMap = new HashMap<>();

    @FXML
    Button designerRunButton;

    @FXML
    Label maxHeightLabel;

    @FXML
    Label maxDistanceLabel;

    @FXML
    Label deltaVLabel;

    @FXML
    Canvas designCanvas;

    @FXML
    VBox simpleRocketForm;

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

        RocketParameters params = getParams();

        JSONObject thruster = rocket.getJSONObject("Thruster");
        try {
            String motorName = this.motorSelection.getSelectionModel().getSelectedItem();
            thruster.put("File", this.motorFiles.get(motorName));
            thruster.put("Name" , motorName);  
            thruster.put("CG_Offset", params.totalLength - params.motorXOffset);
        } catch (Exception e) {}

        JSONObject inertia = rocket.getJSONObject("InertiaEmpty");

        try {
            inertia.put("Mass", Double.parseDouble(emptyMassEntry.getText()));
            inertia.put("CGx", Double.parseDouble(cGxEmptyEntry.getText()));
            inertia.put("Irr", Double.parseDouble(irrEmptyEntry.getText()));
            inertia.put("Ixx", Double.parseDouble(ixxEmptyEntry.getText()));
        } catch (Exception e) {}

        JSONObject aerodynamics = rocket.getJSONObject("Aerodynamics");

        try {
            aerodynamics.put("CD", Double.parseDouble(cdEntry.getText()));
        } catch (Exception e) {}

        try {
            aerodynamics.put("CN_alpha", Double.parseDouble(cnAlphaEntry.getText()));
        } catch (Exception e) {}

        try {
            aerodynamics.put("COP_x", Double.parseDouble(centerOfPressureEntry.getText()));
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
    void loadMotorFile()
    {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(null);
        if(selectedFile != null)
        {
            try 
            {
                String filePath = selectedFile.getAbsolutePath();
                CommercialMotor motor = CommercialMotor.loadRASPFile(this.motorFiles.get(filePath));
                this.motorMap.put(motor.name, motor);
                this.motorFiles.put(motor.name, filePath);
                if(!motorSelection.getItems().contains(motor.name))
                {
                    motorSelection.getItems().add(motor.name);
                    motorSelection.setValue(motor.name);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                //Setting the content of the dialog
                alert.setContentText("Unable to load the motor file: " + selectedFile.getAbsolutePath());
                alert.showAndWait();
            }
        }
    }

    @FXML
    void createMotorFile()
    {
        
    }

    RocketParameters getParams()
    {
        final double CM2M = 0.01;
        final double MM2M = 0.001;
        final double G2KG = 0.001;
        RocketParameters params = new RocketParameters();
        params.finBaseChord = Double.parseDouble(this.finBaseChordEntry.getText())*CM2M;
        params.finTipChord = Double.parseDouble(this.finTipChordEntry.getText())*CM2M;
        params.finChordOffset = Double.parseDouble(this.finChordOffsetEntry.getText())*CM2M;
        params.finSpan = Double.parseDouble(this.finSpanEntry.getText())*CM2M;
        params.finSweep = Math.toRadians(Double.parseDouble(this.finSweepEntry.getText()));
        params.numFins = this.numberOfFinsEntry.getValue();
        params.motorLength = Double.parseDouble(this.motorLengthEntry.getText())*CM2M;
        params.motorRadius = Double.parseDouble(this.motorRadiusEntry.getText())*CM2M;
        params.noseConeLength = Double.parseDouble(this.noseConeLengthEntry.getText())*CM2M;
        params.noseConeSphericalRadius = Double.parseDouble(this.noseSphericalEntry.getText())*CM2M;
        params.payloadFlangeLength = Double.parseDouble(this.payloadFlangeLengthEntry.getText())*CM2M;
        params.payloadTubeLength = Double.parseDouble(this.payloadLengthEntry.getText())*CM2M;
        params.payloadTubeRadius = Double.parseDouble(this.payloadRadiusEntry.getText())*CM2M;
        params.tubeFlangeLength = Double.parseDouble(this.tubeFlangeLengthEntry.getText())*CM2M;
        params.tubeLength = Double.parseDouble(this.tubeLengthEntry.getText())*CM2M;
        params.tubeRadius = Double.parseDouble(this.tubeRadiusEntry.getText())*CM2M;
        params.motorXOffset = Double.parseDouble(this.motorXOffsetEntry.getText())*MM2M;

        switch(this.noseConeTypeEntry.getValue())
        {
            case "Spherical":
                params.noseConeType = RocketShape.NoseConeType.CONICAL;
                break;
            case "Elliptical":
                params.noseConeType = RocketShape.NoseConeType.ELLIPTICAL;
                break;
            case "Parabolic":
                params.noseConeType = RocketShape.NoseConeType.HALF_POWER;
                break;
            case "Tangent Ogive":
                params.noseConeType = RocketShape.NoseConeType.TANGENT_OGIVE;
                break;
            default:    
                params.noseConeType = RocketShape.NoseConeType.CONICAL;
                break;
        }

        params.payloadMass = Double.parseDouble(this.payloadMassEntry.getText())*G2KG; // to kg
        params.payloadCOM = Double.parseDouble(this.payloadCOMEntry.getText())*CM2M;

        params.structureMaterial = this.structureMaterialEntry.getSelectionModel().getSelectedIndex();
        params.structureThickness = Double.parseDouble(this.structureThicknessEntry.getText())*MM2M;

        params.totalLength = params.noseConeLength + params.payloadTubeLength 
            + params.payloadFlangeLength + params.tubeLength + params.tubeFlangeLength  + params.motorLength;
        return params;
    }

    void drawRocket(RocketParameters params, double COM_empty, double COM_full, 
        double COP, double motorRadius, double motorLength)
    {
        RocketShape rs = new RocketShape(params);
        Curve body = rs.getBody();
        Curve fin = rs.getFin();

        GraphicsContext g = this.designCanvas.getGraphicsContext2D();

        g.clearRect(0, 0, designCanvas.getWidth(), designCanvas.getHeight());
        g.setFill(Color.WHITE);
        g.fillRect(0, 0, designCanvas.getWidth(), designCanvas.getHeight());
        g.setStroke(Color.BLACK);

        double xScale = designCanvas.getWidth()*0.95 / body.points.getLast().x;
        double yScale = designCanvas.getHeight()*0.45 / fin.points.get(2).r;

        double scale = Math.min(xScale, yScale);

        double yCenter = designCanvas.getHeight()*0.5;
        double xCenter = designCanvas.getWidth()*0.025;

        g.beginPath();
        g.moveTo(xCenter + scale*body.points.get(0).x, yCenter + scale*body.points.get(0).r);
        for(CurvePoint point : body.points)
        {
            g.lineTo(xCenter + scale*point.x, yCenter + scale*point.r);
        }
        g.lineTo(xCenter + scale*body.points.getLast().x, yCenter);
        g.stroke();

        g.beginPath();
        g.moveTo(xCenter + scale*fin.points.get(0).x, yCenter + scale*fin.points.get(0).r);
        for(CurvePoint point : fin.points)
        {
            g.lineTo(xCenter + scale*point.x, yCenter + scale*point.r);
        }
        g.stroke();

        g.beginPath();
        g.moveTo(xCenter + scale*body.points.get(0).x, yCenter - scale*body.points.get(0).r);
        for(CurvePoint point : body.points)
        {
            g.lineTo(xCenter + scale*point.x, yCenter - scale*point.r);
        }
        g.lineTo(xCenter + scale*body.points.getLast().x, yCenter);
        g.stroke();

        g.beginPath();
        g.moveTo(xCenter + scale*fin.points.get(0).x, yCenter - scale*fin.points.get(0).r);
        for(CurvePoint point : fin.points)
        {
            g.lineTo(xCenter + scale*point.x, yCenter - scale*point.r);
        }
        g.stroke();

        // Motor
        g.setFill(Color.PALETURQUOISE);
        double h = scale*motorRadius;
        double w = scale*motorLength;
        double xStart = xCenter + scale*(params.totalLength - motorLength);
        g.fillRect(xStart, yCenter - h, w, h); 
        g.fillRect(xStart, yCenter, w, h);

        g.setFill(Color.MEDIUMSEAGREEN);
        double xPayload = params.noseConeLength + params.payloadTubeLength*0.5;
        double xStroke = xCenter + scale*xPayload;
        double yStroke = yCenter;
        double radius = 10;
        g.fillOval(xStroke - radius, yStroke - radius, radius*2, radius*2);

        this.drawWeightBalance(COM_empty, COM_full, COP, xCenter, yCenter, scale);
    }

    void drawWeightBalance(final double COM_empty, final double COM_full, final double COP,
        final double xCenter, final double yCenter, final double scale)
    {
        GraphicsContext g = this.designCanvas.getGraphicsContext2D();
        double radius = 6;
        double xStroke = xCenter + scale*COM_empty;
        g.setStroke(Color.CORAL);
        g.strokeOval(xStroke - radius, yCenter - radius, radius*2, radius*2);

        xStroke = xCenter + scale*COP;
        g.setFill(Color.BLUE);
        g.fillOval(xStroke - radius, yCenter - radius, radius*2, radius*2);

        radius = 10;
        xStroke = xCenter + scale*COM_full;
        g.setStroke(Color.RED);
        g.strokeOval(xStroke - radius, yCenter - radius, radius*2, radius*2);
        g.strokeLine(xStroke - radius, yCenter, xStroke + radius, yCenter);
        g.strokeLine(xStroke, yCenter - radius, xStroke, yCenter + radius);

    }

    InertiaSimple computeEmptyInertia(RocketParameters params)
    {
        return InertiaCalc.computeEmptyInertiaFromParams(params);
    }

    @FXML
    void runDesignTool() 
    {
        CommercialMotor motor = this.motorMap.get(this.motorSelection.getSelectionModel().getSelectedItem());

        if(motor == null)
        {   
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            //Setting the content of the dialog
            alert.setContentText("motor not selected");
            alert.showAndWait();
            return;
        }

        RocketParameters params = this.getParams();
        
        InertiaSimple empty = this.computeEmptyInertia(params);

        motor.thrusterXOffset = params.totalLength - params.motorXOffset;

        double[] values = motor.getValuesAtTime(0);
        double mass_full = empty.mass + values[1];

        double cGx_full = (empty.CGx*empty.mass + values[4]*values[1])
            / mass_full;

        this.irrEmptyEntry.setText(Double.toString(empty.Irr));
        this.ixxEmptyEntry.setText(Double.toString(empty.Ixx));
        this.emptyMassEntry.setText(Double.toString(empty.mass));
        this.propellantMassEntry.setText(Double.toString(values[1]));
        this.cGxEmptyEntry.setText(Double.toString(empty.CGx));
        this.cGxFullEntry.setText(Double.toString(cGx_full));

        double[] coef = AeroCalc.barrowmanCoef(params);
        this.centerOfPressureEntry.setText(Double.toString(coef[0]));
        this.cnAlphaEntry.setText(Double.toString(coef[1]));
        this.referenceAreaEntry.setText(Double.toString(coef[2]));

        double cd  = AeroCalc.dragCoef(params);
        this.cdEntry.setText(Double.toString(cd));

        this.drawRocket(params, empty.CGx, cGx_full, coef[0], motor.radius, motor.length);
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

                if(rocket.has("Thruster"))
                {
                    JSONObject obj = rocket.getJSONObject("Thruster");
                    CommercialMotor motor = null;
                    if(obj.has("File") && obj.getString("File").length() > 0)
                    {
                        motor = CommercialMotor.loadRASPFile(obj.getString("File"));
                    }
                    else if(obj.has("Name") && obj.getString("Name").length() > 0)
                    {
                        motor = motorMap.get(obj.getString("Name"));
                    }

                    if(motor != null)
                    {
                        if(motorSelection.getItems().contains(motor.name))
                        {
                            motorSelection.getSelectionModel().select(motor.name);
                        }
                        else
                        {
                            motorSelection.getItems().add(motor.name);
                            motorSelection.getSelectionModel().select(motor.name);
                            motorMap.put(motor.name, motor);
                        }
                    }
                    else
                    {
                        throw new Exception("Couldn't load Thruster");
                    }
                    this.propellantMassEntry.setText(Double.toString(motor.getPropellantMass()));
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
                    cnAlphaEntry.setText(Double.toString(obj.getDouble("CN_alpha")));
                    centerOfPressureEntry.setText(Double.toString(obj.getDouble("COP_x")));
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
                orientations.add(new Quaternion(qx, qy, qz, qw));
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
            double deltaV = 100*9.806*Math.log(massRatio);

            int tIdx = 0;
            final int tIdxFinal = times.size() - 1;
            for(int i = 0; i < TIME_INTERVALS; i++)
            {
                final double time = i*timeIncrement;
                while(tIdx < tIdxFinal && times.get(tIdx + 1) < time)
                {
                    tIdx++;
                }

                final double deltaT = (time - times.get(tIdx))/(times.get(tIdx+1) - times.get(tIdx));

                final double mass = masses.get(tIdx) + deltaT*(masses.get(tIdx+1) - masses.get(tIdx));
                massSeries.getData().add(new Data<Double,Double>(time, mass));

                double speedlo = velocities.get(tIdx).magnitude();
                double speedhi = velocities.get(tIdx+1).magnitude();
                double speed = speedlo + deltaT*(speedhi - speedlo);
                speedSeries.getData().add(new Data<Double,Double>(time, speed));

                double acceleration = (speedhi - speedlo)/(times.get(tIdx+1) - times.get(tIdx)); // simple for now
                accelerationSeries.getData().add(new Data<Double,Double>(time, acceleration));

                double altitudelo = positions.get(tIdx).z;
                double altitudehi = positions.get(tIdx+1).z;
                double altitude = altitudelo + deltaT*(altitudehi - altitudelo);
                altitudSeries.getData().add(new Data<Double,Double>(time, altitude));

                double distancelo = Math.sqrt(positions.get(tIdx).x*positions.get(tIdx).x + 
                    positions.get(tIdx).y*positions.get(tIdx).y);
                double distancehi = Math.sqrt(positions.get(tIdx+1).x*positions.get(tIdx+1).x +
                    positions.get(tIdx+1).y*positions.get(tIdx+1).y);
                double distance = distancelo + deltaT*(distancehi - distancelo);
                groundDistanceSeries.getData().add(new Data<Double,Double>(time, distance));

                maxHeight = Double.max(maxHeight, altitudelo);
                maxDistance = Double.max(maxDistance, distancelo);

                Matrix3 CSlo = orientations.get(tIdx).toRotationMatrix();
                Matrix3 CShi = orientations.get(tIdx+1).toRotationMatrix();
                Vec3 xAxislo = CSlo.getRow0();
                Vec3 xAxishi = CShi.getRow0();
                double pitchlo = Math.acos(xAxislo.z);
                double pitchhi = Math.acos(xAxishi.z);
                double headinglo = Math.atan2(xAxislo.y, xAxislo.x);
                double headinghi = Math.atan2(xAxishi.y, xAxishi.x);
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
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            //Setting the content of the dialog
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.noseConeTypeEntry.getItems().setAll("Cone", "Elliptical",
            "Tangent Ogive", "Secant Ogive", "Parabolic", "1/2 Power", "3/4 Power", "Von Karman");

        this.noseConeTypeEntry.setValue("Cone");

        this.numberOfFinsEntry.getItems().setAll(0,2,3,4);

        this.numberOfFinsEntry.setValue(3);

        this.structureMaterialEntry.getItems().setAll("Cardboard", "Plastic",
             "Aluminum", "Fiberglass", "Carbon Fiber");

        this.structureMaterialEntry.getSelectionModel().selectFirst();
        
        this.overrideValuesBox.onActionProperty().setValue((event) -> {
            boolean flag = !overrideValuesBox.isSelected();
            for(Node node : simpleRocketForm.getChildren())
            {
                if(node instanceof TextField)
                {
                    node.setDisable(flag);
                }
            }
        });

        this.overrideValuesBox.setSelected(true);

        Set<File> files = Stream.of(new File("./etc").listFiles())
            .filter(file -> !file.isDirectory())
            .filter(file -> file.getName().endsWith(".eng"))
            .collect(Collectors.toSet());

        for(File file : files)
        {
            try
            {
                CommercialMotor motor = CommercialMotor.loadRASPFile(file.getAbsolutePath());
                this.motorFiles.put(motor.name, file.getAbsolutePath());
                this.motorMap.put(motor.name, motor);
            }
            catch(Exception e)  
            {
                e.printStackTrace();
            }
        }

        this.motorSelection.getItems().setAll(this.motorMap.keySet());

        /* 
        SwingNode node = new SwingNode();

        Platform.runLater(() -> {
            WorldWindowGLJPanel wwj = new WorldWindowGLJPanel();
            Model m = (Model)WorldWind.createConfigurationComponent(AVKey.MODEL_CLASS_NAME);
            wwj.setModel(m);
            node.setContent(wwj);
        });
        */
    }

}
