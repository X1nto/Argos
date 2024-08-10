import SwiftUI
import ArgosCore

@Observable
class AppViewModel {
    var isLoggedIn = false
    
    private var loginTask: Task<Void, Error>?
    
    private var userRepository: UserRepository {
        DiProvider.shared.userRepository
    }
    
    init() {
        loginTask = Task {
            for try await loggedIn in DiProvider.shared.userRepository.observeLoggedIn() {
                isLoggedIn = loggedIn.boolValue
            }
        }
    }
    
    deinit {
        loginTask?.cancel()
    }
}

@main
struct iOSApp: App {
    
    init() {
        DiProvider.shared.doInitKoin()
    }
    
	var body: some Scene {
		WindowGroup {
            RootScreen()
                .onAppear {
                    if #available(iOS 15.0, *) {
                        let appearance = UITabBarAppearance()
                        appearance.configureWithDefaultBackground()
                        UITabBar.appearance().scrollEdgeAppearance = appearance
                    }
                }
		}
	}
}
