@echo off
echo Building NightsAtIAcademy...

REM CLEAN OLD BUILD
rmdir /s /q NightsAtIAcademy

REM RUN JPACKAGE
jpackage --input "out\artifacts\Five_Nights_At_iACADEMY_jar" --main-jar "Five-Nights-At-iACADEMY.jar" --main-class main.Main --name "NightsAtIAcademy" --app-version 1.0 --type app-image --runtime-image "C:\Program Files\Java\jdk-25" --java-options "--module-path $APPDIR/lib --add-modules javafx.media,javafx.swing,javafx.base,javafx.graphics,javafx.controls --enable-native-access=ALL-UNNAMED"

REM COPY JAVAFX NATIVE DLLS
xcopy "lib\javafx-sdk-25.0.1\bin\*.*" "NightsAtIAcademy\runtime\bin\" /I /Y

echo Done! NightsAtIAcademy is ready.
pause