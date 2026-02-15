#!/bin/bash

# Script para compilar el proyecto con Java 21

echo "🔧 Configurando JAVA_HOME para Java 21..."
export JAVA_HOME=$(/usr/libexec/java_home -v 21)

echo "📦 Compilando el proyecto..."
mvn clean package -DskipTests

echo "✅ Compilación completada!"
