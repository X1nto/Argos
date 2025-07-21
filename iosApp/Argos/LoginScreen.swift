//
//  LoginScreen.swift
//  iosApp
//
//  Created by Tornike Khintibidze on 21.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import GoogleSignIn
import ArgosCore

@Observable
class LoginViewModel {
    
    var state: LoginState = .stale
    
    func login() {
        guard let scene = UIApplication.shared.connectedScenes.first as? UIWindowScene else {
            return
        }
        
        guard let viewController = scene.windows.first?.rootViewController else {
            return
        }
        
        state = .working
        
        Task {
            do {
                let googleResult = try await GIDSignIn.sharedInstance.signIn(withPresenting: viewController)
                guard let googleToken = googleResult.user.idToken?.tokenString else {
                    await MainActor.run {
                        self.state = .error("Failed to fetch Google token")
                    }
                    return
                }
                
                let saveResult = try await UserRepository.shared.login(googleIdToken: googleToken)
                await MainActor.run {
                    state = saveResult.boolValue ? .stale : .error("Could not log in")
                }
                
            } catch {
                self.state = .error(error.localizedDescription)
            }
        }
    }
    
}

enum LoginState {
    case stale
    case working
    case error(String)
}

struct LoginScreen: View {
    
    @State private var viewModel = LoginViewModel()
    
    var body: some View {
        ZStack(alignment: .center) {
            Button(action: {
                viewModel.login()
            }) {
                switch viewModel.state {
                case .stale:
                    Text("Login")
                case .working:
                    ProgressView()
                case .error(let error):
                    Text(error)
                }
            }
        }
        .onOpenURL { url in
            GIDSignIn.sharedInstance.handle(url)
        }
    }
}

#Preview {
    LoginScreen()
}
