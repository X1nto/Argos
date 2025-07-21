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
    
    private var syllabus: String {
        guard case .success(let data) = state else {
            return ""
        }
        
        var syllabus = String()

        func append(section: String, body: String?) {
            guard let body else {
                return
            }
            
            syllabus.append("<div style=\"display:flex; flex-direction:column; gap:8px;\">")
            syllabus.append("<h3>")
            syllabus.append(section)
            syllabus.append("</h3>")
            syllabus.append(body)
            syllabus.append("</div>")
        }
        
        syllabus.append("<div style=\"display:flex; flex-direction:column; gap:12px;\">")
        append(section: "Duration", body: data.duration)
        append(section: "Hours", body: data.hours)
        append(section: "Lecturers", body: data.lecturers)
        append(section: "Prerequisites", body: data.prerequisites)
        append(section: "Methods", body: data.methods)
        append(section: "Mission", body: data.mission)
        append(section: "Topics", body: data.topics)
        append(section: "Outcomes", body: data.outcomes)
        append(section: "Evaluation", body: data.evaluation)
        append(section: "Resources", body: data.resources)
        append(section: "Other resources", body: data.otherResources)
        append(section: "Schedule", body: data.schedule)
        syllabus.append("</div>")
        
        return syllabus
    }
    
    var body: some View {
        switch state {
        case .loading:
            ProgressView()
        case .success:
            HtmlText2(syllabus)
                .htmlText2ContentPadding(12)
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
