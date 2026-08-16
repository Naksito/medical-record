package com.medrec.services;

import com.medrec.grpc.version.Version.GetVersionRequest;
import com.medrec.grpc.version.Version.GetVersionResponse;
import com.medrec.grpc.version.VersionServiceGrpc;
import io.grpc.stub.StreamObserver;

public class VersionService extends VersionServiceGrpc.VersionServiceImplBase {
    private static VersionService instance;

    private VersionService() {}

    public static VersionService getInstance() {
        if (instance == null) {
            instance = new VersionService();
        }
        return instance;
    }

    @Override
    public void getVersion(GetVersionRequest request, StreamObserver<GetVersionResponse> responseObserver) {
        String version = System.getenv("SERVICE_VERSION");
        if (version == null || version.isBlank()) {
            version = "unknown";
        }

        responseObserver.onNext(GetVersionResponse.newBuilder().setVersion(version).build());
        responseObserver.onCompleted();
    }
}
