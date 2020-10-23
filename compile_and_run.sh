#!/bin/bash
javac --module-path $PATH_TO_FX --add-modules javafx.controls,javafx.graphics,javafx.fxml *.java && java --module-path $PATH_TO_FX --add-modules javafx.controls,javafx.graphics,javafx.fxml Main
