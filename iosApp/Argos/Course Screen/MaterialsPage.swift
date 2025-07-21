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
    
    @Environment(\.openURL) private var openUrl
    
    private let viewModel: MaterialsViewModel
    
    init(courseId: String) {
        viewModel = MaterialsViewModel(courseId: courseId)
    }
    
    var body: some View {
        _MaterialsPage(
            materials: viewModel.materials,
            onMaterialDownload: { downloadUrl in
                openUrl(URL(string: downloadUrl)!)
            }
        )
    }
}

struct _MaterialsPage: View {
    
    let materials: PagingItemsObservable<DomainCourseMaterial>?
    let onMaterialDownload: (String) -> Void
    
    var body: some View {
        if let materials = materials {
            //TODO show "X items" at the bottom similar to Files
            List(materials) { material in
                MaterialListItem(
                    material: material,
                    onMaterialDownload: {
                        onMaterialDownload(material.downloadUrl)
                    }
                )
                .contextMenu {
                    Button(action: {}) {
                        Label("Get info", systemImage: "info.circle")
                    }
                }
                .listSectionSeparator(.hidden, edges: .bottom)
            }
            .listStyle(.inset)
        }
    }
}

private struct MaterialListItem: View {
    let material: DomainCourseMaterial
    let onMaterialDownload: () -> Void
    
    private var iconImage: ImageResource {
        switch material.type {
        case .word: .wordDocumentFill
        case .excel: .xlsDocumentFill
        case .powerpoint: .imageDocumentFill //TODO
        case .pdf: .pdfAltDocumentFill
        case .video: .videoDocumentFill
        case .text: .txtDocumentFill
        }
    }
    
    private var iconColor: Color {
        switch material.type {
        case .word: .init(red: 0.102, green: 0.361, blue: 0.741)
        case .excel: .init(red: 0.063, green: 0.486, blue: 0.255)
        case .powerpoint: .init(red: 0.929, green: 0.424, blue: 0.278)
        case .pdf: .init(red: 0.722, green: 0.0, blue: 0.0)
        case .video, .text: .primary
        }
    }
    
    var body: some View {
        HStack(alignment: .center, spacing: 12) {
            Image(iconImage)
                .foregroundStyle(iconColor)
                .font(.system(size: 24))
            VStack(alignment: .leading) {
                Text(material.name)
                Text(material.lecturer.fullName)
                    .font(.subheadline)
                    .foregroundStyle(.secondary)
            }
            Spacer()
            Button(action: onMaterialDownload) {
                Image(systemName: "square.and.arrow.down")
            }
            .foregroundStyle(.secondary)
            .font(.system(size: 20))
        }
    }
}

enum MaterialsState {
    case loading
    case success([DomainCourseMaterial])
    case error
}

#Preview {
    
}
