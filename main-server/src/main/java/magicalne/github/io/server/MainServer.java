package magicalne.github.io.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import magicalne.github.io.proto.main.MainServiceGrpc;
import magicalne.github.io.proto.main.Req;
import magicalne.github.io.proto.main.Res;

import java.io.IOException;

public class MainServer {
  private Server server;
  private final ExternalServiceClient client;
  private MainServer(String externalHost, int externalPort) {
    client = new ExternalServiceClient(externalHost, externalPort);
  }

  private void start() throws IOException, InterruptedException {
    int port = 8888;
    server = ServerBuilder.forPort(port)
      .addService(new ExternalServiceGrpcImpl(client))
      .build();
    server.start();
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      // Use stderr here since the logger may have been reset by its JVM shutdown hook.
      System.err.println("*** shutting down gRPC server since JVM is shutting down");
      System.err.println("*** server shut down");
    }));
    blockUntilShutdown();
  }

  /**
   * Await termination on the main thread since the grpc library uses daemon threads.
   */
  private void blockUntilShutdown() throws InterruptedException {
    if (server != null) {
      server.awaitTermination();
    }
  }

  private static class ExternalServiceGrpcImpl extends MainServiceGrpc.MainServiceImplBase {
    private final ExternalServiceClient client;

    ExternalServiceGrpcImpl(ExternalServiceClient client) {
      this.client = client;
    }

    @Override
    public void main(Req request, StreamObserver<Res> responseObserver) {
      String halo = client.halo();
      Res res = Res.newBuilder().setContent(halo).build();
      responseObserver.onNext(res);
      responseObserver.onCompleted();
    }
  }

  public static void main(String[] args) throws IOException, InterruptedException {
    String host = args[0];
    int port = Integer.parseInt(args[1]);
    MainServer server = new MainServer(host, port);
    server.start();
  }
}
