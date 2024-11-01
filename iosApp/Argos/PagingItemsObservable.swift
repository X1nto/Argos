//
//  PagingItemsObservable.swift
//  iosApp
//
//  Created by Tornike Khintibidze on 30.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import ArgosCore
import Observation

@Observable
class PagingItemsObservable<T: AnyObject> {
    
    @ObservationIgnored private var pagingTask: Task<Void, Error>?
    @ObservationIgnored private var statesTask: Task<Void, Error>?

    @ObservationIgnored private let pagingDataPresenter: IosPagingItems<T>
    
    @ObservationIgnored private let pagingSource: DomainPagedResponsePager<AnyObject, T>

    private var items: [T] = []
    private(set) var loadingStates = Paging_commonCombinedLoadStates(
        refresh: Paging_commonLoadState.Loading(),
        prepend: Paging_commonLoadState.NotLoading(endOfPaginationReached: false),
        append: Paging_commonLoadState.NotLoading(endOfPaginationReached: false),
        source: Paging_commonLoadStates(
            refresh: Paging_commonLoadState.Loading(),
            prepend: Paging_commonLoadState.NotLoading(endOfPaginationReached: false),
            append: Paging_commonLoadState.NotLoading(endOfPaginationReached: false)
        ),
        mediator: nil
    )
    
    init(_ pagingSource: DomainPagedResponsePager<AnyObject, T>) {
        self.pagingSource = pagingSource
        pagingDataPresenter = try! CreateIosPagingItems(flow: pagingSource.flow) as! IosPagingItems<T>
        pagingTask = Task {
            for try await items in pagingDataPresenter.snapshotList {
                self.items = items
            }
        }
        statesTask = Task {
            for try await state in pagingDataPresenter.loadState {
                self.loadingStates = state
            }
        }
    }
    
    var count: Int {
        items.count
    }
    
    func refresh() {
        pagingSource.invalidate()
    }
    
    
    subscript(index: Int) -> T {
        return pagingDataPresenter.get(index: Int32(index))
    }
    
    deinit {
        pagingTask?.cancel()
        statesTask?.cancel()
        pagingDataPresenter.dispose()
    }
    
}


extension List where SelectionValue == Never {
    
    nonisolated init<Data, RowContent: View>(_ pagingData: PagingItemsObservable<Data>, @ViewBuilder rowContent: @escaping (Data) -> RowContent) where Content == ForEach<Range<Int>, Int, RowContent> {
        self.init {
            ForEach(0..<pagingData.count, id: \.self) { index in
                let item = pagingData[index]
                rowContent(item)
            }
        }
    }
}

extension ForEach where Content : View, Data == Range<Int>, ID == Int {
    
    init<PagingData>(
        _ pagingData: PagingItemsObservable<PagingData>,
        @ViewBuilder content: @escaping (PagingData) -> Content
    ) {
        self.init(0..<pagingData.count, id: \.self) { index in
            let item = pagingData[index]
            content(item)
        }
    }
    
}
