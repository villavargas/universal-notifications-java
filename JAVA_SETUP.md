# IMPORTANTE: Configuración de Java 21

Este proyecto requiere **Java 21** para compilar correctamente con Lombok.

## Configuración automática

Antes de ejecutar Maven, asegúrate de ejecutar:

```bash
source setup-java.sh
```

O manualmente:

```bash
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home
export PATH=$JAVA_HOME/bin:$PATH
```

## Verificación

Para verificar que estás usando la versión correcta:

```bash
mvn -version
# Debe mostrar: Java version: 21.0.8
```

## Compilación

```bash
# Establecer Java 21
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home

# Compilar
mvn clean compile

# O compilar y empaquetar
mvn clean package
```
