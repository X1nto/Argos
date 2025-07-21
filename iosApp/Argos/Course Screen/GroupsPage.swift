//
//  GroupsPage.swift
//  iosApp
//
//  Created by Tornike Khintibidze on 26.09.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import Observation
import ArgosCore

@Observable
class GroupsViewModel {
    @ObservationIgnored private var coursesRepository: CoursesRepository {
        DiProvider.shared.coursesRepository
    }
    @ObservationIgnored private var fetchTask: Task<Void, Error>?
    
    var state: GroupsState = .loading
    
    init(courseId: String) {
        fetchTask = Task {
            for try await groupsData in coursesRepository.getCourseGroups(courseId: courseId).flow {
                switch onEnum(of: groupsData) {
                case .loading:
                    state = .loading
                case .success(let groups):
                    state = .success(groups.value! as! [DomainCourseGroup])
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

struct GroupsPage: View {
    
    private let courseId: String
    private let viewModel: GroupsViewModel
    
    @State private var selectedGroup: DomainCourseGroup?
    
    init(courseId: String) {
        self.courseId = courseId
        self.viewModel = .init(courseId: courseId)
    }
    
    var body: some View {
        _GroupsPage(
            state: viewModel.state,
            selected: $selectedGroup,
            preview: { group in
                GroupScheduleLiteScreen(courseId: courseId, groupId: group.id)
            }
        )
//        .navigationDestination(for: DomainCourseGroup.self) { group in
//            GroupScheduleFullScreen(courseId: courseId, group: group)
//        }
        .sheet(item: $selectedGroup) { group in
            GroupScheduleFullScreen(courseId: courseId, group: group)
        }
    }
}

private struct _GroupsPage<GroupPreview: View>: View {
    
    let state: GroupsState
    @Binding var selected: DomainCourseGroup?
    let preview: (DomainCourseGroup) -> GroupPreview
    
    var body: some View {
        switch state {
        case .loading:
            ProgressView()
        case .success(let groups):
            List {
                VStack(alignment: .leading) {
                    Legend(color: .green, label: "Group can be chosen")
                    Legend(color: .yellow, label: "Group schedule conflicts with yours")
                    Legend(color: .red, label: "Group cannot be chosen")
                }
                
                ForEach(groups) { group in
                    Button {
                        selected = group
                    } label: {
                        GroupCard(group: group)
                            .contextMenu {
                                if group.isChosen {
                                    Button("Rechoose", systemImage: "arrow.trianglehead.2.clockwise") {
                                        
                                    }
                                    Button("Remove", systemImage: "minus", role: .destructive) {
                                        
                                    }
                                } else {
                                    Button("Choose", systemImage: "plus") {
                                        
                                    }
                                }
                            } preview: {
                                preview(group)
                            }
                    }
                }
            }
            .listStyle(.plain)
        case .error:
            Text("Error")
        }
    }
}

enum GroupsState {
    case loading
    case success([DomainCourseGroup])
    case error
}

private struct Legend: View {
    
    let color: Color
    let label: LocalizedStringKey
    
    var body: some View {
        HStack(alignment: .center, spacing: 16) {
            Circle()
                .fill(color)
                .frame(width: 8, height: 8)
            
            Text(label)
                .font(.subheadline)
                .foregroundStyle(Color.secondary)
                .fontWeight(.medium)
        }
    }
}

private struct GroupCard: View {
    let group: DomainCourseGroup
    
    private var lecturersString: String {
        group.lecturers.map { $0.fullName }.joined(separator: ", ")
    }
    
    private var statusColor: Color {
        if group.chooseError != nil {
            return .red
        }
        
        if group.isConflicting {
            return .yellow
        }
        
        return .green
    }
    
    var body: some View {
        HStack(spacing: 16) {
            Circle()
                .fill(statusColor)
                .frame(width: 8, height: 8)
            
            VStack(alignment: .leading) {
                Text(group.name)
                Text(lecturersString)
                    .font(.subheadline)
                    .foregroundStyle(Color.secondary)
            }
            
            Spacer()
            
            HStack(spacing: 0) {
                if group.isChosen {
                    Button(systemImage: "minus") {
                        
                    }
                    .tint(Color.red)
                    .disabled(group.removeError != nil)
                    
                    Button(systemImage: "arrow.trianglehead.2.clockwise") {
                        
                    }
                    .tint(Color.orange)
                    .disabled(group.rechooseError != nil)
                } else {
                    Button(systemImage: "plus") {
                        
                    }
                    .tint(Color.green)
                    .disabled(group.chooseError != nil)
                }
            }
            .buttonStyle(SegmentedButtonStyle())
            .clipShape(RoundedRectangle(cornerRadius: 8))
        }
//        .background(NavigationLink("", value: group).opacity(0))
    }
}

private extension Button where Label == Image {
    
    init(systemImage: String, action: @escaping () -> Void) {
        self.init(action: action) {
            Image(systemName: systemImage)
        }
    }
    
    init(role: ButtonRole?, systemImage: String, action: @escaping () -> Void) {
        self.init(role: role, action: action) {
            Image(systemName: systemImage)
        }
    }
}

private struct SegmentedButtonStyle: ButtonStyle {
    
    @Environment(\.isEnabled) private var isEnabled
    @Environment(\.colorScheme) private var colorScheme
    
    private var overlayColor: Color {
        switch colorScheme {
        case .dark:
            Color.black
        case .light:
            Color.white
        }
    }
    
    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .frame(width: 48, height: 32)
            .font(.subheadline)
            .fontWeight(.bold)
            .foregroundStyle(Color.white)
            .background(.tint)
            .overlay(isEnabled ? Color.clear : overlayColor.opacity(0.6))
            .overlay(configuration.isPressed ? overlayColor : Color.clear)
    }
    
}
