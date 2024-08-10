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
    @ObservationIgnored private var newsTask: Task<Void, Error>?
    @ObservationIgnored private var newsRepository: NewsRepository {
        DiProvider.shared.newsRepository
    }
    
    private(set) var newsItems: PagingItemsObservable<DomainNewsPreview>?
    
    init() {
        newsTask = Task {
            newsItems = PagingItemsObservable(newsRepository.getNews())
        }
    }
    
    deinit {
        newsTask?.cancel()
    }
}

struct NewsScreen: View {
    @State private var viewModel = NewsViewModel()
    
    var body: some View {
        _NewsScreen(
            news: viewModel.newsItems
        )
    }
}

struct NewsScreenPreview: View {
    var body: some View {
        EmptyView()
    }
}

struct _NewsScreen: View {
    
    let news: PagingItemsObservable<DomainNewsPreview>?
    
    var body: some View {
        if let news = news {
            switch onEnum(of: news.loadingStates.refresh) {
            case .loading:
                ProgressView()
            case .notLoading:
                List {
                    ForEach(0..<news.items.count, id: \.self) { index in
                        let newsPreview = news[index]
                        VStack {
                            Text(newsPreview.title)
                        }
                    }
                }
            case .error:
                Text("Errpr")
            }
        }
    }
    
}

#Preview {
    NewsScreenPreview()
}
