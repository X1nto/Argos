//
//  Repositories.swift
//  Argos
//
//  Created by Tornike Khintibidze on 06.07.25.
//  Copyright © 2025 orgName. All rights reserved.
//

import ArgosCore

extension UserRepository {
    static let shared = DiProvider.shared.userRepository
}

extension CoursesRepository {
    static let shared = DiProvider.shared.coursesRepository
}

extension LecturesRepository {
    static let shared = DiProvider.shared.lecturesRepository
}

extension NewsRepository {
    static let shared = DiProvider.shared.newsRepository
}

extension MessagesRepository {
    static let shared = DiProvider.shared.messagesRepository
}

extension SemesterRepository {
    static let shared = DiProvider.shared.semesterRepository
}

extension NotificationsRepository {
    static let shared = DiProvider.shared.notificationsRepository
}
