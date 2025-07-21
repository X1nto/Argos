//
//  GroupScheduleSheet.swift
//  Argos
//
//  Created by Tornike Khintibidze on 04.07.25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import ArgosCore

@Observable
private class GroupScheduleLiteViewModel {
    
    var state: GroupScheduleLiteScreenState = .loading
    
    init(courseId: String, groupId: String) {
        Task {
            for try await response in CoursesRepository.shared.getCourseGroupWeekSchedule(courseId: courseId, groupId: groupId).flow {
                state = switch onEnum(of: response) {
                case .loading: .loading
                case let .success(success): .success(success.value as! [DomainCourseGroupSchedule])
                case .error: .error
                }
            }
        }
    }
    
}

struct GroupScheduleLiteScreen: View {
    
    private let viewModel: GroupScheduleLiteViewModel
    
    init(courseId: String, groupId: String) {
        viewModel = .init(courseId: courseId, groupId: groupId)
    }
    
    var body: some View {
        _GroupScheduleLiteScreen(state: viewModel.state)
    }
}

private struct _GroupScheduleLiteScreen: View {
    
    let state: GroupScheduleLiteScreenState
    
    @State private var headerHeight: CGFloat = .zero
    @State private var rowHeights: [DomainCourseGroupSchedule : CGFloat] = [:]
    
    private var layoutHeight: CGFloat {
        var gridHeight = headerHeight + 8 + 32
        
        for (_, value) in rowHeights {
            gridHeight += 8
            gridHeight += value
        }
        
        return gridHeight
    }
    
    var body: some View {
        Group {
            switch state {
            case .loading:
                ProgressView()
            case .success(let schedule):
                Grid(horizontalSpacing: 24, verticalSpacing: 8) {
                    GridRow {
                        Text("Day")
                        Text("Time")
                        Text("Room")
                        Text("Lecturer")
                    }
                    .font(.headline)
                    .fontWeight(.semibold)
//                    .onGeometryChange(for: CGFloat.self) { proxy in
//                        proxy.size.height
//                    } action: { newValue in
//                        if newValue > headerHeight {
//                            headerHeight = newValue
//                        }
//                    }
                    
                    ForEach(schedule) { schedule in
                        Divider()
                        GridRow {
                            Text(schedule.day)
                            Text(schedule.startTime)
//                            VStack {
//                                Text(schedule.day)
//                                Text(schedule.fullTime)
//                            }
                            Text(schedule.room)
                            Text(schedule.lecturer)
                        }
                        .lineLimit(1)
                        .font(.subheadline)
                        .foregroundStyle(Color.secondary)
                        .fixedSize(horizontal: false, vertical: true)
//                        .onGeometryChange(for: CGFloat.self) { proxy in
//                            proxy.size.height
//                        } action: { newValue in
//                            if let existingHeight = rowHeights[schedule] {
//                                if newValue > existingHeight {
//                                    rowHeights[schedule] = newValue
//                                }
//                            }
//                        }
                    }
                }

            case .error:
                Text("Error")
            }
        }
        .padding()
//        .onGeometryChange(for: CGSize.self) { proxy in
//            proxy.size
//        } action: { newValue in
//            gridHeight = newValue.height + 32
//            
//            if case let .success(schedule) = state {
//                gridHeight += CGFloat(schedule.count * 8)
//            }
//        }
//        .frame(minHeight: layoutHeight)
    }
}

private enum GroupScheduleLiteScreenState {
    case loading
    case success([DomainCourseGroupSchedule])
    case error
}
