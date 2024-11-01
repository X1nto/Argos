//
//  SyllabusPage.swift
//  iosApp
//
//  Created by Tornike Khintibidze on 25.09.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import ArgosCore
import Observation

@Observable
class SyllabusViewModel {
    
    @ObservationIgnored private var fetchTask: Task<Void, Error>?
    @ObservationIgnored private var courseRepository: CoursesRepository {
        DiProvider.shared.coursesRepository
    }
    
    var state: SyllabusState = .loading
    
    init(courseId: String) {
        fetchTask = Task {
            for try await syllabusData in courseRepository.getCourseSyllabus(courseId: courseId).flow {
                switch onEnum(of: syllabusData) {
                case .loading:
                    state = .loading
                case .success(let syllabus):
                    state = .success(syllabus.value!)
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

struct SyllabusPage: View {
    
    private let viewModel: SyllabusViewModel
    
    init(courseId: String) {
        viewModel = SyllabusViewModel(courseId: courseId)
    }
    
    var body: some View {
        _SyllabusPage(state: viewModel.state)
    }
}

struct _SyllabusPage: View {
    
    let state: SyllabusState
    
    var body: some View {
        switch state {
        case .loading:
            ProgressView()
        case .success(let syllabus):
            HtmlText2(syllabus.mission!)
        case .error:
            Text("Error")
        }
    }
}

enum SyllabusState {
    case loading
    case success(DomainCourseSyllabus)
    case error
}
