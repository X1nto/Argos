//
//  RootScreen.swift
//  iosApp
//
//  Created by Tornike Khintibidze on 25.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import ArgosCore

@Observable
class RootViewModel {
    var isLoggedIn = false
    
    private var loginTask: Task<Void, Error>?
    
    private var userRepository: UserRepository {
        DiProvider.shared.userRepository
    }
    
    init() {
        loginTask = Task { 
            for try await loggedIn in self.userRepository.observeLoggedIn() {
                self.isLoggedIn = loggedIn.boolValue
            }
        }
    }
    
    deinit {
        loginTask?.cancel()
    }
}

struct RootScreen: View {
    
    @State private var viewModel = RootViewModel()
    
    var body: some View {
        if !viewModel.isLoggedIn {
            LoginScreen()
        } else {
            MainScreen()
        }
    }
}
