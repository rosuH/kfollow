import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    init() {
        HelperKt.doInitKoin()
        HelperKt.doInitLogger()
    }
    var body: some Scene {
        WindowGroup {
            ContentView().edgesIgnoringSafeArea(.all)
        }
    }
}
