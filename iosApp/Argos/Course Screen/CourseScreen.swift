//
//  CourseScreen.swift
//  iosApp
//
//  Created by Tornike Khintibidze on 25.09.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import ArgosCore
import Observation

@Observable
class CourseViewModel {
    private var fetchTask: Task<Void, Error>?
    private var coursesRepository: CoursesRepository {
        DiProvider.shared.coursesRepository
    }
    
    var state: CourseState = .loading
    
    init(courseId: String) {
        fetchTask = Task {
            for try await courseData in coursesRepository.getCourse(courseId: courseId).flow {
                switch onEnum(of: courseData) {
                case .loading:
                    state = .loading
                case let .success(course):
                    state = .success(course.value!)
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

struct CourseScreen: View {
    
    let courseId: String
    private let viewModel: CourseViewModel
    
    init(courseId: String) {
        self.courseId = courseId
        viewModel = .init(courseId: courseId)
    }
    
    var body: some View {
        _CourseScreen(
            state: viewModel.state,
            syllabusPage: SyllabusPage(courseId: courseId),
            groupsPage: GroupsPage(courseId: courseId),
            materialsPage: MaterialsPage(courseId: courseId)
        )
    }
}

struct _CourseScreen<SyllabusPage: View, GroupsPage: View, MaterialsPage: View>: View {
    
    let state: CourseState
    let syllabusPage: SyllabusPage
    let groupsPage: GroupsPage
    let materialsPage: MaterialsPage
    
    @State private var selectedIndex = 1 // Show groups by default
    
    private var navTitle: String? {
        if case let .success(course) = state {
            return course.name
        }
        
        return nil
    }
    
    var body: some View {
        TabPager(
            selectedIndex: $selectedIndex,
            items: CoursePages.all,
            tabContent: { page in
                switch page {
                case .syllabus:
                    Text("Syllabus")
                case .groups:
                    Text("Groups")
                case .materials:
                    Text("Materials")
                }
            }
        ) { page in
            switch page {
            case .syllabus: syllabusPage
            case .groups: groupsPage
            case .materials: materialsPage
            }
        }
        .navigationTitleOptional(navTitle)
        .toolbarTitleDisplayMode(.inline)
    }
}

extension View {
    
    nonisolated func navigationTitleOptional<S: StringProtocol>(_ title: S?) -> some View {
        return Group {
            if let title {
                self.navigationTitle(title)
            } else {
                self
            }
        }
    }
}

enum CourseState {
    case loading
    case success(DomainCourse)
    case failure
}

enum CoursePages {
    case syllabus
    case groups
    case materials
    
    static var all: [CoursePages] {
        [.syllabus, .groups, .materials]
    }
}
