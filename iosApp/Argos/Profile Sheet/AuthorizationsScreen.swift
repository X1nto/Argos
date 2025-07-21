//
//  AuthorizationsScreen.swift
//  Argos
//
//  Created by Tornike Khintibidze on 07.07.25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Observation
import ArgosCore

@Observable
private class AuthorizationsViewModel {
    
    let authorizations = PagingItemsObservable(UserRepository.shared.getUserAuthorizations())
    
}

struct AuthorizationsScreen: View {
    
    private let viewModel = AuthorizationsViewModel()
    
    var body: some View {
        _AuthorizationsScreen(authorizations: viewModel.authorizations)
            .navigationTitle("Authorizations")
    }
}

private struct _AuthorizationsScreen: View {
    
    let authorizations: PagingItemsObservable<DomainUserAuthorization>
    
    var body: some View {
        switch onEnum(of: authorizations.loadingStates.refresh) {
        case .loading:
            ProgressView()
        case .notLoading:
            List(authorizations) { authorization in
                VStack(alignment: .leading, spacing: 6) {
                    HStack(alignment: .center, spacing: 6) {
                        Image(systemName: "clock.fill")
                        Text(authorization.date.fullDateTime)
                    }
                    .font(.caption)
                    .foregroundStyle(Color.secondary)
                    
                    Text(authorization.client)
                    
                    Text(authorization.ip)
                        .padding(.horizontal, 6)
                        .padding(.vertical, 2)
                        .background(.thinMaterial)
                        .clipShape(RoundedRectangle(cornerRadius: 4))
                        .font(.subheadline)
                        .foregroundStyle(Color.secondary)
                }
            }
            .listStyle(.plain)
        case .error:
            Text("Error")
        }
    }
}
