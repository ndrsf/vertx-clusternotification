package de.apwolf.clustering;

import java.net.ServerSocket;

import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.ext.cluster.infinispan.InfinispanClusterManager;

public class MainVerticle extends AbstractVerticle {

	public static void main(String[] args) {
		// Funny enough you only need to initialize any cache manager and add an listener to it - you do not even need to use the cache manager. Seems
		// like Infinispan shares all listener data over all cache managers.
		DefaultCacheManager cacheManager = new DefaultCacheManager(
				new GlobalConfigurationBuilder().transport().defaultTransport().build(),
				new ConfigurationBuilder().build());
		cacheManager.addListener(new ViewChangeListener());

		ClusterManager clusterManager = new InfinispanClusterManager(cacheManager);
		VertxOptions vertxOptions = new VertxOptions().setClusterManager(clusterManager);
		vertxOptions.getEventBusOptions().setClustered(true);

		Vertx.clusteredVertx(vertxOptions, res -> {
			if (res.succeeded()) {

				Vertx vertx = res.result();
				vertx
						.createHttpServer()
						.requestHandler(req -> {
							req
									.response()
									.putHeader("content-type", "text/plain")
									.end("Hello from Vert.x!");
						}).listen(getFreePort(), http -> {
							if (http.succeeded()) {
								System.out.println("HTTP server started on port " + http.result().actualPort());
							} else {
								throw new IllegalStateException("Couldnt start server", http.cause());
							}
						});
			} else {
				throw new IllegalStateException("Couldnt start server", res.cause());
			}
		});

	}

	private static int getFreePort() {
		try {
			ServerSocket socket = new ServerSocket(0);
			int port = socket.getLocalPort();
			socket.close();
			return port;
		} catch (Exception e) {
			throw new IllegalStateException("Can't find an open port", e);
		}
	}

}