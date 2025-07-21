//
//  ScoresPage.swift
//  Argos
//
//  Created by Tornike Khintibidze on 05.07.25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import ArgosCore

@Observable
class ScoresViewModel {
    
    var state: ScoresState = .loading
    
    init(courseId: String) {
        Task {
            for try await response in CoursesRepository.shared.getCourseScores(courseId: courseId).flow {
                state = switch onEnum(of: response) {
                case .loading: .loading
                case .success(let data): .success(data.value!)
                case .error: .error
                }
            }
        }
    }
}

struct ScoresPage: View {
    
    private let viewModel: ScoresViewModel

    init(courseId: String) {
        viewModel = .init(courseId: courseId)
    }
    
    var body: some View {
        _ScoresPage(state: viewModel.state)
    }
}

struct _ScoresPage: View {
    
    let state: ScoresState
    
    var body: some View {
        Group {
            switch state {
            case .loading:
                ProgressView()
            case .success(let scores):
                VStack(alignment: .center, spacing: 24) {
                    Grid(alignment: .center, horizontalSpacing: 8, verticalSpacing: 8){
                        GridRow {
                            Text("Criterion")
                                .padding(.leading, 24)
                            Text("Score")
                                .padding(.trailing)
                        }
                        .font(.title2)
                        .fontWeight(.bold)
                        
                        ForEach(scores.criteria, id: \.name) { criterion in
                            Divider()
                                .padding(.leading, 24)
                            GridRow {
                                Text(criterion.name)
                                    .gridColumnAlignment(.leading)
                                    .padding(.leading, 24)
                                Text(criterion.score.description)
                                    .padding(.trailing)
                            }
                            .font(.body)
                            .foregroundStyle(Color.secondary)
                        }
                    }
//                    .padding(.leading, 24)

                    LabeledContent {
                        HStack {
                            Text(String(scores.acquiredCredits))
                            Divider()
                                .rotationEffect(.degrees(15))
                            Text(String(scores.requiredCredits))
                        }
                        .font(.callout)
                        .fontWeight(.medium)
                        .padding(.horizontal, 12)
                        .padding(.vertical, 6)
                        .background(.background)
                        .clipShape(RoundedRectangle(cornerRadius: 8))
                    } label: {
                        Text("Credits")
                    }
                    .fixedSize(horizontal: false, vertical: true)
                    .labeledContentStyle(ListLabeledContentStyle())
                }
                .padding(.top, 12)
            case .error:
                Text("Error")
            }
        }
    }
}

private struct ListLabeledContentStyle: LabeledContentStyle {
    
    @Environment(\.colorScheme) private var colorScheme
    
    private var rowColor: AnyShapeStyle {
        switch colorScheme {
        case .dark:
//            AnyShapeStyle(.regularMaterial)
            AnyShapeStyle(Color(UIColor.systemGray6))
        case .light:
            AnyShapeStyle(Color(UIColor.systemBackground))
        }
    }
    
    func makeBody(configuration: Configuration) -> some View {
        HStack {
            configuration.label
                .font(.body)
            Spacer()
            configuration.content
        }
        .padding(.horizontal, 12)
        .padding(.vertical, 8)
        .background(.thinMaterial)
        .clipShape(RoundedRectangle(cornerRadius: 12))
        .padding(.horizontal)
    }
    
}

enum ScoresState {
    case loading
    case success(DomainCourseScores)
    case error
}
