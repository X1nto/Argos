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
class NewsListViewModel {
    @ObservationIgnored private var newsTask: Task<Void, Error>?
    @ObservationIgnored private var newsRepository: NewsRepository {
        DiProvider.shared.newsRepository
    }
    
    private(set) var newsItems: PagingItemsObservable<DomainNewsPreview>?
    
    init() {
        newsTask = Task {
            newsItems = PagingItemsObservable(newsRepository.getAllNews())
        }
    }
    
    deinit {
        newsTask?.cancel()
    }
}

struct NewsListScreen: View {
    @State private var viewModel = NewsListViewModel()
    
    var body: some View {
        _NewsListScreen(
            news: viewModel.newsItems
        )
        .navigationDestination(for: DomainNewsPreview.self) { news in
            NewsScreen(id: news.id)
        }
    }
}

struct NewsListScreenPreview: View {
    var body: some View {
        EmptyView()
    }
}

struct _NewsListScreen: View {
    
    let news: PagingItemsObservable<DomainNewsPreview>?
    
    var body: some View {
        Group {
            if let news = news {
                switch onEnum(of: news.loadingStates.refresh) {
                case .loading:
                    ProgressView()
                case .notLoading:
                    List(news) { news in
                        NavigationLink(value: news) {
                            HStack {
                                Circle()
                                    .size(width: 8, height: 8)
                                    .fill(news.seen ? Color.clear : Color.accentColor)
                                    .frame(width: 16, height: 16, alignment: .center)
                                
                                VStack(alignment: .leading, spacing: 6) {
                                    Text(news.title)
                                        .lineLimit(2)
                                    HStack(alignment: .center, spacing: 6) {
                                        Image(systemName: "clock.fill")
                                        Text(news.publishDate.fullDateTime)
                                    }
                                    .font(.footnote)
                                    .foregroundStyle(Color.secondary)
                                }
                            }
                        }
                        .alignmentGuide(.listRowSeparatorLeading) { _ in
                            24
                        }
                    }
                    .listStyle(.inset)
                case .error:
                    Text("Errpr")
                }
            }
        }
        .navigationTitle("News")
        .toolbarTitleDisplayMode(.inlineLarge)
    }
    
}

#Preview {
    NewsListScreenPreview()
}
