//
//  CourseCatalogScreen.swift
//  Argos
//
//  Created by Tornike Khintibidze on 06.03.25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import ArgosCore
import Observation

@Observable
class CourseCatalogViewModel {
    
    var query = ""
    
    var state = CourseCatalogState.loading
    var pagedCatalogs: PagingItemsObservable<DomainCourse>?
    
    init() {
        search()
    }
    
    func search() {
        pagedCatalogs = nil
        pagedCatalogs = PagingItemsObservable(CoursesRepository.shared.getCourseCatalog(search: query))
        
        Task {
            guard let allChoices = await getAllCourseChoices() else {
                state = .error
                return
            }
            guard let currentChoices = await getCurrentCourseChoices() else {
                state = .error
                return
            }
            
            var pastChoices: [DomainMyCourse] = []
            for choice in allChoices.course {
                if !currentChoices.contains(where: { $0.id == choice.id }) {
                    pastChoices.append(choice)
                }
            }
            
            state = .success(current: currentChoices, past: pastChoices)
        }
    }
    
    private func getAllCourseChoices() async -> DomainCourseChoices? {
        var choices: DomainCourseChoices? = nil
        fetch: for try await response in CoursesRepository.shared.getMyCourseChoices().flow {
            switch onEnum(of: response) {
            case .loading:
                break
            case .success(let success):
                choices = success.value
                break fetch
            case .error:
                choices = nil
                break fetch
            }
        }
        return choices
    }
    
    private func getCurrentCourseChoices() async -> [DomainMyCourse]? {
        var choices: [DomainMyCourse]?
        fetch: for try await result in CoursesRepository.shared.getMyCurrentCourseChoices().flow {
            switch onEnum(of: result) {
            case .loading:
                break
            case .success(let success):
                choices = success.value! as? [DomainMyCourse]
                break fetch
            case .error:
                choices = nil
                break fetch
            }
        }
        return choices
    }
}

struct CourseCatalogScreen: View {
    
    @State private var viewModel = CourseCatalogViewModel()
    
    var body: some View {
        _CourseCatalogScreen(
            query: $viewModel.query,
            state: viewModel.state,
            allCourses: viewModel.pagedCatalogs
        )
        .navigationDestination(for: DomainCourse.self) { course in
            CourseScreen(courseId: course.id)
        }
        .navigationDestination(for: DomainMyCourse.self) { myCourse in
            CourseScreen(courseId: myCourse.id)
        }
    }
}

struct CourseCatalogScreenPreview: View {
    
    @State private var query: String = ""
    
    private let courses = [
        DomainMyCourse(
            id: "0",
            name: "Test Course 1",
            code: "CENCENGEN / 0 / 0",
            receivedCredits: 6,
            courseCredits: 6,
            score: 97.35,
            status: DomainMyCourse.Status.completed,
            type: DomainMyCourse.Type_.general,
            creditType: "ილიაუნი"
        )
    ]
    
    var body: some View {
        _CourseCatalogScreen(
            query: $query,
            state: .success(current: courses, past: courses),
            allCourses: nil
        )
    }
}

struct _CourseCatalogScreen: View {
    
    @Environment(\.colorScheme) private var colorScheme
    
    @State private var mode: CourseCatalogMode = .my
    
    @Binding var query: String
    let state: CourseCatalogState
    let allCourses: PagingItemsObservable<DomainCourse>?
    
    private var backgroundColor: Color {
        switch mode {
        case .all:
            Color(UIColor.systemBackground)
        case .my:
            Color(UIColor.systemGroupedBackground)
        }
    }
    
    private var morphinLabelBackgroundStyle: AnyShapeStyle {
        switch mode {
        case .all:
            AnyShapeStyle(.thinMaterial)
        case .my:
            switch colorScheme {
            case .dark:
                AnyShapeStyle(Color(UIColor.systemGray6))
            case .light:
                AnyShapeStyle(Color(UIColor.systemBackground))
            }
        }
    }
    
    var body: some View {
        ScrollView {
            LazyVStack(spacing: 0) {
                MorphingPicker(selection: $mode) {
                    MorphingLabel(title: "My", systemImage: "list.bullet", color: .blue)
                        .tag(CourseCatalogMode.my)
                    MorphingLabel(title: "All", systemImage: "list.bullet.rectangle", color: .red)
                        .tag(CourseCatalogMode.all)
                }
                .padding(.horizontal)
                .padding(.bottom, 8)
                .morphingLabelBackgroundStyle(morphinLabelBackgroundStyle)
                
                switch mode {
                case .my:
                    switch state {
                    case .loading:
                        ProgressView()
                    case let .success(current, past):
                        InsetGroupedSection("Current") {
                            ForEach(current) { myCourse in
//                                ListNavigationLink(value: myCourse) {
                                    MyCourseView(course: myCourse)
//                                }
                                .separatorVisible(current.last != myCourse)
                            }
                        }
                        
                        InsetGroupedSection("Past") {
                            ForEach(past) { myCourse in
//                                ListNavigationLink(value: myCourse) {
                                    MyCourseView(course: myCourse)
//                                }
                                .separatorVisible(past.last != myCourse)
                            }
                        }
                    case .error:
                        Text("Error")
                    }
                case .all:
                    if let courses = allCourses {
                        ForEach(courses) { course in
                            CourseView(course: course)
                        }
                    }
                }
            }
        }
        .background(backgroundColor)
        .searchable(text: $query)
        .navigationTitle("Courses")
        .toolbarTitleDisplayMode(.inlineLarge)
    }
        
}

private struct MyCourseView: View {
    
    let course: DomainMyCourse
    
    private var moduleColor: Color {
        switch course.type {
        case .general: .blue
        case .program: .gray
        }
    }
    
    private var statusColor: Color {
        switch course.status {
        case .ongoing: .yellow
        case .completed: .green
        case .failed: .red
        }
    }
    
    var body: some View {
        ListNavigationLink(value: course) {
            VStack(alignment: .leading) {
                HStack {
                    Circle()
                        .frame(width: 6, height: 6)
                        .foregroundStyle(moduleColor)
                        .padding(.leading, 2)
                    
                    Text(course.code)
                        .font(.caption)
                        .foregroundStyle(Color.secondary)
                }
                
                Text(course.name)
                    .foregroundStyle(Color.primary)
                    .multilineTextAlignment(.leading)
                
                HStack(alignment: .center, spacing: 4) {
                    Image(systemName: "star.circle.fill")
                        .foregroundStyle(statusColor)
                    Text(course.score.description)
                    
                    Text("•")
                    
                    HStack {
                        Text(course.receivedCredits.description)
                        Divider()
                            .rotationEffect(.degrees(10))
                        Text(course.courseCredits.description)
                    }
                    .padding(.vertical, 2)
                    .padding(.horizontal, 6)
                    .background(.thinMaterial)
                    .clipShape(RoundedRectangle(cornerRadius: 4))
                    .fixedSize(horizontal: false, vertical: true)
                    
                    Text("•")
                    
                    Text(course.creditType)
                }
                .font(.subheadline)
                .foregroundStyle(Color.secondary)
            }
        }
    }
    
}

private struct CourseView: View {
    
    let course: DomainCourse
    
    private var moduleColor: Color {
        course.isGeneral ? .blue : .gray
    }
    
    var body: some View {
        ListNavigationLink(value: course) {
            VStack(alignment: .leading) {
                HStack {
                    Circle()
                        .frame(width: 6, height: 6)
                        .foregroundStyle(moduleColor)
                        .padding(.leading, 2)
                    
                    Text(course.programCode)
                        .font(.caption)
                        .foregroundStyle(Color.secondary)
                }
                Text(course.name)
                    .foregroundStyle(Color.primary)
                    .multilineTextAlignment(.leading)
                HStack(alignment: .center, spacing: 4) {
                    Text(course.code)
                        .padding(.vertical, 2)
                        .padding(.horizontal, 6)
                        .background(.thinMaterial)
                        .clipShape(RoundedRectangle(cornerRadius: 4))
                        .fixedSize(horizontal: false, vertical: true)
                    
                    Text("•")
                    
                    Text(course.credits.description)
                        .padding(.vertical, 2)
                        .padding(.horizontal, 6)
                        .background(.thinMaterial)
                        .clipShape(RoundedRectangle(cornerRadius: 4))
                        .fixedSize(horizontal: false, vertical: true)
                }
                .font(.subheadline)
                .foregroundStyle(Color.secondary)
            }
        }
    }
}

enum CourseCatalogState {
    case loading
    case success(
        current: [DomainMyCourse],
        past: [DomainMyCourse]
    )
    case error
}

enum CourseCatalogMode: Hashable {
    case all
    case my
}

#Preview {
    CourseCatalogScreenPreview()
}
