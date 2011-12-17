package ca.cutterslade.securerest;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Enumeration;
import java.util.logging.Handler;
import java.util.logging.LogManager;

import javax.ws.rs.core.UriBuilder;

import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.ssl.SSLContextConfigurator;
import org.glassfish.grizzly.ssl.SSLEngineConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.google.common.base.Preconditions;
import com.sun.jersey.api.container.ContainerFactory;
import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;

public class Main {

  private static final Logger log = LoggerFactory.getLogger(Main.class);

  private static URI getBaseURI() {
    return UriBuilder.fromUri("https://localhost/").port(8443).build();
  }

  public static final URI BASE_URI = getBaseURI();

  protected static HttpServer startServer() throws IOException {
    System.out.println("Starting grizzly...");
    final ResourceConfig rc = new PackagesResourceConfig("ca.cutterslade.securerest.rest");
    final HttpHandler httpHandler = ContainerFactory.createContainer(HttpHandler.class, rc);
    return GrizzlyServerFactory.createHttpServer(BASE_URI, httpHandler, true, getSllConfig());
  }

  private static SSLEngineConfigurator getSllConfig() {
    final SSLContextConfigurator config = SSLContextConfigurator.DEFAULT_CONFIG;
    log.debug("Validating SSLContextConfigurator");
    Preconditions.checkState(config.validateConfiguration(), "Config is invalid");
    return new SSLEngineConfigurator(config);
  }

  public static void main(final String[] args) throws IOException {
    setupJulToSlf4j();
    log.info("Starting...");
    final HttpServer httpServer = startServer();
    System.out.println(String.format("Jersey app started with WADL available at "
        + "%sapplication.wadl\nTry out %shelloworld\nHit enter to stop it...",
        BASE_URI, BASE_URI));
    System.in.read();
    httpServer.stop();
  }

  private static void setupJulToSlf4j() {
    final LogManager logManager = LogManager.getLogManager();
    final Enumeration<String> names = logManager.getLoggerNames();
    for (final String name : Collections.list(names)) {
      final java.util.logging.Logger logger = logManager.getLogger(name);
      for (final Handler handler : logger.getHandlers()) {
        logger.removeHandler(handler);
      }
    }
    SLF4JBridgeHandler.install();
  }

}
