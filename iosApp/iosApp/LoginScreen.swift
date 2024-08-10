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
    
    private var userRepository: UserRepository {
        DiProvider.shared.userRepository
    }
    
    func login() {
        guard let scene = UIApplication.shared.connectedScenes.first as? UIWindowScene else {
            return
        }
        
        guard let viewController = scene.windows.first?.rootViewController else {
            return
        }
        
        state = .working
        
        GIDSignIn.sharedInstance.signIn(withPresenting: viewController) { result, error in
            if error != nil {
                self.state = .error(error!.localizedDescription)
                return
            }
            
            guard let tokenString = result!.user.idToken?.tokenString else {
                return
            }
            
            Task {
                let result = try! await self.userRepository.login(googleIdToken: tokenString)
                if result.boolValue {
                    self.state = .stale
                } else {
                    self.state = .error("Could not log in")
                }
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
