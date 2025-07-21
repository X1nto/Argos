//
//  GroupScheduleSheet.swift
//  Argos
//
//  Created by Tornike Khintibidze on 04.07.25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import ArgosCore

@Observable
private class GroupScheduleViewModel {
    
    let schedule: PagingItemsObservable<DomainCourseGroupSchedule>
    
    init(courseId: String, groupId: String) {
        self.schedule = PagingItemsObservable(DiProvider.shared.coursesRepository.getCourseGroupSchedule(courseId: courseId, groupId: groupId))
    }
}

struct GroupScheduleFullScreen: View {
    
    private let viewModel: GroupScheduleViewModel
    private let group: DomainCourseGroup
    
    init(courseId: String, group: DomainCourseGroup) {
        self.group = group
        viewModel = .init(courseId: courseId, groupId: group.id)
    }
    
    var body: some View {
        _GroupScheduleFullScreen(
            schedule: viewModel.schedule,
            group: group
        )
    }
}

private struct _GroupScheduleFullScreen: View {
    
    let schedule: PagingItemsObservable<DomainCourseGroupSchedule>
    let group: DomainCourseGroup
    
    let columns = [
        GridItem(.flexible(minimum: 125), spacing: 0, alignment: .center),
        GridItem(.fixed(75), spacing: 0, alignment: .center),
        GridItem(.fixed(75), spacing: 0, alignment: .center),
        GridItem(.flexible(minimum: 125), spacing: 0, alignment: .center)
    ]
    
    private var lecturers: String {
        group.lecturers.map { $0.fullName }.joined()
    }
    
    var body: some View {
        NavigationView {
            Group {
                switch onEnum(of: schedule.loadingStates.refresh) {
                case .loading:
                    ProgressView()
                case .notLoading:
                    ScrollView {
                        Color.clear
                            .frame(height: 4)
                        LazyVGrid(columns: columns, alignment: .center, spacing: 12)  {
                            Group {
                                Text("Date")
                                Text("Time")
                                Text("Room")
                                Text("Lecturer")
                            }
                            .font(.subheadline)
                            .fontWeight(.bold)
                            
                            ForEach(schedule) { schedule in
                                Group {
                                    VStack {
                                        Text(schedule.day)
                                        Text(schedule.date)
                                    }
                                    Text(schedule.startTime)
                                    Text(schedule.room)
                                    Text(schedule.lecturer)
                                }
                                .multilineTextAlignment(.center)
                                .font(.subheadline)
                                .foregroundStyle(Color.secondary)
                            }
                        }
                    }
                case .error:
                    Text("Error")
                }
            }
            .toolbar {
                ToolbarItem(placement: .principal) {
                    VStack {
                        Text(group.name)
                            .font(.headline)
                        
                        Text(lecturers)
                            .font(.subheadline)
                            .foregroundStyle(Color.secondary)
                    }
                }
            }
            .toolbarTitleDisplayMode(.inline)
//            .navigationTitle(group.name)
//            .navigationBarTitleDisplayMode(.inline)
        }
    }
}

private enum GroupScheduleSheetState {
    case loading
    case success([DomainCourseGroupSchedule])
    case error
}
