# ============================================
# ÉTAPE 1: Construction de l'application
# ============================================
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Définir le répertoire de travail
WORKDIR /app

# Copier le fichier pom.xml et télécharger les dépendances
COPY pom.xml .
RUN mvn dependency:go-offline

# Copier tout le code source
COPY src ./src

# Compiler et empaqueter l'application
RUN mvn clean package -DskipTests

# ============================================
# ÉTAPE 2: Exécution de l'application
# ============================================
FROM eclipse-temurin:21-jre-alpine

# Définir le répertoire de travail
WORKDIR /app

# Copier le JAR depuis l'étape de build
COPY --from=build /app/target/*.jar app.jar

# Créer le groupe/utilisateur non-root et lui accorder la propriété du répertoire /app
RUN addgroup -S appgroup && \
    adduser -S appuser -G appgroup && \
    chown -R appuser:appgroup /app

# Basculer vers l'utilisateur sécurisé non-root
USER appuser

# Exposer le port 8080
EXPOSE 8080

# Commande de démarrage
ENTRYPOINT ["java", "-jar", "app.jar"]