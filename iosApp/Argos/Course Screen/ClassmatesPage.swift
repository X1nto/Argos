//
//  ClassmatesPage.swift
//  Argos
//
//  Created by Tornike Khintibidze on 06.07.25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Observation
import ArgosCore

@Observable
private class ClassmatesViewModel {
    
    private(set) var state: ClassmatesState = .loading
    
    init(courseId: String) {
        Task {
            for try await response in CoursesRepository.shared.getCourseClassmates(courseId: courseId).flow {
                await MainActor.run {
                    state = switch onEnum(of: response) {
                    case .loading: .loading
                    case .success(let data): .success(data.value as! [DomainCourseClassmate])
                    case .error: .error
                    }
                }
            }
        }
    }
    
}

struct ClassmatesPage: View {
    
    private let viewModel: ClassmatesViewModel
    
    init(courseId: String) {
        viewModel = .init(courseId: courseId)
    }
    
    var body: some View {
        _ClassmatesPage(state: viewModel.state)
    }
}

private struct _ClassmatesPage: View {
    
    let state: ClassmatesState
    
    var body: some View {
        switch state {
        case .loading:
            ProgressView()
        case .success(let classmates):
            List(classmates) { classmate in
                NavigationLink(value: classmate) {
                    HStack(alignment: .center, spacing: 12) {
                        PrettyAvatarView(name: classmate.fullName)
                        Text(classmate.fullName)
                    }
                }
            }
            .listStyle(.plain)
        case .error:
            Text("Error")
        }
    }
}

private enum ClassmatesState {
    case loading
    case success([DomainCourseClassmate])
    case error
}
