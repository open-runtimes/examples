// swift-tools-version:5.3
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
    name: "discord",
    dependencies: [
        .package(url: "https://github.com/swift-server/async-http-client.git", from: "1.9.0"),
    ],
    targets: [
        .target(
            name: "discord",
            dependencies: [
                .product(name: "AsyncHTTPClient", package: "async-http-client"),
            ],
            path: "Sources")
    ]
)