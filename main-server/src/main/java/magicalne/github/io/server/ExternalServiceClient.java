package magicalne.github.io.server;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import magicalne.github.io.proto.external.ExternalServiceGrpc;
import magicalne.github.io.proto.external.Req;
import magicalne.github.io.proto.external.Res;

import java.util.concurrent.TimeUnit;

public class ExternalServiceClient {

  private final ManagedChannel channel;
  private final ExternalServiceGrpc.ExternalServiceBlockingStub bockingStub;

  public ExternalServiceClient(String host, int port) {
    this.channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext(true).build();
    bockingStub = ExternalServiceGrpc.newBlockingStub(channel);
  }

  public void shutdown() throws InterruptedException {
    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
  }

  public String halo() {
    Res res = this.bockingStub.halo(Req.getDefaultInstance());
    return res.getContent();
  }
}
