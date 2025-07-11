# Script PowerShell per configurare l'ambiente Java
# Eseguire come amministratore se necessario

Write-Host "=== CONFIGURAZIONE AMBIENTE JAVA PER DCMGEST ===" -ForegroundColor Green

# Verifica Java corrente
Write-Host "`nVersione Java corrente:" -ForegroundColor Yellow
java -version

Write-Host "`nJAVA_HOME corrente:" -ForegroundColor Yellow
Write-Host $env:JAVA_HOME

# Verifica Maven
Write-Host "`nVersione Maven:" -ForegroundColor Yellow
mvn -version

# Opzioni per risolvere il problema
Write-Host "`n=== OPZIONI RISOLUZIONE ===" -ForegroundColor Cyan

Write-Host "`n1. SOLUZIONE RAPIDA - Configurare Maven per Java 17" -ForegroundColor Yellow
Write-Host "   Creare file .mvn/jvm.config con configurazione Java 17"

Write-Host "`n2. SOLUZIONE COMPLETA - Installare Java 17" -ForegroundColor Yellow
Write-Host "   Scaricare e installare Java 17 da Adoptium"

Write-Host "`n3. SOLUZIONE TEMPORANEA - Usare Java 8" -ForegroundColor Yellow
Write-Host "   Configurare Maven per usare Java 8 esistente"

# Controlla se esiste Java 8
$java8Path = "C:\Program Files\Java\jre1.8.0_441"
if (Test-Path $java8Path) {
    Write-Host "`n✅ JAVA 8 TROVATO: $java8Path" -ForegroundColor Green
    Write-Host "   Configurazione temporanea possibile"
    
    $choice = Read-Host "`nVuoi configurare Maven per usare Java 8? (s/n)"
    if ($choice -eq 's' -or $choice -eq 'S') {
        # Crea directory .mvn se non esiste
        $mvnDir = ".mvn"
        if (!(Test-Path $mvnDir)) {
            New-Item -ItemType Directory -Path $mvnDir
            Write-Host "Creata directory .mvn" -ForegroundColor Green
        }
        
        # Crea file jvm.config
        $jvmConfig = "$mvnDir/jvm.config"
        $javaHome8 = "$java8Path"
        $content = "-Djava.home=$javaHome8"
        Set-Content -Path $jvmConfig -Value $content
        
        Write-Host "Creato file $jvmConfig con configurazione Java 8" -ForegroundColor Green
        Write-Host "Contenuto: $content" -ForegroundColor Cyan
        
        # Test compilation
        Write-Host "`nTest compilazione..." -ForegroundColor Yellow
        mvn clean compile -X
    }
} else {
    Write-Host "`n⚠️  Java 8 non trovato in: $java8Path" -ForegroundColor Red
}

Write-Host "`n=== ISTRUZIONI MANUALI ===" -ForegroundColor Cyan

Write-Host "`nPer installare Java 17 manualmente:" -ForegroundColor Yellow
Write-Host "1. Andare su: https://adoptium.net/"
Write-Host "2. Scaricare OpenJDK 17 (LTS)"
Write-Host "3. Installare in: C:\Program Files\Java\jdk-17"
Write-Host "4. Configurare JAVA_HOME:"
Write-Host '   $env:JAVA_HOME = "C:\Program Files\Java\jdk-17"'
Write-Host '   $env:PATH = "$env:JAVA_HOME\bin;$env:PATH"'

Write-Host "`nPer testare la compilazione:" -ForegroundColor Yellow
Write-Host "   mvn clean compile"

Write-Host "`nPer testare solo la sintassi:" -ForegroundColor Yellow
Write-Host "   mvn validate"

Write-Host "`n=== FINE CONFIGURAZIONE ===" -ForegroundColor Green
