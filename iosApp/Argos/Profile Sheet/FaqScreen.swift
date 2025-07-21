//
//  FaqScreen.swift
//  Argos
//
//  Created by Tornike Khintibidze on 07.07.25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Observation
import ArgosCore

@Observable
private class FaqViewModel {
    
    var state: FaqState = .loading
    
    init() {
        Task {
            
        }
    }
    
}

struct FaqScreen: View {
    
    var body: some View {
        
    }
}

private struct _FaqScreen: View {
    
    var body: some View {
        
    }
}

private enum FaqState {
    case loading
    case error
}
