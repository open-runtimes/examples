// swift-tools-version:5.8
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription


let package = Package(
    name: "twitter",
    platforms: [.macOS(.v12)],
    dependencies: [
        .package(url: "https://github.com/swift-server/async-http-client", from: "1.18.0"),
        .package(url: "https://github.com/mw99/OhhAuth", from: "1.2.0")
    ],
    targets: [
        .executableTarget(
            name: "twitter",
            dependencies: [
                .product(name: "AsyncHTTPClient", package: "async-http-client"),
                .product(name: "OhhAuth", package: "OhhAuth")
            ])
    ]
)
