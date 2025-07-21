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
    
    init() {
        Task {
            for try await loggedIn in UserRepository.shared.observeLoggedIn() {
                self.isLoggedIn = loggedIn.boolValue
            }
        }
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
