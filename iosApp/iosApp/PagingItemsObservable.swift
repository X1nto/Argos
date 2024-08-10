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

    private(set) var items: [T] = []
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
    
    var count: Int {
        items.count
    }
    
    init(_ pagingData: Kotlinx_coroutines_coreFlow) {
        pagingDataPresenter = try! CreateIosPagingItems(flow: pagingData) as! IosPagingItems<T>
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
    
    convenience init(_ pagingSource: DomainPagedResponsePager<AnyObject, T>) {
        self.init(pagingSource.flow)
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
