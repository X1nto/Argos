//
//  ArgosNavigation.swift
//  iosApp
//
//  Created by Tornike Khintibidze on 24.10.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import Observation
import ArgosCore

@Observable
class ArgosTabViewModel {
    
    private var fetchTask: Task<Void, Error>?

    var state: ArgosTabState = .loading
    
    init() {
        fetchTask = Task {
            for try await infoAndState in userRepository.meUserInfoAndStateFlow {
                switch onEnum(of: infoAndState) {
                case .loading:
                    state = .loading
                case .success(let data):
                    state = .success(
                        userInfo: data.value!.first!,
                        userState: data.value!.second!
                    )
                case .error:
                    state = .error
                }
            }
        }
    }
    
    deinit {
        fetchTask?.cancel()
    }
    
}

struct ArgosTab<Content: View>: View {
    
    private let viewModel = ArgosTabViewModel()
    
    let content: () -> Content
    
    init(@ViewBuilder content: @escaping () -> Content) {
        self.content = content
    }
    
    var body: some View {
        _ArgosTab(
            notificationsScreen: {
                NotificationsScreen()
            },
            userSheet: {
                UserSheet()
            },
            state: viewModel.state,
            content: content
        )
    }
}

struct _ArgosTab<NotificationScreen: View, UserSheet: View, Content: View> : View {
    
    let notificationsScreen: () -> NotificationScreen
    let userSheet: () -> UserSheet
    let state: ArgosTabState
    
    let content: () -> Content
    
    init(
        notificationsScreen: @escaping () -> NotificationScreen,
        userSheet: @escaping () -> UserSheet,
        state: ArgosTabState,
        content: @escaping () -> Content
    ) {
        self.notificationsScreen = notificationsScreen
        self.userSheet = userSheet
        self.state = state
        self.content = content
    }
    
    private var notificationsUnreadCount: Int {
        if case let .success(_, state) = state {
            return Int(state.newsUnread)
        }
        return 0
    }
    
    @State private var userSheetVisible = false
    
    var body: some View {
        NavigationStack {
            content()
                .toolbar {
                    ToolbarItemGroup {
                        NavigationLink(destination: notificationsScreen) {
                            Image(systemName: "bell")
                                .badge(notificationsUnreadCount)
                        }
                        
                        Button(action: {
                            userSheetVisible = true
                        }) {
                            switch state {
                            case .loading:
                                ProgressView()
                            case let .success(userInfo, _):
                                if let photoUrl = userInfo.photoUrl {
                                    AsyncImage(url: URL(string: photoUrl)) { image in
                                        image
                                            .resizable()
                                            .scaledToFill()
                                            .clipShape(Circle())
                                    } placeholder: {
                                        ProgressView()
                                    }.frame(maxWidth: 28, maxHeight: 28)
                                } else {
                                    Image(systemName: "person.crop.circle")
                                }
                            case .error:
                                Image(systemName: "person.crop.circle.badge.exclamationmark")
                            }
                        }.buttonStyle(.borderless)
                    }
                }
        }
        .sheet(isPresented: $userSheetVisible) {
            userSheet()
        }
    }
    
}

enum ArgosTabState {
    case loading
    case success(userInfo: DomainMeUserInfo, userState: DomainMeUserState)
    case error
}
