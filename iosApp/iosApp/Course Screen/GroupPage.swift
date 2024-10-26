//
//  GroupPage.swift
//  iosApp
//
//  Created by Tornike Khintibidze on 01.10.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Observation
import SwiftUI
import ArgosCore

class GroupViewModel {
    @ObservationIgnored private var fetchTask: Task<Void, Error>?
    @ObservationIgnored private let scheduleResponse: DomainResponseSource<AnyObject, NSArray>
    
    var state: GroupState = .loading
    
    init(courseId: String, groupId: String) {
        scheduleResponse = DiProvider.shared.coursesRepository.getCourseGroupSchedule(courseId: courseId, groupId: groupId)
        fetchTask = Task {
            for try await scheduleData in scheduleResponse.flow {
                switch onEnum(of: scheduleData) {
                case .loading:
                    state = .loading
                case .success(let schedule):
                    state = .success(schedule.value! as! [DomainCourseGroupSchedule])
                case .error:
                    state = .failure
                }
            }
        }
    }
    
    deinit {
        fetchTask?.cancel()
    }
}

enum GroupState {
    case loading
    case success([DomainCourseGroupSchedule])
    case failure
}
