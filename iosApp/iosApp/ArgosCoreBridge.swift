//
//  ArgosCoreBridge.swift
//  iosApp
//
//  Created by Tornike Khintibidze on 23.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import ArgosCore

extension DomainMessage : @retroactive Identifiable {
    
}

extension DomainMessageSent : @retroactive Identifiable {
    
}

extension DomainMessageReceived : @retroactive Identifiable {
    
}

typealias PagingData<T: AnyObject> = Paging_commonPagingData<T>
let PagingDataCompanion = Paging_commonPagingDataCompanion.shared

extension Observation.Observable {
    var userRepository: UserRepository {
        DiProvider.shared.userRepository
    }
    
    var messagesRepository: MessagesRepository {
        DiProvider.shared.messagesRepository
    }
    
    var coursesRepository: CoursesRepository {
        DiProvider.shared.coursesRepository
    }
    
    var notificationsRepository: NotificationsRepository {
        DiProvider.shared.notificationsRepository
    }
    
    var newsRepository: NewsRepository {
        DiProvider.shared.newsRepository
    }
}
