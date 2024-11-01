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
            state: viewModel.state,
            courseScreen: { courseId in
                CourseScreen(courseId: courseId)
            }
        )
    }
}

enum HomeScreenState {
    case loading
    case success(selectedDay: Int, lectures: OrderedDictionary<String, [DomainLectureInfo]>)
    case error
}

struct _HomeScreen<CourseScreen: View>: View {
    let state: HomeScreenState
    let courseScreen: (String) -> CourseScreen
    
    @State private var selectedDay: Int
    
    init(state: HomeScreenState, courseScreen: @escaping (String) -> CourseScreen) {
        self.state = state
        self.courseScreen = courseScreen
        
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
            case let .success(_, lecturesByDay):
                List {
                    ForEach(Array(lecturesByDay.keys), id: \.self) { day in
                        Section {
                            ForEach(lecturesByDay[day]!, id: \.hashValue) { lecture in
                                NavigationLink(
                                    destination: {
                                        courseScreen(lecture.id)
                                    }
                                ) {
                                    VStack(alignment: .leading) {
                                        Text(lecture.name)
                                        Text(lecture.lecturer)
                                        Text(lecture.time)
                                    }
                                }
                            }
                        } header: {
                            Text(day)
                                .padding(.leading, -12)
                        }
                    }
                }
            case .error:
                Text("Error")
            }
        }
        .navigationTitle("Home")
        .toolbarTitleDisplayMode(.inlineLarge)
        .headerProminence(.increased)
    }
}
