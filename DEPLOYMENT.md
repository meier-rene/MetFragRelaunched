# MetFragWeb Deployment Guide

## Overview

MetFragWeb is a Jakarta EE 10 web application that can be deployed to various servlet containers and application servers, including Apache TomEE and Apache Tomcat.

## Prerequisites

- **Java**: JDK 21 or higher
- **Application Server**: 
  - Apache TomEE 8.x+ (Jakarta EE 10 - **Recommended**)
  - Apache Tomcat 10.1+ (requires additional setup)
- **Memory**: Recommended minimum 2GB heap size

## Building the WAR File

```bash
# Set Java home
export JAVA_HOME=/path/to/jdk-21

# Build the project
mvn clean package -pl MetFragWeb -am

# The WAR file will be created at:
# MetFragWeb/target/MetFragWeb.war
```

## Deployment to Apache TomEE (Recommended)

TomEE is a full Jakarta EE application server with all necessary components built-in.

### Manual Deployment

1. Copy the WAR file to TomEE's webapps directory:
   ```bash
   cp MetFragWeb/target/MetFragWeb.war /path/to/tomee/webapps/
   ```

2. Start TomEE:
   ```bash
   /path/to/tomee/bin/catalina.sh run
   ```

3. Access the application at:
   ```
   http://localhost:8080/MetFragWeb/
   ```

## Deployment to Apache Tomcat

Tomcat is a servlet container and requires additional Jakarta EE runtime libraries.

### Prerequisites for Tomcat

The following Jakarta EE implementations must be added to Tomcat's classpath or bundled in the WAR:

- **Jakarta Faces 4.0.7** (Mojarra) - JSF implementation
- **Weld 5.1.3** - CDI (Contexts and Dependency Injection)
- **Hibernate Validator 8.0.1** - Bean Validation
- **Expressly 5.0.0** - Expression Language

These dependencies are declared with `<scope>provided</scope>` in the POM, so they are NOT included in the WAR by default (for TomEE compatibility). To deploy to Tomcat, you have two options:

#### Option 1: Modify POM for Tomcat (Not Recommended)

Remove the `<scope>provided</scope>` from the Jakarta EE dependencies in `MetFragWeb/pom.xml`, then rebuild.

#### Option 2: Use TomEE Instead

We recommend using TomEE instead of Tomcat as it provides all Jakarta EE components out of the box.

### Docker Deployment for Tomcat

If you still need to use Tomcat, you can create a custom Docker image with the required libraries:

```dockerfile
FROM tomcat:10.1-jdk21

# Add required Jakarta EE libraries
ADD https://repo1.maven.org/maven2/org/glassfish/jakarta.faces/4.0.7/jakarta.faces-4.0.7.jar /usr/local/tomcat/lib/
ADD https://repo1.maven.org/maven2/org/jboss/weld/servlet/weld-servlet-shaded/5.1.3.Final/weld-servlet-shaded-5.1.3.Final.jar /usr/local/tomcat/lib/
ADD https://repo1.maven.org/maven2/org/hibernate/validator/hibernate-validator/8.0.1.Final/hibernate-validator-8.0.1.Final.jar /usr/local/tomcat/lib/
ADD https://repo1.maven.org/maven2/org/glassfish/expressly/expressly/5.0.0/expressly-5.0.0.jar /usr/local/tomcat/lib/

# Copy the WAR file
COPY MetFragWeb.war /usr/local/tomcat/webapps/

# Expose port
EXPOSE 8080

CMD ["catalina.sh", "run"]
```

## Runtime Dependencies

The WAR file includes application-specific dependencies:

- **PrimeFaces 14.0.0** - UI component library
- **OmniFaces 4.6.5** - JSF utility library
- **MetFragLib** - Core MetFrag functionality
- **CDK 2.11** - Chemistry Development Kit

Jakarta EE platform APIs (Faces, CDI, Validation, EL) are provided by the application server (TomEE) and NOT bundled in the WAR.

## Configuration

### Settings File

The application requires a `settings.properties` file which is included in the WAR. This file contains optional configuration:

- ChemSpider API token
- Thread pool size
- Email feedback configuration
- Proxy settings for external services
- Local database connections (PubChem, KEGG, LipidMaps)

To customize, edit:
```
MetFragWeb/src/main/webapp/resources/settings.properties
```

Then rebuild the WAR file.

### Tomcat Configuration

For optimal performance, configure Tomcat with adequate memory:

**catalina.sh / catalina.bat:**
```bash
JAVA_OPTS="-Xms2048m -Xmx4096m -XX:+UseG1GC"
```

**server.xml:**
```xml
<Connector port="8080" protocol="HTTP/1.1"
           connectionTimeout="20000"
           maxThreads="200"
           redirectPort="8443" />
```

## Troubleshooting

### ClassNotFoundException Errors

If you see errors about missing classes (e.g., `jakarta.faces.context.FacesContext`), verify that:

1. You're using Tomcat 10.1 or higher (Jakarta EE 10 support)
2. The WAR file includes all runtime dependencies
3. No conflicting libraries in Tomcat's lib directory

### Memory Issues

If the application runs out of memory:

1. Increase heap size in Tomcat configuration
2. Monitor memory usage with JConsole or VisualVM
3. Consider reducing concurrent processing threads

### Chart Display Issues

If charts don't render properly:

1. Check browser console for JavaScript errors
2. Verify PrimeFaces resources are loading (check network tab)
3. Ensure Chart.js resources are accessible at:
   ```
   /MetFragWeb/jakarta.faces.resource/chart/chart.js.xhtml?ln=primefaces
   ```

### Database Connection Issues

If local database features don't work:

1. Verify database connection settings in `settings.properties`
2. Check database server is accessible from application server
3. Review Tomcat logs for JDBC errors

## Production Considerations

### Security

1. **HTTPS**: Configure SSL/TLS in Tomcat or use a reverse proxy (nginx, Apache)
2. **Session Security**: Review `web.xml` for secure session configuration
3. **File Upload**: Configure appropriate file size limits and upload directories
4. **CORS**: If needed, configure CORS headers for API access

### Performance

1. **Connection Pool**: Configure database connection pooling for local databases
2. **Caching**: Enable browser caching for static resources
3. **CDN**: Consider using CDN for PrimeFaces resources in production
4. **Compression**: Enable gzip compression in Tomcat

### Monitoring

1. **Logging**: Configure Log4j2 for appropriate logging levels
2. **Metrics**: Monitor application metrics (response times, memory usage)
3. **Health Checks**: Implement health check endpoints
4. **Backup**: Regular backup of database and configuration files

## Version Compatibility

- **Jakarta EE**: 10.0
- **Servlet API**: 6.0
- **JSF (Faces)**: 4.0
- **CDI**: 4.0
- **Bean Validation**: 3.0
- **Expression Language**: 5.0

## Support

For issues and questions:
- GitHub Issues: https://github.com/meier-rene/MetFragRelaunched/issues
- Documentation: See PRIMEFACES_14_MIGRATION.md for migration details
