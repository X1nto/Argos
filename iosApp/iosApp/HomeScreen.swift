//
//  Home Screen.swift
//  iosApp
//
//  Created by Tornike Khintibidze on 21.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import ArgosCore
import OrderedCollections
import Observation

@Observable
class HomeViewModel {
    @ObservationIgnored private var fetchTask: Task<Void, Error>?
    @ObservationIgnored private var lecturesRepository: LecturesRepository {
        DiProvider.shared.lecturesRepository
    }
    
    var state: HomeScreenState = .loading
    
    init() {
        fetchTask = Task {
            for try await lectures in lecturesRepository.observeLectures() {
                switch onEnum(of: lectures) {
                case .error:
                    state = .error
                case .loading:
                    state = .loading
                case .success(let data):
                    let keys = data.value!.allKeys as! [String]
                    let values = data.value!.allValues as! [[DomainLectureInfo]]
                    
                    let lectures = OrderedDictionary(uniqueKeys: keys, values: values)
                    state = .success(selectedDay: 0, lectures: lectures)
                }
            }
        }
    }
}

struct HomeScreen: View {
    @State var viewModel = HomeViewModel()
    
    var body: some View {
        _HomeScreen(
            state: viewModel.state
        )
    }
}

enum HomeScreenState {
    case loading
    case success(selectedDay: Int, lectures: OrderedDictionary<String, [DomainLectureInfo]>)
    case error
}

struct _HomeScreen: View {
    let state: HomeScreenState
    
    @State private var selectedDay: Int
    
    init(state: HomeScreenState) {
        self.state = state
        
        if case let .success(selectedDay, _) = state {
            self.selectedDay = selectedDay
        } else {
            self.selectedDay = 0
        }
    }
    
    var body: some View {
        Group {
            switch state {
            case .loading:
                ProgressView()
            case .success(selectedDay: _, lectures: let lectures):
                TabPager(
                    selectedIndex: $selectedDay,
                    items: lectures,
                    tabContent: { Text($0) }
                ) { lectures in
                    List(lectures, id: \.hashValue) { lecture in
                        NavigationLink(
                            destination: {
                            }
                        ) {
                            VStack(alignment: .leading) {
                                Text(lecture.name)
                                Text(lecture.lecturer)
                                Text(lecture.time)
                            }
                        }
                    }
                }
            case .error:
                Text("Error")
            }
        }
        .navigationTitle("Home")
        .toolbarTitleDisplayMode(.inlineLarge)
    }
}
