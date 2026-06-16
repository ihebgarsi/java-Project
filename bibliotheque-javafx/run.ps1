# Compilation et lancement sans Maven
$ErrorActionPreference = "Stop"
$ProjectRoot = $PSScriptRoot
$Src = Join-Path $ProjectRoot "src\main\java"
$Resources = Join-Path $ProjectRoot "src\main\resources"
$Out = Join-Path $ProjectRoot "out"
$Lib = Join-Path $ProjectRoot "lib"
$JavaFxVersion = "21.0.2"
$BaseUrl = "https://repo1.maven.org/maven2/org/openjfx"

$Artifacts = @("javafx-base", "javafx-graphics", "javafx-controls", "javafx-fxml")

function Ensure-JavaFxJars {
    New-Item -ItemType Directory -Force -Path $Lib | Out-Null
    foreach ($artifact in $Artifacts) {
        $jarName = "$artifact-$JavaFxVersion-win.jar"
        $jar = Join-Path $Lib $jarName
        if (-not (Test-Path $jar)) {
            $url = "$BaseUrl/$artifact/$JavaFxVersion/$jarName"
            Write-Host "Telechargement $jarName..."
            curl.exe -L -o $jar $url
            if ($LASTEXITCODE -ne 0) {
                throw "Echec telechargement JavaFX : $url"
            }
        }
    }
}

Ensure-JavaFxJars

$javaFiles = @(Get-ChildItem -Path $Src -Filter "*.java" -Recurse | ForEach-Object { $_.FullName })
New-Item -ItemType Directory -Force -Path $Out | Out-Null

Write-Host "Compilation..."
& javac --release 21 `
    --module-path $Lib `
    --add-modules javafx.controls,javafx.fxml `
    -d $Out `
    -cp $Resources `
    $javaFiles

if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host "Lancement de l'application..."
Set-Location $ProjectRoot
& java --module-path $Lib `
    --add-modules javafx.controls,javafx.fxml `
    -cp "$Out;$Resources" `
    tn.bibliotheque.ui.MainApp
