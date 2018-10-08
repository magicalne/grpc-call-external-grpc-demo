package magicalne.github.io.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import magicalne.github.io.proto.external.ExternalServiceGrpc;
import magicalne.github.io.proto.external.Req;
import magicalne.github.io.proto.external.Res;

import java.io.IOException;

public class ExternalServer {
  private Server server;

  private void start() throws IOException, InterruptedException {
    int port = 8888;
    server = ServerBuilder.forPort(port)
      .addService(new ExternalServiceGrpcImpl())
      .build();
    server.start();
    System.out.println("*** server is up ***");
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

  private static class ExternalServiceGrpcImpl extends ExternalServiceGrpc.ExternalServiceImplBase {
    @Override
    public void halo(Req request, StreamObserver<Res> responseObserver) {
      Res res = Res.newBuilder().setContent("Hello, world!").build();
      responseObserver.onNext(res);
      responseObserver.onCompleted();
    }
  }

  public static void main(String[] args) throws IOException, InterruptedException {
    ExternalServer externalServer = new ExternalServer();
    externalServer.start();
  }
}
