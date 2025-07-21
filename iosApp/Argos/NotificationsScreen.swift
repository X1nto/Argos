//
//  NotificationsSheet.swift
//  iosApp
//
//  Created by Tornike Khintibidze on 10.08.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import ArgosCore
import Observation

@Observable
class NotificationsViewModel {
    
    private(set) var notifications: PagingItemsObservable<DomainNotification>?
    
    init() {
        refresh()
    }
    
    func refresh() {
        notifications = PagingItemsObservable(NotificationsRepository.shared.getNotifications())
    }
}

struct NotificationsScreen: View {
    
    @State private var viewModel = NotificationsViewModel()
    
    var body: some View {
        _NotificationsScreen(
            notifications: viewModel.notifications
        )
        .refreshable {
            viewModel.refresh()
        }
    }
}

struct _NotificationsScreen: View {
    
    let notifications: PagingItemsObservable<DomainNotification>?
    
    var body: some View {
        Group {
            if let notifications = notifications {
                switch onEnum(of: notifications.loadingStates.refresh) {
                case .loading:
                    ProgressView()
                case .notLoading:
                    List {
                        ForEach(0..<notifications.count, id: \.self) { index in
                            let notification = notifications[index]
                            VStack {
                                Text(notification.text)
                            }
                        }
                    }
                case .error:
                    Text("Error")
                }
                
            }
        }
        .navigationTitle("Notifications")
    }
}

#Preview {
    NotificationsScreen()
}
