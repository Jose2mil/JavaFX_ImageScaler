module ImageScalerFX {
    requires javafx.fxml;
    requires  javafx.controls;
    requires java.desktop;
    opens imagescalerfx;
    opens imagescalerfx.utils;
    opens imagescalerfx.views.mainview;
    opens imagescalerfx.views.chartview;
}