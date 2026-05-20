$ErrorActionPreference = "Stop"

$projectRoot = Split-Path -Parent $PSScriptRoot
$javaHome = "C:\Program Files\Eclipse Adoptium\jdk-21.0.11.10-hotspot"
$javac = Join-Path $javaHome "bin\javac.exe"
$java = Join-Path $javaHome "bin\java.exe"
$outDir = Join-Path $projectRoot "out-test"
$sourceDir = Join-Path $projectRoot "src\main\java\planner"
$testDir = Join-Path $projectRoot "java\tests"

if (Test-Path $outDir) {
    Remove-Item -Recurse -Force $outDir
}
New-Item -ItemType Directory -Path $outDir | Out-Null

$sourceFiles = Get-ChildItem $sourceDir -Filter *.java | ForEach-Object { $_.FullName }
$testFiles = Get-ChildItem $testDir -Filter *.java | ForEach-Object { $_.FullName }
& $javac -d $outDir $sourceFiles $testFiles
Push-Location $projectRoot
try {
    & $java -cp $outDir planner.tests.CapacityPlannerTest
} finally {
    Pop-Location
}
