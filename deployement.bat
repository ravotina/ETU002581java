
@REM @echo off
@REM REM definition des variable contenant le nom des dossier
@REM set /p nomAppli=Entrez le nom du dossier de l'application :
@REM set source="D:\kandra\sprint9\ETU002581"
@REM set "destination=D:\kandra\sprint9\Test"
@REM set "destinationTemp=D:\kandra\sprint9\Temp"
@REM set "Temp=temp"
@REM set "lib=lib"
@REM set "src=Controlleur"


@REM echo le deployement est traiter dans le dossier "%destination%"

@REM REM ############ SUPRESSION DU DOSSIER Temp ################
@REM if exist "%destinationTemp%\%Temp%" (
@REM     rmdir /s /q "%destinationTemp%\%Temp%"
@REM     echo le dossier "%Temp%" a ete suprimer

@REM     mkdir "%destinationTemp%\%Temp%"
@REM     echo le dossier "%Temp%" a ete recree
@REM ) else (
@REM     mkdir "%destinationTemp%\%Temp%"
@REM    echo le dossier "%Temp%" a ete cree
@REM )

@REM REM ########## COMPILATION DES FICHIER JAVA ##########################
@REM set source_jar=%source%\%lib%\*

@REM javac -g -cp %source_jar% -d %destinationTemp%\%Temp% %source%\%src%\*.java

@REM REM ######### COMPRESSER LE FICHIER DOSSIER Temp EN .war
@REM set nameFileWar=%src%.jar
@REM jar -cvf "%destination%\%lib%\%nameFileWar%" -C "%destinationTemp%\%Temp%" .

@REM echo "deployement terminer"

@REM pause



@echo off
REM définition des variables contenant les noms des dossiers
set /p nomAppli=Entrez le nom du dossier de l'application :
set source=D:\kandra\sprint13\ETU002581
set destination=D:\kandra\sprint13\Test
set destinationTemp=D:\kandra\sprint12\Temp
set Temp=temp
set lib=lib
set src=Controlleur

echo Déploiement traité dans le dossier "%destination%"

REM ############ SUPPRESSION DU DOSSIER Temp ################
if exist "%destinationTemp%\%Temp%" (
    rmdir /s /q "%destinationTemp%\%Temp%"
    echo Le dossier "%Temp%" a été supprimé

    mkdir "%destinationTemp%\%Temp%"
    echo Le dossier "%Temp%" a été recréé
) else (
    mkdir "%destinationTemp%\%Temp%"
    echo Le dossier "%Temp%" a été créé
)

REM ########## COMPILATION DES FICHIERS JAVA ##########################
set source_jar=%source%\%lib%\*

REM Compilation des fichiers .java dans le répertoire de destination temporaire
javac -g -cp "%source_jar%" -d "%destinationTemp%\%Temp%" "%source%\%src%\*.java"

REM ######### COMPRESSER LE DOSSIER Temp EN .jar #####################
set nameFileWar=%src%.jar

REM Compression en .jar
jar -cvf "%destination%\%lib%\%nameFileWar%" -C "%destinationTemp%\%Temp%" .

echo Déploiement terminé

pause
