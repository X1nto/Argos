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
    @ObservationIgnored private var fetchTask: Task<Void, Error>?
    @ObservationIgnored private var coursesRepository: CoursesRepository {
        DiProvider.shared.coursesRepository
    }
    
    var state: CourseState = .loading
    
    init(courseId: String) {
        fetchTask = Task {
            for try await courseData in coursesRepository.getCourse(courseId: courseId).flow {
                state = switch onEnum(of: courseData) {
                case .loading:  .loading
                case let .success(course): .success(course.value!)
                case .error: .failure
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
            syllabusPage: {
                SyllabusPage(courseId: courseId)
            },
            groupsPage: {
                GroupsPage(courseId: courseId)
            },
            scoresPage: {
                ScoresPage(courseId: courseId)
            },
            materialsPage: {
                MaterialsPage(courseId: courseId)
            },
            classmatesPage: {
                ClassmatesPage(courseId: courseId)
            }
        )
    }
}

struct _CourseScreen<SyllabusPage, GroupsPage, ScoresPage, MaterialsPage, ClassmatesPage>: View
where SyllabusPage: View, GroupsPage: View, ScoresPage: View, MaterialsPage: View, ClassmatesPage: View {
    
    let state: CourseState
    @ViewBuilder var syllabusPage: SyllabusPage
    @ViewBuilder var groupsPage: GroupsPage
    @ViewBuilder var scoresPage: ScoresPage
    @ViewBuilder var materialsPage: MaterialsPage
    @ViewBuilder var classmatesPage: ClassmatesPage
    
    @State private var selectedPage = CoursePages.groups
    
    private var navTitle: String? {
        if case let .success(course) = state {
            return course.name
        }
        
        return nil
    }
    
    var body: some View {
        MorphingTabView(selection: $selectedPage) {
            syllabusPage
                .morphingTab("Syllabus", systemIcon: "book.pages.fill", color: .red)
                .tag(CoursePages.syllabus)
            
            groupsPage
                .morphingTab("Groups", systemIcon: "rectangle.3.group.fill", color: .green)
                .tag(CoursePages.groups)
            
            scoresPage
                .morphingTab("Scores", systemIcon: "list.star", color: .teal)
                .tag(CoursePages.scores)
            
            materialsPage
                .morphingTab("Materials", systemIcon: "books.vertical.fill", color: .indigo)
                .tag(CoursePages.materials)
            
            classmatesPage
                .morphingTab("Classmates", systemIcon: "person.3.fill", color: .mint)
                .tag(CoursePages.classmates)
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

enum CoursePages: Hashable {
    case syllabus
    case groups
    case scores
    case materials
    case classmates
}

#Preview {
//    _CourseScreen(
//        state: CourseState.success(
//            
//        ),
//        syllabusPage: <#T##SyllabusPage#>,
//        groupsPage: <#T##GroupsPage#>,
//        materialsPage: <#T##MaterialsPage#>
//    )
}
