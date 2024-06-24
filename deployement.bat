
@echo off
REM definition des variable contenant le nom des dossier
set /p nomAppli=Entrez le nom du dossier de l'application :
set source="D:\kandra\sprint7\ETU002581"
set "destination=D:\kandra\sprint7\Test"
set "destinationTemp=D:\kandra\sprint7\Temp"
set "Temp=temp"
set "lib=lib"
set "src=Controlleur"


echo le deployement est traiter dans le dossier "%destination%"

REM ############ SUPRESSION DU DOSSIER Temp ################
if exist "%destinationTemp%\%Temp%" (
    rmdir /s /q "%destinationTemp%\%Temp%"
    echo le dossier "%Temp%" a ete suprimer

    mkdir "%destinationTemp%\%Temp%"
    echo le dossier "%Temp%" a ete recree
) else (
    mkdir "%destinationTemp%\%Temp%"
   echo le dossier "%Temp%" a ete cree
)

REM ########## COMPILATION DES FICHIER JAVA ##########################
set source_jar=%source%\%lib%\*

javac -g -cp %source_jar% -d %destinationTemp%\%Temp% %source%\%src%\*.java

REM ######### COMPRESSER LE FICHIER DOSSIER Temp EN .war
set nameFileWar=%src%.jar
jar -cvf "%destination%\%lib%\%nameFileWar%" -C "%destinationTemp%\%Temp%" .

echo "deployement terminer"

pause
