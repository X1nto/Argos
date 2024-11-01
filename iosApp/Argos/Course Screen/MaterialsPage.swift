//
//  MaterialsPage.swift
//  iosApp
//
//  Created by Tornike Khintibidze on 27.09.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import Observation
import ArgosCore

@Observable
class MaterialsViewModel {
    private var fetchTask: Task<Void, Error>?
    private var coursesRepository: CoursesRepository {
        DiProvider.shared.coursesRepository
    }
    
    var materials: PagingItemsObservable<DomainCourseMaterial>?
    
    init(courseId: String) {
        fetchTask = Task {
            materials = PagingItemsObservable(coursesRepository.getCourseGroupMaterials(courseId: courseId))
        }
    }
    
    deinit {
        fetchTask?.cancel()
    }
}

struct MaterialsPage: View {
    
    private let viewModel: MaterialsViewModel
    
    init(courseId: String) {
        viewModel = MaterialsViewModel(courseId: courseId)
    }
    
    var body: some View {
        _MaterialsPage(materials: viewModel.materials)
    }
}

struct _MaterialsPage: View {
    
    let materials: PagingItemsObservable<DomainCourseMaterial>?
    
    var body: some View {
        if let materials = materials {
            List(materials) { material in
                Text(material.name)
            }
        }
    }
}

enum MaterialsState {
    case loading
    case success([DomainCourseMaterial])
    case error
}
