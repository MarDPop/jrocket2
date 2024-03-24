module com.mardpop.jrocket {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;
    requires java.logging;

    opens com.mardpop.jrocket to javafx.fxml;
    exports com.mardpop.jrocket;
    exports com.mardpop.jrocket.vehicle;
    exports com.mardpop.jrocket.atmosphere;
    exports com.mardpop.jrocket.util;
    exports com.mardpop.jrocket.vehicle.gnc;
    exports com.mardpop.jrocket.designer;
    exports com.mardpop.jrocket.vehicle.propulsion;
    exports com.mardpop.jrocket.vehicle.aerodynamics;
}
