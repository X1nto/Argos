import SwiftUI
import ArgosCore

@main
struct iOSApp: App {
    
    init() {
        DiProvider.shared.doInitKoin()
    }
    
	var body: some Scene {
		WindowGroup {
            RootScreen()
//                .onAppear {
//                    if #available(iOS 15.0, *) {
//                        let tabBarAppearance = UITabBarAppearance()
//                        tabBarAppearance.configureWithDefaultBackground()
//                        UITabBar.appearance().scrollEdgeAppearance = tabBarAppearance
//                    }
//                }
		}
	}
}
