//
//  NewsScreen\.swift
//  iosApp
//
//  Created by Tornike Khintibidze on 10.08.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import ArgosCore
import Observation

@Observable
class NewsViewModel {
    
    var state: NewsState = .loading
    
    init(id: String) {
        Task {
            for try await result in NewsRepository.shared.getNews(id: id).flow {
                self.state = switch onEnum(of: result) {
                    case .loading: .loading
                    case .success(let success): .success(news: success.value!)
                    case .error: .error
                }
            }
        }
    }
}

struct NewsScreen: View {
    let id: String
    
    @State private var viewModel: NewsViewModel
    
    init(id: String) {
        self.id = id
        self.viewModel = NewsViewModel(id: id)
    }
    
    var body: some View {
        _NewsScreen(
            state: viewModel.state
        )
    }
}

struct NewsScreenPreview: View {
    var body: some View {
        EmptyView()
    }
}

struct _NewsScreen: View {
    
    let state: NewsState
    
    var body: some View {
        Group {
            switch state {
            case .loading:
                ProgressView()
            case .success(let news):
                HtmlText2(
                    """
                    <div style="display:flex; flex-direction:column; gap:8px;">
                        <h4>\(news.title)</h4>
                        \(news.text)
                    </div>
                    """
                )
                .htmlText2ContentPadding(16)
            case .error:
                Text("Error")
            }
        }
    }
}

enum NewsState {
    case loading
    case success(news: DomainNews)
    case error
}

#Preview {
    NewsListScreenPreview()
}
