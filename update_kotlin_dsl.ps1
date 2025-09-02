# Script to update all build.gradle.kts files to use new Kotlin compiler options DSL

# List of all module build files
$buildFiles = @(
    "C:\MemoriaAI\MemoriaOs\feature-module\build.gradle.kts",
    "C:\MemoriaAI\MemoriaOs\datavein-oracle-native\build.gradle.kts",
    "C:\MemoriaAI\MemoriaOs\secure-comm\build.gradle.kts",
    "C:\MemoriaAI\MemoriaOs\oracle-drive-integration\build.gradle.kts",
    "C:\MemoriaAI\MemoriaOs\module-a\build.gradle.kts",
    "C:\MemoriaAI\MemoriaOs\module-b\build.gradle.kts",
    "C:\MemoriaAI\MemoriaOs\module-c\build.gradle.kts",
    "C:\MemoriaAI\MemoriaOs\module-d\build.gradle.kts",
    "C:\MemoriaAI\MemoriaOs\sandbox-ui\build.gradle.kts"
)

# Update each build file
foreach ($file in $buildFiles) {
    if (Test-Path $file) {
        Write-Host "Updating $file"
        
        # Read the file content
        $content = Get-Content -Path $file -Raw
        
        # 1. Add imports if not present
        if (-not ($content -match "import org.jetbrains.kotlin.gradle.dsl.JvmTarget")) {
            $content = $content -replace "import.*KotlinJvmOptions", "`$0`nimport org.jetbrains.kotlin.gradle.dsl.JvmTarget"
        }
        if (-not ($content -match "import org.jetbrains.kotlin.gradle.dsl.KotlinVersion")) {
            $content = $content -replace "import org.jetbrains.kotlin.gradle.dsl.JvmTarget", "`$0`nimport org.jetbrains.kotlin.gradle.dsl.KotlinVersion"
        }
        
        # 2. Replace kotlinOptions with new DSL
        $content = $content -replace "kotlinOptions \{\s+jvmTarget = ""(\d+)""\s+apiVersion = ""([\d.]+)""\s+languageVersion = ""([\d.]+)""\s+\}", "kotlin {`n        compilerOptions {`n            jvmTarget.set(JvmTarget.JVM_`$1)`n            apiVersion.set(KotlinVersion.KOTLIN_`$2)`n            languageVersion.set(KotlinVersion.KOTLIN_`$3)`n        }`n    }"
        
        # 3. Update composeCompiler to use compilerOptions
        $content = $content -replace "composeCompiler \{\s+enableStrongSkippingMode = true\s+reportsDestination = layout.buildDirectory\.dir\(""compose_compiler""\)\s+\}", "composeCompiler {`n        reportsDestination = layout.buildDirectory.dir("compose_compiler")`n        compilerOptions {`n            includeSourceInformation = true`n            suppressKotlinVersionCompatibilityCheck = "2.2.20-RC"`n        }`n    }"
        
        # Write the updated content back to the file
        $content | Set-Content -Path $file -NoNewline
        
        Write-Host "✅ Updated $file"
    } else {
        Write-Host "⚠️  File not found: $file"
    }
}

Write-Host "\nAll files have been updated. Please review the changes and sync your project."
