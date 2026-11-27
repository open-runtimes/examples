// swift-tools-version: 5.5
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
    name: "ProllyFinal",
    dependencies: [
        .package(url: "https://github.com/swift-server/async-http-client.git", from: "1.9.0"),
    ],
    targets: [
        .target(
            name: "ProllyFinal",
            dependencies: [
                .product(name: "AsyncHTTPClient", package: "async-http-client"),
            ])
    ]
)
