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
    @ObservationIgnored private var notificationsTask: Task<Void, Error>?
    @ObservationIgnored private var notificationsRepository: NotificationsRepository {
        DiProvider.shared.notificationsRepository
    }
    
    private(set) var notifications: PagingItemsObservable<DomainNotification>?
    
    init() {
        notificationsTask = Task {
            notifications = PagingItemsObservable(notificationsRepository.getNotifications())
        }
    }
    
    deinit {
        notificationsTask?.cancel()
    }
}

struct NotificationsScreen: View {
    
    @State private var viewModel = NotificationsViewModel()
    
    var body: some View {
        _NotificationsScreen(
            notifications: viewModel.notifications
        )
    }
}

struct _NotificationsScreen: View {
    
    let notifications: PagingItemsObservable<DomainNotification>?
    
    var body: some View {
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
}

#Preview {
    NotificationsScreen()
}
