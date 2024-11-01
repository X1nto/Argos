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
    
    let viewModel: GroupsViewModel
    
    init(courseId: String) {
        self.viewModel = .init(courseId: courseId)
    }
    
    var body: some View {
        _GroupsPage(
            state: viewModel.state,
            groupScreen: { group in
                EmptyView()
            }
        )
    }
}

struct _GroupsPage<GroupScreen: View>: View {
    
    let state: GroupsState
    let groupScreen: (DomainCourseGroup) -> GroupScreen
    
    @State private var selection: String?
    
    var body: some View {
        switch state {
        case .loading:
            ProgressView()
        case .success(let groups):
            List(groups, id: \.id) { group in
                GroupCard(
                    group: group,
                    groupScreen: { groupScreen(group) }
                )
                .listRowSeparator(.hidden)
            }
            .listStyle(.inset)
            .scrollIndicators(.visible)
            .listRowSpacing(12)
            .contentMargins(12, for: .scrollContent)
            .scrollContentBackground(.hidden)
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

private struct GroupCard<GroupScreen: View>: View {
    let group: DomainCourseGroup
    let groupScreen: () -> GroupScreen
    
    private var lecturersString: String {
        group.lecturers.map { $0.fullName }.joined(separator: ", ")
    }
    
    @State var isTapped: Bool = false
    
    var body: some View {
        NavigationLink(destination: groupScreen) {
            VStack(alignment: .leading) {
                Text(lecturersString)
                Text(group.name)
                HStack {
                    if (group.isChosen) {
                        Button(action: {}) {
                            Text("Rechoose")
                        }
                        .tint(Color.orange)
                        .opacity(group.rechooseError == nil ? 1.0 : 0.7)
                        
                        Button(action: {}) {
                            Text("Remove")
                        }
                        .tint(Color.red)
                        .opacity(group.removeError == nil ? 1.0 : 0.7)
                    } else {
                        Button(action: {}) {
                            Text("Choose")
                        }
                        .tint(Color.green)
                        .opacity(group.chooseError == nil ? 1.0 : 0.7)
                    }
                }
                .buttonStyle(.borderedProminent)
            }
        }
        .listRowBackground(
            EmptyView()
                .background(.thinMaterial)
                .clipShape(RoundedRectangle(cornerRadius: 12))
                .opacity(isTapped ? 0 : 1)
        )
    }
}
