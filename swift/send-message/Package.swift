// swift-tools-version:5.5
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
    name: "send-message",
    dependencies: [
        .package(url: "https://github.com/swift-server/async-http-client.git", from: "1.9.0"),
        .package(url: "https://github.com/apple/swift-crypto.git", from: "2.0.3"),
    ],
    targets: [
        .executableTarget(
            name: "send-message",
            dependencies: [
                .product(name: "AsyncHTTPClient", package: "async-http-client"),
                .product(name: "Crypto", package: "swift-crypto"),
            ],
            path: "Sources"
        ),
    ]
)